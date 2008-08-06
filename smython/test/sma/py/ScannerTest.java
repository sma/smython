/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import sma.py.Scanner;
import junit.framework.TestCase;

public class ScannerTest extends TestCase {

  public void testEmpty() {
    assertEquals("", scan(""));
    assertEquals("", scan("\n"));
    assertEquals("", scan(" \n"));
    assertEquals("", scan("\n\n\n"));
    assertEquals("", scan("# comment"));
    assertEquals("", scan("# comment\n"));
    assertEquals("", scan(" # comment\n"));
    assertEquals("", scan(" # comment\n "));
    assertEquals("", scan(" # comment\n \n"));
  }

  public void testNumber() {
    assertEquals("NUMBER", scan("42"));
    assertEquals("NUMBER", scan(" 42 "));
    assertEquals("NUMBER NUMBER NUMBER", scan(" 1 2 3 "));
    assertEquals("NUMBER", scan("8905488580394859083405834958345834"));
    assertEquals("NUMBER", scan("42L"));
    assertEquals("NUMBER", scan("8905488580394859083405834958345834L"));
  }

  public void testString1() {
    assertEquals("STRING", scan("''"));
    assertEquals("STRING", scan(" '' "));
    assertEquals("STRING", scan("'abc'"));
    assertEquals("STRING", scan(" 'abc' "));
    assertEquals("STRING", scan(" '\"' "));
  }

  public void testString2() {
    assertEquals("STRING", scan("\"\""));
    assertEquals("STRING", scan(" \"\" "));
    assertEquals("STRING", scan("\"abc\""));
    assertEquals("STRING", scan(" \"abc\" "));
    assertEquals("STRING", scan(" \"'\" "));
  }

  public void testMultilineString1() {
    assertEquals("STRING", scan("''''''"));
    assertEquals("STRING", scan(" ''' ''' "));
    assertEquals("STRING", scan("'''\n\n'''"));
    assertEquals("STRING", scan("'''' '''"));
    assertEquals("STRING", scan("''''' '''"));
  }

  public void testMultilineString2() {
    assertEquals("STRING", scan("\"\"\"\"\"\""));
    assertEquals("STRING", scan(" \"\"\" \"\"\" "));
    assertEquals("STRING", scan("\"\"\"\n\n\"\"\""));
    assertEquals("STRING", scan("\"\"\"\" \"\"\""));
    assertEquals("STRING", scan("\"\"\"\"\" \"\"\""));
  }

  public void testStringEscapes() {
    assertEquals("STRING", scan("\"\\n\""));
    assertEquals("STRING", scan("\"\\\"\""));
    assertEquals("STRING", scan("\"\\\n\""));
  }

  public void testName() {
    assertEquals("NAME", scan("foo"));
    assertEquals("NAME", scan(" bar_baz"));
    assertEquals("NAME", scan("__init__ "));
    assertEquals("NAME NAME", scan(" a1 b2 "));
    assertEquals("NAME NAME", scan(" a1 b2 # comment"));
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

  private String scan(String source) {
    return scan(source, true);
  }

  private String scan(String source, boolean autoNewline) {
    Scanner scanner = new Scanner(source);
    StringBuilder b = new StringBuilder();
    String t;
    while ((t = scanner.getToken()) != null) {
      b.append(' ').append(t);
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
