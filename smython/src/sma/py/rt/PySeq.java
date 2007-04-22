/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public abstract class PySeq extends PyObject {
  @Override
  public boolean truth() {
    return len().as_int() != 0;
  }
}
