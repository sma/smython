/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

/**
 * Represents a slice or subscript as part of a slicing.
 * @see PySlicing
 */
public class PySubscript {
  private final boolean ellipsis;
  private final PyExpr single;
  private final PyExpr left;
  private final PyExpr right;
  private final PyExpr stride;

  public PySubscript() {
    ellipsis = true;
    single = left = right = stride = null;
  }

  /** Single subscription or sliceItem. */
  public PySubscript(PyExpr single) {
    this.ellipsis = false;
    this.single = single;
    this.left = right = stride = null;
  }

  /** Short slice or long slice as part of a slicelist. */
  public PySubscript(PyExpr left, PyExpr right, PyExpr stride) {
    this.ellipsis = false;
    this.single = null;
    this.left = left;
    this.right = right;
    this.stride = stride;
  }

  public boolean isEllipsis() {
    return ellipsis;
  }

  public PyExpr getSingle() {
    return single;
  }

  public PyExpr getLeft() {
    return left;
  }

  public PyExpr getRight() {
    return right;
  }

  public PyExpr getStride() {
    return stride;
  }

  @Override
  public String toString() {
    if (isEllipsis()) {
      return "...";
    }
    if (single != null) {
      return single.toString();
    }
    return (left != null ? left : "") + ":" + (right != null ? right : "") + (stride != null ? ":" + stride : "");
  }
}
