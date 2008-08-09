/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;
import sma.py.rt.Py;

/**
 * Represents a <code>except</code> as part of a <code>try</code> statement.
 * @see PyTryExceptStmt
 */
public class PyExceptClause {
  private final PyExpr exception;
  private final PyExpr target;
  private final PySuite exceptClause;

  public PyExceptClause(PyExpr exception, PyExpr target, PySuite exceptClause) {
    this.exception = exception;
    this.target = target;
    this.exceptClause = exceptClause;
  }

  @Override
  public String toString() {
    return " except " + (exception != null ? exception + (target != null ? ", " + target : "") : "") + ": " + exceptClause;
  }

  public boolean execute(PyFrame frame, Py.RaiseSignal raise) {
    if (this.exception != null) {
      PyObject ex = this.exception.eval(frame);
      if (ex.compareTo(raise.getException()) != 0) {
        return false;
      }
      if (target != null) {
        target.assign(frame, raise.getInstance());
      }
    }
    exceptClause.execute(frame);
    return true;
  }

}
