/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a literal number or string, see §5.2.2.
 */
public class PyLiteral extends PyExpr {
  private final PyObject value;

  public PyLiteral(PyObject value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value == null ? "<null>" : value.toString();
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return value;
  }

}
