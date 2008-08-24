package sma.py.rt;

/**
 * Iterates a sequence or sequence-like instance.
 */
public interface PyIterator {
  /**
   * Returns the next element of the iteration or {@code null} if there are no more elements.
   */
  PyObject next();
}
