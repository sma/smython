/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the power operation (**), see §5.4. 
 */
public class PyPowExpr extends PyBinaryExpr {

  public PyPowExpr(PyExpr left, PyExpr right) {
    super(left, right);
  }

  @Override
  protected String op() {
    return "**";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return left.eval(frame).pow(right.eval(frame));
  }
}
