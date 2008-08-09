/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;

/**
 * Represents the <code>print</code> statement, see §6.6.
 */
public class PyPrintStmt extends PyStmt {
  private final PyExprList expressions;

  public PyPrintStmt(PyExprList expressions) {
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    return "print " + expressions;
  }

  @Override
  public void execute(PyFrame frame) {
    //TODO automatically emit spaces if not at the begin of line
    System.out.print(expressions.eval(frame));
    if (!expressions.isTuple()) {
      System.out.println();
    }
  }

}
