/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public abstract class PyCallable extends PyObject {
  @Override
  public PyObject call(PyFrame frame, PyInstance self, PyObject... arguments) {
    PyObject[] narguments = new PyObject[arguments.length + 1];
    narguments[0] = self;
    System.arraycopy(arguments, 0, narguments, 1, arguments.length);
    return apply(frame, new PyTuple(narguments), new PyDict()); // TODO support keyword arguments
  }
}
