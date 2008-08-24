package sma.py;

/**
 * Demonstrates a runtime system supporting a hypothetical Java-translated Python.
 *
 * This mostly ignores OOP and takes objects are structs.
 */
public class Python3 {

  boolean lessThan(Obj a, Obj b) {
    if (a instanceof Int && b instanceof Int) {
      return ((Int) a).value < ((Int) b).value;
    }
    throw new RuntimeException();
  }

  Obj add(Obj a, Obj b) {
    if (a instanceof Int && b instanceof Int) {
      return Int(((Int) a).value + ((Int) b).value);
    }
    throw new RuntimeException();
  }

  Obj subtract(Obj a, Obj b) {
    if (a instanceof Int && b instanceof Int) {
      return Int(((Int) a).value - ((Int) b).value);
    }
    throw new RuntimeException();
  }

  Obj call(Func f, Obj arg) {
    return f.call(this, arg);
  }

  static class Obj {}

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

  Obj fib(Obj n) {
    if (lessThan(n, _2)) {
      return _1;
    } else {
      return add(fib(subtract(n, _1)), fib(subtract(n, _2)));
    }
  }

  static abstract class Func {
    abstract Obj call(Python3 p, Obj arg);
  }

  Func fib = new Func() {
    @Override
    Obj call(Python3 p, Obj n) {
      if (p.lessThan(n, _2)) {
        return _1;
      } else {
        return p.add(fib.call(p, p.subtract(n, _1)), fib.call(p, p.subtract(n, _2)));
      }
    }
  };


  public static void main(String[] args) {
    //Obj fib = globals.getItem(Str("fib"));
    Python3 p = new Python3();
    // warm up
    for (int i = 0; i < 10; i++) {
      //fib.call(Int(28));
      p.fib.call(p, Int(28));
    }
    // benchmark
    long t = System.currentTimeMillis();
    long f = 0;
    for (int i = 0; i < 100; i++) {
      f += ((Int) p.fib.call(p, Int(28))).value;
    }
    System.out.println("fib = " + f);
    System.out.println("time = " + (System.currentTimeMillis() - t));
  }
}
