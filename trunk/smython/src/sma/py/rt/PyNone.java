/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

/**
 * The singleton {@code None} value.
 */
public class PyNone extends PyObject {
  private static final PyString NAME = intern("None");

  PyNone() {
  }

  @Override
  public PyString repr() {
    return NAME;
  }

  @Override
  public PyInt nonzero() {
    return False;
  }

  @Override
  public boolean truth() {
    return false;
  }

}
