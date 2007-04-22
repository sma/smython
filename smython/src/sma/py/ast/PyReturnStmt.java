/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;

/**
 * Represents the <code>return</code> statement, see §6.7.
 */
public class PyReturnStmt extends PyStmt {
  private final PyExprList expressions;

  public PyReturnStmt(PyExprList expressions) {
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    return "return " + expressions;
  }

  @Override
  public void execute(PyFrame frame) {
    throw new Py.ReturnSignal(expressions.eval(frame));
  }

}
