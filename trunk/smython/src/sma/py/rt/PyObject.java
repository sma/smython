/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for all Python runtime objects, see §3.2.
 */
public abstract class PyObject implements Comparable<PyObject> {

  // --------------------------------------------------------------------------------------------------------
  // intern strings as symbols

  private static final Map<String, PyStringReference> symbols = new HashMap<String, PyStringReference>();
  private static final ReferenceQueue<PyString> symbolsQueue = new ReferenceQueue<PyString>();

  public static synchronized PyString intern(String name) {
    PyStringReference ref = symbols.get(name);
    PyString str;
    if (ref == null || (str = ref.get()) == null) {
      while ((ref = (PyStringReference) symbolsQueue.poll()) != null) {
        symbols.remove(ref.name);
      }
      symbols.put(name, new PyStringReference(str = make(name), symbolsQueue));
    }
    return str;
  }

  private static class PyStringReference extends WeakReference<PyString> {
    private final String name;

    public PyStringReference(PyString str, ReferenceQueue<? super PyString> q) {
      super(str, q);
      this.name = str.value();
    }
  }

  // --------------------------------------------------------------------------------------------------------
  // predefined symbols

  protected static final PyString __BASES__ = intern("__bases__");
  protected static final PyString __CLASS__ = intern("__class__");
  protected static final PyString __DICT__ = intern("__dict__");
  protected static final PyString __NAME__ = intern("__name__");
  protected static final PyString __GETATTR__ = intern("__getattr__");
  protected static final PyString __SETATTR__ = intern("__setattr__");
  protected static final PyString __DELATTR__ = intern("__delattr__");

  // --------------------------------------------------------------------------------------------------------
  // object constructors

  private static final int INTS_MIN = -1024;
  private static final int INTS_MAX = 1024;
  private static final PyInt[] INTS = new PyInt[INTS_MAX - INTS_MIN];
  static {
    for (int i = 0; i < INTS.length; i++) {
      INTS[i] = new PyInt(i + INTS_MIN);
    }
  }

  public static PyInt make(int value) {
    return value >= INTS_MIN && value < INTS_MAX ? INTS[value - INTS_MIN] : new PyInt(value);
  }

  public static PyNumber make(long value) {
    return value < -2147483648L || value > 2147483647L ? make(BigInteger.valueOf(value)) : make((int) value);
  }

  public static PyLong make(BigInteger value) {
    return new PyLong(value);
  }

  private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
  private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);

  public static PyNumber makeIntOrLong(BigInteger value) {
    return value.compareTo(INT_MIN) < 0 || value.compareTo(INT_MAX) > 0  ? make(value) : make(value.intValue());
  }

  public static final PyString EmptyString = new PyString("");

  public static PyString make(String value) {
    return value.length() == 0 ? EmptyString : new PyString(value);
  }

  public static PyObject make(Double value) {
    return make(value.intValue()); //TODO implement floats
  }



  // --------------------------------------------------------------------------------------------------------
  // predefined constants

  public static final PyNone None = new PyNone();
  public static final PyInt True = make(1);
  public static final PyInt False = make(0);

  // --------------------------------------------------------------------------------------------------------
  // string representation, comparison, hashing and nonzero API

  /**
   * Returns the "official" string representation of this object.
   * Called by the builtin function {@code repr()} and string conversions (reverse quote).
   * Same as Java's {@link Object#toString()}.
   */
  public PyString repr() {
    return make(toString());
  }

  /**
   * Returns an "informal" string representation of this object.
   * Called by the builtin function {@code str()} and the {@code print} statement.
   */
  public PyString str() {
    return repr();
  }

  /**
   * Returns a negative integer if self &lt; other, zero if self == other, and a positive integer otherwise.
   * Called by the comparison operations. Same as Java's {@link Comparable#compareTo(Object)} method.
   */
  public PyInt cmp(PyObject other) {
    return make(compareTo(other));
  }

  /**
   * Returns a 32-bit integer usable as a hash value of this object for dictionary operations.
   * Objects, which compare equal must have the same hash value.
   * Called by the builtin function {@code hash()}. Same as Java' {@link Object#hashCode()} method.
   */
  public PyInt hash() {
    return make(hashCode());
  }

  /**
   * Returns 1 if this object should be considered as true for thruth value testings and 0 otherwise.
   * Called for testing the conditions of {@code if} and {@code while} statements.
   */
  public PyInt nonzero() {
    return truth() ? True : False;
  }

  // --------------------------------------------------------------------------------------------------------
  // attribute and sequence API

  public PyObject getAttr(PyString name) {
    throw new UnsupportedOperationException();
  }

  public void setAttr(PyString name, PyObject value) {
    throw new UnsupportedOperationException();
  }

  public void delAttr(PyString name) {
    throw new UnsupportedOperationException();
  }

  public PyObject call(PyInstance self, PyObject... arguments) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the length of a sizable object.
   * Called by the builtin function {@code len()}.
   */
  public PyObject len() {
    throw new UnsupportedOperationException();
  }

  public PyObject getItem(PyObject key) {
    throw new UnsupportedOperationException();
  }

  public void setItem(PyObject key, PyObject value) {
    throw new UnsupportedOperationException();
  }

  public void delItem(PyObject key) {
    throw new UnsupportedOperationException();
  }

  public boolean hasItem(PyObject key) {
    throw new UnsupportedOperationException();
  }

  public PyObject getSlice(PyObject left, PyObject right) {
    throw new UnsupportedOperationException();
  }

  public void setSlice(PyObject left, PyObject right, PyObject value) {
    throw new UnsupportedOperationException();
  }

  public void delSlice(PyObject left, PyObject right) {
    throw new UnsupportedOperationException();
  }

  // --------------------------------------------------------------------------------------------------------
  // arithmetic API

  public PyObject add(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject sub(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject mul(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject div(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject mod(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject divmod(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject pow(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject lshift(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject rshift(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject and(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject xor(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject or(PyObject other) {
    throw new UnsupportedOperationException();
  }

  public PyObject neg() {
    throw new UnsupportedOperationException();
  }

  public PyObject pos() {
    throw new UnsupportedOperationException();
  }

  public PyObject abs() {
    throw new UnsupportedOperationException();
  }

  public PyObject invert() {
    throw new UnsupportedOperationException();
  }

  public PyObject coerce(PyObject other) {
    throw new UnsupportedOperationException();
  }

  // --------------------------------------------------------------------------------------------------------
  // private coercion

  protected BigInteger as_bigint() {
    throw new UnsupportedOperationException();
  }

  protected int as_int() {
    throw new UnsupportedOperationException();
  }

  // --------------------------------------------------------------------------------------------------------
  // exceptions

  protected static RuntimeException attributeError(PyString name) {
    return new RuntimeException("AttributeError: '" + name + "' not found");
  }

  protected static RuntimeException typeError(String message) {
    return new RuntimeException("TypeError: " + message);
  }

  // --------------------------------------------------------------------------------------------------------
  // Java support

  public boolean truth() {
    return true;
  }

  @Override
  public int compareTo(PyObject o) {
    return equals(o) ? 0 : System.identityHashCode(this) - System.identityHashCode(o);
  }

  public PyObject apply(PyFrame frame, PyTuple positionalArguments, PyDict keywordArguments) {
    throw new UnsupportedOperationException();
  }
}
