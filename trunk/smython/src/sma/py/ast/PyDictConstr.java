/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a dictionary display, see §5.2.5.
 */
public class PyDictConstr extends PyConstructor {

  public PyDictConstr(PyExprList values) {
    super(values);
  }

  @Override
  protected String delim() {
    return "{}";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return values.evalAsDictionary(frame);
  }
}
