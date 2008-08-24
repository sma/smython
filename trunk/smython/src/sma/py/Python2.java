package sma.py;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Demonstrates a runtime system supporting a hypothetical Java-translated Python.
 *
 * Using a chain of frames as local environment which is passed around so that for
 * example the built-in function {@code locals()} can be implemented easily.
 */
public class Python2 {
  /** Abstact base class for all Python types. */
  static abstract class Obj {

    /** Returns {@code true} if the receiver is considered to be less than the given argument. */
    public boolean lessThan(Frame frame, Obj obj) {
      throw new UnsupportedOperationException();
    }

    /** Adds an object to the receiver and returns the result. */
    public Obj add(Frame frame, Obj obj) {
      throw new UnsupportedOperationException();
    }

    /** Substracts an object from the receiver and returns the result. */
    public Obj subtract(Frame frame, Obj obj) {
      throw new UnsupportedOperationException();
    }

    /** Calls the receiver like a function with one argument. */
    Obj call(Frame frame, Obj arg) {
      throw new UnsupportedOperationException();
    }

  }

  /** Small integers. */
  static class Int extends Obj {
    final int value;

    Int(int value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      return o == this || o instanceof Int && ((Int) o).value == value;
    }

    @Override
    public int hashCode() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @Override
    public boolean lessThan(Frame frame, Obj obj) {
      return value < ((Int) obj).value;
    }

    @Override
    public Obj add(Frame frame, Obj obj) {
      long v = (long) value + ((Int) obj).value;
      if (Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE) {
        return Int((int) v);
      }
      throw new RuntimeException();
    }

    @Override
    public Obj subtract(Frame frame, Obj obj) {
      long v = (long) value - ((Int) obj).value;
      if (Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE) {
        return Int((int) v);
      }
      throw new RuntimeException();
    }
  }

  private static final Int[] preallocated = new Int[2000];
  static {
    for (int i = 0; i < preallocated.length; i++) {
      preallocated[i] = new Int(i - 1000);
    }
  }

  static Int Int(int n) {
    return n >= -1000 && n < 1000 ? preallocated[n + 1000] : new Int(n);
  }

  static final Int _0 = Int(0);
  static final Int _1 = Int(1);
  static final Int _2 = Int(2);

  /** Implements fib(n) with a simple static Java method. */
  static Obj fib(Frame frame, Obj n) {
    if (n.lessThan(frame, _2)) {
      return _1;
    } else {
      return fib(frame, n.subtract(frame, _1)).add(frame, fib(frame, n.subtract(frame, _2)));
    }
  }

  /** Callable function. */
  static class Func extends Obj {
  }

  /** Implements fib(n) using a global function object. */
  static Func fib = new Func() {
    @Override
    Obj call(Frame frame, Obj n) {
      if (n.lessThan(frame, _2)) {
        return _1;
      } else {
        return fib.call(frame, n.subtract(frame, _1)).add(frame, fib.call(frame, n.subtract(frame, _2)));
      }
    }
  };

  /** Strings (immutable as in Java). */
  static class Str extends Obj {
    String value;

    Str(String value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      return o == this || o instanceof Str && ((Str) o).value.equals(value);
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }

    @Override
    public String toString() {
      return value;
    }
  }

  static Str Str(String value) {
    return new Str(value);
  }

  /** Dictionaries (mutable). */
  static class Dict extends Obj {
    Map<Obj, Obj> objects = new HashMap<Obj, Obj>();

    /** Returns the object stored under the given key or throws an exception. */
    Obj getItem(Obj key) {
      Obj value = objects.get(key);
      if (value != null) {
        return value;
      }
      throw new RuntimeException();
    }

    /** Stores an object under the given key. */
    void setItem(Obj key, Obj value) {
      objects.put(key, value);
    }
  }

  static Dict Dict() {
    return new Dict();
  }

  static class LDict extends Obj {
    Obj[] objects = new Obj[14];

    /** Returns the object stored under the given key or throws an exception. */
    Obj getItem(Obj key) {
      int len = objects.length / 2;
      for (int i = 0; i < len; i++) {
        if (objects[i].equals(key)) {
          return objects[i + len];
        }
      }
      throw new RuntimeException();
    }

    /** Stores an object under the given key. */
    void setItem(Obj key, Obj value) {
      int len = objects.length / 2;
      for (int i = 0; i < len; i++) {
        if (objects[i] == null) {
          objects[i] = key;
          objects[i + len] = value;
          return;
        }
      }
      throw new RuntimeException();
    }
  }

  static Str s_fib = Str("fib");
  static Dict globals = Dict();
  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Frame frame, Obj n) {
        if (n.lessThan(frame, _2)) {
          return _1;
        } else {
          return globals.getItem(s_fib).call(frame, n.subtract(frame, _1)).add(
              frame, globals.getItem(s_fib).call(frame, n.subtract(frame, _2)));
        }
      }
    });
  }

  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Frame frame, Obj n) {
        LDict locals = new LDict();
        Str s_n = Str("n");
        locals.setItem(s_n, n);
        if (locals.getItem(s_n).lessThan(frame, _2)) {
          return _1;
        } else {
          return globals.getItem(s_fib).call(frame, locals.getItem(s_n).subtract(frame, _1)).add(
              frame, globals.getItem(s_fib).call(frame, locals.getItem(s_n).subtract(frame, _2)));
        }
      }
    });
  }

  /** Frames storing local variables, linking back to the frame of the caller. */
  static class Frame extends Obj {
    final Frame back;
    final Obj[] locals;
    Frame(Frame back, Obj... locals) {
      this.back = back;
      this.locals = locals;
    }
  }

  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Frame frame, Obj n) {
        frame = new Frame(frame, n);
        if (frame.locals[0].lessThan(frame, _2)) {
          return _1;
        } else {
          return globals.getItem(s_fib).call(frame, frame.locals[0].subtract(frame, _1)).add(
              frame, globals.getItem(s_fib).call(frame, frame.locals[0].subtract(frame, _2)));
        }
      }
    });
  }

  // for i, r in enumerate(map(lamba n: n * n, range(20))): print i, r
  static void example(Frame frame) {
    Func f = new Func() {
      @Override
      Obj call(Frame frame, Obj n) {
        return n.add(frame, n);
      }
    };
    for (Iterator<Obj> it = enumerate(map(f, range(Int(20)))).iterator(frame); it.hasNext();) {
      Tuple t = (Tuple) it.next();
      Obj i = t.objects[0];
      Obj r = t.objects[1];
      print(i, r);
    }
  }

  static class Tuple extends Obj {
    final Obj[] objects;

    Tuple(Obj... objects) {
      this.objects = objects;
    }
  }

  static abstract class Iter extends Obj {
    abstract Obj next(Frame frame);

    public Iterator<Obj> iterator(final Frame frame) {
      return new Iterator<Obj>() {
        private Obj next;

        public boolean hasNext() {
          if (next == null) {
            try {
              next = Iter.this.next(frame);
            } catch (StopIteration e) {
              next = null;
            }
          }
          return next != null;
        }

        public Obj next() {
          if (hasNext()) {
            try {
              return next;
            } finally {
              next = null;
            }
          }
          throw new NoSuchElementException();
        }

        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }
  }

  static void print(Obj... objects) {
    for (int i = 0; i < objects.length; i++) {
      if (i > 0) {
        System.out.print(' ');
      }
      System.out.print(objects[i]);
    }
    System.out.println();
  }

  static Iter range(final Obj n) {
    return new Iter() {
      int index = 0, max = ((Int) n).value;

      Int next(Frame frame) {
        if (index < max) {
          return Int(index++);
        }
        throw new StopIteration();
      }
    };
  }

  static class StopIteration extends RuntimeException {
    @Override
    public Throwable fillInStackTrace() {
      return null;
    }
  }

  static Iter map(final Func func, final Iter iter) {
    return new Iter() {
      public Obj next(Frame frame) {
        return func.call(frame, iter.next(frame));
      }
    };
  }

  static Iter enumerate(final Iter iter) {
    return new Iter() {
      private int index = 0;

      public Tuple next(Frame frame) {
        return new Tuple(Int(index++), iter.next(frame));
      }
    };
  }

  public static void main(String[] args) {
    Frame frame = new Frame(null);
    //example(frame); System.exit(0);

    Obj fib = globals.getItem(Str("fib"));
    // warm up
    for (int i = 0; i < 10; i++) {
      fib.call(frame, Int(28));
    }
    // benchmark
    long t = System.currentTimeMillis();
    long f = 0;
    for (int i = 0; i < 100; i++) {
      f += ((Int) fib.call(frame, Int(28))).value;
    }
    System.out.println("fib = " + f);
    System.out.println("time = " + (System.currentTimeMillis() - t));
  }

}
