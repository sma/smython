/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents an expression as a statement, see §6.1.
 */
public class PyExprStmt extends PyStmt {
  private final PyExprList expressions;

  public PyExprStmt(PyExprList list) {
    this.expressions = list;
  }

  @Override
  public String toString() {
    return expressions.toString();
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return expressions.eval(frame);
  }

  @Override
  public void execute(PyFrame frame) {
    expressions.eval(frame);
  }
}