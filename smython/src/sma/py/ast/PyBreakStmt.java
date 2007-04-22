/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.Py;
import sma.py.rt.PyFrame;

/**
 * Represents the <code>break</code> statement, see §6.9.
 */
public class PyBreakStmt extends PyStmt {

  @Override
  public String toString() {
    return "break";
  }

  @Override
  public void execute(PyFrame frame) {
    throw new Py.BreakSignal();
  }
}
