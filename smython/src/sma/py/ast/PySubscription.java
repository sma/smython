/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a subscription, see §5.3.2.
 */
public class PySubscription extends PyExpr {
  private final PyExpr primary;
  private final PyExprList expressions;

  public PySubscription(PyExpr primary, PyExprList expressions) {
    this.primary = primary;
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    return primary + "[" + expressions + "]";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyObject key = expressions.eval(frame);
    PyObject value = primary.eval(frame).getItem(key);
    if (value == null) {
      throw Py.keyError(key);
    }
    return value;
  }

  @Override
  public boolean isTarget() {
    return true;
  }

  @Override
  public void assign(PyFrame frame, PyObject value) {
    primary.eval(frame).setItem(expressions.eval(frame), value);
  }

  @Override
  public void del(PyFrame frame) {
    primary.eval(frame).delItem(expressions.eval(frame));
  }

}
