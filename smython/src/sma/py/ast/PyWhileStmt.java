/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;

/**
 * Represents the <code>while</code> statement, see §7.2.
 */
public class PyWhileStmt extends PyStmt {
  private final PyExpr condition;
  private final PySuite bodyClause;
  private final PySuite elseClause;

  public PyWhileStmt(PyExpr condition, PySuite bodyClause, PySuite elseClause) {
    this.condition = condition;
    this.bodyClause = bodyClause;
    this.elseClause = elseClause;
  }

  @Override
  public String toString() {
    return "while " + condition + ": " + bodyClause + (elseClause != null ? " else: " + elseClause : "");
  }

  @Override
  public void execute(PyFrame frame) {
    while (condition.eval(frame).truth()) {
      try {
        bodyClause.execute(frame);
      } catch (Py.BreakSignal s) {
        return;
      } catch (Py.ContinueSignal s) {
        //noinspection UnnecessaryContinue
        continue;
      }
    }
    if (elseClause != null) {
      elseClause.execute(frame);
    }
  }
}
