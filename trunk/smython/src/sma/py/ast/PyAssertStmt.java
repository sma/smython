/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the <code>assert</code> statement, see §6.2.
 */
public class PyAssertStmt extends PyStmt {
  private final PyExpr test;
  private final PyExpr message;

  public PyAssertStmt(PyExpr test, PyExpr message) {
    this.test = test;
    this.message = message;
  }

  @Override
  public String toString() {
    return "assert " + test + (message != null ? ", " + message : "");
  }

  @Override
  public void execute(PyFrame frame) {
    if (!test.eval(frame).truth()) {
      if (message != null) {
        throw new Py.RaiseSignal(PyObject.make("AssertError"), message.eval(frame).str(), null);
      }
      throw new Py.RaiseSignal(PyObject.make("AssertError"), null, null);
    }
  }
}
