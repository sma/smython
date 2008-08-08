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
   * Converts the given list into a string that contains the string representation of the
   * list elements delimited by the first character of the also given delimiter string,
   * optionally enclosed in the second and third character of the delimiter string
   *
   * @param list a list of elements
   * @param delim if empty string, simply concatenate the elements' string representations;
   * if a single character, separate string represenations with that character followed by
   * a space character; otherwise prepend the second character and append the third character.
   * @return the list as string
   */
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
