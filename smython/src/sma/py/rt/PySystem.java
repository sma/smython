package sma.py.rt;

/**
 * Represents a (mostly threadsafe) Python runtime system instance, keeping track of
 * all global state of the system. Systems share immutable objects like strings,
 * numbers or tuples, though. They have separate import paths and module dictionaries.
 */
public class PySystem {
    /*
    static class PFrame {
        PFrame back;

        PFrame(PFrame back) {
            this.back = back;
        }
    }

    static class PObject {
        PObject getItem(PySystem system, PObject key) {
            throw new UnsupportedOperationException();
        }

        PObject getAttr(PySystem system, PString name) {
            throw new UnsupportedOperationException();
        }

        PObject call(PySystem system, PTuple args, PDict kwargs) {
            throw new UnsupportedOperationException();
        }

        PObject abs(PySystem system) {
            throw system.raiseTypeError("abs() requires numeric argument");
        }
    }

    static class PString extends PObject {
        String string;

        PString(String string) {
            this.string = string;
        }
    }

    static class PTuple extends PObject {
        PObject[] elements;

        PTuple(PObject... elements) {
            this.elements = elements;
        }
    }

    static class PDict extends PObject {
    }

    static class PClass extends PObject {
        private PString name;
        private PTuple bases;
        private PDict dict;

        @Override
        PObject getAttr(PySystem system, PString name) {
            PObject value = primGetAttr(system, name);
            if (value != null) {
                return value;
            }
            throw system.raiseTypeError("no attribute");
        }

        PObject primGetAttr(PySystem system, PString name) {
            PObject value = dict.getItem(system, name);
            if (value != null) {
                return value;
            }
            for (int i = 0, size = bases.elements.length; i < size; i++) {
                PClass c = (PClass) bases.elements[i];
                value = c.primGetAttr(system, name);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }
    }

    static class PInstance extends PObject {
        private PClass clasz;
        private PDict dict;

        @Override
        PObject getAttr(PySystem system, PString name) {
            PObject value = dict.getItem(system, name);
            if (value != null) {
                return value;
            }
            value = clasz.primGetAttr(system, name);
            if (value != null) {
                return value; // make it a method
            }
            value = clasz.primGetAttr(system, new PString("__getattr__"));
            if (value != null) {
                return value.call(system, new PTuple(name), null);
            }
            throw system.raiseTypeError("no attribute");
        }
    }

    static class PFunction extends PObject {
        @Override
        PObject call(PySystem system, PTuple args, PDict kwargs) {
            system.current = new PFrame(system.current);
            try {
                // doit
                return null;
            } finally {
                system.current = system.current.back;
            }
        }
    }

    static class PMethod extends PObject {
        PFunction func;
        PInstance self;

        @Override
        PObject call(PySystem system, PTuple args, PDict kwargs) {
            PObject[] elements = new PObject[args != null ? args.elements.length + 1 : 1];
            if (args != null) {
                System.arraycopy(args.elements, 0, elements, 1, args.elements.length);
            }
            elements[0] = self;
            return func.call(system, new PTuple(elements), kwargs);
        }
    }

    static class PBuiltin extends PObject {
        String name;
        Method method;
        int min, max;

        @Override
        PObject call(PySystem system, PTuple args, PDict kwargs) {
            if (kwargs != null) {
                throw system.raiseTypeError("this function takes no keyword arguments");
            }
            PObject[] arguments = args.elements;
            int length = arguments.length;
            if (min == max) {
                if (length != min) {
                    throw system.raiseTypeError(name + " requires exactly " + min + " arguments; " + length + " given");
                }
            } else {
                if (length < min) {
                    throw system.raiseTypeError(name + " requires at least " + min + " arguments; " + length + " given");
                }
                if (length > max) {
                    throw system.raiseTypeError(name + " requires at most " + max + " arguments; " + length + " given");
                }
                if (length < max) {
                    arguments = new PObject[max];
                    System.arraycopy(args.elements, 0, arguments, 0, length);
                }
            }
            try {
                return (PObject) method.invoke(system, (PObject[]) arguments);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    @Builtin(name="abs", args=1)
    PObject abs(PObject n) {
        return n.abs(this);
    }

    private RuntimeException raiseTypeError(String message) {
        return new RuntimeException(message);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Builtin {
        String name();
        int args();
    }
    */
}
