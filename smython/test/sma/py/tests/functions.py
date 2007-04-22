### simple function definition
>>> def f(): return 1
>>> if f: a = 1
>>> else: a = 0
>>> a
1

### simple function application
>>> def f(): return 1
>>> f()
1

### function application with parameters
>>> def f(a, b=-1): return a, b
>>> f(1)
(1, -1)
>>> f(2, 3)
(2, 3)
>>> f(4, b=5)
(4, 5)
>>> f(a=6)
(6, -1)
>>> f(a=7, b=8)
(7, 8)
>>> f(b=10, a=9)
(9, 10)

### function appliation with rest list
>>> def f(a, *rest): return a, rest
>>> f(1)
(1, ())
>>> f(1, 2, 3)
(1, (2, 3))

### function application with kwrest list
>>> def f(a=0, **rest): return a, rest
>>> f()
(0, {})
>>> f(1)
(1, {})
>>> f(a=2)
(2, {})
>>> f(a=3, b=4)
(3, {'b': 4})
>>> f(b=6, a=5)
(5, {'b': 6})
>>> f(b=7, c=8)
(0, {'b': 7, 'c': 8})
