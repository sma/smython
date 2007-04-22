/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the unary bit-wise invert operation (~), see §5.5.
 */
public class PyBitInvert extends PyUnaryExpr {

  public PyBitInvert(PyExpr expr) {
    super(expr);
  }

  @Override
  public String toString() {
    return "~" + expr;
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return expr.eval(frame).invert();
  }

}
