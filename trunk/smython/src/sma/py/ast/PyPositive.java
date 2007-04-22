/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the unary arithmetic positive operation (+), see §5.5.
 */
public class PyPositive extends PyUnaryExpr {

  public PyPositive(PyExpr expr) {
    super(expr);
  }

  @Override
  public String toString() {
    return "+" + expr;
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return expr.eval(frame).pos();
  }

}
