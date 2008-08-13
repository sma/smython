/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyClass;
import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

/**
 * Represents a <code>class</code> definition statement, see §7.6.
 */
public class PyClassStmt extends PyStmt {
  private final PyString name;
  private final PyExprList bases;
  private final PySuite suite;

  public PyClassStmt(PyString name, PyExprList bases, PySuite suite) {
    this.name = name;
    this.bases = bases;
    this.suite = suite;
  }

  @Override
  public String toString() {
    return "class " + name + (bases == null ? "" : "(" + bases + ")") + ": " + suite;
  }

  @Override
  public void execute(PyFrame frame) {
    PyTuple classes = bases != null ? bases.evalAsTuple(frame) : new PyTuple(); //TODO there should be one empty tuple
    PyDict locals = new PyDict();
    PyFrame classframe = new PyFrame(frame, locals, frame.getGlobals(), frame.getBuiltins());
    suite.execute(classframe);
    frame.setLocal(name, new PyClass(name, classes, locals));
  }

}
