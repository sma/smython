/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public class PyEllipsis extends PyObject {
  private static final PyString NAME = intern("...");
  
  PyEllipsis() {
  }

  @Override
  public String toString() {
    return "...";
  }

  @Override
  public PyString repr() {
    return NAME;
  }
}
