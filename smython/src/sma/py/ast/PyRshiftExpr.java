/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the binary right-shift operation, see §5.7.
 */
public class PyRshiftExpr extends PyBinaryExpr {

  public PyRshiftExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return ">>";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).rshift(right.eval(frame));
  }

}
