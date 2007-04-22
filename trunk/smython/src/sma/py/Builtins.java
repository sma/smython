/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import sma.py.rt.PyBuiltinFunction;
import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;
import sma.py.rt.PyInstance;
import sma.py.rt.PyInt;
import sma.py.rt.PyLong;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

public class Builtins {
  
  public static void register(PyFrame frame) {
    for (Method m : Builtins.class.getMethods()) {
      Builtin b = m.getAnnotation(Builtin.class);
      if (b != null) {
        register(frame, m, b.value());
      }
    }
  }
  
  private static void register(PyFrame frame, final Method method, String parameters) {
    frame.bind(PyObject.make(method.getName()), new PyBuiltinFunction() {
      @Override
      public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
        return invoke(positionalArguments.get(0));
      }

      private PyObject invoke(PyObject argument) {
        try {
          return (PyObject) method.invoke(null, argument);
        } catch (IllegalArgumentException e) {
          throw new Error(e);
        } catch (IllegalAccessException e) {
          throw new Error(e);
        } catch (InvocationTargetException e) {
          if (e.getCause() instanceof RuntimeException) {
            throw (RuntimeException) e.getCause();
          }
          throw new Error(e);
        }
      }
    });
  }

  @Builtin("x")
  public static PyObject abs(PyObject obj) {
    return PyObject.None;
  }
  
  private static final PyString __ABS__ = PyObject.intern("__abs__");
  
  public static PyObject abs(PyInt obj) {
    return obj.abs();
  }
  
  public static PyObject abs(PyLong obj) {
    return obj.abs();
  }
  
  public static PyObject abs(PyInstance obj) {
    return obj.getAttr(__ABS__).call(obj);
  }

  public @interface Builtin {
    String value();
  }
}
