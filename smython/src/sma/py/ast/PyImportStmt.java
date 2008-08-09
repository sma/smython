/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.List;

import sma.py.rt.*;

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
    for (PyString name : modules) {
      if (name.value().equals("sys")) { //TODO need to generalize
        PyModule module = new PyModule(new PyDict());
        module.setAttr(PyObject.intern("__name__"), name);
        module.setAttr(PyObject.intern("maxint"), PyObject.make(2147483647));
        frame.setLocal(name, module);
      }
    }
  }

}
