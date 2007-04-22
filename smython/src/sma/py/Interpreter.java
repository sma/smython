/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import sma.py.rt.Py;
import sma.py.rt.PyBuiltinFunction;
import sma.py.rt.PyClass;
import sma.py.rt.PyDict;
import sma.py.rt.PyFrame;
import sma.py.rt.PyInstance;
import sma.py.rt.PyInt;
import sma.py.rt.PyList;
import sma.py.rt.PyLong;
import sma.py.rt.PyObject;
import sma.py.rt.PyString;
import sma.py.rt.PyTuple;

/**
 * Evaluates chunks of code in a global context, implementing a (subset of) a Python interpreter.
 * <p>
 * Contains a very experimental set of builtin functions!
 */
public class Interpreter {
  private final PyFrame frame = new PyFrame(null);
  
  public Interpreter() {
    init();
    Builtins.register(frame);
  }
  
  protected void init() {
    // register the None in the global environment (TODO it's the local one...)
    frame.bind(PyObject.intern("None"), PyObject.None);

    // register a native function... doesn't look nice... 
    register("type", new PyBuiltinFunction() {
      @Override
      public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
        PyObject o = positionalArguments.get(0);
        if (o instanceof PyInt) return intern("int");
        if (o instanceof PyLong) return intern("long");
        if (o instanceof PyClass) return intern("class");
        if (o instanceof PyInstance) return intern("instance");
        if (o instanceof PyTuple) return intern("tuple");
        if (o instanceof PyString) return intern("string");
        if (o instanceof PyList) return intern("list");
        if (o instanceof PyDict) return intern("dict");
        return intern("system");
      }
    });
    
    // register another function, init() becomes too long...
    register("tuple", new PyBuiltinFunction() {
      @Override
      public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
        PyObject o = positionalArguments.get(0);
        if (o instanceof PyTuple) return o;
        if (o instanceof PyString) {
          String s = ((PyString) o).value(); 
          PyObject[] objects = new PyObject[s.length()];
          for (int i = 0; i < objects.length; i++) {
            objects[i] = make(s.substring(i, i + 1));
          }
          return new PyTuple(objects);
        }
        if (o instanceof PyList) {
          List<PyObject> l = ((PyList) o).list();
          PyObject[] objects = new PyObject[l.size()];
          for (int i = 0; i < objects.length; i++) {
            objects[i] = l.get(i);
          }
          return new PyTuple(objects);
        }
        throw new Py.RaiseSignal(make("TypeError"), make("bad operand for tuple()"), null);
      }
    });

    // basic unit tests shouldn't require that this works...
    Reader reader = new InputStreamReader(getClass().getResourceAsStream("builtins.py"));
    try {
      try {
        eval(reader);
      } finally {
        reader.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void register(String name, PyObject value) {
    frame.bind(PyObject.intern(name), value);
  }

  public PyObject eval(Reader reader) throws IOException {
    reader = new BufferedReader(reader);
    StringBuilder b = new StringBuilder();
    int ch;
    while ((ch = reader.read()) != -1) {
      b.append((char) ch);
    }
    return eval(b.toString());
  }
  
  public PyObject eval(String source) {
    return new Parser(source).interactiveInput().eval(frame);
  }
  
  public static void main(String[] args) {
    Interpreter i = new Interpreter();
    i.eval("fib(28)");
    long t = System.currentTimeMillis();
    i.eval("fib(28)");
    System.out.println(System.currentTimeMillis() - t);
    t = System.currentTimeMillis();
    i.eval("fib(28)");
    System.out.println(System.currentTimeMillis() - t);
  }

}
