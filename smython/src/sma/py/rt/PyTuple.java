/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.util.Iterator;

public class PyTuple extends PyImmutableSeq implements Iterable<PyObject> {
  private final PyObject[] objects;
  private int hash;

  public PyTuple(PyObject... objects) {
    this.objects = objects;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof PyTuple) {
      Object[] o1 = objects;
      Object[] o2 = ((PyTuple) obj).objects;
      int len = o1.length;
      if (len != o2.length) {
        return false;
      }
      for (int i = 0; i < len; i++) {
        if (!o1[i].equals(o2[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = hash;
    if (h == 0) {
      for (int i = 0; i < objects.length; i++) {
        h = h << 3 ^ objects[i].hashCode();
      }
      hash = h;
    }
    return h;
  }

  @Override
  public int compareTo(PyObject o) {
    if (this == o) {
      return 0;
    }
    if (o instanceof PyTuple) {
      PyObject[] o1 = objects;
      PyObject[] o2 = ((PyTuple) o).objects;
      int len1 = o1.length;
      int len2 = o2.length;
      int n = Math.min(len1, len2);
      for (int i = 0; i < n; i++) {
        int c = o1[i].compareTo(o2[i]);
        if (c != 0) {
          return c;
        }
      }
      return len1 - len2;
    }
    return super.compareTo(o);
  }

  public int size() {
    return objects.length;
  }

  public PyObject get(int index) {
    return objects[index];
  }

  @Override
  public Iterator<PyObject> iterator() {
    return new Iterator<PyObject>() {
      private int index = 0;

      public boolean hasNext() {
        return index < objects.length;
      }

      public PyObject next() {
        return objects[index++];
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append('(');
    for (int i = 0; i < objects.length; i++) {
      if (i > 0) {
        b.append(',').append(' ');
      }
      b.append(objects[i]);
    }
    if (objects.length == 1) {
      b.append(',');
    }
    b.append(')');
    return b.toString();
  }

  PyObject[] objects() { //TODO should not reveal this information
    return objects;
  }

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyString repr() {
    return make(toString());
  }

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyObject len() {
    return make(objects.length);
  }

  @Override
  public PyObject getItem(PyObject key) {
    return objects[normalize(key)];
  }

  @Override
  public boolean hasItem(PyObject key) {
    for (PyObject obj : objects) {
      if (obj.equals(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public PyObject getSlice(PyObject left, PyObject right) {
    int leftIndex = normalize(left);
    int rightIndex = normalize(right);
    PyObject[] nobjects = new PyObject[rightIndex - leftIndex];
    System.arraycopy(objects, leftIndex, nobjects, 0, rightIndex - leftIndex);
    return new PyTuple(nobjects);
  }

  private int normalize(PyObject index) {
    int n = index.as_int();
    return n < 0 ? n + size() : n;
  }

  // --------------------------------------------------------------------------------------------------------

  PyTuple prepend(PyObject obj) {
    int length = objects.length;
    PyObject[] nobjects = new PyObject[length + 1];
    nobjects[0] = obj;
    System.arraycopy(objects, 0, nobjects, 1, length);
    return new PyTuple(nobjects);
  }

  PyTuple append(PyObject obj) {
    int length = objects.length;
    PyObject[] nobjects = new PyObject[length + 1];
    System.arraycopy(objects, 0, nobjects, 0, length);
    nobjects[length] = obj;
    return new PyTuple(nobjects);
  }



}
