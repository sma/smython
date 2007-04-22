/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public class PyInstance extends PyCallable {
  private PyClass clasz;
  private PyDict dict;

  public PyInstance(PyClass clasz) {
    this.clasz = clasz;
    this.dict = new PyDict();
  }
  
  @Override
  public String toString() {
    return "<" + clasz.getAttr(__NAME__) + " object>";
  }

  public void init(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    PyObject f = clasz.getAttr0(intern("__init__"));  //TODO constant
    if (f != null) {
      f.apply(frame, positionalArguments.prepend(this), keywordArguments);
    } else if (positionalArguments.size() > 0 || keywordArguments.size() > 0) {
      throw typeError("constructor takes no arguments"); //TODO
    }
  }

  @Override
  public PyString repr() {
    PyObject f = clasz.getAttr0(intern("__repr__")); //TODO constant
    if (f != null) {
      return (PyString) f.call(this); //TODO cast
    }
    return make("<" + clasz.str() + " instance>");
  }

  @Override
  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    return clasz.getAttr(intern("__call__")).apply(frame, positionalArguments, keywordArguments); //TODO constant
  }

  // ...

  @Override
  public PyObject getAttr(PyString name) {
    if (name == __CLASS__) {
      return clasz;
    }
    if (name == __DICT__) {
      return dict;
    }
    PyObject o = dict.getItem(name);
    if (o == null) {
      o = clasz.getAttr0(name);
    }
    if (o == null) {
      PyObject f = clasz.getAttr(__GETATTR__);
      if (f != null) {
        return f.call(this, name);
      }
      throw attributeError(name);
    }
    if (o instanceof PyFunction) {
      o = new PyMethod((PyFunction) o, this);
    }
    return o;
  }

  @Override
  public void setAttr(PyString name, PyObject value) {
    if (name == __CLASS__) {
      clasz = (PyClass) value; //TODO cast
    } else if (name == __DICT__) {
      dict = (PyDict) value; //TODO cast
    }
    PyObject f = clasz.getAttr0(__SETATTR__);
    if (f != null) {
      f.call(this, name, value);
      return;
    }
    dict.setItem(name, value);
  }

  @Override
  public void delAttr(PyString name) {
    PyObject f = clasz.getAttr0(__DELATTR__);
    if (f != null) {
      f.call(this, name);
      return;
    }
    dict.delItem(name);
  }
  
  @Override
  public PyObject abs() {
    return clasz.getAttr(intern("__abs__")).call(this); //TODO constant
  }
  
  // --------------------------------------------------------------------------------------------------------

  @Override
  public boolean truth() {
    PyObject f = clasz.getAttr0(intern("__nonzero__")); //TODO constant
    if (f != null) {
      return f.call(this).truth();
    }
    f = clasz.getAttr0(intern("__len__")); //TODO constant
    if (f != null) {
      return f.call(this).truth();
    }
    return true;
  }
}
