/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import sma.py.ast.PySuite;

public class PyUserFunction extends PyFunction {
  private final PyDict globals;
  private final PyString name;
  private final int nargs;
  private final PyTuple params;
  private final PyTuple defaults;
  private final PyString rest;
  private final PyString kwrest;
  private final PySuite suite;

  public PyUserFunction(PyDict globals, PyString name, int nargs, PyTuple params, PyTuple defaults, PyString rest, PyString kwrest, PySuite suite) {
    this.globals = globals;
    this.name = name;
    this.nargs = nargs;
    this.params = params;
    this.defaults = defaults;
    this.rest = rest;
    this.kwrest = kwrest;
    this.suite = suite;
  }

  @Override
  public PyObject getAttr(PyString name) {
    if (name == __NAME__) return name;
    return super.getAttr(name);
  }
  
  @Override
  public String toString() {
    return "<function " + name.value() + ">";
  }

  @Override
  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    frame = new PyFrame(frame, new PyDict(), frame.getGlobals(), frame.getBuiltins());

    int n = Math.min(params.size() , positionalArguments.size());

    // bind as many positional arguments as possible
    for (int i = 0; i < n; i++) {
      PyString name = (PyString) params.get(i); //TODO - could be a PyTuple
      frame.setLocal(name, positionalArguments.get(i));
      if (keywordArguments.getItem(name) != null) {
        throw Py.typeError("multiple values for '" + name + "'");
      }
    }
    if (rest != null) {
      frame.setLocal(rest, positionalArguments.getSlice(make(n), positionalArguments.len()));
    } else if (positionalArguments.size() > n) {
      throw Py.typeError("to many arguments");
    }

    // bind keyword parameters, otherwise supply default
    for (int i = n; i < params.size(); i++) {
      PyString name = (PyString) params.get(i); //TODO - could be a PyTuple
      PyObject value = keywordArguments.getItem(name);
      if (value == null) {
        if (i < nargs) {
          throw Py.typeError("not enough parameters");
        }
        value = defaults.get(i - nargs);
      } else {
        keywordArguments.delItem(name);
      }
      frame.setLocal(name, value);
    }

    if (kwrest != null) {
      frame.setLocal(kwrest, keywordArguments);
    } else if (keywordArguments.size() > 0) {
      throw Py.typeError("to many keyword arguments");
    }

    try {
      suite.execute(frame);
    } catch (Py.ReturnSignal s) {
      return s.getResult();
    }
    return None;
  }

}