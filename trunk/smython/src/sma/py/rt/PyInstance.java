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

  public PyClass getClasz() {
    return clasz;
  }
  
  @Override
  public String toString() {
    return repr().value();
  }

  public void init(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    PyObject f = clasz.getAttr0(S__INIT__);
    if (f != null) {
      f.apply(frame, positionalArguments.prepend(this), keywordArguments);
    } else if (positionalArguments.size() > 0 || keywordArguments.size() > 0) {
      throw Py.typeError("constructor takes no arguments");
    }
  }

  @Override
  public PyString repr() {
    PyObject f = clasz.getAttr0(S__REPR__);
    if (f != null) {
      f = f.call(null, this);
      try {
        return (PyString) f;
      } catch (ClassCastException e) {
        throw Py.typeError("__repr__ returned non-string");
      }
    }
    return make("<" + clasz.str() + " instance>");
  }

  @Override
  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    return clasz.getAttr(S__CALL__).apply(frame, positionalArguments, keywordArguments);
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
        return f.call(null, this, name);
      }
      throw Py.attributeError(name);
    }
    if (o instanceof PyFunction) {
      o = new PyMethod((PyFunction) o, this);
    }
    return o;
  }

  @Override
  public void setAttr(PyString name, PyObject value) {
    if (name == __CLASS__) {
      try {
        clasz = (PyClass) value;
      } catch (ClassCastException e) {
        throw Py.typeError("__class__ must be a class object");
      }
    } else if (name == __DICT__) {
      try {
        dict = (PyDict) value;
      } catch (ClassCastException e) {
        throw Py.typeError("__dict__ must be a dictionary object");
      }
    }
    PyObject f = clasz.getAttr0(__SETATTR__);
    if (f != null) {
      f.call(null, this, name, value);
      return;
    }
    dict.setItem(name, value);
  }

  @Override
  public void delAttr(PyString name) {
    PyObject f = clasz.getAttr0(__DELATTR__);
    if (f != null) {
      f.call(null, this, name);
      return;
    }
    dict.delItem(name);
  }
  
  @Override
  public PyObject abs() {
    return clasz.getAttr(intern("__abs__")).call(null, this); //TODO constant
  }
  
  // --------------------------------------------------------------------------------------------------------

  @Override
  public boolean truth() {
    PyObject f = clasz.getAttr0(intern("__nonzero__")); //TODO constant
    if (f != null) {
      return f.call(null, this).truth();
    }
    f = clasz.getAttr0(intern("__len__")); //TODO constant
    return f == null || f.call(null, this).truth();
  }

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyIterator iter() {
    return new PyIterator() {
      private int index;

      public PyObject next() {
        try {
          return getItem(make(index++));
        } catch (Py.RaiseSignal s) {
          if ("IndexError".equals(s.getException().str().value())) {
            return null;
          }
          throw s;
        }
      }
    };
  }

  @Override
  public PyObject getItem(PyObject key) {
    PyObject f = clasz.getAttr0(intern("__getitem__")); //TODO constant
    if (f != null) {
      return f.call(null, this, key);
    }
    return super.getItem(key);
  }

  @Override
  public PyObject len() {
    PyObject f = clasz.getAttr0(intern("__len__")); //TODO constant
    if (f != null) {
      return f.call(null, this);
    }
    return super.len();
  }

  @Override
  public boolean exceptionType() {
    return true;
  }

  @Override
  public PyObject add(PyObject other) {
    return clasz.getAttr(intern("__add__")).call(null, this, other);
  }

  @Override
  public PyObject radd(PyObject other) {
    return clasz.getAttr(intern("__radd__")).call(null, this, other);
  }
}
