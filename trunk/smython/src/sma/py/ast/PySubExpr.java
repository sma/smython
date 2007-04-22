/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the binary arithmetic subtract (-) operation, see §5.6.
 */
public class PySubExpr extends PyBinaryExpr {

  public PySubExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "-";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).sub(right.eval(frame));
  }

}
