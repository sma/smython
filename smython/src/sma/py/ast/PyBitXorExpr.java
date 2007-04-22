/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the binary bit-wise xor operation (^), see §5.8.
 */
public class PyBitXorExpr extends PyBinaryExpr {

  public PyBitXorExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "^";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).xor(right.eval(frame));
  }
}
