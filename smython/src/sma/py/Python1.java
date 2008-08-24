package sma.py;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Demonstrates a runtime system supporting a hypothetical Java-translated Python.
 *
 * Using one global environment, optionally stored in a thread-local.
 */
public class Python1 {
  /** Abstact base class for all Python types. */
  static abstract class Obj {

    /** Returns {@code true} if the receiver is considered to be less than the given argument. */ 
    public boolean lessThan(Obj obj) {
      throw new UnsupportedOperationException();
    }

    /** Adds an object to the receiver and returns the result. */
    public Obj add(Obj obj) {
      throw new UnsupportedOperationException();
    }

    /** Substracts an object from the receiver and returns the result. */
    public Obj subtract(Obj obj) {
      throw new UnsupportedOperationException();
    }

    /** Calls the receiver like a function with one argument. */
    Obj call(Obj arg) {
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
    public boolean lessThan(Obj obj) {
      return value < ((Int) obj).value;
    }

    @Override
    public Obj add(Obj obj) {
      return Int(value + ((Int) obj).value); // there's no overflow checking
    }

    @Override
    public Obj subtract(Obj obj) {
      return Int(value - ((Int) obj).value); // there's no overflow checking
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
  static Obj fib(Obj n) {
    if (n.lessThan(_2)) {
      return _1;
    } else {
      return fib(n.subtract(_1)).add(fib(n.subtract(_2)));
    }
  }

  /** Callable function. */
  static class Func extends Obj {
  }

  /** Implements fib(n) using a global function object. */
  static Func fib = new Func() {
    @Override
    Obj call(Obj n) {
      if (n.lessThan(_2)) {
        return _1;
      } else {
        return fib.call(n.subtract(_1)).add(fib.call(n.subtract(_2)));
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

  static interface IDict {
    Obj getItem(Obj key);
    void setItem(Obj key, Obj value);
    boolean deleteItem(Obj key);
  }

  /** Dictionaries (mutable). */
  static class Dict extends Obj implements IDict {
    final Map<Obj, Obj> objects = new HashMap<Obj, Obj>();

    /** Returns the object stored under the given key or throws an exception. */
    public Obj getItem(Obj key) {
      Obj value = objects.get(key);
      if (value != null) {
        return value;
      }
      throw new RuntimeException();
    }

    /** Stores an object under the given key. */
    public void setItem(Obj key, Obj value) {
      objects.put(key, value);
    }

    /** Removes the object stored under the given key from the receiver. */
    public boolean deleteItem(Obj key) {
      return objects.remove(key) != null;
    }
  }

  static Dict Dict() {
    return new Dict();
  }

  /** Special dictionary for a small set of values (mutable). */
  static class LDict extends Obj implements IDict {
    final Obj[] objects;

    LDict(int capacity) {
      objects = new Obj[capacity << 1];
    }

    /** Returns the object stored under the given key or throws an exception. */
    public Obj getItem(Obj key) {
      int len = objects.length / 2;
      for (int i = 0; i < len; i++) {
        if (objects[i].equals(key)) {
          return objects[i + len];
        }
      }
      throw new RuntimeException();
    }

    /** Stores an object under the given key. */
    public void setItem(Obj key, Obj value) {
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

    /** Removes the object stored under the given key from the receiver. */
    public boolean deleteItem(Obj key) {
      int len = objects.length / 2;
      for (int i = 0; i < len; i++) {
        if (objects[i].equals(key)) {
          objects[i + len] = objects[i] = null;
          return true;
        }
      }
      return false;
    }
  }

  // Implements fib(n) using a global function object stored in a central global dictionary.
  static Str s_fib = Str("fib");
  static Dict globals = Dict();
  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Obj n) {
        if (n.lessThan(_2)) {
          return _1;
        } else {
          return globals.getItem(s_fib).call(n.subtract(_1)).add(globals.getItem(s_fib).call(n.subtract(_2)));
        }
      }
    });
  }

  // Implements fib(n) using a global function object stored in a central global dictionary
  // also using a dictionary for local dictionary lookup.
  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Obj n) {
        LDict locals = new LDict(1);
        Str s_n = Str("n");
        locals.setItem(s_n, n);
        if (locals.getItem(s_n).lessThan(_2)) {
          return _1;
        } else {
          return globals.getItem(s_fib).call(locals.getItem(s_n).subtract(_1)).add(
              globals.getItem(s_fib).call(locals.getItem(s_n).subtract(_2)));
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

  // Implements fib(n) using a global function object stored in a central global dictionary
  // also using a dictionary for local dictionary lookup as part of chained frame objects that
  // are kept in a thread-local variable.
  static ThreadLocal<Frame> current = new ThreadLocal<Frame>();
  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Obj n) {
        Frame frame = new Frame(current.get(), n);
        current.set(frame);
        try {
          if (frame.locals[0].lessThan(_2)) {
            return _1;
          } else {
            return globals.getItem(s_fib).call(frame.locals[0].subtract(_1)).add(
                globals.getItem(s_fib).call(frame.locals[0].subtract(_2)));
          }
        } finally {
          current.set(frame.back);
        }
      }
    });
  }

  static class Environment {
    Frame current;

    Obj[] setup(Obj... locals) {
      current = new Frame(current, locals);
      return locals;
    }

    void cleanup() {
      current = current.back;
    }
  }

  static class EFunc extends Func {
    Environment environment;

    EFunc(Environment environment) {
      this.environment = environment;
    }

  }
/*
  // Implements fib(n) using a global function object stored in a central global dictionary
  // also using a dictionary for local dictionary lookup as part of chained frame objects that
  // are kept in global environment which is know to the function. This is NOT threadsafe!
  static {
      globals.setItem(s_fib, new EFunc(new Environment()) {
        @Override
        Obj call(Obj n) {
          Obj[] locals = environment.setup(n);
          try {
            if (locals[0].lessThan(_2)) {
              return _1;
            } else {
              return globals.getItem(s_fib).call(locals[0].subtract(_1)).add(
                  globals.getItem(s_fib).call(locals[0].subtract(_2)));
            }
          } finally {
            environment.cleanup();
          }
        }
      });
    }
*/
  // for i, r in enumerate(map(lamba n: n * n, range(20))): print i, r
  static void example() {
    Func f = new Func() {
      @Override
      Obj call(Obj n) {
        return n.add(n);
      }
    };
    for (Obj o : enumerate(map(f, range(Int(20))))) {
      Obj i = ((Tuple) o).objects[0];
      Obj r = ((Tuple) o).objects[1];
      print(i, r);
    }
  }

  static class Tuple extends Obj {
    final Obj[] objects;

    Tuple(Obj... objects) {
      this.objects = objects;
    }
  }

  static abstract class Iter extends Obj implements Iterable<Obj> {
    abstract Obj next();

    public Iterator<Obj> iterator() {
      return new Iterator<Obj>() {
        private Obj next;

        public boolean hasNext() {
          if (next == null) {
            try {
              next = Iter.this.next();
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

      Int next() {
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
      public Obj next() {
        return func.call(iter.next());
      }
    };
  }

  static Iter enumerate(final Iter iter) {
    return new Iter() {
      private int index = 0;

      public Tuple next() {
        return new Tuple(Int(index++), iter.next());
      }
    };
  }

  static class FrameThread extends Thread {
    private Frame current;

    Obj[] setup(Obj... locals) {
      current = new Frame(current, locals);
      return locals;
    }

    void cleanup() {
      current = current.back;
    }
  }
/*
  // Implements fib(n) using a global function object stored in a central global dictionary
  // also using a dictionary for local dictionary lookup as part of chained frame objects that
  // are kept in a special thread attribute.
  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Obj n) {
        FrameThread ft = (FrameThread) Thread.currentThread();

        Obj[] locals = ft.setup(n);
        try {
          if (locals[0].lessThan(_2)) {
            return _1;
          } else {
            return globals.getItem(s_fib).call(locals[0].subtract(_1)).add(
                globals.getItem(s_fib).call(locals[0].subtract(_2)));
          }
        } finally {
          ft.cleanup();
        }
      }
    });
  }
*/
  public static void main2(String[] args) {
    //example(); System.exit(0);

    Obj fib = globals.getItem(Str("fib"));
    // warm up
    for (int i = 0; i < 10; i++) {
      fib.call(Int(28));
    }
    // benchmark
    long t = System.currentTimeMillis();
    long f = 0;
    for (int i = 0; i < 100; i++) {
      f += ((Int) fib.call(Int(28))).value;
    }
    System.out.println("fib = " + f);
    System.out.println("time = " + (System.currentTimeMillis() - t));
  }

  static class H implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
      Thread.UncaughtExceptionHandler h = Thread.getDefaultUncaughtExceptionHandler();
      if (h != null) {
        h.uncaughtException(t, e);
      }
    }

    private Frame current;

    Obj[] setup(Obj... locals) {
      current = new Frame(current, locals);
      return locals;
    }

    void cleanup() {
      current = current.back;
    }
  }

  // Implements fib(n) using a global function object stored in a central global dictionary
  // also using a dictionary for local dictionary lookup as part of chained frame objects that
  // are kept in a special thread attribute.
  static {
    globals.setItem(s_fib, new Func() {
      @Override
      Obj call(Obj n) {
        H h = (H) Thread.currentThread().getUncaughtExceptionHandler();

        Obj[] locals = h.setup(n);
        try {
          if (locals[0].lessThan(_2)) {
            return _1;
          } else {
            return globals.getItem(s_fib).call(locals[0].subtract(_1)).add(
                globals.getItem(s_fib).call(locals[0].subtract(_2)));
          }
        } finally {
          h.cleanup();
        }
      }
    });
  }
  
  public static void main(String[] args) throws InterruptedException {
    Thread t = new FrameThread() {
      public void run() {
        main2(null);
      }
    };
    //t.start();
    //t.join();

    Thread.currentThread().setUncaughtExceptionHandler(new H());
    main2(null);
  }

}
