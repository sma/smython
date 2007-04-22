/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.ArrayList;
import java.util.List;

import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;
import sma.py.rt.PyTuple;

/**
 * Represents a list of {@code PyExpr} nodes. An expression list knows whether it is an assignment
 * or deletion node and can either evaluate all expressions of the list as tuple, as a dictionary
 * or assign to those expressions. If the expression list has no trailing comma and just one 
 * element, the expression list evaluates to a single value and not a tuple.
 */
public class PyExprList extends PyNode {
  private final List<PyExpr> expressions;
  private boolean tuple;

  public PyExprList() {
    this.expressions = new ArrayList<PyExpr>();
  }

  public void add(PyExpr expr) {
    expressions.add(expr);
  }

  public boolean isTuple() {
    return tuple;
  }

  public void setTuple(boolean tuple) {
    this.tuple = tuple;
  }

  public int size() {
    return expressions.size();
  }

  public PyExpr get(int index) {
    return expressions.get(index);
  }

  @Override
  public String toString() {
    return list(expressions, ",") + (tuple ? "," : "");
  }

  /**
   * Evaluates all expression nodes in the context of the given frame and and returns a tuple with
   * all results of the evaluation.
   */
  public PyObject eval(PyFrame frame) {
    if (!tuple && expressions.size() == 1) {
      return expressions.get(0).eval(frame);
    }
    return evalAsTuple(frame);
  }
  
  public PyTuple evalAsTuple(PyFrame frame) {
    int size = expressions.size();
    PyObject[] objects = new PyObject[size];
    for (int i = 0; i < size; i++) {
      objects[i] = expressions.get(i).eval(frame);
    }
    return new PyTuple(objects);
    
  }
  
  public PyDict evalAsDictionary(PyFrame frame) {
    int size = expressions.size();
    PyDict dict = new PyDict(size / 2);
    for (int i = 0; i < size; i += 2) {
      dict.setItem(expressions.get(i).eval(frame), expressions.get(i + 1).eval(frame));
    }
    return dict;
  }
  

  /**
   * Returns whether all expression nodes can play the role of a target and
   * {@link #assign(PyFrame, PyObject)} or {@link #del(PyFrame)} can be called.
   */
  public boolean isTarget() {
    for (PyExpr expr : expressions) {
      if (!expr.isTarget()) {
        return false;
      }
    }
    return true;
  }

  public void assign(PyFrame frame, PyObject value) {
    if (!tuple && expressions.size() == 1) {
      expressions.get(0).assign(frame, value);
    } else {
      PyTuple tuple = (PyTuple) value;
      int size = tuple.size();
      if (expressions.size() < size) {
        throw new RuntimeException("too many values to unpack");
      }
      if (expressions.size() > size) {
        throw new RuntimeException("not enough values to unpack");
      }
      for (int i = 0; i < size; i++) {
        expressions.get(i).assign(frame, tuple.get(i));
      }
    }
  }

  public void del(PyFrame frame) {
    for (PyExpr expr : expressions) {
      expr.del(frame);
    }
  }

}
