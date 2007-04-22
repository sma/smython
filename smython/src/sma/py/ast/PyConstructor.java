/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

/**
 * Abstract base class for constructor expressions.
 */
public abstract class PyConstructor extends PyExpr {
  protected final PyExprList values;

  public PyConstructor(PyExprList values) {
    this.values = values;
  }

  @Override
  public String toString() {
    return "" + delim().charAt(0) + values + delim().charAt(1);
  }

  protected abstract String delim();
}
