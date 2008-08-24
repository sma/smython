/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;
import sma.py.rt.PyIterator;
import sma.py.rt.PyObject;

/**
 * Represents the <code>for</code> statement, see §7.3.
 */
public class PyForStmt extends PyStmt {
  private final PyExprList targets;
  private final PyExprList expressions;
  private final PySuite bodyClause;
  private final PySuite elseClause;

  public PyForStmt(PyExprList targets, PyExprList expressions, PySuite bodyClause, PySuite elseClause) {
    this.targets = targets;
    this.expressions = expressions;
    this.bodyClause = bodyClause;
    this.elseClause = elseClause;
  }

  @Override
  public String toString() {
    return "for " + targets + " in " + expressions + ": " + bodyClause + (elseClause != null ? " else: " + elseClause : "");
  }

  @Override
  public void execute(PyFrame frame) {
    PyIterator iterator = expressions.eval(frame).iter();
    PyObject object;
    while ((object = iterator.next()) != null) {
      try {
        targets.assign(frame, object);
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
