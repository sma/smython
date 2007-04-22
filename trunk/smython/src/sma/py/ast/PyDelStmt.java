/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;

/**
 * Represents the <code>del</code> statement, see §6.5.
 */
public class PyDelStmt extends PyStmt {
  private final PyExprList targets;

  public PyDelStmt(PyExprList targets) {
    this.targets = targets;
  }

  @Override
  public String toString() {
    return "del " + targets;
  }

  @Override
  public void execute(PyFrame frame) {
    targets.del(frame);
  }

}
