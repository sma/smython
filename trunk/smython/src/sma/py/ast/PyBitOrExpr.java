/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the binary bit-wise or operation (|), see §5.8.
 */
public class PyBitOrExpr extends PyBinaryExpr {

  public PyBitOrExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "|";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).or(right.eval(frame));
  }
}
