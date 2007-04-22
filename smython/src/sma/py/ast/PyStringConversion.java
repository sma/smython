/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a string conversion, see §5.2.6.
 */
public class PyStringConversion extends PyConstructor {

  public PyStringConversion(PyExprList values) {
    super(values);
  }

  @Override
  protected String delim() {
    return "``";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return values.eval(frame).repr();
  }
}
