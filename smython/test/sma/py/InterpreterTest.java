/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import junit.framework.TestCase;

public class InterpreterTest extends TestCase {
  public void testEmpty() {
    assertNotNull(new Interpreter());
  }
}
