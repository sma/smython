/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the logical not operation, see §5.10.
 */
public class PyNot extends PyUnaryExpr {

  public PyNot(PyExpr expr) {
    super(expr);
  }

  @Override
  public String toString() {
    return "not " + expr;
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return expr.eval(frame).truth() ? PyObject.False : PyObject.True;
  }

}
