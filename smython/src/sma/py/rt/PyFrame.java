/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

/**
 * Provides an execution context for {@code PyNode}s.
 */
public class PyFrame extends PyObject {
  private final PyFrame back;
  private final PyDict locals;
  private final PyDict globals;
  private final PyDict builtins;

  private static final PyString F_BACK = intern("f_back");
  private static final PyString F_LOCALS = intern("f_locals");
  private static final PyString F_GLOBALS = intern("f_globals");
  private static final PyString F_BUILTINS = intern("f_builtins");

  public PyFrame() {
    this(null, null, null, new PyDict());
  }

  /**
   * Constructs a new execution context. If "back" is {@code null}, a global context is created.
   * Otherwise a nested context is created which normally shares the dictionaries for global and
   * built-in variables with its ancestor. 
   */
  public PyFrame(PyFrame back, PyDict locals, PyDict globals, PyDict builtins) {
    this.back = back;
    if (back == null) {
      this.locals = this.globals = new PyDict();
    } else {
      this.locals = locals;
      this.globals = globals;
    }
    this.builtins = builtins;
  }

  public PyDict getLocals() {
    return locals;
  }

  public PyDict getGlobals() {
    return globals;
  }

  public PyDict getBuiltins() {
    return builtins;
  }

  @Override
  public PyObject getAttr(PyString name) {
    if (name == F_BACK) {
      return back != null ? back : None;
    }
    if (name == F_LOCALS) {
      return locals;
    }
    if (name == F_GLOBALS) {
      return globals;
    }
    if (name == F_BUILTINS) {
      return builtins;
    }
    throw Py.attributeError(name);
  }

  /**
   * Returns the value of a local variable (if defined) or global or built-in variable.
   * If no such variable exists, a <code>NameError</code> is raised.
   */
  public PyObject getLocal(PyString name) {
    PyObject value = locals.getItem(name);
    if (value == null) {
      value = getGlobal(name);
    }
    return value;
  }

  /**
   * Updates or creates a local variable. The may shadow a global variable.
   */
  public void setLocal(PyString name, PyObject value) {
    locals.setItem(name, value);
  }

  /**
   * Deletes an existing local variable.
   * If no such variable exists, a <code>NameError</code> is raised.
   */
  public void delLocal(PyString name) {
    if (!locals.hasItem(name)) {
      throw Py.nameError(name);
    }
    locals.delItem(name);
  }

  /**
   * Return the value of a global or built-in variable.
   * If no such variable exists, a <code>NameError</code> is raised.
   */
  public PyObject getGlobal(PyString name) {
    PyObject value = globals.getItem(name);
    if (value == null) {
      value = builtins.getItem(name);
      if (value == null) {
        throw Py.nameError(name);
      }
    }
    return value;
  }

  /**
   * Updates or creates a global variable. This may shadow a built-in variable.
   */
  public void setGlobal(PyString name, PyObject value) {
    globals.setItem(name, value);
  }

  /**
   * Deletes an existing global variable. Built-in variables cannot be deleted.
   * If no such variable exists, a <code>NameError</code> is raised.
   */
  public void delGlobal(PyString name) {
    if (!globals.hasItem(name)) {
      throw Py.nameError(name);
    }
    globals.delItem(name);
  }

}
