/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.io.*;

/**
 * Tries to read in all Python files found in the given folder(s) to exercise the parser and scanner.
 */
public class PyReader {
  public static void main(String[] args) throws IOException {
    args = new String[]{"/Users/sma/Work/Python-1.4/Lib/test"};
    for (String name : args) {
      search(new File(name));
    }

    Interpreter i = new Interpreter();
    i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_support.py")));
    i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_grammar.py")));
    i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_opcodes.py")));
    i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_operations.py")));
    //i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_b1.py")));
    //i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_b2.py")));
    //i.execute(read(new File("/Users/sma/Work/Python-1.4/Lib/test/test_types.py")));
  }

  /**
   * Traverses a directory recursively for files ending in ".py", calling
   * the scanner and parser on each file found.
   */
  private static void search(File dir) throws IOException {
    File[] files = dir.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.isDirectory() || pathname.getName().endsWith(".py");
      }
    });
    for (File file : files) {
      if (file.isDirectory()) {
        search(file);
      } else {
        String source = read(file);
        scan(file, source);
        parse(file, source);
      }
    }
  }

  /**
   * Reads the contents of the given file, returing it as string.
   */
  private static String read(File file) throws IOException {
    StringBuilder b = new StringBuilder(16384);
    char[] buf = new char[4096];
    FileReader r = new FileReader(file);
    try {
      int len;
      while ((len = r.read(buf)) != -1) {
        b.append(buf, 0, len);
      }
    } finally {
      r.close();
    }
    return b.toString();
  }

  private static void scan(File file, String source) throws IOException {
    System.out.println("Scanning " + file.getPath());
    Scanner scanner = new Scanner(source);
    while (scanner.tokenType != null) {
      scanner.advance();
    }
  }

  private static void parse(File file, String source) throws IOException {
    System.out.println("Parsing " + file.getPath());
    Parser parser = new Parser(source);
    parser.interactiveInput();
    //System.out.println(parser.interactiveInput());
  }
}
