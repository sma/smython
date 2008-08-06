/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Abstract base class for all expression nodes. An expression knows whether it is an assignment
 * or deletion target and whether it can be either evaluated or assigned to.
 */
public abstract class PyExpr extends PyNode {
  /**
   * Evaluates the expression node in the context of the given frame and returns the result of the
   * evaluation.
   *
   * @param frame the current context, storing local and global variables
   * @return the result of the evaluation
   */
  public abstract PyObject eval(PyFrame frame);

  /**
   * Returns whether this expression node can play the role of a target and
   * {@link #assign(PyFrame, PyObject)} or {@link #del(PyFrame)} can be called.
   *
   * @return <code>true</code> if the receiver can be a target; variables, attribute
   * references, subscriptions and slices are targets as well as list and tuple
   * constuctors which contain only targets.
   */
  public boolean isTarget() {
    return false;
  }

  /**
   * Assigns the given value to the target represented by this expression node.
   * You may only call this method if {@link #isTarget()} returns true.
   *
   * @param frame the current context, storing local and global variables
   * @param value the value to assign
   */
  public void assign(PyFrame frame, PyObject value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Deletes something from the target represented by this expression node.
   * You may only call this method if {@link #isTarget()} returns true.
   *
   * @param frame the current context, storing local and global variables
   */
  public void del(PyFrame frame) {
    throw new UnsupportedOperationException();
  }
}
