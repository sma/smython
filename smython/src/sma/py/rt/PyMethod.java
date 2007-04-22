/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

public class PyMethod extends PyCallable {
  private final PyFunction func;
  private final PyInstance self;

  public PyMethod(PyFunction func, PyInstance self) {
    this.func = func;
    this.self = self;
  }

  @Override
  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    if (self == null) {
      // unbound method, first positional argument must be instanceof method's class
      // TODO check that first argument is of correct type
      return func.apply(frame, positionalArguments, keywordArguments);
    } else {
      // bound method, function is called with self as the first parameter
      return func.apply(frame, positionalArguments.prepend(self), keywordArguments);
    }
  }

}
