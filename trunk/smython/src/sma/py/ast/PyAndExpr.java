/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the logical and operation, see §5.10.
 */
public class PyAndExpr extends PyBinaryExpr {

  public PyAndExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "and";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyObject obj = left.eval(frame);
    return obj.truth() ? right.eval(frame) : obj;
  }
}
