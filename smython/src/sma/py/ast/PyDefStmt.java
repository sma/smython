/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;
import sma.py.rt.PyUserFunction;

/**
 * Represents the <code>def</code> statement to define functions and methods, see §7.5.
 */
public class PyDefStmt extends PyStmt {
  private final PyString name;
  private final PyParamList parameters;
  private final PySuite suite;

  public PyDefStmt(PyString name, PyParamList parameters, PySuite suite) {
    this.name = name;
    this.parameters = parameters;
    this.suite = suite;
  }

  @Override
  public String toString() {
    return "def " + name + "(" + parameters + "): " + suite;
  }

  @Override
  public void execute(PyFrame frame) {
    frame.bind(name, new PyUserFunction(
      name,
      parameters.nargs,
      parameters.names,
      (PyTuple) parameters.inits.eval(frame),
      parameters.rest,
      parameters.kwrest,
      suite));
  }

}
