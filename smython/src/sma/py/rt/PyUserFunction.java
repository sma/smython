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
    frame = new PyFrame(frame, new PyDict(), globals);

    int n = Math.min(params.size() , positionalArguments.size());

    // bind as many positional arguments as possible
    for (int i = 0; i < n; i++) {
      bindArgument(params.get(i), positionalArguments.get(i), keywordArguments, frame);
    }

    // left over positional arguments are bound to a rest parameter
    if (rest != null) {
      frame.setLocal(rest, positionalArguments.getSlice(make(n), positionalArguments.len()));
    } else if (positionalArguments.size() > n) {
      throw Py.typeError("to many arguments");
    }

    // bind keyword parameters, otherwise supply default
    for (int i = n, size = params.size(); i < size; i++) {
      PyObject nameOrTuple = params.get(i);
      if (nameOrTuple instanceof PyString) {
        PyString name = (PyString) nameOrTuple;
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
      } else {
        PyTuple tuple = (PyTuple) nameOrTuple;
        PyTuple values = (PyTuple) keywordArguments.getItem(name); //TODO cast may fail
        if (values == null) {
          if (i < nargs) {
            throw Py.typeError("not enough parameters");
          }
          values = (PyTuple) defaults.get(i - nargs); //TODO cast may fail
        } else {
          keywordArguments.delItem(name);
        }
        for (int j = 0, len = tuple.size(); j < len; j++) {
          bindArgument(tuple.get(j), values.get(j), keywordArguments, frame);
        }
      }
    }

    // left over keyword arguments are bound to a rest parameter
    if (kwrest != null) {
      frame.setLocal(kwrest, keywordArguments);
    } else if (keywordArguments.size() > 0) {
      throw Py.typeError("too many keyword arguments");
    }

    // execute the function
    try {
      suite.execute(frame);
    } catch (Py.ReturnSignal s) {
      return s.getResult();
    }
    return None;
  }

  private static void bindArgument(PyObject nameOrTuple, PyObject argument, PyDict kwargs, PyFrame frame) {
    if (nameOrTuple instanceof PyString) {
      PyString name = (PyString) nameOrTuple;
      frame.setLocal(name, argument);
      if (kwargs.getItem(name) != null) {
        throw Py.typeError("multiple values for '" + name + "'");
      }
    } else {
      PyTuple tuple = (PyTuple) nameOrTuple;
      PyTuple values = (PyTuple) argument; //TODO cast may fail
      for (int j = 0, len = tuple.size(); j < len; j++) {
        bindArgument(tuple.get(j), values.get(j), kwargs, frame);
      }
    }
  }
}
