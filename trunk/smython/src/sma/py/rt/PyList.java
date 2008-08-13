/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PyList extends PySeq implements Iterable<PyObject> {
  private final List<PyObject> list;

  public PyList() {
    this(new ArrayList<PyObject>());
  }

  public PyList(List<PyObject> list) {
    this.list = list;
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof PyList && list.equals(((PyList) obj).list));
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }

  @Override
  public int compareTo(PyObject o) {
    if (this == o) {
      return 0;
    }
    if (o instanceof PyList) {
      int len1 = list.size();
      int len2 = ((PyList) o).list.size();
      // TODO
      return len1 - len2;
    }
    return super.compareTo(o);
  }

  @Override
  public Iterator<PyObject> iterator() {
    return list.iterator();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append('[');
    for (int i = 0; i < list.size(); i++) {
      if (i > 0) {
        b.append(", ");
      }
      b.append(list.get(i));
    }
    b.append(']');
    return b.toString();
  }

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyObject len() {
    return make(list.size());
  }

  @Override
  public PyObject getItem(PyObject key) {
    int index = key.as_int();
    if (index < 0) {
      index += list.size();
    }
    return list.get(index);
  }

  @Override
  public void setItem(PyObject key, PyObject value) {
    int index = key.as_int();
    if (index < 0) {
      index += list.size();
    }
    list.set(index, value);
  }

  @Override
  public void delItem(PyObject key) {
    list.remove(key.as_int());
  }

  @Override
  public boolean hasItem(PyObject key) {
    return list.contains(key);
  }

  @Override
  public PyObject getSlice(PyObject left, PyObject right) {
    int leftIndex = left.as_int();
    int rightIndex = right.as_int();
    return new PyList(list.subList(leftIndex, rightIndex));
  }

  public List<PyObject> list() {
    return list;
  }

  public void append(PyObject object) {
    list.add(object);
  }

  @Override
  public PyObject getAttr(PyString name) {
    String n = name.value();
    if ("append".equals(n)) {
      return new PyBuiltinFunction() {
        @Override
        public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
          append(positionalArguments.get(0));
          return None;
        }
      };
    }
    return super.getAttr(name);
  }
}
