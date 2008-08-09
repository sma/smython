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
    PyDict newLocals = frame.getLocals();
    PyDict newGlobals = frame.getGlobals();
    if (globals != null) {
      newGlobals = (PyDict) globals.eval(frame);
      if (locals != null) {
        newLocals = (PyDict) locals.eval(frame);
      } else {
        newLocals = newGlobals;
      }
    }
    //TODO exec also support code objects
    new Parser(expr.eval(frame).str().value()).interactiveInput().execute(new PyFrame(frame, newLocals, newGlobals, frame.getBuiltins()));
  }

}
