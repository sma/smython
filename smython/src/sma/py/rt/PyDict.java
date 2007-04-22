/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PyDict extends PyMapping implements Iterable<Entry<PyObject, PyObject>> {
  private final Map<PyObject, PyObject> dict;
  
  public PyDict() {
    this(new HashMap<PyObject, PyObject>());
  }

  public PyDict(int capacity) {
    this(new HashMap<PyObject, PyObject>(capacity));
  }

  public PyDict(Map<PyObject,PyObject> dict) {
    this.dict = dict;
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof PyDict && dict.equals(((PyDict) obj).dict));
  }

  @Override
  public int hashCode() {
    return dict.hashCode();
  }

  @Override
  public Iterator<Entry<PyObject, PyObject>> iterator() {
    return dict.entrySet().iterator();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append('{');
    if (dict.size() > 0) {
      List<PyObject> keys = new ArrayList<PyObject>(dict.keySet());
      Collections.sort(keys);
      boolean first = true;
      for (PyObject key : keys) {
        if (first) {
          first = false;
        } else {
          b.append(", ");
        }
        b.append(key);
        b.append(": ");
        b.append(dict.get(key));
      }
    }
    b.append('}');
    return b.toString();
  }

  public int size() {
    return dict.size();
  }

  // --------------------------------------------------------------------------------------------------------

  // --------------------------------------------------------------------------------------------------------

  @Override
  public PyObject len() {
    return make(dict.size());
  }

  @Override
  public PyObject getItem(PyObject key) {
    return dict.get(key);
  }

  @Override
  public void setItem(PyObject key, PyObject value) {
    dict.put(key, value);
  }

  @Override
  public void delItem(PyObject key) {
    dict.remove(key);
  }

  @Override
  public boolean hasItem(PyObject key) {
    return dict.containsKey(key);
  }
}
