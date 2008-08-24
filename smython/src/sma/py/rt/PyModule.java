/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public class PyModule extends PyObject {
  private PyDict dict;

  public PyModule(PyDict dict) {
    this.dict = dict;
  }

  public PyDict getDict() {
    return dict;
  }

  private PyString getName() {
    return (PyString) dict.get(__NAME__);
  }

  private PyString getFile() {
    return (PyString) dict.get(intern("__file__"));
  }

  @Override
  public String toString() {
    PyString file = getFile();
    return "<module " + getName() + (file == null ? " (built-in)" : " from " + file) + ">";
  }

  @Override
  public PyObject getAttr(PyString name) {
    if (name == __DICT__) {
      return this.dict;
    }
    PyObject value = dict.getItem(name);
    if (value != null) {
      return value;
    }
    throw Py.attributeError(name);
  }

  @Override
  public void setAttr(PyString name, PyObject value) {
    if (name == __DICT__) {
      this.dict = (PyDict) value;
    } else {
      dict.setItem(name, value);
    }
  }

  @Override
  public void delAttr(PyString name) {
    if (name == __DICT__) {
      throw Py.typeError("readonly attribute");
    }
    dict.delItem(name);
  }
}
