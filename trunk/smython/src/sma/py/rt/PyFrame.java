/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

/**
 * Provides an execution context for {@code PyNode}s.
 */
public class PyFrame extends PyObject {
  private final PyFrame back;
  //private final PyDict locals;
  private final PyDict globals;
  private final PyDict builtins;

  private static final PyString F_BACK = intern("f_back");
  private static final PyString F_LOCALS = intern("f_locals");
  private static final PyString F_GLOBALS = intern("f_globals");
  private static final PyString F_BUILTINS = intern("f_builtins");

  static class Link {
    final Link next;
    final PyString name;
    PyObject value;
    Link(Link next, PyString name, PyObject value) {
      this.next = next;
      this.name = name;
      this.value = value;
    }
  }
  private Link _locals;
  
  /**
   * Constructs a new execution context. If "back" is {@code null}, a global context is created.
   * Otherwise a nested context is created which inherits the global and builtin definitions from
   * the specified "back" frame.
   */
  public PyFrame(PyFrame back) {
    this.back = back;
    //this.locals = new PyDict();
    if (back != null) {
      this.globals = back.globals;
      this.builtins = back.builtins;
    } else {
      this.globals = new PyDict(); //locals;
      this.builtins = null; //locals;
    }
  }

  public PyFrame(PyDict gdict, PyDict ldict) { //TODO for exec
    this.back = null;
    //this.locals = ldict;
    this.globals = gdict;
    this.builtins = gdict;
  }

  public PyDict locals() {
    //return locals;
    PyDict dict = new PyDict();
    for (Link l = _locals; l != null; l = l.next) {
      dict.setItem(l.name, l.value);
    }
    return dict;
  }

  @Override
  public PyObject getAttr(PyString name) {
    if (name == F_BACK) {
      return back;
    }
    if (name == F_LOCALS) {
      return null; //locals;
    }
    if (name == F_GLOBALS) {
      return globals;
    }
    if (name == F_BUILTINS) {
      return builtins;
    }
    throw attributeError(name);
  }

  public PyObject lookup(PyString name) {
    Link l = find(name);
    if (l != null) return l.value;
    
    //PyObject obj = locals.getItem(name);
    //if (obj == null) {
      PyObject obj = globals.getItem(name);
    //}
    if (obj == null) {
      obj = builtins.getItem(name);
    }
    if (obj == null) {
      throw attributeError(name);
    }
    return obj;
  }

  public void bind(PyString name, PyObject value) {
    _locals = new Link(_locals, name, value);
    if (back == null) globals.setItem(name, value);
    //locals.setItem(name, value);
  }

  public void unbind(PyString name) {
    //locals.delItem(name);
  }
  
  Link find(PyString name) {
    for (Link l = _locals; l != null; l = l.next) {
      if (l.name == name) return l;
    }
    return null;
  }
}
