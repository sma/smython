/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.ast;

import java.util.List;

import sma.py.rt.PyFrame;
import sma.py.rt.PyObject;

/**
 * Abstract base class for all abstract syntax tree nodes.
 */
public abstract class PyNode {
  /**
   * Evaluates the expression node in the context of the given frame and returns the result of the
   * evaluation.
   */
  public abstract PyObject eval(PyFrame frame);

  protected static String list(List<?> list, String delim) {
    StringBuilder b = new StringBuilder();
    if (delim.length() > 1) {
      b.append(delim.charAt(1));
    }
    for (int i = 0; i < list.size(); i++) {
      if (i > 0 && delim.length() > 0) {
        b.append(delim.charAt(0)).append(' ');
      }
      b.append(list.get(i));
    }
    if (delim.length() > 2) {
      b.append(delim.charAt(2));
    }
    return b.toString();
  }

}
