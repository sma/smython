/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

import java.util.List;

/**
 * Represents a slicing, see §5.3.3.
 */
public class PySlicing extends PyExpr {
  private final PyExpr primary;
  private final List<PySubscript> subscripts;

  public PySlicing(PyExpr primary, List<PySubscript> subscripts) {
    this.primary = primary;
    this.subscripts = subscripts;
  }

  @Override
  public String toString() {
    return primary + "[" + list(subscripts, ",") + "]";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyObject sequence = primary.eval(frame);
    for (PySubscript sub : subscripts) { //TODO Hack!
      return sequence.getSlice(
        sub.getLeft() != null ? sub.getLeft().eval(frame) : PyObject.make(0),
        sub.getRight() != null ? sub.getRight().eval(frame) : sequence.len());
    }
    return PyObject.None; //TODO this is obviously wrong!
  }

  @Override
  public boolean isTarget() {
    return true;
  }

  @Override
  public void assign(PyFrame frame, PyObject value) {
    PyObject sequence = primary.eval(frame);
    for (PySubscript sub : subscripts) {
      sequence.setSlice(
        sub.getLeft() != null ? sub.getLeft().eval(frame) : PyObject.make(0),
        sub.getRight() != null ? sub.getRight().eval(frame) : sequence.len(),
        value);
    }
  }

  @Override
  public void del(PyFrame frame) {
    PyObject sequence = primary.eval(frame);
    for (PySubscript sub : subscripts) {
      sequence.delSlice(
        sub.getLeft() != null ? sub.getLeft().eval(frame) : PyObject.make(0),
        sub.getRight() != null ? sub.getRight().eval(frame) : sequence.len());
    }
  }

}
