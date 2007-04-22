/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;

/**
 * Represents the <code>try/finally</code> statement, see §7.4.
 */
public class PyTryFinallyStmt extends PyStmt {
  private final PySuite tryClause;
  private final PySuite finallyClause;

  public PyTryFinallyStmt(PySuite tryClause, PySuite finallyClause) {
    this.tryClause = tryClause;
    this.finallyClause = finallyClause;
  }

  @Override
  public String toString() {
    return "try: " + tryClause + " finally: " + finallyClause;
  }

  @Override
  public void execute(PyFrame frame) {
    try {
      tryClause.execute(frame);
    } finally {
      finallyClause.execute(frame);
    }
  }

}
