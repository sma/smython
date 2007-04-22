/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;

/**
 * Represents the <code>pass</code> statement, see §6.4.
 */
public class PyPassStmt extends PyStmt {

  @Override
  public String toString() {
    return "pass";
  }

  @Override
  public void execute(PyFrame frame) {
  }

}
