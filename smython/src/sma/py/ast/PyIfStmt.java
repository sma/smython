/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;

/**
 * Represents the <code>if</code> statement, see §7.1.
 */
public class PyIfStmt extends PyStmt {
  private final PyExpr condition;
  private final PySuite thenClause;
  private final PySuite elseClause;

  public PyIfStmt(PyExpr condition, PySuite thenClause, PySuite elseClause) {
    this.condition = condition;
    this.thenClause = thenClause;
    this.elseClause = elseClause;
  }

  @Override
  public String toString() {
    return "if " + condition + ": " + thenClause + (elseClause != null ? " else: " + elseClause : "");
  }

  @Override
  public void execute(PyFrame frame) {
    if (condition.eval(frame).truth()) {
      thenClause.execute(frame);
    } else if (elseClause != null) {
      elseClause.execute(frame);
    }
  }

}
