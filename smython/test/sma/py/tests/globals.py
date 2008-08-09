### undefined local variable
>>> try: u
... except "NameError", n: a=n
>>> a
'u'

### deleting local variable
>>> u=1; del u
>>> try: u
... except "NameError", n: a=n
>>> a
'u'

### returning a global variable
>>> g = 1
>>> def a(): return g
>>> a()
1

### returning a local variable (global unchanged)
>>> g = 1
>>> def a(): g = 2; return g
>>> g, a()
(1, 2)

### changing a global variable
>>> g = 0
>>> def a(): global g; g = 1
>>> a(); g
1

### deleting global variables
>>> g = 1
>>> def f(): global g; del g
>>> f()
>>> try: g; assert 0
... except "NameError", e: a=e
>>> a
'g'
>>> try: f()
... except "NameError", e: b=e
>>> b
'g'
