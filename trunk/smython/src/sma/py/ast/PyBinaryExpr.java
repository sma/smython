/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

/**
 * Abstract base class for binary operations.
 */
public abstract class PyBinaryExpr extends PyExpr {
  protected final PyExpr left;
  protected final PyExpr right;

  public PyBinaryExpr(PyExpr left, PyExpr right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString() {
    return left + " " + op() + " " + right;
  }

  protected abstract String op();

}
