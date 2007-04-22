/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;

/**
 * Represents the attribute reference, see §5.3.1.
 */
public class PyAttrRef extends PyExpr {
  private final PyExpr primary;
  private final PyString name;

  public PyAttrRef(PyExpr primary, PyString name) {
    this.primary = primary;
    this.name = name;
  }

  @Override
  public String toString() {
    return primary + "." + name.value();
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return primary.eval(frame).getAttr(name);
  }

  @Override
  public boolean isTarget() {
    return true;
  }

  @Override
  public void assign(PyFrame frame, PyObject value) {
    primary.eval(frame).setAttr(name, value);
  }

  @Override
  public void del(PyFrame frame) {
    primary.eval(frame).delAttr(name);
  }
}
