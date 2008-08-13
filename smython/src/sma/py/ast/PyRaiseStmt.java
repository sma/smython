/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyClass;
import sma.py.rt.PyFrame;
import sma.py.rt.PyInstance;
import sma.py.rt.PyObject;

/**
 * Represents the <code>raise</code> statement, see §6.8.
 */
public class PyRaiseStmt extends PyStmt {
  private final PyExpr exception;
  private final PyExpr instance;
  private final PyExpr traceback;

  public PyRaiseStmt(PyExpr exception, PyExpr instance, PyExpr traceback) {
    this.exception = exception; // a string, class or instance
    this.instance = instance; // any object, an instance or None
    this.traceback = traceback;
  }

  @Override
  public String toString() {
    return "raise " + exception + (instance != null ? ", " + instance + (traceback != null ? ", " + traceback : "") : "");
  }

  @Override
  public void execute(PyFrame frame) {
    PyObject exc = exception.eval(frame);
    PyObject arg = instance != null ? instance.eval(frame) : PyObject.None;
    PyObject tb = traceback != null ? traceback.eval(frame) : PyObject.None;
    if (!exc.exceptionType()) {
      throw Py.typeError("exceptions must be strings, classes, or instances");
    }
    if (exc instanceof PyInstance) {
      if (arg != PyObject.None) {
        throw Py.typeError("an instance exception must not have a second parameter");
      }
      arg = exc;
      exc = ((PyInstance) exc).getClasz();
    } else if (exc instanceof PyClass) {
      if (!((PyClass) exc).isInstance(arg)) {
        throw Py.typeError("second parameter of class exception must be instance of that class");
      }
    }
    throw new Py.RaiseSignal(exc, arg, tb);
  }

}
