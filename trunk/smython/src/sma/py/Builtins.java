/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
  
  public static void register(PyDict dict) {
    for (Method m : Builtins.class.getMethods()) {
      Builtin b = m.getAnnotation(Builtin.class);
      if (b != null) {
        dict.setItem(PyObject.intern(b.value()), makeFunction(m));
      }
    }
  }
  
  private static PyBuiltinFunction makeFunction(final Method method) {
    return new PyBuiltinFunction() {
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
    };
  }

  @Builtin("abs")
  public static PyObject abs(PyObject obj) {
    return obj.abs();
  }

  @Builtin("chr")
  public static PyObject chr(PyInt val) {
    return PyObject.make(String.valueOf((char) val.value()));
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface Builtin {
    String value();
  }
}
