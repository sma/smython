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
      if (name == __NAME__) {
        return name;
      }
      if (name == __BASES__) {
        return bases;
      }
      if (name == __DICT__) {
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
    if (name == __NAME__) {
      this.name = (PyString) value;
    } else if (name == __BASES__) {
      bases = (PyTuple) value;
    } else if (name == __DICT__) {
      dict = (PyDict) value;
    } else {
      dict.setItem(name, value);
    }
  }

  @Override
  public void delAttr(PyString name) {
    dict.delItem(name);
  }
  
  

  @Override
  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    PyInstance inst = new PyInstance(this);
    inst.init(frame, positionalArguments, keywordArguments);
    return inst;
  }
}
