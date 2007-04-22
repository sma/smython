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
  private final PyString module;
  private final List<PyString> names;

  public PyImportStmt(PyString module, List<PyString>names) {
    this.module = module;
    this.names = names;
  }

  @Override
  public String toString() {
    if (module == null) {
      return "import " + names;
    }
    return "from " + module + " import " + (names == null ? "*" : names);
  }

  @Override
  public void execute(PyFrame frame) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}
