/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

/**
 * Abstract base class for unary operations.
 */
public abstract class PyUnaryExpr extends PyExpr {
  protected final PyExpr expr;

  public PyUnaryExpr(PyExpr expr) {
    this.expr = expr;
  }

}
