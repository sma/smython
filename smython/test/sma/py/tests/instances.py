### simple class definition
>>> class C: pass
>>> if C: a = 1
... else: a = 0
>>> a
1

### normal class definition
>>> class C:
...   def m1(): return 1
...   def m2(a):
...     return a
>>> not C
0
>>> not C.m1, not C.m2
(0, 0)

### class attributes
>>> class C:
...   c = 2
>>> C.c
2
>>> C.c = 3
>>> C.c
3

### instantiation
>>> class C: pass
>>> not C()
0

### instantiation with __init__
>>> class C:
...   def __init__(self, *a): self.a = a
...   def args(self): return self.a
>>> C().args()
()
>>> C(1).args()
(1,)
>>> C(1,).args()
(1,)
>>> C(1, 2).args()
(1, 2)

### instance attributes
>>> class C: pass
>>> c = C()
>>> c.a = 5
>>> c.a
5

### method application
>>> class C:
...   def m(self): return 42
>>> C().m()
42
>>> m = C().m
>>> m()
42

### unbound method application
>>> class C:
...   def m(self, a): return -a
>>> m = C.m
>>> m(C(), 4)
-4

### inheritance of attributes
>>> class C: a = 'c'
>>> class D(C): pass
>>> D.a
'c'
>>> class E(D): a = 'd'
>>> E.a
'd'

### inheritance of methods
>>> class C:
...   def __init__(self, a): self.a = a
...   def m(self): return self.a
>>> class D(C):
...   def __init__(self):
...     C.__init__(self, 7)
>>> C(3).m()
3
>>> D().m()
7
