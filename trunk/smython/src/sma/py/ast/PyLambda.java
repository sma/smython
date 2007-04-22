/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;
import sma.py.rt.PyUserFunction;

/**
 * Represents the <code>lambda</code> expression, see §5.10.
 */
public class PyLambda extends PyExpr {
  private final PyParamList parameters;
  private final PyExpr expr;

  public PyLambda(PyParamList parameters, PyExpr expr) {
    this.parameters = parameters;
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "lambda " + parameters + ": " + expr;
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyExprList list = new PyExprList();
    list.add(expr);
    PySuite suite = new PySuite();
    suite.add(new PyReturnStmt(list));

    return new PyUserFunction(
      PyObject.intern("<lambda>"), //TODO
      parameters.nargs,
      parameters.names,
      parameters.inits.evalAsTuple(frame),
      parameters.rest,
      parameters.kwrest,
      suite);
  }

}
