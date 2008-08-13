/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyString;

import java.util.List;

/**
 * Represents the <code>global</code> statement, see §6.12.
 */
public class PyGlobalStmt extends PyStmt {

  private final List<PyString> names;

  public PyGlobalStmt(List<PyString> names) {
    this.names = names;
  }

  @Override
  public String toString() {
    return "global " + list(names, ",");
  }

  @Override
  public void execute(PyFrame frame) {
  }

}
