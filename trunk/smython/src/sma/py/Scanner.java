/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.math.BigInteger;

/**
 * Takes a source and provides a stream of tokens.
 */
public class Scanner {
  private static final String[] KEYWORDS = ("and assert break class continue " +
      "def del elif else except exec finally for from global if import in is " +
      "lambda not or pass print raise return try while").split(" ");

  public static final boolean VERSION_1_4 = false;
  
  private final String source;
  private int index;

  private int openParens;
  private int lineIndent;
  private final int[] indents = new int[32];
  private int ii;
  private boolean beginOfLine = true;

  /**
   * Constructs a new scanner for the given source string.
   *
   * @param source the Python source
   */
  public Scanner(String source) {
    this.source = source;
    advance();
  }

  /**
   * The current token type; filled by calling {@link #advance()}.
   */
  protected String tokenType;

  /**
   * The current token value; filled by calling {@link #advance()}.
   */
  protected Object token;

  /**
   * Reads the next token and stores it as the new current token.
   * @return the token value of the previous token
   */
  public Object advance() {
    Object t = token;
    token = null;
    tokenType = nextToken();
    return t;
  }

  protected RuntimeException notify(String message) {
    return new RuntimeException(message + " in line " + line() + " of " + source);
  }

  private int line() {
    int line = 1;
    for (int i = 0, end = Math.min(index, source.length() - 1); i < end; i++) {
      if (source.charAt(i) == '\r' && source.charAt(i + 1) != '\n' || source.charAt(i) == '\n') {
        line += 1;
      }
    }
    return line;
  }

  /**
   * Returns the next token. The scanner generates NEWLINE, INDENT and DEDENT token as required by
   * the specification. Each logical line is terminated by a NEWLINE token. If the line's
   * indentation is larger than the one of the previous logical line, an INDENT token is generated
   * before the first actual token of that line will be returned. If the line's indentation is
   * smaller than the one of the previous line, as many DEDENT tokens are generated as required to
   * reach the old indentation level.
   * <p>
   * A physical line is terminated by \r, \n, \r\n or the end-of-source marker. If a physical line
   * ends with a \ followed only by spaces, \r, \n or a comment, the next physical line is part of
   * the current logical line. A logical line does not end unless there's a closing ) ] } for each
   * open ( [ {. Empty lines or lines only containing spaces or a comment are ignored and do not
   * count for indenting.
   * <p>
   * Comments start with '#' and go upto the end of the physical line.
   * <p>
   * Note: The first line of the source must not be indented.
   *
   * @return the token type, one of "INDENT", "DEDENT", "NEWLINE", "STRING", "NUMBER", "NAME",
   * "!=", "%", "&", "(", ")", "*", "**", "+", ",", "-", ".", "...", "/", ":", ";", "<", "<<",
   * "<=", "<>", "=", "==", ">", ">>", ">=", "[", "]", "{", "}", "^", "~", "|", "`", or a Python
   * keyword; result is <code>null</code> if the end of the source string has been reached
   */
  private String nextToken() {
    // do we need to generate DEDENT tokens?
    if (lineIndent < indents[ii]) {
      return dedent();
    }

    // find the next significant character, skipping whitespaces and comments, computing indentations
    char ch = next();

    if (openParens > 0) {
      // we're inside an open (, [ or {, no logical line start possible
      while (true) {
        while (ch == ' ' || ch == '\n' || ch == '\t') { // skip upto interesting char
          ch = next();
        }
        if (ch == '#') { // also skip comment
          ch = skipComment();
          continue;
        }
        if (ch == '\\') { // line continuation
          ch = skipLineContinuation();
          continue;
        }
        break;
      }
    } else {
      // all (, [ and { are balanced, start of logical line possible
      if (beginOfLine) {
        int indent;
        while (true) { // search for non-empty line
          indent = 0;
          while (ch == ' ' || ch == '\t') { // compute indentation
            if (ch == '\t') {
              indent = (indent / 8 + 1) * 8 - 1;
            }
            ch = next();
            indent++;
          }
          if (ch == '#') { // skip comment
            ch = skipComment();
          }
          if (ch == '\n') { // ignore empty lines
            ch = next();
            continue;
          }
          break;
        }
        if (ch != 0) {
          beginOfLine = false;
          this.lineIndent = indent;
          if (indent > indents[ii]) {
            back();
            indents[++ii] = indent;
            return "INDENT";
          }
          if (indent < indents[ii]) {
            back();
            return dedent();
          }
        }
      } else {
        while (true) {
          while (ch == ' ' || ch == '\t') {
            ch = next();
          }
          if (ch == '#') {
            ch = skipComment();
          }
          if (ch == '\\') {
            ch = skipLineContinuation();
            continue;
          }
          break;
        }
      }
    }

    // end of stream marker
    if (beginOfLine && ch == 0) {
      lineIndent = 0;
      if (ii > 0) {
        return dedent();
      }
      return null;
    }

    switch (ch) {
    case '\0':
    case '\n':
      beginOfLine = true;
      return "NEWLINE";
    case '!':
      if (next() == '=') {
        return "!=";
      }
      back();
      break; // fall into exception
    case '"':
      return nextString(ch, false);
    case '%':
      return "%";
    case '&':
      return "&";
    case '\'':
      return nextString(ch, false);
    case '(':
      openParens++;
      return "(";
    case ')':
      openParens--;
      return ")";
    case '*':
      if (next() == '*') {
        return "**";
      }
      back();
      return "*";
    case '+':
      return "+";
    case ',':
      return ",";
    case '-':
      return "-";
    case '.':
      ch = next();
      if (ch == '.') {
        if (next() == '.') {
          return "...";
        }
        back();
      }
      back();
      if (Character.isDigit(ch)) {
        back();
        return nextNumber('0');
      }
      return ".";
    case '/':
      return "/";
    case '0':
    case '1':
    case '2':
    case '3':
    case '4':
    case '5':
    case '6':
    case '7':
    case '8':
    case '9':
      return nextNumber(ch);
    case ':':
      return ":";
    case ';':
      return ";";
    case '<':
      ch = next();
      if (ch == '<') {
        return "<<";
      } else if (ch == '=') {
        return "<=";
      } else if (ch == '>') {
        return "<>";
      }
      back();
      return "<";
    case '=':
      if (next() == '=') {
        return "==";
      }
      back();
      return "=";
    case '>':
      ch = next();
      if (ch == '>') {
        return ">>";
      } else if (ch == '=') {
        return ">=";
      }
      back();
      return ">";
    case '?':
    case '@':
      break;
    case 'A':
    case 'B':
    case 'C':
    case 'D':
    case 'E':
    case 'F':
    case 'G':
    case 'H':
    case 'I':
    case 'J':
    case 'K':
    case 'L':
    case 'M':
    case 'N':
    case 'O':
    case 'P':
    case 'Q':
    case 'R':
    case 'S':
    case 'T':
    case 'U':
    case 'V':
    case 'W':
    case 'X':
    case 'Y':
    case 'Z':
      return nextNameOrKeyword(ch);
    case '[':
      openParens++;
      return "[";
    case ']':
      openParens--;
      return "]";
    case '^':
      return "^";
    case '_':
      return nextNameOrKeyword(ch);
    case '`':
      return "`";
    case 'a':
    case 'b':
    case 'c':
    case 'd':
    case 'e':
    case 'f':
    case 'g':
    case 'h':
    case 'i':
    case 'j':
    case 'k':
    case 'l':
    case 'm':
    case 'n':
    case 'o':
    case 'p':
    case 'q':
    case 'r':
    case 's':
    case 't':
    case 'u':
    case 'v':
    case 'w':
    case 'x':
    case 'y':
    case 'z':
      return nextNameOrKeyword(ch);
    case '{':
      openParens++;
      return "{";
    case '|':
      return "|";
    case '}':
      openParens--;
      return "}";
    case '~':
      return "~";
    default:
      if (Character.isLetter(ch)) {
        return nextNameOrKeyword(ch);
      }
    }
    throw notify("invalid character " + ch + " (" + (int) ch + ")");
  }

  private String dedent() {
    if (lineIndent > indents[--ii]) {
      throw notify("inconsistent indent " + lineIndent + " vs " + indents[ii]);
    }
    return "DEDENT";
  }

  private char skipComment() {
    char ch = next();
    while (ch != 0 && ch != '\n') {
      ch = next();
    }
    return ch;
  }

  private char skipLineContinuation() {
    char ch = next();
    while (ch == ' ' || ch == '\t') {
      ch = next();
    }
    if (ch == '#') {
      ch = skipComment();
    }
    if (ch != '\n') {
      throw notify("invalid line continuation");
    }
    return next();
  }

  private String nextString(char delim, boolean raw) {
    StringBuilder b = new StringBuilder(256);
    boolean shortString = true;
    char ch = next();
    if (ch == delim) {
      ch = next();
      if (ch == delim) {
        shortString = false;
        ch = next();
      } else {
        back();
        ch = delim;
      }
    }
    while (!end(ch, delim, shortString)) {
      if (ch == '\\') {
        ch = next();
        if (raw) {
          if (ch == 0) {
            throw notify("invalid escape");
          }
          b.append('\\');
        } else {
          if (ch == '\n') {
            ch = next();
            continue;
          }
          if (ch == 'a') {
            ch = '\u0007';
          } else if (ch == 'b') {
            ch = '\b';
          } else if (ch == 'f') {
            ch = '\f';
          } else if (ch == 'n') {
            ch = '\n';
          } else if (ch == 'r') {
            ch = '\r';
          } else if (ch == 't') {
            ch = '\t';
          } else if (ch == 'v') {
            ch = '\u000b';
          } else if (ch == 'x') {
            int n1 = Character.digit(next(), 16);
            int n2 = Character.digit(next(), 16);
            if (n1 == -1 || n2 == -1) {
              throw notify("invalid escape");
            }
            ch = (char) (n1 * 16 + n2);
          } else if (ch >= '0' && ch <= '7') {
            int value = Character.digit(ch, 8);
            ch = next();
            if (ch >= '0' && ch <= '7') {
              value = value * 8 + Character.digit(ch, 8);
              ch = next();
              if (ch >= '0' && ch <= '7') {
                value = value * 8 + Character.digit(ch, 8);
              } else {
                back();
              }
            } else {
              back();
            }
            ch = (char) value;
          } else if (ch != '\\' && ch != '"' && ch != '\'') {
            if (ch == 0) {
              throw notify("invalid escape");
            }
            b.append('\\');
          }
        }
      }
      b.append(ch);
      ch = next();
    }
    token = b.toString();
    return "STRING";
  }

  private boolean end(char ch, char delim, boolean shortString) {
    if (ch == 0) {
      throw notify("unterminated string literal");
    }
    if (ch == delim) {
      if (shortString) {
        return true;
      }
      char ch1 = next();
      char ch2 = next();
      if (ch1 == delim && ch2 == delim) {
        return true;
      }
      back();
      back();
    }
    return false;
  }

  private String nextNumber(char ch) {
    StringBuilder b = new StringBuilder(32);
    int radix = 10;
    if (ch == '0') {
      b.append(ch);
      ch = next();
      if (ch == 'x' || ch == 'X') {
        radix = 16;
        ch = next();
      } else if (ch != '.'){
        radix = 8;
      }
    }
    while (Character.digit(ch, radix) != -1) {
      b.append(ch);
      ch = next();
    }
    if (radix == 10) {
      if (ch == '.' || ch == 'e' || ch == 'E') {
        if (ch == '.') {
          b.append(ch);
          ch = next();
          while (Character.isDigit(ch)) {
            b.append(ch);
            ch = next();
          }
        }
        if (ch == 'e' || ch == 'E') {
          b.append(ch);
          ch = next();
          if (ch == '-' || ch == '+') {
            b.append(ch);
            ch = next();
          }
          while (Character.isDigit(ch)) {
            b.append(ch);
            ch = next();
          }
        }
        if (ch != 'j' && ch != 'J') { //TODO implement complex numbers
          back();
        }
        token = Double.valueOf(b.toString());
        return "NUMBER";
      }
    }
    BigInteger value = new BigInteger(b.toString(), radix);
    if (ch != 'L' && ch != 'l' && ch != 'j' && ch != 'J') { //TODO implement complex numbers
      back();
    }
    if (ch != 'L' && ch != 'l' && (value.longValue() >>> 32) == 0) {
      token = value.intValue();
    } else {
      token = value;
    }
    return "NUMBER";
  }

  private String nextNameOrKeyword(char ch) {
    if (ch == 'r' || ch == 'R') {
      char delim = next();
      if (delim == '"' || delim == '\'') {
        return nextString(delim, true);
      }
      back();
    }
    if (ch == 'u' || ch == 'U') {
      char delim = next();
      if (delim == '"' || delim == '\'') {
        return nextString(delim, false);
      }
      back();
    }
    StringBuilder b = new StringBuilder(32);
    while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
      b.append(ch);
      ch = next();
    }
    back();
    token = b.toString();
    for (String keyword : KEYWORDS) {
      if (VERSION_1_4 && keyword.equals("assert")) continue;
      if (keyword.equals(token)) {
        token = keyword;
        return keyword;
      }
    }
    return "NAME";
  }

  /**
   * Returns the next character from the source, normalizing \r and \r\n to \n.
   * @return next character or 0 if the end of the source string has been reached
   */
  private char next() {
    char ch = index++ < source.length() ? source.charAt(index - 1) : 0;
    if (ch == '\r') {
      if (index < source.length() && source.charAt(index) == '\n') {
        index++;
      }
      ch = '\n';
    }
    // XXX ignore ^L
    if (ch == '\u000c') {
      ch = ' ';
    }
    return ch;
  }

  /**
   * Resets the source index so that the next call to {@link #next()} returns the same character as
   * the last call did.
   */
  private void back() {
    --index;
  }
}
