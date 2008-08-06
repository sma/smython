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
    for (String name : args) {
      read(new File(name));
    }
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
}
