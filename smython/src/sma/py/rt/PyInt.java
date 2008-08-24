/*
 * Copyright (c) 2007, Stefan Matthias Aust. All rights reserved. See LICENSE.txt.
 */
package sma.py.rt;

import java.math.BigInteger;

public class PyInt extends PyNumber {
  private final int value;

  PyInt(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PyInt && value == ((PyInt) obj).value;
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public int compareTo(PyObject o) {
    if (this == o) {
      return 0;
    }
    if (o instanceof PyInt) {
      return value - ((PyInt) o).value;
    }
    if (o instanceof PyLong) {
      return as_bigint().compareTo(o.as_bigint());
    }
    return super.compareTo(o);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  // ----------------------------------------------------------------------------------------------
  // common API

  @Override
  public PyInt cmp(PyObject other) {
    if (other instanceof PyInt) {
      return make(value - ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().compareTo(other.as_bigint()));
    }
    return super.cmp(other);
  }

  @Override
  public PyInt nonzero() {
    return value == 0 ? this : True;
  }

  // --------------------------------------------------------------------------------------------------------
  // arithmetic API

  @Override
  public PyObject add(PyObject other) {
    if (other instanceof PyInt) {
      return make((long) value + ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().add(other.as_bigint()));
    }
    return super.add(other);
  }

  @Override
  public PyObject sub(PyObject other) {
    if (other instanceof PyInt) {
      return make((long) value - ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().subtract(other.as_bigint()));
    }
    return super.sub(other);
  }

  @Override
  public PyObject mul(PyObject other) {
    if (other instanceof PyInt) {
      return make((long) value * ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().multiply(other.as_bigint()));
    }
    return super.mul(other);
  }

  @Override
  public PyObject div(PyObject other) {
    try {
      if (other instanceof PyInt) {
        long v = (long) value;
        long o = ((PyInt) other).value;
        if (v < 0) {
          if (o > 0) {
            v -= o - 1;
          }
        } else {
          if (o < 0) {
            v -= o + 1;
          }
        }
        return make(v / o);
      }
      if (other instanceof PyLong) {
        return make(PyLong.pydiv(as_bigint(), other.as_bigint()));
      }
      return super.div(other);
    } catch (ArithmeticException e) {
      throw Py.raise(intern("ZeroDivisionError"), None);
    }
  }

  @Override
  public PyObject mod(PyObject other) {
    try {
      if (other instanceof PyInt) {
        long v = (long) value;
        long o = ((PyInt) other).value;
        if (v < 0 ^ o < 0) {
          v += o;
        }
        return make(v % o);
      }
      if (other instanceof PyLong) {
        return make(PyLong.pymod(as_bigint(), other.as_bigint()));
      }
      return super.mod(other);
    } catch (ArithmeticException e) {
      throw Py.raise(intern("ZeroDivisionError"), None);
    }
  }

  @Override
  public PyObject pow(PyObject other) {
    BigInteger i = as_bigint().pow(other.as_int());
    return makeIntOrLong(i);
  }

  @Override
  public PyObject lshift(PyObject other) {
    int n = other.as_int();
    if (n < 0) {
      return make(value >> -n);
    }
    if (n == 0) {
      return this;
    }
    return makeIntOrLong(as_bigint().shiftLeft(n));
  }

  @Override
  public PyObject rshift(PyObject other) {
    int n = other.as_int();
    if (n < 0) {
      return makeIntOrLong(as_bigint().shiftLeft(-n));
    }
    if (n == 0) {
      return this;
    }
    return make(value >> n);
  }

  @Override
  public PyObject and(PyObject other) {
    if (other instanceof PyInt) {
      return make(value & ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().and(other.as_bigint()));
    }
    return super.and(other);
  }

  @Override
  public PyObject xor(PyObject other) {
    if (other instanceof PyInt) {
      return make(value ^ ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().xor(other.as_bigint()));
    }
    return super.and(other);
  }

  @Override
  public PyObject or(PyObject other) {
    if (other instanceof PyInt) {
      return make(value | ((PyInt) other).value);
    }
    if (other instanceof PyLong) {
      return make(as_bigint().or(other.as_bigint()));
    }
    return super.and(other);
  }

  @Override
  public PyObject abs() {
    return value < 0 ? neg() : this;
  }

  @Override
  public PyObject neg() {
    return value == Integer.MIN_VALUE ? make(-(long) value) : make(-value);
  }

  @Override
  public PyObject invert() {
    return make(~value);
  }

  // ----------------------------------------------------------------------------------------------

  @Override
  protected int as_int() {
    return value;
  }

  @Override
  protected BigInteger as_bigint() {
    return BigInteger.valueOf(value);
  }

  @Override
  public boolean truth() {
    return value != 0;
  }

}
