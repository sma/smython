/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.math.BigInteger;

public class PyLong extends PyNumber {
  private final BigInteger value;

  PyLong(BigInteger value) {
    this.value = value;
  }

  public BigInteger value() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PyLong && value.equals(((PyLong) obj).value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public int compareTo(PyObject o) {
    if (this == o) {
      return 0;
    }
    if (o instanceof PyInt) {
      return value.compareTo(o.as_bigint());
    }
    if (o instanceof PyLong) {
      return value.compareTo(o.as_bigint());
    }
    return super.compareTo(o);
  }

  @Override
  public String toString() {
    return value + "L";
  }

  // ----------------------------------------------------------------------------------------------
  // common API

  @Override
  public PyString repr() {
    return make(toString());
  }

  @Override
  public PyInt cmp(PyObject other) {
    if (other instanceof PyInt) {
      return make(value.compareTo(BigInteger.valueOf(((PyInt) other).value())));
    }
    if (other instanceof PyLong) {
      return make(value.compareTo(((PyLong) other).value));
    }
    return super.cmp(other);
  }

  @Override
  public PyInt nonzero() {
    return make(value.equals(BigInteger.ZERO) ? 0 : 1);
  }

  // ----------------------------------------------------------------------------------------------
  // arithmetic API

  @Override
  public PyObject add(PyObject other) {
    if (other instanceof PyInt) {
      return make(other.as_bigint().add(value));
    }
    if (other instanceof PyLong) {
      return make(value.add(other.as_bigint()));
    }
    return super.add(other);
  }

  @Override
  public PyObject sub(PyObject other) {
    if (other instanceof PyInt) {
      return make(other.as_bigint().subtract(value));
    }
    if (other instanceof PyLong) {
      return make(value.subtract(other.as_bigint()));
    }
    return super.add(other);
  }

  @Override
  public PyObject mul(PyObject other) {
    if (other instanceof PyInt) {
      return make(other.as_bigint().multiply(value));
    }
    if (other instanceof PyLong) {
      return make(value.multiply(other.as_bigint()));
    }
    return super.add(other);
  }

  @Override
  public PyObject div(PyObject other) {
    if (other instanceof PyInt) {
      return make(other.as_bigint().divide(value));
    }
    if (other instanceof PyLong) {
      return make(value.divide(other.as_bigint()));
    }
    return super.add(other);
  }

  @Override
  public PyObject mod(PyObject other) {
    if (other instanceof PyInt) {
      return make(other.as_bigint().mod(value));
    }
    if (other instanceof PyLong) {
      return make(value.mod(other.as_bigint()));
    }
    return super.add(other);
  }

  @Override
  public PyObject pow(PyObject other) {
    return make(value.pow(other.as_int()));
  }

  @Override
  public PyObject lshift(PyObject other) {
    return make(value.shiftLeft(other.as_int()));
  }

  @Override
  public PyObject rshift(PyObject other) {
    return make(value.shiftRight(other.as_int()));
  }

  @Override
  public PyObject and(PyObject other) {
    if (other instanceof PyNumber) {
      return make(value.and(other.as_bigint()));
    }
    return super.and(other);
  }

  @Override
  public PyObject xor(PyObject other) {
    if (other instanceof PyNumber) {
      return make(value.xor(other.as_bigint()));
    }
    return super.and(other);
  }

  @Override
  public PyObject or(PyObject other) {
    if (other instanceof PyNumber) {
      return make(value.or(other.as_bigint()));
    }
    return super.and(other);
  }
  
  @Override
  public PyObject abs() {
    return value.signum() == -1 ? make(value.negate()) : this;
  }

  @Override
  public PyObject neg() {
    return make(value.negate());
  }

  @Override
  public PyObject invert() {
    return make(value.not());
  }

  // ----------------------------------------------------------------------------------------------
  // private arithmetic API

  /*
  public PyObject add(PyLong other) {
    return make(value.add(other.value));
  }

  public PyObject sub(PyLong other) {
    return make(value.subtract(other.value));
  }

  public PyObject mul(PyLong other) {
    return make(value.multiply(other.value));
  }

  public PyObject div(PyLong other) {
    return make(value.divide(other.value));
  }

  public PyObject mod(PyLong other) {
    return make(value.mod(other.value));
  }

  public PyObject divmod(PyLong other) {
    BigInteger[] b = value.divideAndRemainder(other.value);
    return new PyTuple(make(b[0]), make(b[1]));
  }

  public PyObject lshift(PyLong other) {
    return make(value.shiftLeft(other.asInt()));
  }

  public PyObject rshift(PyLong other) {
    return make(value.shiftRight(other.asInt()));
  }

  public PyObject and(PyLong other) {
    return make(value.and(other.value));
  }

  public PyObject xor(PyLong other) {
    return make(value.xor(other.value));
  }

  public PyObject or(PyLong other) {
    return make(value.or(other.value));
  }

  public PyObject neg() {
    return make(value.negate());
  }

  public PyObject pos() {
    return this;
  }

  public PyObject abs() {
    return make(value.abs());
  }

  public PyObject invert() {
    return make(value.not());
  }

  // ----------------------------------------------------------------------------------------------

  //TODO check for overflow
  private int asInt() {
    return value.intValue();
  }
  */

  // ----------------------------------------------------------------------------------------------

  @Override
  protected BigInteger as_bigint() {
    return value;
  }

  @Override
  protected int as_int() {
    return value.intValue();
  }

  @Override
  public boolean truth() {
    return !value.equals(BigInteger.ZERO);
  }
}