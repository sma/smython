/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;
import sma.py.rt.PyTuple;

/**
 * Represents a call, see §5.3.4,
 */
public class PyCall extends PyExpr {
  private final PyExpr primary;
  private final PyExprList pargs;
  private final PyExprList kwargs;

  public PyCall(PyExpr primary, PyExprList[] lists) {
    this.primary = primary;
    this.pargs = lists[0];
    this.kwargs = lists[1];
  }

  @Override
  public String toString() {
    return primary + "(" + pargs + "/" + kwargs + ")";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyObject callable = primary.eval(frame);
    
    PyTuple positionalArguments = pargs.evalAsTuple(frame);
    PyDict keywordArguments = kwargs.evalAsDictionary(frame);
    return callable.apply(frame, positionalArguments, keywordArguments);
  }

}
