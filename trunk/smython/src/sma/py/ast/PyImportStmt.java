/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.List;

import sma.py.rt.PyFrame;
import sma.py.rt.PyString;

/**
 * Represents the <code>import</code> statement, see §6.11.
 */
public class PyImportStmt extends PyStmt {
  private final List<PyString> modules;

  public PyImportStmt(List<PyString> modules) {
    this.modules = modules;
  }

  @Override
  public String toString() {
    return "import " + list(modules, ",");
  }

  @Override
  public void execute(PyFrame frame) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}
