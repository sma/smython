/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public class PyClass extends PyCallable {
  private PyString name;
  private PyTuple bases;
  private PyDict dict;

  public PyClass(PyString name, PyTuple bases, PyDict dict) {
    this.name = name;
    this.bases = bases;
    this.dict = dict;
  }

  @Override
  public String toString() {
    return "<class " + name + ">"; 
  }
  
  @Override
  public PyObject getAttr(PyString name) {
    PyObject value = getAttr0(name);
    if (value == null) {
      if (__NAME__.equals(name)) {
        return name;
      }
      if (__BASES__.equals(name)) {
        return bases;
      }
      if (__DICT__.equals(name)) {
        return dict;
      }
      throw Py.attributeError(name);
    }
    if (value instanceof PyFunction) {
      value = new PyMethod((PyFunction) value, null);
    }
    return value;
  }

  PyObject getAttr0(PyString name) {
    PyObject value = dict.getItem(name);
    if (value == null) {
      for (PyObject b : bases) {
        //XXX get rid of cast in next line
        if ((value = ((PyClass) b).getAttr0(name)) != null) {
          return value;
        }
      }
    }
    return value;
  }

  @Override
  public void setAttr(PyString name, PyObject value) {
    if (name.equals(__NAME__)) {
      try {
        this.name = (PyString) value;
      } catch (ClassCastException e) {
        throw Py.typeError("__name__ must be a string object");
      }
    } else if (name.equals(__BASES__)) {
      try {
        bases = (PyTuple) value;
      } catch (ClassCastException e) {
        throw Py.typeError("__bases__ must be a tuple object");
      }
    } else if (name.equals(__DICT__)) {
      try {
        dict = (PyDict) value;
      } catch (ClassCastException e) {
        throw Py.typeError("__dict__ must be a dictionary object");
      }
    } else {
      dict.setItem(name, value);
    }
  }

  @Override
  public void delAttr(PyString name) {
    dict.delItem(name); //TODO throw attribute error if attribute does not exist
  }

  @Override
  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    PyInstance inst = new PyInstance(this);
    inst.init(frame, positionalArguments, keywordArguments);
    return inst;
  }

  @Override
  public boolean exceptionType() {
    return true;
  }

  @Override
  public boolean exceptionMatches(PyObject object) {
    if (object == this) {
      return true;
    }
    if (object instanceof PyClass) {
      for (PyObject c : ((PyClass) object).bases) {
        if (exceptionMatches(c)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isInstance(PyObject object) {
    return object instanceof PyInstance && exceptionMatches(((PyInstance) object).getClasz());
  }
}
