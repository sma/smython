package sma.py.ast;

import sma.py.rt.PyString;
import sma.py.rt.PyObject;
import sma.py.rt.PyFrame;
import sma.py.rt.Py;

/**
 * Represents a global identifier, see §5.2.1, or a target, see §6.3.
 */
public class PyGlobalIdentifier extends PyIdentifier {
  public PyGlobalIdentifier(PyString name) {
    super(name);
  }

  @Override
  public PyObject eval(PyFrame frame) {
    return frame.getGlobal(getName());
  }

  @Override
  public void assign(PyFrame frame, PyObject value) {
    frame.setGlobal(getName(), value);
  }

  @Override
  public void del(PyFrame frame) {
    frame.delGlobal(getName());
  }

}
