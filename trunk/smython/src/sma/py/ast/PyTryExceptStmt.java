/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.List;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;

/**
 * Represents the <code>try/except</code> statement, see §7.4.
 */
public class PyTryExceptStmt extends PyStmt {
  private final PySuite tryClause;
  private final List<PyExceptClause> exceptClauses;
  private final PySuite elseClause;

  public PyTryExceptStmt(PySuite tryClause, List<PyExceptClause> exceptClauses, PySuite elseClause) {
    this.tryClause = tryClause;
    this.exceptClauses = exceptClauses;
    this.elseClause = elseClause;
  }

  @Override
  public String toString() {
    return "try: " + tryClause + list(exceptClauses, "") + (elseClause != null ? " else:" + elseClause : "");
  }

  @Override
  public void execute(PyFrame frame) {
    try {
      tryClause.execute(frame);
    } catch (Py.RaiseSignal s) {
      for (PyExceptClause except : exceptClauses) {
        if (except.execute(frame, s.getException())) {
          return;
        }
      }
    }
    if (elseClause != null) {
      elseClause.execute(frame);
    }
    //TODO need to rethrow exception
  }

}
