/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public class Py {
  /**
   * Abstract base class for signals. Because we do not need Java to fill in the 
   * stack trace, we overwrite the {@code fillInStackTrace()} method. This greately
   * improves the performance of raised exceptions.
   */
  public abstract static class Signal extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
      return null; // omit stack trace creation for performance reasons
    }
  }

  /**
   * Signals a Python exception. Can be caught with a Python {@code try/except} statement.
   */
  public static class RaiseSignal extends Signal {
    private final PyObject exception;
    private final PyObject instance;
    private final PyObject traceback;

    public RaiseSignal(PyObject exception, PyObject instance, PyObject traceback) {
      this.exception = exception;
      this.instance = instance;
      this.traceback = traceback;
    }

    public PyObject getException() {
      return exception;
    }

    public PyObject getInstance() {
      return instance;
    }

    public PyObject getTraceback() {
      return traceback;
    }
  }

  /**
   * Signals returning a result from a function application.
   */
  public static class ReturnSignal extends Signal {
    private final PyObject result;

    public ReturnSignal(PyObject result) {
      this.result = result;
    }

    public PyObject getResult() {
      return result;
    }
  }

  /**
   * Signals breaking a {@code for} or {@code while} loop.
   */
  public static class BreakSignal extends Signal {
  }

  /**
   * Signals continuing a {@code for} or {@code while} loop.
   */
  public static class ContinueSignal extends Signal {
  }
}
