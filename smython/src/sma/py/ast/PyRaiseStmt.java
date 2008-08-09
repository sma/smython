/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents the <code>raise</code> statement, see §6.8.
 */
public class PyRaiseStmt extends PyStmt {
  private final PyExpr exception;
  private final PyExpr instance;
  private final PyExpr traceback;

  public PyRaiseStmt(PyExpr exception, PyExpr instance, PyExpr traceback) {
    this.exception = exception;
    this.instance = instance;
    this.traceback = traceback;
  }

  @Override
  public String toString() {
    return "raise " + exception + (instance != null ? ", " + instance + (traceback != null ? ", " + traceback : "") : "");
  }

  @Override
  public void execute(PyFrame frame) {
    throw new Py.RaiseSignal(
      exception.eval(frame),
      instance != null ? instance.eval(frame) : PyObject.None,
      traceback != null ? traceback.eval(frame) : PyObject.None);
  }

}
