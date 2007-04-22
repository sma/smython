/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.ArrayList;
import java.util.List;

import sma.py.rt.PyFrame;
import sma.py.rt.PyList;
import sma.py.rt.PyObject;

/**
 * Represents a list display, see §5.2.4.
 */
public class PyListConstr extends PyConstructor {
  public PyListConstr(PyExprList values) {
    super(values);
  }

  @Override
  protected String delim() {
    return "[]";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    int size = values.size();
    List<PyObject> list = new ArrayList<PyObject>(size);
    for (int i = 0; i < size; i++) {
      list.add(values.get(i).eval(frame));
    }
    return new PyList(list);
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

}
