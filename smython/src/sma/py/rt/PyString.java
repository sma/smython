/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;



public class PyString extends PyImmutableSeq {
  private final String str;

  public PyString(String str) {
    this.str = str;
  }

  public String value() {
    return str;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PyString && str.equals(((PyString) obj).str);
  }

  @Override
  public int hashCode() {
    return str.hashCode();
  }

  @Override
  public int compareTo(PyObject o) {
    if (o instanceof PyString) {
      return str.compareTo(((PyString) o).str);
    }
    return super.compareTo(o);
  }

  @Override
  public String toString() {
    if (str.indexOf('\'') != -1) {
      return '"' + str + '"';
    }
    return '\'' + str + '\'';
  }

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyString str() {
    return this;
  }

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyObject add(PyObject other) {
    if (other instanceof PyString) {
      if (str.length() == 0) {
        return other;
      }
      String otherStr = ((PyString) other).str;
      if (otherStr.length() == 0) {
        return this;
      }
      return make(str + otherStr);
    }
    return super.add(other);
  }
  
  @Override
  public PyObject mul(PyObject other) {
    if (other instanceof PyInt) {
      int i = other.as_int();
      if (i < 1) {
        return EmptyString;
      }
      if (i == 1) {
        return this;
      }
      StringBuilder b = new StringBuilder(str.length() * i);
      while (i > 0) {
        b.append(str);
        i--;
      }
      return make(b.toString());
    }
    return super.mul(other);
  }
  
  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyObject len() {
    return make(str.length());
  }

  @Override
  public PyObject getItem(PyObject key) {
    int index = key.as_int();
    if (index < 0) {
      index += str.length();
    }
    return new PyString(str.substring(index, index + 1));
  }

  @Override
  public boolean hasItem(PyObject key) {
    return str.contains(key.str().value());
  }

  @Override
  public PyObject getSlice(PyObject left, PyObject right) {
    int leftIndex = left.as_int();
    int rightIndex = right.as_int();
    return new PyString(str.substring(leftIndex, rightIndex));
  }

}
