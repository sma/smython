/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.ArrayList;
import java.util.List;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Represents a sequence of comparisons, see §5.9.
 */
public class PyComparison extends PyExpr {
  private final PyExpr expr;
  private final List<Comp> comps = new ArrayList<Comp>();

  public PyComparison(PyExpr expr) {
    this.expr = expr;
  }

  public void add(Op op, PyExpr expr) {
    comps.add(new Comp(op, expr));
  }

  @Override
  public String toString() {
    return expr + list(comps, "");
  }

  @Override
  public PyObject eval(PyFrame frame) {
    PyObject left = expr.eval(frame);
    for (Comp c : comps) {
      PyObject right = c.expr.eval(frame);
      if (!comp(c.op, left, right)) {
        return PyObject.False;
      }
      left = right;
    }
    return PyObject.True;
  }

  private boolean comp(Op op, PyObject left, PyObject right) {
    switch (op) {
    case LT: return left.compareTo(right) < 0;
    case GT: return left.compareTo(right) > 0;
    case LE: return left.compareTo(right) <= 0;
    case GE: return left.compareTo(right) >= 0;
    case EQ: return left.compareTo(right) == 0;
    case NE: return left.compareTo(right) != 0;
    case IS: return left == right;
    case IS_NOT: return left != right;
    case IN: return right.hasItem(left);
    case NOT_IN: return !right.hasItem(left);
    default:
      throw new Error(); // cannot happen
    }
  }

  private static class Comp {
    final Op op;
    final PyExpr expr;

    Comp(Op op, PyExpr expr) {
      this.op = op;
      this.expr = expr;
    }

    @Override
    public String toString() {
      return " " + op() + " " + expr;
    }

    private String op() {
      switch (op) {
      case LT: return "<";
      case GT: return ">";
      case LE: return "<=";
      case GE: return ">=";
      case EQ: return "==";
      case NE: return "!=";
      case IN: return "in";
      case NOT_IN: return "not in";
      case IS: return "is";
      case IS_NOT: return "is not";
      default: throw new Error();
      }
    }
  }

  public static enum Op {
    LT, GT, LE, GE, EQ, NE, IN, NOT_IN, IS, IS_NOT;
  }

}
