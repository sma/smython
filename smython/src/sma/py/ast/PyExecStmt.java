/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.Parser;
import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;

/**
 * Represents the <code>exec</code> statement, see §6.13.
 */
public class PyExecStmt extends PyStmt {
  private final PyExpr expr;
  private final PyExpr globals;
  private final PyExpr locals;

  public PyExecStmt(PyExpr expr, PyExpr globals, PyExpr locals) {
    this.expr = expr;
    this.globals = globals;
    this.locals = locals;
  }

  @Override
  public String toString() {
    return "exec " + expr + (globals != null ? " in " + globals + (locals != null ? ", " + locals : "") : "");
  }

  @Override
  public void execute(PyFrame frame) {
    PyFrame evalFrame = frame;
    if (globals != null) {
      //TODO constructing the frame is wrong
      PyDict gdict = (PyDict) globals.eval(frame);
      PyDict ldict = gdict;
      if (locals != null) {
        ldict = (PyDict) locals.eval(frame);
      }
      evalFrame = new PyFrame(gdict, ldict);
    }
    //TODO exec also support code objects
    new Parser(expr.eval(frame).str().value()).interactiveInput().execute(evalFrame);
  }

}
