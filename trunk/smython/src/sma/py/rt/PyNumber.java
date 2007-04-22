/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public abstract class PyNumber extends PyObject {
  @Override
  public PyObject pos() {
    return this;
  }
}
