/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;

/**
 * Represents the <code>continue</code> statement, see §6.10.
 */
public class PyContinueStmt extends PyStmt {
  @Override
  public String toString() {
    return "continue";
  }

  @Override
  public void execute(PyFrame frame) {
    throw new Py.ContinueSignal();
  }
}
