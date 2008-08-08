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
  private final PyExpr primary; // must evaluate to a callable
  private final PyExprList args; // will evaluate to a tuple
  private final PyExprList kwargs; // will evaluate to a dict
  private PyExpr restArgs; // must evaluate to a sequence
  private PyExpr restKwargs; // must evaluate to a dict

  public PyCall(PyExpr primary) {
    this.primary = primary;
    this.args = new PyExprList();
    this.kwargs = new PyExprList();
  }

  public void addArg(PyExpr expr) {
    args.add(expr);
  }

  public void addKwarg(PyExpr expr) {
    kwargs.add(expr);
  }

  public void setRestArgs(PyExpr expr) {
    restArgs = expr;
  }

  public void setRestKwargs(PyExpr expr) {
    restKwargs = expr;
  }

  @Override
  public String toString() {
    return primary + "(" + args + "/" + kwargs + ")";
  }

  @Override
  public PyObject eval(PyFrame frame) {
    if (restArgs != null || restKwargs != null) {
      throw new UnsupportedOperationException();
    }

    PyObject callable = primary.eval(frame);
    
    PyTuple positionalArguments = args.evalAsTuple(frame);
    PyDict keywordArguments = kwargs.evalAsDictionary(frame);
    return callable.apply(frame, positionalArguments, keywordArguments);
  }

}