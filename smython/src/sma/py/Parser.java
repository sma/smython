/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import sma.py.ast.*;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

/**
 * Takes a source and returns an abstract syntax tree.
 */
public class Parser extends Scanner {
  /**
   * Constructs a new parser for the given source string.
   * 
   * @param source the Python source
   */
  public Parser(String source) {
    super(source);
  }

  /**
   * Parses an sequence of statements.
   * @return a suite statement node containing the statement nodes
   */
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
   * Called from {@code compoundStmt()} with "class" already consumed.
   * <pre>
   * classdef: 'class' NAME ['(' testlist ')'] ':' suite
   * </pre>
   * @return a class definition statement node
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
   * Called from {@code compoundStmt()} with "def" already consumed.
   * <pre>
   * funcdef: 'def' NAME parameters ':' suite
   * parameters: '(' [varargslist] ')'
   * </pre>
   * @return a function definition statement node
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
   * Called from {@code test()} with "lambda" already consumed.
   * <pre>
   * lambdef: 'lambda' [varargslist] ':' test
   * </pre>
   * @return a lambda expression node
   */
  PyLambda lambDef() {
    PyParamList list = parameters();
    expect(":");
    return new PyLambda(list, test());
  }

  /**
   * Parses the parameter list of a function or lambda definition.
   * Called from {@code funcDef()} or {@code lambDef()}.
   * <pre>
   * varargslist: (fpdef ['=' test] ',')* ('*' NAME [',' '**' NAME] | '**' NAME)
   *              | fpdef ['=' test] (',' fpdef ['=' test])* [',']
   * </pre>
   * @return a parameter list node containing tuple objects for parameters, an
   * expression list node with expression nodes for default parameters and
   * strings for the optional rest and keyword rest parameters
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
   * @return a tuple of strings or more tuples representing a parameter definition
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
   * @return a suite statement node containing statement nodes
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
   * @return a statement node
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
   * @return a statement node or <code>null</code> if the next statement is neither
   * an if, while, try, function defition or class definition statement
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
   * @return an if statement node
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
   * @return a while statement node
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
   * @return a for statement node
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
   * Called by {@code delStmt()} and {@code forStmt()}.
   * <pre>
   * targetlist: target (',' target) [',']
   * </pre>
   * @return an expression list node containing target expression nodes
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
   * Called by {@code targetList()} and {@code tryStmt()}.
   * <pre>
   * target: NAME | '(' targetlist ')' | '[' targetlist ']' | attributeref | subscription | slicing
   * </pre>
   * @return a target expression
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
   * @return a try/finally or a try/except statement node
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
   * @return a single statement node or a suite statement node containing statement nodes
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
   * </pre>
   * @return a statement node
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
   * Parses a print statement, see §6.6.
   * <pre>
   * printStmt: 'print' [testtest]
   * </pre>
   * @return a print statement node
   */
  PyStmt printStmt() {
    return new PyPrintStmt(optTestlist());
  }

  /**
   * Parses a del statement, see §6.5.
   * <pre>
   * delStmt: 'del' exprlist
   * </pre>
   * @return a del statement node
   */
  PyStmt delStmt() {
    return new PyDelStmt(targetlist());
  }

  /**
   * Parses a pass statement, see §6.4.
   * <pre>
   * passStmt: 'pass'
   * </pre>
   * @return a pass statement node
   */
  PyStmt passStmt() {
    return new PyPassStmt();
  }

  /**
   * Parses a break statement, see §6.9.
   * <pre>
   * breakStmt: 'break'
   * </pre>
   * @return a break statement node
   */
  PyStmt breakStmt() {
    return new PyBreakStmt();
  }

  /**
   * Parses a continue statement, see §6.10.
   * <pre>
   * continueStmt: 'continue'
   * </pre>
   * @return a continue statement node
   */
  PyStmt continueStmt() {
    return new PyContinueStmt();
  }

  /**
   * Parses a return statement, , see §6.7.
   * <pre>
   * returnStmt: 'return' [testlist]
   * </pre>
   * @return a return statement node
   */
  PyStmt returnStmt() {
    return new PyReturnStmt(optTestlist());
  }

  /**
   * Parses a raise statement, see §6.8.
   * <pre>
   * raiseStmt: 'raise' [test [',' test [',' test]]]
   * </pre>
   * @return a raise statement node
   */
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

  /**
   * Parses an import statement, see §6.11.
   * <pre>
   * importStmt: 'import' dottedName (',' dottedName)*
   *              | ...
   * dottedName: NAME ('.' NAME)*
   * </pre>
   * @return an import statement node
   */
  PyStmt importStmt() {
    List<PyString> modules = new ArrayList<PyString>();
    modules.add(dottedName());
    while (match(",")) {
      modules.add(dottedName());
    }
    return new PyImportStmt(modules);
  }

  /**
   * Parses a from-import statement, see §6.11.
   * <pre>
   * importStmt: ...
   *              | 'from' dottedName 'import' ('*' | NAME (',' NAME)*)
   * dottedName: NAME ('.' NAME)*
   * </pre>
   * @return a from-import statement node
   */
  PyStmt fromImportStmt() {
    PyString module = dottedName();
    expect("import");
    if (match("*")) {
      return new PyFromImportStmt(module, null);
    }
    List<PyString> names = new ArrayList<PyString>();
    names.add(name("name expected"));
    while (match(",")) {
      names.add(name("name expected"));
    }
    return new PyFromImportStmt(module, names);
  }


  /**
   * Parses a sequence of names separated by '.'.
   * @return a string
   */
  PyString dottedName() {
    StringBuilder b = new StringBuilder(64);
    b.append(name("name expected"));
    while (match(".")) {
      b.append(name("name expected"));
    }
    return PyObject.make(b.toString());
  }

  /**
   * Parses a global statement, see §6.12.
   * <pre>
   * globalStmt: 'global' NAME (',' NAME)*
   * </pre>
   * @return a global statement node
   */
  PyStmt globalStmt() {
    List<PyString> names = new ArrayList<PyString>();
    names.add(name("name expected"));
    while (match(",")) {
      names.add(name("name expected"));
    }
    return new PyGlobalStmt(names);
  }

  /**
   * Parses an exec statement, see §6.13.
   * <pre>
   * execStmt: 'exec' test ['in' test [',' test]]
   * </pre>
   * @return an exec statement node
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
   * @return an assert statement node
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
   * @return an assigment statement node or an expression statement node
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
   * @return an expression list node
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
   * @return a general expression node, an or-expression node or a lambda-expression node
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
   * @return a general expression node or an and-expression node
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
   * @return a general expression node or a not-expression node
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
   * @return a general expression node or a comparison expression node
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

  /**
   * Parses a comparison operator.
   * <pre>
   * compOp: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not'
   * </pre>
   * @return a comparison enumeration value or <code>null</code> the next token is comparison operator
   */
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
   * @return a general expression node or an bit-or-expression node
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
   * @return a general expression node or a bit-xor-expression node
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
   * @return a general expression node or a bit-and-expression node
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
   * @return a general expression node or a shift expression node
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
   * @return a general expression node or an add-expression or sub-expression node
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
   * @return a general expression node or a multiplication-, division- or modulo-expression node
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
   * @return a general expression node or a negation expression node
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
   * @return a general expression node or a power-operation-expression node
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
   * @return an expression node
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
   * @return a variable expression node, a literal expression node or some list constructor node
   */
  PyExpr atom() {
    if (is("NAME")) {
      return new PyIdentifier(PyObject.intern((String) advance()));
    }
    if (is("NUMBER")) {
      Object value = advance();
      if (value instanceof Double) {
        return new PyLiteral(PyObject.make((Double) value));
      }
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
   * @return a dictionary construction expression node
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
   * <b>Note:</b> the documentation used "expr" instead of "test" for "args".
   * <pre>
   * arglist: (args [',' kwargs] | kwargs) [',']
   * args: test (',' test)*
   * kwargs: kwitem (',' kwitem)*
   * kwitem: NAME '=' test
   * </pre>
   * @return two expression list nodes for argument expression and keyword expressions;
   * the latter containing name literal nodes and general expression nodes
   */
  PyExprList[] arglist() {
    PyExprList args = new PyExprList();
    PyExprList kwargs = new PyExprList(); 
    boolean seenkw = false;
    while (!is(")")) {
      PyExpr arg = test();
      if (match("=")) {
        if (!(arg instanceof PyIdentifier)) {
          throw notify("name before = in arglist expected");
        }
        kwargs.add(new PyLiteral(((PyIdentifier) arg).getName()));
        kwargs.add(test());
        seenkw = true;
      } else {
        if (seenkw) {
          throw notify("positional argument behind keyword argument");
        }
        args.add(arg);
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
   * @param tuple will contain <code>true</code> if result is a an extended subscript
   * @return a list of subscript objects
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
   * @return a subscript object
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
   * @param message the error message raised if the current token is not a name
   * @return a Python string object with the name
   */
  private PyString name(String message) {
    if (!is("NAME")) {
      throw notify(message);
    }
    return PyObject.intern((String) advance());
  }

  /**
   * Returns a possibly empty testlist.
   * @return a possibly empty expression list node containing expression nodes
   */
  PyExprList optTestlist() {
    if (!isTestStart()) {
      return new PyExprList();
    }
    return testlist();
  }

  /**
   * Returns true if the current token is one of "first set" of the test production rule.
   * @return <code>true</code> if the current token is in <code>FIRST(test)</code>
   */
  private boolean isTestStart() {
    return is("lambda") || is("not") || is("+") || is("-") || is("~") || isTargetStart();
  }

  /**
   * Returns true if the current token is one of "first set" of the target production rule.
   * @return <code>true</code> if the current token is in <code>FIRST(target)</code>
   */
  private boolean isTargetStart() {
    return is("(") || is("[") || is("{") || is("`") || is("NAME") || is("NUMBER") || is("STRING");
  }

  /**
   * Returns true if the current token is of the given type and false otherwise.
   * @param type the token type to test for; see {@link Scanner#nextToken()} for
   * the list of valid token types
   * @return <code>true</code> if the current token's type is the given type
   */
  private boolean is(String type) {
    return type.equals(tokenType);
  }

  /**
   * Returns true if current token is of the given type and advances to the next token.
   * Returns false otherwise with the current token unchanged.
   * @param type the token type to test for; see {@link Scanner#nextToken()} for
   * the list of valid token types
   * @return <code>true</code> and consume the token if the current token's type is the
   * given type; return <code>false</code> otherwise and do not consume anything
   */
  private boolean match(String type) {
    if (is(type)) {
      advance();
      return true;
    }
    return false;
  }

  /**
   * Raises an error if the current token is not of the given type. If the current
   * token is of the given type, consume it.
   * @param type the token type to test for; see {@link Scanner#nextToken()} for
   * the list of valid token types
   */
  private void expect(String type) {
    if (!match(type)) {
      throw notify(type + " expected");
    }
  }
}
