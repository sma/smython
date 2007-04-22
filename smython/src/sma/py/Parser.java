/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import sma.py.ast.PyAddExpr;
import sma.py.ast.PyAndExpr;
import sma.py.ast.PyAssertStmt;
import sma.py.ast.PyAssignStmt;
import sma.py.ast.PyAttrRef;
import sma.py.ast.PyBitAndExpr;
import sma.py.ast.PyBitInvert;
import sma.py.ast.PyBitOrExpr;
import sma.py.ast.PyBitXorExpr;
import sma.py.ast.PyBreakStmt;
import sma.py.ast.PyCall;
import sma.py.ast.PyClassStmt;
import sma.py.ast.PyComparison;
import sma.py.ast.PyContinueStmt;
import sma.py.ast.PyDefStmt;
import sma.py.ast.PyDelStmt;
import sma.py.ast.PyDictConstr;
import sma.py.ast.PyDivExpr;
import sma.py.ast.PyExceptClause;
import sma.py.ast.PyExecStmt;
import sma.py.ast.PyExpr;
import sma.py.ast.PyExprList;
import sma.py.ast.PyExprStmt;
import sma.py.ast.PyForStmt;
import sma.py.ast.PyGlobalStmt;
import sma.py.ast.PyIfStmt;
import sma.py.ast.PyImportStmt;
import sma.py.ast.PyLambda;
import sma.py.ast.PyListConstr;
import sma.py.ast.PyLiteral;
import sma.py.ast.PyLshiftExpr;
import sma.py.ast.PyModExpr;
import sma.py.ast.PyMulExpr;
import sma.py.ast.PyNegate;
import sma.py.ast.PyNot;
import sma.py.ast.PyOrExpr;
import sma.py.ast.PyParamList;
import sma.py.ast.PyPassStmt;
import sma.py.ast.PyPositive;
import sma.py.ast.PyPowExpr;
import sma.py.ast.PyPrintStmt;
import sma.py.ast.PyRaiseStmt;
import sma.py.ast.PyReturnStmt;
import sma.py.ast.PyRshiftExpr;
import sma.py.ast.PySlicing;
import sma.py.ast.PyStmt;
import sma.py.ast.PyStringConversion;
import sma.py.ast.PySubExpr;
import sma.py.ast.PySubscript;
import sma.py.ast.PySubscription;
import sma.py.ast.PySuite;
import sma.py.ast.PyTryExceptStmt;
import sma.py.ast.PyTryFinallyStmt;
import sma.py.ast.PyTupleConstr;
import sma.py.ast.PyIdentifier;
import sma.py.ast.PyWhileStmt;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

/**
 * Takes a source and returns an abstract syntax tree.
 */
public class Parser extends Scanner {
  public Parser(String source) {
    super(source);
  }

  public PySuite interactiveInput() {
    PySuite suite = new PySuite();
    while (tokenType != null) {
      while (is("NEWLINE")) {
        advance();
      }
      suite.add(stmt());
    }
    return suite;
  }

  /**
   * Parses a class definition, see §7.6.
   * <pre>
   * classdef: 'class' NAME ['(' testlist ')'] ':' suite
   * </pre>
   */
  PyClassStmt classDef() {
    PyString name = name("class name missing");
    PyExprList superclasses = null;
    if (match("(")) {
      superclasses = testlist();
      superclasses.setTuple(true);
      expect(")");
    }
    expect(":");
    return new PyClassStmt(name, superclasses, suite());
  }

  /**
   * Parses a function definition, see §7.5.
   * <pre>
   * funcdef: 'def' NAME parameters ':' suite
   * parameters: '(' [varargslist] ')'
   * </pre>
   */
  PyDefStmt funcDef() {
    PyString name = name("function name missing");
    expect("(");
    PyParamList parameters = parameters();
    expect(")");
    expect(":");
    return new PyDefStmt(name, parameters, suite());
  }

  /**
   * Parses a lambda expression, see §5.10.
   * <pre>
   * lambdef: 'lambda' [varargslist] ':' test
   * </pre>
   */
  PyLambda lambDef() {
    PyParamList list = parameters();
    expect(":");
    return new PyLambda(list, test());
  }

  /**
   * Parses the parameter list of a function or lambda definition.
   * <pre>
   * varargslist: (fpdef ['=' test] ',')* ('*' NAME [',' '**' NAME] | '**' NAME)
   *              | fpdef ['=' test] (',' fpdef ['=' test])* [',']
   * </pre>
   */
  PyParamList parameters() {
    int nargs = 0;
    List<PyObject> parameters = new ArrayList<PyObject>();
    PyString rest = null;
    PyString kwrest = null;
    PyExprList inits = new PyExprList();
    inits.setTuple(true);

    while (is("NAME") || is("(")) {
      parameters.add(parameter());
      if (match("=")) {
        inits.add(test());
      } else {
        nargs++;
      }
      if (!is(")") && !is(":")) {
        expect(",");
      }
    }
    if (match("*")) {
      rest = name("name after * in parameter list expected");
      if (!is(")") && !is(":")) {
        expect(",");
      }
    }
    if (match("**")) {
      kwrest = name("name after ** in parameter list expected");
    }

    return new PyParamList(nargs, new PyTuple(parameters.toArray(new PyObject[parameters.size()])), rest, kwrest, inits);
  }

  /**
   * Parses a single parameter of a function or lambda definition.
   * <pre>
   * fpdef: NAME | '(' fplist ')'
   * fplist: fpdef (',' fpdef)* [',']
   * </pre>
   */
  PyObject parameter() {
    if (is("NAME")) {
      return PyObject.intern((String) advance());
    }
    if (match("(")) {
      List<PyObject> list = new ArrayList<PyObject>();
      list.add(parameter());
      while (match(",")) {
        if (is(")")) {
          break;
        }
        list.add(parameter());
      }
      expect(")");
      return new PyTuple(list.toArray(new PyObject[list.size()]));
    }
    throw notify("invalid syntax, name or sublist expected");
  }

  /**
   * Parses a suite, a sequence of multiple statements in one line or a sequence of indented statements.
   * <pre>
   * suite: simple_stmt | NEWLINE INDENT stmt+ DEDENT
   * </pre>
   */
  PySuite suite() {
    PySuite suite = new PySuite();
    if (match("NEWLINE")) {
      expect("INDENT");
      if (match("DEDENT")) {
        throw notify("suite may not be empty");
      }
      while (!match("DEDENT")) {
        suite.add(stmt());
      }
    } else {
      suite.add(simpleStmt());
    }
    return suite;
  }

  /**
   * Parses a statement.
   * <pre>
   * stmt: simpleStmt | compoundStmt
   * </pre>
   */
  PyStmt stmt() {
    PyStmt stmt = compoundStmt();
    if (stmt == null) {
      stmt = simpleStmt();
    }
    return stmt;
  }

  /**
   * Parses a compound statement that contains a group of other statements, see §7.
   * <pre>
   * compoundStmt: ifStmt | whileStmt | forStmt | tryStmt | funcDef | classDef
   * </pre>
   */
  PyStmt compoundStmt() {
    if (match("if")) {
      return ifStmt();
    } else if (match("while")) {
      return whileStmt();
    } else if (match("for")) {
      return forStmt();
    } else if (match("try")) {
      return tryStmt();
    } else if (match("def")) {
      return funcDef();
    } else if (match("class")) {
      return classDef();
    }
    return null;
  }

  /**
   * Parses an {@code if} statement, see §7.1.
   * Called from {@code compoundStmt()} with "if" already consumed.
   * <pre>
   * ifStmt: 'if' test ':' suite {'elif' test ':' suite} ['else' ':' suite]
   * </pre>
   */
  PyStmt ifStmt() {
    PyExpr condition = test();
    expect(":");
    PySuite thenClause = suite();
    PySuite elseClause = null;
    if (match("elif")) {
      elseClause = new PySuite();
      elseClause.add(ifStmt());
    } else if (match("else")) {
      expect(":");
      elseClause = suite();
    }
    return new PyIfStmt(condition, thenClause, elseClause);
  }

  /**
   * Parses a while statement, see §7.2.
   * Called from {@code compoundStmt()} with "while" already consumed.
   * <pre>
   * whileStmt: 'while' test ':' suite ['else' ':' suite]
   * </pre>
   */
  PyStmt whileStmt() {
    PyExpr condition = test();
    expect(":");
    PySuite bodyClause = suite();
    PySuite elseClause = null;
    if (match("else")) {
      expect(":");
      elseClause = suite();
    }
    return new PyWhileStmt(condition, bodyClause, elseClause);
  }

  /**
   * Parses a for statement, see §7.3.
   * Called from {@code compoundStmt()} with "for" already consumed.
   * <pre>
   * forStmt: 'for' targetlist 'in' testlist ':' suite ['else' ':' suite]
   * </pre>
   */
  PyStmt forStmt() {
    PyExprList targets = targetlist();
    expect("in");
    PyExprList expressions = testlist();
    expect(":");
    PySuite bodyClause = suite();
    PySuite elseClause = null;
    if (match("else")) {
      expect(":");
      elseClause = suite();
    }
    return new PyForStmt(targets, expressions, bodyClause, elseClause);
  }

  /**
   * Parses a list of target expressions, see §6.2.
   * <pre>
   * targetlist: target (',' target) [',']
   * </pre>
   */
  PyExprList targetlist() {
    PyExprList list = new PyExprList();
    list.add(target());
    while (match(",")) {
      if (!isTargetStart()) {
        break;
      }
      list.add(target());
    }
    return list;
  }

  /**
   * Parses a target expression, see §6.2.
   * <pre>
   * target: NAME | '(' targetlist ')' | '[' targetlist ']' | attributeref | subscription | slicing
   * </pre>
   */
  PyExpr target() {
    PyExpr primary = primary();
    if (!primary.isTarget()) {
      throw notify("target expected, got " + primary);
    }
    return primary;
  }

  /**
   * Parses a try statement, see §7.4.
   * Called from {@code compoundStmt()} with "try" already consumed.
   * <pre>
   * tryStmt: 'try' ':' suite (exceptClause ':' suite)+ ['else' ':' suite] |
   *          'try' ':' suite 'finally' ':' suite
   * exceptClause: 'except' [test [',' target]]
   * </pre>
   */
  PyStmt tryStmt() {
    expect(":");
    PySuite tryClause = suite();
    if (match("finally")) {
      expect(":");
      return new PyTryFinallyStmt(tryClause, suite());
    }
    List<PyExceptClause> exceptClauses = new ArrayList<PyExceptClause>();
    while (match("except")) {
      PyExpr exception = null;
      PyExpr target = null;
      if (!is(":")) {
        exception = test();
        if (match(",")) {
          target = target();
        }
      }
      expect(":");
      exceptClauses.add(new PyExceptClause(exception, target, suite()));
    }
    if (exceptClauses.isEmpty()) {
      throw notify("except expected");
    }
    PySuite elseClause = null;
    if (match("else")) {
      expect(":");
      elseClause = suite();
    }
    return new PyTryExceptStmt(tryClause, exceptClauses, elseClause);
  }

  /**
   * Parses a single simple statement or a sequence of simple statements separated by ";", see §7.
   * <pre>
   * simpleStmt: smallStmt (';' smallStmt)* [';'] NEWLINE
   * </pre>
   */
  PyStmt simpleStmt() {
    PyStmt stmt = smallStmt();
    if (is(";")) {
      PySuite suite = new PySuite();
      suite.add(stmt);
      while (match(";")) {
        if (is("NEWLINE")) {
          break;
        }
        suite.add(smallStmt());
      }
      stmt = suite;
    }
    expect("NEWLINE");
    return stmt;
  }

  /**
   * Parses a simple statement that fits into a single logical line, see §6.
   * Several simple statements may occur on a single line separated by semicolons.
   * <pre>
   * smallStmt: exprStmt | printStmt | delStmt | passStmt | flowStmt | importStmt | globalStmt | execStmt | assertStmt.
   * flowStmt: breakStmt | continueStmt | returnStmt | raiseStmt
   */
  PyStmt smallStmt() {
    if (match("print")) {
      return printStmt();
    }
    if (match("del")) {
      return delStmt();
    }
    if (match("pass")) {
      return passStmt();
    }
    if (match("break")) {
      return breakStmt();
    }
    if (match("continue")) {
      return continueStmt();
    }
    if (match("return")) {
      return returnStmt();
    }
    if (match("raise")) {
      return raiseStmt();
    }
    if (match("import")) {
      return importStmt();
    }
    if (match("from")) {
      return fromImportStmt();
    }
    if (match("global")) {
      return globalStmt();
    }
    if (match("exec")) {
      return execStmt();
    }
    if (match("assert")) {
      return assertStmt();
    }
    return exprStmt();
  }

  /**
   * Parses a print statement.
   * <pre>
   * printStmt: 'print' [testtest]
   * </pre>
   */
  PyStmt printStmt() {
    return new PyPrintStmt(optTestlist());
  }

  /** delStmt: 'del' exprlist. */
  PyStmt delStmt() {
    return new PyDelStmt(targetlist());
  }

  /** passStmt: 'pass'. */
  PyStmt passStmt() {
    return new PyPassStmt();
  }

  /** breakStmt: 'break'. */
  PyStmt breakStmt() {
    return new PyBreakStmt();
  }

  /** continueStmt: 'continue'. */
  PyStmt continueStmt() {
    return new PyContinueStmt();
  }

  /** returnStmt: 'return' [testlist]. */
  PyStmt returnStmt() {
    return new PyReturnStmt(optTestlist());
  }

  /** raiseStmt: 'raise' [test [',' test [',' test]]]. */
  PyStmt raiseStmt() {
    PyExpr exception = test();
    PyExpr instance = null;
    PyExpr traceback = null;
    if (match(",")) {
      instance = test();
      if (match(",")) {
        traceback = test();
      }
    }
    return new PyRaiseStmt(exception, instance, traceback);
  }

  /** importStmt: 'import' dottedName (',' dottedName)*
   *              | 'from' dottedName 'import' ('*' | NAME (',' NAME)*)
   * dottedName: NAME ('.' NAME)*. */
  PyStmt importStmt() {
    List<PyString> names = new ArrayList<PyString>();
    names.add(dottedName());
    while (match(",")) {
      names.add(dottedName());
    }
    return new PyImportStmt(null, names);
  }

  PyStmt fromImportStmt() {
    PyString module = dottedName();
    expect("import");
    if (match("*")) {
      return new PyImportStmt(module, null);
    }
    List<PyString> names = new ArrayList<PyString>();
    names.add(name("name expected"));
    while (match(",")) {
      names.add(name("name expected"));
    }
    return new PyImportStmt(module, names);
  }

  PyString dottedName() {
    StringBuilder b = new StringBuilder(64);
    b.append(name("name expected"));
    while (match(".")) {
      b.append(name("name expected"));
    }
    return PyObject.make(b.toString());
  }

  /** globalStmt: 'global' NAME (',' NAME)*. */
  PyStmt globalStmt() {
    List<PyString> names = new ArrayList<PyString>();
    names.add(name("name expected"));
    while (match(",")) {
      names.add(name("name expected"));
    }
    return new PyGlobalStmt(names);
  }

  /**
   * Parses an exec statement, see §6.12.
   * <pre>
   * execStmt: 'exec' test ['in' test [',' test]]
   * </pre>
   */
  PyStmt execStmt() {
    PyExpr expr = expr(); //XXX if using test(), the "in" is taken as comparison
    PyExpr globals = null;
    PyExpr locals = null;
    if (match("in")) {
      globals = test();
      if (match(",")) {
        locals = test();
      }
    }
    return new PyExecStmt(expr, globals, locals);
  }

  /**
   * Parses an assert statement, see §6.2.
   * <pre>
   * assertStmt: 'assert' test [',' test]
   * </pre>
   */
  PyStmt assertStmt() {
    PyExpr condition = test();
    PyExpr message = match(",") ? test() : null;
    return new PyAssertStmt(condition, message);
  }

  /**
   * Parses a list of expressions that are evaluated because of the side effect, see §6.1,
   * or an assignment expression to assign the result of evaluating one or more values
   * to one or more variables, see §6.3.
   * <pre>
   * exprStmt: testlist
   * assignStmt: (targetlist '=')+ testlist
   * </pre>
   */
  PyStmt exprStmt() {
    PyExprList list = testlist();
    if (match("=")) {
      if (!list.isTarget()) {
        throw notify("expected targetlist");
      }
      return new PyAssignStmt(list, testlist());
    }
    return new PyExprStmt(list);
  }

  /**
   * Parses a list of one or more expressions, see §5.11.
   * <pre>
   * testlist: test (',' test)* [',']
   * </pre>
   */
  PyExprList testlist() {
    PyExprList list = new PyExprList();
    list.add(test());
    while (match(",")) {
      if (!isTestStart()) {
        list.setTuple(true);
        break;
      }
      list.add(test());
    }
    return list;
  }

  /**
   * Parses a lambda expression or a sequence of logical or expressions, see § 5.10.
   * <pre>
   * test: andTest ('or' andTest)* | lambdef
   * </pre>
   */
  PyExpr test() {
    if (match("lambda")) {
      return lambDef();
    }
    PyExpr expr = andTest();
    while (match("or")) {
      expr = new PyOrExpr(expr, andTest());
    }
    return expr;
  }

  /**
   * Parses a sequence of logical and expressions, see §5.10.
   * <pre>
   * andTest: notTest ('and' notTest)*
   * </pre>
   */
  PyExpr andTest() {
    PyExpr expr = notTest();
    while (match("and")) {
      expr = new PyAndExpr(expr, notTest());
    }
    return expr;
  }

  /**
   * Parses a sequence of negations, see §5.10.
   * <pre>
   * notTest: 'not' notTest | comparison
   * </pre>
   */
  PyExpr notTest() {
    if (match("not")) {
      return new PyNot(notTest());
    }
    return comparison();
  }

  /**
   * Parses a sequence of comparisons, see §5.9.
   * <pre>
   * comparison: expr (compOp expr)*
   * </pre>
   */
  PyExpr comparison() {
    PyExpr expr = expr();
    PyComparison.Op op = compOp();
    if (op != null) {
      PyComparison compExpr = new PyComparison(expr);
      while (op != null) {
        compExpr.add(op, expr());
        op = compOp();
      }
      expr = compExpr;
    }
    return expr;
  }

  /** compOp: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not'. */
  PyComparison.Op compOp() {
    if (match("<")) {
      return PyComparison.Op.LT;
    } else if (match(">")) {
      return PyComparison.Op.GT;
    } else if (match("<=")) {
      return PyComparison.Op.LE;
    } else if (match(">=")) {
      return PyComparison.Op.GE;
    } else if (match("==")) {
      return PyComparison.Op.EQ;
    } else if (match("!=")) {
      return PyComparison.Op.NE;
    } else if (match("<>")) {
      return PyComparison.Op.NE;
    } else if (match("in")) {
      return PyComparison.Op.IN;
    } else if (match("is")) {
      if (match("not")) {
        return PyComparison.Op.IS_NOT;
      }
      return PyComparison.Op.IS;
    } else if (match("not")) {
      if (match("in")) {
        return PyComparison.Op.NOT_IN;
      }
      throw notify("invalid syntax - not");
    }
    return null;
  }

  /**
   * Parses a sequence of bit-wise or expressions, see §5.8.
   * <pre>
   * expr: xorExpr ('|' xorExpr)*
   * </pre>
   */
  PyExpr expr() {
    PyExpr expr = xorExpr();
    while (match("|")) {
      expr = new PyBitOrExpr(expr, xorExpr());
    }
    return expr;
  }

  /**
   * Parses a sequence of bit-wise xor expressions, see §5.8.
   * <pre>
   * xorExpr: andExpr ('^' andExpr)*
   * </pre>
   */
  PyExpr xorExpr() {
    PyExpr expr = andExpr();
    while (match("^")) {
      expr = new PyBitXorExpr(expr, andExpr());
    }
    return expr;
  }

  /**
   * Parses a sequence of bit-wise and expressions, see §5.8.
   * <pre>
   * andExpr: shiftExpr ('&' shiftExpr)*
   * </pre>
   */
  PyExpr andExpr() {
    PyExpr expr = shiftExpr();
    while (match("&")) {
      expr = new PyBitAndExpr(expr, shiftExpr());
    }
    return expr;
  }

  /**
   * Parses a sequence of bit-shift expressions, see §5.7.
   * <pre>
   * shiftExpr: arithExpr (('<<'|'>>') arithExpr)*
   * </pre>
   */
  PyExpr shiftExpr() {
    PyExpr expr = arithExpr();
    String t;
    while ((t = tokenType) != null && (t.equals("<<") || t.equals(">>"))) {
      advance();
      if (t.equals("<<")) {
        expr = new PyLshiftExpr(expr, arithExpr());
      } else {
        expr = new PyRshiftExpr(expr, arithExpr());
      }
    }
    return expr;
  }

  /**
   * Parses a sequence of additive expressions, see §5.6.
   * <pre>
   * arithExpr: term (('+'|'-') term)*
   * </pre>
   */
  PyExpr arithExpr() {
    PyExpr expr = term();
    String t;
    while ((t = tokenType) != null && (t.equals("+") || t.equals("-"))) {
      advance();
      if (t.equals("+")) {
        expr = new PyAddExpr(expr, term());
      } else {
        expr = new PySubExpr(expr, term());
      }
    }
    return expr;
  }

  /**
   * Parses a sequence of multiplicative expressions, see §5.6.
   * <pre>
   * term: factor (('*'|'/'|'%') factor)*
   * </pre>
   */
  PyExpr term() {
    PyExpr expr = factor();
    String t;
    while ((t = tokenType) != null && (t.equals("*") || t.equals("/") || t.equals("%"))) {
      advance();
      if (t.equals("*")) {
        expr = new PyMulExpr(expr, factor());
      } else if (t.equals("/")) {
        expr = new PyDivExpr(expr, factor());
      } else {
        expr = new PyModExpr(expr, factor());
      }
    }
    return expr;
  }

  /**
   * Parses a sequence of unary arithmetic expressions, see §5.5.
   * <pre>
   * factor: ('+'|'-'|'~') factor | power
   * </pre>
   */
  PyExpr factor() {
    if (match("+")) {
      return new PyPositive(factor());
    }
    if (match("-")) {
      return new PyNegate(factor());
    }
    if (match("~")) {
      return new PyBitInvert(factor());
    }
    return power();
  }

  /**
   * Parses a power expression, see §5.4.
   * <pre>
   * power: primary ['**' factor]
   * </pre>
   */
  PyExpr power() {
    PyExpr expr = primary();
    if (match("**")) {
      expr = new PyPowExpr(expr, factor());
    }
    return expr;
  }

  /**
   * Parses a primary expression, see §5.3.
   * <pre>
   * primary: atom | attributeref | subscription | slicing | call
   * attributeref: atom '.' NAME
   * subscription: primary '[' testlist ']'
   * slicing: primary '[' shortSlice | sliceList ']'
   * shortSlice: [lowerBound] ':' [upperBound]
   * sliceList: sliceItem (',' sliceItem)* [',']
   * sliceItem: test | properSlice | "..."
   * properSlice: shortSlice | longSlice
   * longSlice: shortSlice ':' [test]
   * </pre>
   */
  PyExpr primary() {
    PyExpr primary = atom();
    while (true) {
      if (match(".")) {
        primary = new PyAttrRef(primary, name("name expected after ."));
        continue;
      }
      if (match("[")) {
        boolean[] tuple = new boolean[1];
        List<PySubscript> list = subscriptlist(tuple);
        boolean subscript = true;
        for (PySubscript sub : list) {
          if (sub.getSingle() == null) {
            subscript = false;
            break;
          }
        }
        if (subscript) {
          PyExprList exprlist = new PyExprList();
          exprlist.setTuple(tuple[0]);
          for (PySubscript sub : list) {
            exprlist.add(sub.getSingle());
          }
          primary = new PySubscription(primary, exprlist);
        } else {
          primary = new PySlicing(primary, list);
        }
        expect("]");
        continue;
      }
      if (match("(")) {
        primary = new PyCall(primary, arglist());
        expect(")");
        continue;
      }
      break;
    }
    return primary;
  }

  /**
   * Parses an atom, the most basic form of expression, see §5.2.
   * <pre>
   * atom: NAME | NUMBER | STRING+ | '(' [testlist] ')' | '[' [testlist] ']' | '{' [dictmaker] '}' | '`' testlist '`'
   * </pre>
   */
  PyExpr atom() {
    if (is("NAME")) {
      return new PyIdentifier(PyObject.intern((String) advance()));
    }
    if (is("NUMBER")) {
      Object value = advance();
      return new PyLiteral(value instanceof BigInteger ? PyObject.make((BigInteger) value) : PyObject.make((Integer) value));
    }
    if (is("STRING")) {
      StringBuilder b = new StringBuilder(256);
      while (is("STRING")) {
        b.append(advance());
      }
      return new PyLiteral(PyObject.make(b.toString()));
    }
    if (match("(")) {
      PyExpr atom = new PyTupleConstr(optTestlist());
      expect(")");
      return atom;
    }
    if (match("[")) {
      PyExpr atom = new PyListConstr(optTestlist());
      expect("]");
      return atom;
    }
    if (match("{")) {
      PyExpr atom = dictMaker();
      expect("}");
      return atom;
    }
    if (match("`")) {
      PyExpr atom = new PyStringConversion(testlist());
      expect("`");
      return atom;
    }
    throw notify("invalid syntax, found " + tokenType);
  }

  /**
   * Parses an optional dictionary display, see § 5.2.5.
   * <pre>
   * dictmaker: test ':' test (',' test ':' test)* [',']
   * </pre>
   */
  PyExpr dictMaker() {
    PyExprList list = new PyExprList();
    while (!is("}")) {
      list.add(test());
      expect(":");
      list.add(test());
      if (!is("}")) {
        expect(",");
        if (is("}")) {
          break;
        }
      }
    }
    return new PyDictConstr(list);
  }

  /**
   * Parses the argument list of a function or method call, see 5.3.4.
   * <pre>
   * arglist: (args [',' kwargs] | kwargs) [',']
   * args: expr (',' expr)*
   * kwargs: kwitem (',' kwitem)*
   * kwitem: NAME '=' expr
   * </pre>
   */
  PyExprList[] arglist() {
    PyExprList args = new PyExprList();
    PyExprList kwargs = new PyExprList(); 
    boolean seenkw = false;
    while (!is(")")) {
      PyExpr expr = expr();
      if (match("=")) {
        if (!(expr instanceof PyIdentifier)) {
          throw notify("name before = in arglist expected");
        }
        kwargs.add(new PyLiteral(((PyIdentifier) expr).getName()));
        kwargs.add(expr());
        seenkw = true;
      } else {
        if (seenkw) {
          throw notify("positional argument behind keyword argument");
        }
        args.add(expr);
      }
      if (!is(")")) {
        expect(",");
      }
    }
    return new PyExprList[] { args, kwargs };
  }

  /**
   * Parses a subscription list or an extended slicing.
   * Unfortunately, we cannot tell in advance.
   * <pre>
   * subscriptlist: subscript (',' subscript)* [',']
   * </pre>
   */
  List<PySubscript> subscriptlist(boolean[] tuple) {
    List<PySubscript> list = new ArrayList<PySubscript>();
    while (!is("]")) {
      list.add(subscript());
      if (!is("]")) {
        expect(",");
        tuple[0] = true;
      }
    }
    if (list.isEmpty()) {
      throw notify("invalid syntax");
    }
    return list;
  }

  /**
   * Parses a subscription or slicing.
   * Unfortunately, we cannot tell in advance,
   * <pre>
   * subscript: '...' | test | [test] ':' [test] [stride]
   * stride: ':' [test]
   * </pre>
   */
  PySubscript subscript() {
    if (match("...")) {
      return new PySubscript();
    }
    PyExpr left, right, stride;
    if (match(":")) {
      left = null;
      if (!is(":") && !is("]") && !is(",")) {
        right = test();
      } else {
        right = null;
      }
      if (match(":")) {
        if (!is("]") && !is(",")) {
          stride = test();
        } else {
          stride = null;
        }
      } else {
        stride = null;
      }
    } else {
      left = test();
      if (match(":")) {
        if (!is(":") && !is(",") && !is("]")) {
          right = test();
        } else {
          right = null;
        }
        if (match(":")) {
          stride = test();
        } else {
          stride = null;
        }
      } else {
        return new PySubscript(left);
      }
    }
    return new PySubscript(left, right, stride);
  }

  // ----------------------------------------------------------------------------------------------

  /**
   * Returns a name, raising an error if the current token is not a name.
   */
  private PyString name(String message) {
    if (!is("NAME")) {
      throw notify(message);
    }
    return PyObject.intern((String) advance());
  }

  /**
   * Returns a possibly empty testlist.
   */
  PyExprList optTestlist() {
    if (!isTestStart()) {
      return new PyExprList();
    }
    return testlist();
  }

  /**
   * Returns true if the current token is one of "first set" of the test production rule.
   */
  private boolean isTestStart() {
    return is("lambda") || is("not") || is("+") || is("-") || is("~") || isTargetStart();
  }

  /**
   * Returns true if the current token is one of "first set" of the target production rule.
   */
  private boolean isTargetStart() {
    return is("(") || is("[") || is("{") || is("`") || is("NAME") || is("NUMBER") || is("STRING");
  }

  /**
   * Returns true if the current token is of the given type and false otherwise.
   */
  private boolean is(String type) {
    return type.equals(tokenType);
  }

  /**
   * Returns true if current token is of the given type and advances to the next token.
   * Returns false otherwise with the current token unchanged.
   */
  private boolean match(String type) {
    if (is(type)) {
      advance();
      return true;
    }
    return false;
  }

  /**
   * Raises an error if the current token is not of the given type.
   */
  private void expect(String type) {
    if (!match(type)) {
      throw notify(type + " expected");
    }
  }
}
