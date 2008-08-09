/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;

/**
 * Represents a identifier, see §5.2.1, or a target, see §6.3. 
 */
public class PyIdentifier extends PyExpr {
  private final PyString name;

  public PyIdentifier(PyString name) {
    this.name = name;
  }

  public PyString getName() {
    return name;
  }

  @Override
  public String toString() {
    return name.value();
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return frame.getLocal(name);
  }

  @Override
  public boolean isTarget() {
    return true;
  }

  @Override
  public void assign(PyFrame frame, PyObject value) {
    frame.setLocal(name, value);
  }

  @Override
  public void del(PyFrame frame) {
    frame.delLocal(name);
  }

}
