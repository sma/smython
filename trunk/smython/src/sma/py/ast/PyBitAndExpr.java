/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the binary bit-wise and operation (&amp;), see §5.8.
 */
public class PyBitAndExpr extends PyBinaryExpr {

  public PyBitAndExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "&";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).and(right.eval(frame));
  }

}
