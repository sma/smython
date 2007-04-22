/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.ArrayList;
import java.util.List;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a suite of statements, see §7.
 */
public class PySuite extends PyStmt {
  private final List<PyStmt> stmts = new ArrayList<PyStmt>();

  public void add(PyStmt stmt) {
    stmts.add(stmt);
  }

  @Override
  public String toString() {
    return list(stmts, ";«»");
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyObject result = PyObject.None;
    for (PyStmt stmt : stmts) {
      result = stmt.eval(frame);
    }
    return result;
  }

  @Override
  public void execute(PyFrame frame) {
    for (PyStmt stmt : stmts) {
      stmt.execute(frame);
    }
  }

}
