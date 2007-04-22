/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the binary left-shift operation, see §5.7.
 */
public class PyLshiftExpr extends PyBinaryExpr {

  public PyLshiftExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "<<";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).lshift(right.eval(frame));
  }
}
