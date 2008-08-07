/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.io.*;

/**
 * Tries to read in all Python files found in the given folder(s) to exercise the scanner.
 */
public class PyReader {


  public static void main(String[] args) throws IOException {
    args = new String[]{"/Users/sma/Desktop/Python-1.6.1/Lib/test"};
    for (String name : args) {
      read(new File(name));
    }
    //scan(new File("/Users/sma/Desktop/Python-1.6.1/Lib/dos-8x3/test_gra.py"));
  }

  private static void read(File dir) throws IOException {
    File[] files = dir.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.isDirectory() || pathname.getName().endsWith(".py");
      }
    });
    for (File file : files) {
      if (file.isDirectory()) {
        read(file);
      } else {
        scan(file);
        parse(file);
      }
    }
  }

  private static void scan(File file) throws IOException {
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

    System.out.println("Scanning " + file.getPath());
    Scanner scanner = new Scanner(b.toString());
    while (scanner.tokenType != null) {
      scanner.advance();
    }
  }

  private static void parse(File file) throws IOException {
    if (file.getName().equals("test_b1.py")) return; //complex
    if (file.getName().equals("test_cpickle.py")) return; //complex
    if (file.getName().equals("test_extcall.py")) return; // unsplice
    if (file.getName().equals("test_grammar.py")) return; // unsplice
    if (file.getName().equals("test_pickle.py")) return; // complex

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

    System.out.println("Parsing " + file.getPath());
    Parser parser = new Parser(b.toString());
    parser.interactiveInput();
  }
}
