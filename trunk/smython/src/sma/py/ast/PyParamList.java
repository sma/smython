/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

/**
 * Represents a parameter list definition of a {@code def} statement or {@code lambda} expression.
 * @see PyDefStmt
 * @see PyLambda
 */
public class PyParamList {
  final int nargs;
  final PyTuple names;
  final PyString rest;
  final PyString kwrest;
  final PyExprList inits;

  public PyParamList(int nargs, PyTuple names, PyString rest, PyString kwrest, PyExprList inits) {
    this.nargs = nargs;
    this.names = names;
    this.rest = rest;
    this.kwrest = kwrest;
    this.inits = inits;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < names.size(); i++) {
      if (i > 0) {
        b.append(",");
      }
      b.append(names.get(i));
      if (i >= nargs) {
        b.append("=").append(inits.get(i - nargs));
      }
    }
    if (rest != null) {
      if (names.size() > 0) {
        b.append(",");
      }
      b.append("*").append(rest.value());
    }
    if (kwrest != null) {
      if (names.size() > 0 || rest != null) {
        b.append(",");
      }
      b.append("**").append(kwrest.value());
    }
    return b.toString();
  }

}
