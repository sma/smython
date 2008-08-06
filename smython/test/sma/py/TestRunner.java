/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Runs all Python tests in the <i>sma.py.tests</i> package. Tests are separated by <code>###</code>.
 * Each test consists of one or multiple lines starting with <code>&gt;&gt;&gt;</code> or <code>...</code>.
 * A non-empty line is the expected result of evaluating the lines just before that result line.
 */
public class TestRunner {

  public static Test suite() throws URISyntaxException, IOException {
    TestSuite suite = new TestSuite("Python Tests");

    File tests = new File(TestRunner.class.getResource("tests").toURI());
    for (File f : tests.listFiles()) {
      createTests(suite, f);
    }
    return suite;
  }

  private static void createTests(TestSuite master, File f) throws IOException {
    TestSuite suite = new TestSuite(f.getName());
    BufferedReader r = new BufferedReader(new FileReader(f));
    try {
      PyTestCase test = null;
      String lines = "";
      String line;
      while ((line = r.readLine()) != null) {
        if (line.startsWith("### ")) {
          if (test != null) {
            suite.addTest(test);
          }
          test = new PyTestCase(line.substring(4).trim());
          lines = "";
        } else if (line.startsWith(">>> ") || line.startsWith("... ")) {
          lines = lines + line.substring(4) + "\n";
        } else if (line.trim().length() > 0) {
          if (test == null) throw new IOException("missing ### header");
          test.addAssert(lines, line.trim());
          lines = "";
        }
      }
      if (test != null) {
        suite.addTest(test);
      }
    } finally {
      r.close();
    }
    master.addTest(suite);
  }

  private static class PyTestCase extends TestCase {
    private final List<PyAssert> asserts = new ArrayList<PyAssert>();

    public PyTestCase(String name) {
      super(name);
    }

    public void addAssert(String lines, String result) {
      asserts.add(new PyAssert(lines, result));
    }

    @Override
    protected void runTest() throws Throwable {
      Interpreter i = new Interpreter();
      for (PyAssert a : asserts) {
        a.runIn(i);
      }
    }
  }

  private static class PyAssert {
    private final String lines;
    private final String result;

    public PyAssert(String lines, String result) {
      this.lines = lines;
      this.result = result;
    }

    public void runIn(Interpreter i) {
      Assert.assertEquals(result, i.eval(lines).repr().value());
    }

  }

}
