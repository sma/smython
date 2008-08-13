/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyBuiltinFunction;
import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;
import sma.py.rt.PyList;
import sma.py.rt.PyModule;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

import java.util.List;

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
        PyList path = new PyList();
        path.append(PyObject.make("."));
        module.setAttr(PyObject.intern("path"), path);
        module.setAttr(PyObject.intern("modules"), sysModules);
        frame.setLocal(name, module);
        sysModules.setItem(name, module);
      }
      if (name.value().equals("time")) {
        PyModule module = new PyModule(new PyDict()); //TODO need to generalize
        module.setAttr(PyObject.intern("time"), new PyBuiltinFunction() {
          @Override
          public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
            return make(System.currentTimeMillis() / 1000);
          }
        });
        frame.setLocal(name, module);
        sysModules.setItem(name, module);
      }
    }
  }

  private static final PyDict sysModules = new PyDict();

}
