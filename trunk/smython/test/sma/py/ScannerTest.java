/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import junit.framework.TestCase;

import java.math.BigInteger;

public class ScannerTest extends TestCase {

  public void testEmpty() {
    assertEquals("", scan("", false));
    assertEquals("", scan("\n", false));
    assertEquals("", scan(" \n ", false));
    assertEquals("", scan("\n\n\n", false));
    assertEquals("", scan("# comment", false));
    assertEquals("", scan("# comment\n", false));
    assertEquals("", scan(" # comment\n", false));
    assertEquals("", scan(" # comment\n ", false));
    assertEquals("", scan(" # comment\n \n", false));
    assertEquals("", scan(" # comment\n \n # comment", false));
  }

  public void testNumber() {
    assertEquals("NUMBER", scan("42"));
    assertEquals("NUMBER", scan("\n42 "));
    assertEquals("NUMBER NUMBER NUMBER", scan("\n1 2 3 "));
    assertEquals("NUMBER", scan("8905488580394859083405834958345834"));
    assertEquals("NUMBER", scan("42L"));
    assertEquals("NUMBER", scan("8905488580394859083405834958345834L"));
  }

  public void testDouble() {
    assertEquals("NUMBER +", scan("1.+"));
    assertEquals("NUMBER +", scan("1.2+"));
    assertEquals("NUMBER +", scan("1e6+"));
    assertEquals("NUMBER +", scan("1.2e6+"));
    assertEquals("NUMBER +", scan("1.2e-6+"));
    assertEquals("NUMBER +", scan("1.2e+6+"));
    assertEquals("NUMBER +", scan(".1+"));
    assertEquals("NUMBER +", scan(".1e6+"));
  }

  public void testString1() {
    assertEquals("STRING", scan("''"));
    assertEquals("STRING", scan("'' "));
    assertEquals("STRING", scan("'abc'"));
    assertEquals("STRING", scan("'abc' "));
    assertEquals("STRING", scan("'\"'"));
    assertEquals("STRING", scan("r'\"'"));
    assertEquals("STRING", scan("r'\\''"));
  }

  public void testString2() {
    assertEquals("STRING", scan("\"\""));
    assertEquals("STRING", scan("\"\" "));
    assertEquals("STRING", scan("\"abc\""));
    assertEquals("STRING", scan("\"abc\" "));
    assertEquals("STRING", scan("\"'\""));
    assertEquals("STRING", scan("r\"'\""));
    assertEquals("STRING", scan("r\"\\\"\""));
  }

  public void testMultilineString1() {
    assertEquals("STRING", scan("''''''"));
    assertEquals("STRING", scan("''' ''' "));
    assertEquals("STRING", scan("'''\n\n'''"));
    assertEquals("STRING", scan("'''' '''"));
    assertEquals("STRING", scan("''''' '''"));
    assertEquals("STRING", scan("r'''\n'''"));
  }

  public void testMultilineString2() {
    assertEquals("STRING", scan("\"\"\"\"\"\""));
    assertEquals("STRING", scan("\"\"\" \"\"\" "));
    assertEquals("STRING", scan("\"\"\"\n\n\"\"\""));
    assertEquals("STRING", scan("\"\"\"\" \"\"\""));
    assertEquals("STRING", scan("\"\"\"\"\" \"\"\""));
    assertEquals("STRING", scan("r\"\"\"\n\"\"\""));
  }

  public void testStringEscapes() {
    assertEquals("STRING", scan("\"\\n\""));
    assertEquals("STRING", scan("\"\\\"\""));
    assertEquals("STRING", scan("\"\\\n\""));
  }

  public void testName() {
    assertEquals("NAME", scan("foo"));
    assertEquals("NAME", scan("bar_baz"));
    assertEquals("NAME", scan("__init__ "));
    assertEquals("NAME NAME", scan("a1 b2 "));
    assertEquals("NAME NAME", scan("a1 b2 # comment"));
    assertEquals("NAME NAME NAME", scan("r u a"));
  }

  public void testKeyword() {
    assertEquals("if elif else", scan("if elif else"));
    assertEquals("if NEWLINE elif NEWLINE else", scan("if\nelif\nelse\n"));
  }

  public void testNewline() {
    assertEquals("while NEWLINE not", scan("while\nnot"));
    assertEquals("while NEWLINE not", scan("while\nnot\n"));
    assertEquals("while ( not )", scan("while(\nnot\n)"));
    assertEquals("while ( not )", scan("while(\n  not\n)"));
    assertEquals("while ( not )", scan("while(\n  not # comment \n\n)"));
  }

  public void testIndentAndDedent() {
    assertEquals("def NEWLINE INDENT class NEWLINE DEDENT", scan("def\n  class", false));
    assertEquals("def NEWLINE INDENT class NEWLINE DEDENT", scan("def\n  class\n", false));
    assertEquals("def NEWLINE INDENT class NEWLINE DEDENT", scan("def\n  class\n# eof", false));
    assertEquals("def NEWLINE INDENT class NEWLINE INDENT for NEWLINE DEDENT DEDENT", scan("def\n  class\n   for", false));
  }

  public void testLineContinuation() {
    assertEquals("def try", scan("def \\\n   try"));
    assertEquals("def try", scan("def \\ # comment \ntry"));
    assertEquals("def NEWLINE try", scan("def # comment\\\ntry"));
    assertEquals("( def try )", scan("(def \n try)"));
    assertEquals("( def try )", scan("(def \\\n try)"));
  }

  public void testComment() {
    assertEquals("NUMBER", scan("1 \n# comment"));
    assertEquals("NUMBER", scan("# comment \n0"));
  }

  public void testEscapes() {
    assertEquals("\u0000", new Scanner("'\\0'").token);
    assertEquals("\nx", new Scanner("'\\12x'").token);
    assertEquals("\n3", new Scanner("'\\0123'").token);
    assertEquals("@1", new Scanner("'\\x401'").token);
    assertEquals("\b\n\r\t", new Scanner("'\\b\\n\\r\\t'").token);
    assertEquals("ab", new Scanner("'a\\\nb'").token);
  }

  public void testNumberValues() {
    assertEquals(0, new Scanner("0").token);
    assertEquals(1, new Scanner("1").token);
    assertEquals(BigInteger.valueOf(2), new Scanner("2L").token);
    assertEquals(BigInteger.valueOf(204), new Scanner("0xCcL").token);
    assertEquals(3.14, new Scanner("31.4e-1").token);
    assertEquals(3.14, new Scanner(".314e+1").token);
    assertEquals(2147483647, new Scanner("2147483647").token);
    assertEquals(BigInteger.valueOf(2147483648L), new Scanner("2147483648").token);
  }

  private String scan(String source) {
    return scan(source, true);
  }

  private String scan(String source, boolean autoNewline) {
    Scanner scanner = new Scanner(source);
    StringBuilder b = new StringBuilder();
    while (scanner.tokenType != null) {
      b.append(' ').append(scanner.tokenType); scanner.advance();
    }
    if (autoNewline) {
      assertTrue(b.toString().endsWith(" NEWLINE"));
      b.setLength(b.length() - 8);
    }
    if (b.length() > 0) {
      b.deleteCharAt(0);
    }
    return b.toString();
  }

}
