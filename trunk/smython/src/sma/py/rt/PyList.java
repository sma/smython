/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PyList extends PySeq {
  private final List<PyObject> list;

  public PyList() {
    this(new ArrayList<PyObject>());
  }

  public PyList(int capacity) {
    this(new ArrayList<PyObject>(capacity));
  }

  public PyList(PyObject... elements) {
    this(elements.length);
    list.addAll(Arrays.asList((PyObject[]) elements));
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
      // TODO implement comparison of lists... but how?
      return len1 - len2;
    }
    return super.compareTo(o);
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

  public int size() {
    return list.size();
  }

  public PyObject get(int index) {
    return list.get(index);
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
    try {
      return list.get(index);
    } catch (IndexOutOfBoundsException e) {
      throw Py.indexError(key);
    }
  }

  @Override
  public void setItem(PyObject key, PyObject value) {
    int index = key.as_int();
    if (index < 0) {
      index += list.size();
    }
    try {
      list.set(index, value);
    } catch (IndexOutOfBoundsException e) {
      throw Py.indexError(key);
    }
  }

  @Override
  public void delItem(PyObject key) {
    int index = key.as_int();
    if (index < 0) {
      index += list.size();
    }
    try {
      list.remove(index);
    } catch (IndexOutOfBoundsException e) {
      throw Py.indexError(key);
    }
  }

  @Override
  public boolean hasItem(PyObject key) {
    return list.contains(key);
  }

  @Override
  public PyObject getSlice(PyObject left, PyObject right) {
    int length = list.size();
    int leftIndex = left.as_int();
    if (leftIndex < 0) {
      leftIndex += length;
      if (leftIndex < 0) {
        leftIndex = 0;
      }
    } else if (leftIndex > length) {
      leftIndex = length;
    }
    int rightIndex = right.as_int();
    if (rightIndex < 0) {
      rightIndex += length;
      if (rightIndex < 0) {
        rightIndex = 0;
      }
    } else if (rightIndex > length) {
      rightIndex = length;
    }
    if (rightIndex - leftIndex < 1) {
      return new PyList();
    }
    return new PyList(list.subList(leftIndex, rightIndex));
  }

  @Override
  public void setSlice(PyObject left, PyObject right, PyObject value) {
    int length = list.size();
    int leftIndex = left.as_int();
    if (leftIndex < 0) {
      leftIndex += length;
      if (leftIndex < 0) {
        leftIndex = 0;
      }
    } else if (leftIndex > length) {
      leftIndex = length;
    }
    int rightIndex = right.as_int();
    if (rightIndex < 0) {
      rightIndex += length;
      if (rightIndex < 0) {
        rightIndex = 0;
      }
    } else if (rightIndex > length) {
      rightIndex = length;
    }
    int count = rightIndex - leftIndex;
    if (count >= length) {
      list.clear();
    } else {
      while (count-- > 0) {
        list.remove(leftIndex);
      }
    }
    PyIterator iter = value.iter();
    PyObject obj;
    while ((obj = iter.next()) != null) {
      list.add(leftIndex, obj);
    }
  }

  @Override
  public void delSlice(PyObject left, PyObject right) {
    int length = list.size();
    int leftIndex = left.as_int();
    if (leftIndex < 0) {
      leftIndex += length;
      if (leftIndex < 0) {
        leftIndex = 0;
      }
    } else if (leftIndex > length) {
      leftIndex = length;
    }
    int rightIndex = right.as_int();
    if (rightIndex < 0) {
      rightIndex += length;
      if (rightIndex < 0) {
        rightIndex = 0;
      }
    } else if (rightIndex > length) {
      rightIndex = length;
    }
    int count = rightIndex - leftIndex;
    if (count >= length) {
      list.clear();
    } else {
      while (count-- > 0) {
        list.remove(leftIndex);
      }
    }
  }

  @Override
  public PyIterator iter() {
    return new PyIterator() {
      private Iterator<PyObject> iterator = list.iterator();
      public PyObject next() {
        if (iterator.hasNext()) {
          return iterator.next();
        }
        return null;
      }
    };
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
