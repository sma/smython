### abs - absolute value of a number

>>> abs(0)
0
>>> abs(42)
42
>>> abs(-1)
1
>>> abs(-12345678901234567890)
12345678901234567890L
>>> class A:
...   def __abs__(self): return 42
>>> abs(A())
42

### apply - call a callable object

### chr - int to string

### cmp - compare two objects

### ...

### tuple - convert any sequence into a tuple

>>> tuple("")
()
>>> tuple("abc")
('a', 'b', 'c')
>>> tuple(())
()
>>> tuple((1,))
(1,)
>>> tuple((1, 2, 3))
(1, 2, 3)
>>> tuple([])
()
>>> tuple([1,2])
(1, 2)
 