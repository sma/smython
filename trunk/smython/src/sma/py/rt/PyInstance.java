/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.util.Iterator;

public class PyInstance extends PyCallable implements Iterable<PyObject> {
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
    return "<" + clasz.getAttr(__NAME__) + " object>";
  }

  public void init(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    PyObject f = clasz.getAttr0(intern("__init__"));  //TODO constant
    if (f != null) {
      f.apply(frame, positionalArguments.prepend(this), keywordArguments);
    } else if (positionalArguments.size() > 0 || keywordArguments.size() > 0) {
      throw Py.typeError("constructor takes no arguments"); //TODO
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
    return f == null || f.call(this).truth();
  }

  // --------------------------------------------------------------------------------------------------------

  public Iterator<PyObject> iterator() {
    return new Iterator<PyObject>() {
      private int index;
      private boolean end;
      private PyObject value;

      public boolean hasNext() {
        if (!end) {
          if (value == null) {
            try {
              value = getItem(make(index++));
            } catch (Py.RaiseSignal s) {
              if ("IndexError".equals(s.getException().str().value())) {
                end = true;
              }
            }
          }
        }
        return !end;
      }

      public PyObject next() {
        try {
          return value;
        } finally {
          value = null;
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public PyObject getItem(PyObject key) {
    PyObject f = clasz.getAttr0(intern("__getitem__")); //TODO constant
    if (f != null) {
      return f.call(this, key);
    }
    return super.getItem(key);
  }

  @Override
  public PyObject len() {
    PyObject f = clasz.getAttr0(intern("__len__")); //TODO constant
    if (f != null) {
      return f.call(this);
    }
    return super.len();
  }

  @Override
  public boolean exceptionType() {
    return true;
  }
}
