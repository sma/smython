/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Abstract base class for all statement nodes. A statement can be executed.
 */
public abstract class PyStmt extends PyNode {
  /**
   * Evaluates the expression node in the context of the given frame and returns the result of the
   * evaluation.
   */
  public PyObject eval(PyFrame frame) {
    execute(frame);
    return PyObject.None;
  }

  /**
   * Executes the statement node in the context of the given frame.
   */
  public abstract void execute(PyFrame frame);

}
