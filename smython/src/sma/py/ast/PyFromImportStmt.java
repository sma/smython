/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.List;

import sma.py.rt.*;

/**
 * Represents the <code>import</code> statement, see §6.11.
 */
public class PyFromImportStmt extends PyStmt {
  private final PyString module;
  private final List<PyString> names;

  public PyFromImportStmt(PyString module, List<PyString>names) {
    this.module = module;
    this.names = names;
  }

  @Override
  public String toString() {
    return "from " + module + " import " + (names == null ? "*" : list(names, ","));
  }

  @Override
  public void execute(PyFrame frame) {
    if (module.value().equals("sys")) { //TODO need to generalize
      PyModule module = new PyModule(new PyDict());
      module.setAttr(PyObject.intern("__name__"), module);
      module.setAttr(PyObject.intern("maxint"), PyObject.make(2147483647));
      for (PyString name : names) {
        frame.bind(name, module.getAttr(name));
      }
    }
  }

}
