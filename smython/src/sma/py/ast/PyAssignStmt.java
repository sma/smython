/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;

/**
 * Represents the assignment statement, see §6.3.
 */
public class PyAssignStmt extends PyStmt {
  private final PyExprList targets;
  private final PyExprList expressions;

  public PyAssignStmt(PyExprList targets, PyExprList expressions) {
    this.targets = targets;
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    return targets + " = " + expressions;
  }

  @Override
  public void execute(PyFrame frame) {
    targets.assign(frame, expressions.eval(frame));
  }

}
