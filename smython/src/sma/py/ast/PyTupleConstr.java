/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a parenthesized form, see §5.2.3.
 */
public class PyTupleConstr extends PyConstructor {

  public PyTupleConstr(PyExprList values) {
    super(values);
  }

  @Override
  protected String delim() {
    return "()";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return values.eval(frame);
  }

  @Override
  public boolean isTarget() {
    for (int i = 0, size = values.size(); i < size; i++) {
      if (!values.get(i).isTarget()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void assign(PyFrame frame, PyObject value) {
    values.assign(frame, value);
  }

  @Override
  public void del(PyFrame frame) {
    values.del(frame);
  }

}
