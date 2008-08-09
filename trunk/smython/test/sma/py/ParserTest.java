/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import junit.framework.TestCase;
import sma.py.ast.PyExprList;
import sma.py.ast.PyNode;

public class ParserTest extends TestCase {

  // 7.6 class definitions
  public void testClassDef() {
    assertNotNull(parseClassDef("class C: pass"));
    assertNotNull(parseClassDef("class C:\n pass"));
    assertNotNull(parseClassDef("class C(Object):\n pass"));
    assertNotNull(parseClassDef("class C(Object,):\n pass"));
    assertNotNull(parseClassDef("class C(Object,Mixin):pass;pass"));
    assertNotNull(parseClassDef("class C(Object,Mixin,):pass;pass"));
  }

  private PyNode parseClassDef(String source) {
    Parser p = new Parser(source);
    assertEquals("class", p.tokenType);
    p.advance();
    PyNode n = p.classDef();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  // 7.5 function definitions
  public void testFuncDef() {
    assertNotNull(parseFuncDef("def f():pass"));
    assertNotNull(parseFuncDef("def f():\n pass"));
    assertNotNull(parseFuncDef("def f(a):pass"));
    assertNotNull(parseFuncDef("def f(a, ):pass"));
    assertNotNull(parseFuncDef("def f(a, b):pass"));
    assertNotNull(parseFuncDef("def f(a, b, ):pass"));
    assertNotNull(parseFuncDef("def f(a, b=0):pass"));
    assertNotNull(parseFuncDef("def f(a = 0, b = 0):pass"));
    assertNotNull(parseFuncDef("def f(*rest):pass"));
    assertNotNull(parseFuncDef("def f(*rest, **kwargs):pass"));
    assertNotNull(parseFuncDef("def f(a, *rest, **kwargs):pass"));
    assertNotNull(parseFuncDef("def f(a, b, *rest, **kwargs):pass"));
    assertNotNull(parseFuncDef("def f(a, b = 0, *rest, **kwargs):pass"));
    assertNotNull(parseFuncDef("def f(a = 0, b = 0, *rest, **kwargs):pass"));

    assertNotNull(parseFuncDef("def f((a)):pass"));
    assertNotNull(parseFuncDef("def f((a,)):pass"));
    assertNotNull(parseFuncDef("def f((a, b)):pass"));
    assertNotNull(parseFuncDef("def f((a, b,)):pass"));
    assertNotNull(parseFuncDef("def f(((a))):pass"));
  }

  private PyNode parseFuncDef(String source) {
    Parser p = new Parser(source);
    assertEquals("def", p.tokenType);
    p.advance();
    PyNode n = p.funcDef();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  public void testLambDef() {
    assertNotNull(parseLambDef("lambda:42"));
    assertNotNull(parseLambDef("lambda a:42"));
    assertNotNull(parseLambDef("lambda a,:42"));
    assertNotNull(parseLambDef("lambda a, b:42"));
    assertNotNull(parseLambDef("lambda a, b=0:42"));
    assertNotNull(parseLambDef("lambda a = 0, b = 0:42"));
    assertNotNull(parseLambDef("lambda *rest:42"));
    assertNotNull(parseLambDef("lambda *rest, **kwargs:42"));
    assertNotNull(parseLambDef("lambda a, *rest, **kwargs:42"));
    assertNotNull(parseLambDef("lambda a, b, *rest, **kwargs:42"));
    assertNotNull(parseLambDef("lambda a, b = 0, *rest, **kwargs:42"));
    assertNotNull(parseLambDef("lambda a = 0, b = 0, *rest, **kwargs:42"));
    assertNotNull(parseLambDef("lambda (a,(b)):42"));
  }

  private PyNode parseLambDef(String source) {
    Parser p = new Parser(source);
    assertEquals("lambda", p.tokenType);
    p.advance();
    PyNode n = p.lambDef();
    assertEquals("NEWLINE", p.tokenType);
    p.advance();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  // -----------------------
  // Compound statements (7)
  // -----------------------

  // 7.1 the if statement
  public void testIfStmt() {
    assertNotNull(parseStmt("if 1:pass"));
    assertNotNull(parseStmt("if 1:\n pass"));
    assertNotNull(parseStmt("if 1:\n pass\nelse:pass"));
    assertNotNull(parseStmt("if 1:\n pass\nelif 2:\n pass\n"));
    assertNotNull(parseStmt("if 1:\n pass\nelif 2:\n pass\nelse:pass"));
    assertNotNull(parseStmt("if 1:\n pass\nelif 2:\n pass\nelif 3:\n pass\nelse:pass"));
  }

  // 7.2 the while statement
  public void testWhileStmt() {
    assertNotNull(parseStmt("while 1:pass"));
    assertNotNull(parseStmt("while 1:\n pass"));
    assertNotNull(parseStmt("while 1:pass\nelse:pass"));
    assertNotNull(parseStmt("while 1:\n pass\nelse:\n pass"));
  }

  // 7.3 the for statement
  public void testForStmt() {
    assertNotNull(parseStmt("for t in list: pass"));
    assertNotNull(parseStmt("for t1, t2 in list: pass"));
    assertNotNull(parseStmt("for t in list1, list2: pass"));
    assertNotNull(parseStmt("for t1, t2 in list1, list2: pass\nelse: pass"));
  }

  // 7.4 the try-except statement
  public void testTryExceptStmt() {
    assertNotNull(parseStmt("try: pass\nexcept: pass"));
    assertNotNull(parseStmt("try: pass\nexcept 0: pass"));
    assertNotNull(parseStmt("try: pass\nexcept 0, t: pass"));
    assertNotNull(parseStmt("try: pass\nexcept 0, t: pass\nexcept 1, t: pass"));
    assertNotNull(parseStmt("try:\n pass\nexcept 0, t:\n pass\nexcept 1, t:\n pass\nelse:\n pass"));
  }

  // 7.4 the try-finally statement
  public void testTryFinallyStmt() {
    assertNotNull(parseStmt("try: pass\nfinally: pass"));
    assertNotNull(parseStmt("try:\n pass\nfinally:\n pass\n pass"));
  }

  public void testClassDefAndFuncDefStmt() {
    assertNotNull(parseStmt("class C: pass"));
    assertNotNull(parseStmt("def f(): pass"));
  }

  // ---------------------
  // Simple statements (6)
  // ---------------------

  public void testSimpleStmtSequence() {
    assertNotNull(parseStmt("pass;pass"));
    assertNotNull(parseStmt("pass;pass;"));
    assertNotNull(parseStmt("pass;pass;pass"));
    assertNotNull(parseStmt("pass;pass;pass;"));
  }

  // 6.5 The print statement
  public void testPrintStmt() {
    assertNotNull(parseStmt("print"));
    assertNotNull(parseStmt("print a"));
    assertNotNull(parseStmt("print a, "));
    assertNotNull(parseStmt("print a, b"));
    assertNotNull(parseStmt("print a, b, "));
  }

  // 6.4 The del statement
  public void testDelStmt() {
    assertNotNull(parseStmt("del a"));
    assertNotNull(parseStmt("del a,"));
    assertNotNull(parseStmt("del a, b, c"));
    assertNotNull(parseStmt("del a, (b), [c], "));
    assertNotNull(parseStmt("del (a)"));
    assertNotNull(parseStmt("del (a,)"));
    assertNotNull(parseStmt("del (a, b,), c"));
    assertNotNull(parseStmt("del [a]"));
    assertNotNull(parseStmt("del [a,]"));
    assertNotNull(parseStmt("del [a, b,], c"));
    assertNotNull(parseStmt("del a.x, b[0], c[::], d[a, ...]"));
  }

  // 6.3 The pass statement
  public void testPassStmt() {
    assertNotNull(parseStmt("pass"));
  }

  // 6.8 The break statement
  public void testBreakStmt() {
    assertNotNull(parseStmt("break"));
  }

  // 6.9 The continue statement
  public void testContinueStmt() {
    assertNotNull(parseStmt("continue"));
  }

  // 6.6 The return statement
  public void testReturnStmt() {
    assertNotNull(parseStmt("return"));
    assertNotNull(parseStmt("return 'foo'"));
    assertNotNull(parseStmt("return 'foo',"));
    assertNotNull(parseStmt("return 'foo', 'bar'"));
    assertNotNull(parseStmt("return 'foo', 'bar',"));
  }

  // 6.7 The raise statement
  public void testRaiseStmt() {
    assertNotNull(parseStmt("raise Exception"));
    assertNotNull(parseStmt("raise Class, instance"));
    assertNotNull(parseStmt("raise Class, instance, traceback"));
  }

  // 6.10 The import statement
  public void testImportStmt() {
    assertNotNull(parseStmt("import X"));
    assertNotNull(parseStmt("import X, Y, Z"));
  }

  // 6.10 The from-import statement
  public void testFromImportStmt() {
    assertNotNull(parseStmt("from M import X"));
    assertNotNull(parseStmt("from M import X, Y, Z"));
    assertNotNull(parseStmt("from M import *"));
  }

  // 6.11 The global statement
  public void testGlobalStmt() {
    assertNotNull(parseStmt("global X"));
    assertNotNull(parseStmt("global X, Y"));
    assertNotNull(parseStmt("global X, Y, Z"));
  }

  // 6.12 The exec statement
  public void testExecStmt() {
    assertNotNull(parseStmt("exec 'foo'"));
    assertNotNull(parseStmt("exec 'foo' in globals"));
    assertNotNull(parseStmt("exec 'foo' in {}, {}"));
  }
  
  public void testAssertStmt() {
    if (Parser.VERSION_1_4) return;
    assertNotNull(parseStmt("assert 3+4"));
    assertNotNull(parseStmt("assert 3+4, 'should be 7'"));
  }

  // 6.1 Expression statements
  public void testExprStmt() {
    assertNotNull(parseStmt("name"));
    assertNotNull(parseStmt("name,"));
    assertNotNull(parseStmt("'docstring'"));
    assertNotNull(parseStmt("call(), a[0], b.c"));
  }

  // 6.2 Assignment statements
  public void testAssignmentStatements() {
    assertNotNull(parseStmt("a = 1"));
    assertNotNull(parseStmt("a, = 1"));
    assertNotNull(parseStmt("a, b = 1"));
    assertNotNull(parseStmt("a, b, = 1"));
    assertNotNull(parseStmt("a = 1, 2"));
    assertNotNull(parseStmt("a = 1, 2, "));
    assertNotNull(parseStmt("a, b = b, a"));
    assertNotNull(parseStmt("(a, b) = x"));
    assertNotNull(parseStmt("[a, b] = x"));
    assertNotNull(parseStmt("(a, [b]) = x"));
    assertNotNull(parseStmt("a.b.c = x"));
    assertNotNull(parseStmt("a, (b), [c] = (x, y), (z) "));
    assertNotNull(parseStmt("a, (b), [c], = (x, y,), (z,), "));
    assertNotNull(parseStmt("a[0] = x"));
    assertNotNull(parseStmt("a[0, 2] = x"));
    assertNotNull(parseStmt("a[:] = x"));
    assertNotNull(parseStmt("a[1:] = x"));
    assertNotNull(parseStmt("a[:2] = x"));
    assertNotNull(parseStmt("a[1:2] = x"));
    assertNotNull(parseStmt("a[1:2:3] = x"));
    assertNotNull(parseStmt("a[...] = x"));
    assertNotNull(parseStmt("(a) = x"));
    assertNotNull(parseStmt("(a,) = x"));
    assertNotNull(parseStmt("(a,b) = x"));
    assertNotNull(parseStmt("(a,b,) = x"));
    assertNotNull(parseStmt("(a.x, b[0], c[...]) = x"));
    assertNotNull(parseStmt("((a)) = x"));
    assertNotNull(parseStmt("([a]) = x"));
    assertNotNull(parseStmt("[a] = x"));
    assertNotNull(parseStmt("[a,] = x"));
    assertNotNull(parseStmt("[a,b] = x"));
    assertNotNull(parseStmt("[a,b,] = x"));
    assertNotNull(parseStmt("[a.x, b[0], c[...]] = x"));
    assertNotNull(parseStmt("[(a)] = x"));
    assertNotNull(parseStmt("[[a]] = x"));
    assertNotNull(parseStmt("a = b = 0"));
    assertNotNull(parseStmt("a, b = [c] = d = 'a', 'b'"));
  }

  private PyNode parseStmt(String source) {
    Parser p = new Parser(source);
    PyNode n = p.stmt();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  // ---------------
  // Expressions (5)
  // ---------------

  // Atoms (5.2)
  // -----------

  // 5.2.1 Identifiers (Names)
  public void testIdentifiers() {
    assertNotNull(parseExpr("name"));
    assertNotNull(parseExpr("_Name"));
    assertNotNull(parseExpr("__N8M3__"));
    assertNotNull(parseExpr("foo_bar_1"));
  }

  // 5.2.2 Literals
  public void testLiterals() {
    assertNotNull(parseExpr("314159265"));
    assertNotNull(parseExpr("314159265L"));
    assertNotNull(parseExpr("1e-6"));
    assertNotNull(parseExpr("1j"));
    assertNotNull(parseExpr("'str'"));
    assertNotNull(parseExpr("\"str\""));
    assertNotNull(parseExpr("\"str1\" \"str2\""));
  }

  // 5.2.3  Parenthesized forms
  public void testParenthesizedForm() {
    assertNotNull(parseExpr("()"));
    assertNotNull(parseExpr("(a)"));
    assertNotNull(parseExpr("(a,)"));
    assertNotNull(parseExpr("(a, \n b)"));
    assertNotNull(parseExpr("(a, \n b,\n)"));
  }

  // 5.2.4 List displays
  public void testListDisplay() {
    assertNotNull(parseExpr("[]"));
    assertNotNull(parseExpr("[a]"));
    assertNotNull(parseExpr("[a,]"));
    assertNotNull(parseExpr("[a, \n b]"));
    assertNotNull(parseExpr("[a, \n b,\n]"));
  }

  // 5.2.5 Dictionary displays
  public void testDoctionaryDisplay() {
    assertNotNull(parseExpr("{}"));
    assertNotNull(parseExpr("{a:b}"));
    assertNotNull(parseExpr("{a:b, }"));
    assertNotNull(parseExpr("{a:b, \n c:d}"));
    assertNotNull(parseExpr("{a:b, \n c:d,\n}"));
  }

  // 5.2.6 String conversions
  public void testStringConversion() {
    assertNotNull(parseExpr("`1`"));
    //assertNotNull(parseExpr("`1,`"));
    assertNotNull(parseExpr("`1, 2`"));
    //assertNotNull(parseExpr("`1, 2,`"));
  }

  // Primaries (5.3)
  // ---------------

  // 5.3.1 Attribute references
  public void testAttributeReference() {
    assertNotNull(parseExpr("self.a"));
    assertNotNull(parseExpr("self.a.b"));
    assertNotNull(parseExpr("(a,b).len"));
  }

  // 5.3.2 Subscriptions
  public void testSubscriptions() {
    assertNotNull(parseExpr("foo[1]"));
    assertNotNull(parseExpr("foo[1, ]"));
    assertNotNull(parseExpr("foo[1, 2]"));
    assertNotNull(parseExpr("foo[1, 2, ]"));
  }

  // 5.3.3 Slicings
  public void testSlicings() {
    assertNotNull(parseExpr("bar[:]"));
    assertNotNull(parseExpr("bar[lower:]"));
    assertNotNull(parseExpr("bar[:upper]"));
    assertNotNull(parseExpr("bar[lower:upper]"));
    assertNotNull(parseExpr("bar[lower:upper:step]"));
    assertNotNull(parseExpr("bar[...]"));
    assertNotNull(parseExpr("bar[1, :, ..., ]"));
  }

  // 5.3.4 Calls
  public void testCalls() {
    assertNotNull(parseExpr("f()"));
    assertNotNull(parseExpr("f(0)"));
    assertNotNull(parseExpr("f(0,)"));
    assertNotNull(parseExpr("f(a=1)"));
    assertNotNull(parseExpr("f(0,a=1)"));
    assertNotNull(parseExpr("f(0,\n  1,\n a=1,\n b=2)"));
  }

  // Power operation (5.4)
  public void testPowerOperator() {
    assertNotNull(parseExpr("2 ** 8"));
    assertNotNull(parseExpr("2 ** 8 ** 2"));
  }

  // Unary arithmetic operations (5.5)
  public void testUnaryArithmeticOperations() {
    assertNotNull(parseExpr("+1"));
    assertNotNull(parseExpr("++1"));
    assertNotNull(parseExpr("-1"));
    assertNotNull(parseExpr("--1"));
    assertNotNull(parseExpr("+-1"));
    assertNotNull(parseExpr("-+1"));
    assertNotNull(parseExpr("~1"));
    assertNotNull(parseExpr("~~1"));
  }

  // Binary arithmetic operations (5.6)
  public void testBinaryArithmeticOperations() {
    assertNotNull(parseExpr("1"));
    assertNotNull(parseExpr("1 + 2"));
    assertNotNull(parseExpr("1 + 2 - 3"));
    assertNotNull(parseExpr("1 - 2 + 3"));

    assertNotNull(parseExpr("2 * 3"));
    assertNotNull(parseExpr("4 / 2"));
    assertNotNull(parseExpr("2 * 3 / 4"));
    assertNotNull(parseExpr("2 / 3 * 4"));
    assertNotNull(parseExpr("2 * 3 / 4 % 5"));
  }

  // Shifting operations (5.7)
  public void testShiftingOperations() {
    assertNotNull(parseExpr("1 << 2"));
    assertNotNull(parseExpr("1 << 2 << 3"));
    assertNotNull(parseExpr("8 >> 2"));
    assertNotNull(parseExpr("8 >> 2 >> 1"));
  }

  // Binary bitwise operations (5.8)
  public void testBitOperations() {
    assertNotNull(parseExpr("3 & 4"));
    assertNotNull(parseExpr("3 ^ 4"));
    assertNotNull(parseExpr("3 | 4"));
  }

  private PyNode parseExpr(String source) {
    Parser p = new Parser(source);
    PyNode n = p.expr();
    assertEquals("NEWLINE", p.tokenType);
    p.advance();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  // Comparisons (5.9)
  public void testComparisons() {
    assertNotNull(parseTest("1 < 2"));
    assertNotNull(parseTest("1 > 2"));
    assertNotNull(parseTest("1 <= 2"));
    assertNotNull(parseTest("1 >= 2"));
    assertNotNull(parseTest("1 == 2"));
    assertNotNull(parseTest("1 != 2"));
    assertNotNull(parseTest("1 <> 2"));
    assertNotNull(parseTest("1 is Integer"));
    assertNotNull(parseTest("1 is not Integer"));
    assertNotNull(parseTest("1 in [1, 2, 3]"));
    assertNotNull(parseTest("1 not in [1, 2, 3]"));

    assertNotNull(parseTest("1 < 2 < 3 < 4"));
    assertNotNull(parseTest("1 > 2 <= 3 >= 4 != 5 == 6"));
  }

  // Boolean operations (5.10)
  public void testBooleanOperations() {
    assertNotNull(parseTest("True and False"));
    assertNotNull(parseTest("True and False or True"));
    assertNotNull(parseTest("True or False and True"));
    assertNotNull(parseTest("not True and not False"));
    assertNotNull(parseTest("lambda: True"));

    assertNotNull(parseTest("not 1 < 2"));
    assertNotNull(parseTest("(not 1)"));
  }

  private PyNode parseTest(String source) {
    Parser p = new Parser(source);
    PyNode n = p.test();
    assertEquals("NEWLINE", p.tokenType);
    p.advance();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  // Expression lists (5.11)
  public void testExpressionLists() {
    assertEquals(1, parseTestlist("3+4").size());
    assertEquals(1, parseTestlist("3+4, ").size());
    assertEquals(2, parseTestlist("3+4, 7").size());
    assertEquals(2, parseTestlist("3+4, 7,").size());
  }

  private PyExprList parseTestlist(String source) {
    Parser p = new Parser(source);
    PyExprList n = p.testlist();
    assertEquals("NEWLINE", p.tokenType);
    p.advance();
    assertEquals(null, p.tokenType);
    emit(n);
    return n;
  }

  private void emit(PyNode n) {
    //System.out.println(n);
  }

  // more tests

  // documentation used "expr" for "arglist" but it should be "test", see 5.3.4
  public void testArglistExpr() {
    assertNotNull(parseStmt("map(lambda x: x)"));
  }

  // documentation does not mention "*args" and "**keyword" syntax but tests excercise it, see 5.3.4
  public void testUnsplice() {
    assertNotNull(parseStmt("foo(*args)"));
    assertNotNull(parseStmt("foo(a, b, *args)"));
    assertNotNull(parseStmt("foo(a, b=0, *args)"));
    assertNotNull(parseStmt("foo(a=0, b=0, *args)"));
    assertNotNull(parseStmt("foo(**kwargs)"));
    assertNotNull(parseStmt("foo(a, b=0, **kwargs)"));
    assertNotNull(parseStmt("foo(*args, **kwargs)"));
    assertNotNull(parseStmt("foo(a, b=0, *args, **kwargs)"));
  }
}
