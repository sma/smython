### simple if True
>>> a = ''
>>> if 1: a = 'ok'
>>> a
'ok'

### normal if True
>>> a = ''
>>> if 1:
...   a = '..'
...   a = 'ok'
>>> a
'ok'

### short if True
>>> a = ''; b = ''
>>> if 1: a = 'ok'; b = 'ok'
>>> a, b
('ok', 'ok')

### if True with simple else 
>>> a = ''
>>> if 1:
...   a = 'ok'
... else: a = 'not ok'
>>> a
'ok'

### if True with normal else
>>> a = ''
>>> if 1:
...   a = 'ok'
... else:
...   a = '..'
...   a = 'not ok'
>>> a
'ok'

### if False without else
>>> a = ''
>>> if 0: a = 'not ok'
>>> a
''

### if False with simple else
>>> a = ''
>>> if 0: a = 'not ok'
... else: a = 'ok'
>>> a
'ok'

### if False with normal else
>>> a = ''
>>> if 0: a = 'not ok'
... else:
...   a = '..' 
...   a = 'ok'
>>> a
'ok'

### if False with short else
>>> a = ''; b = ''
>>> if 0: a = 'not ok'; b = 'not ok'
>>> else: a = 'ok'; b = 'ok'
>>> a, b
('ok', 'ok')


### simple if/elif combination
>>> a = ''
>>> if 0: pass
>>> elif 1: a = 'ok'
>>> a
'ok'

### normal if/elif combination
>>> a = ''
>>> if 0: pass
>>> elif 1:
...   a = '..'
...   a = 'ok'
>>> a
'ok'

### short if/elif combination
>>> a = ''
>>> if 0: pass
>>> elif 1:
...   a = '..'; a = 'ok'
>>> a
'ok'

### if/elif with simple else
>>> a = ''
>>> if 0: pass
... elif 1: a = 'ok'
... else: a = 'not ok'
>>> a
'ok'

### if/elif with normal else
>>> a = ''
>>> if 0: pass
... elif 1: a = 'ok'
... else:
...  a = '..'
...  a = 'not ok'
>>> a
'ok'

### if/elif with short else
>>> a = ''
>>> if 0: pass
... elif 1: a = 'ok'
... else:
...  a = '..'; a = 'not ok'
>>> a
'ok'

### if/elif with succeeding else
>>> a = ''
>>> if 0: pass
... elif 0: pass
... else: a = 'ok'
>>> a
'ok'

### complex if/elif/else
>>> a = ''
>>> if 0: pass
... elif 0: pass
... elif 0: pass
... elif 1: a = 'ok'
... else: pass
>>> a
'ok'

### while False
>>> a = 'ok'
>>> while 0: a = 'not ok'
>>> a
'ok'

### while False with else
>>> a = 0
>>> while 0: assert 0
... else: a = 'ok'
>>> a
'ok'

### simple while True
>>> a = 0
>>> while a < 3: a = a + 1
>>> a
3

### normal while True
>>> a = 0
>>> while a < 3:
...   a = a + 1 
...   a = a + 1
>>> a
4

### while True with simple else
>>> a = 0
>>> while a < 3:
...   a = a + 1
... else: a = a + 4
>>> a
7

### while True with normal else
>>> a = 0
>>> while a < 3:
...   a = a + 1
... else:
...   a = a + 2
...   a = a + 2
>>> a
7

### while True with break
>>> a = 0
>>> while 1:
...   break
...   a = 1
>>> a
0

### while True with break and else
>>> a = 0
>>> while 1:
...   break
...   a = 1
... else:
...   a = 2
>>> a
0

### while with break and continue
>>> a = 0
>>> while a < 5:
...   a = a + 1
...   if a == 2: continue
...   if a == 2: break
...   if a == 3: break
... else: a = 99
>>> a
3

### while with continue and else
>>> a = 0
>>> while a < 2:
...   if a == 1:
...     a = 2
...     continue
...   a = a + 1
... else: a = 4
>>> a
4

### simple for
>>> a = 0
>>> for b in (1, 2, 3): a = a + b
>>> a, b
(6, 3)

### simple for, implicit tuple 
>>> a = 0
>>> for b in 1, 2, 3: a = a + b
>>> a, b
(6, 3)

### normal for
>>> a = 0
>>> for b in (1, 2, 3):
...   a = a + b
...   a = a + b
>>> a, b
(12, 3)

### short for
>>> a = 0
>>> for b in (1, 2, 3): a = a + b; a = a + b
>>> a, b
(12, 3)

### for without elements
>>> a = 0
>>> for b in (): a = 1
>>> a
0

### for without elements but with else
>>> a = 0
>>> for b in (): a = 1
>>> else: a = 4
>>> a
4

### for with simple else
>>> a = 0
>>> for b in (): a = 1
... else: a = 2
>>> a
2

### for with normal else
>>> a = 0
>>> for b in 99,: a = 1
... else:
...   a = 2
...   a = 3
>>> a, b
(3, 99)

### for with break
>>> for a in 1, 2, 3:
...   if a == 2: break
>>> a
2

### for with break and else
>>> for a in 1, 2, 3:
...   if a == 2: break
... else: a = 4
>>> a
2

### for with continue
>>> b = 0
>>> for a in 1, 2:
...   if a == 2: continue
...   b = a
>>> b
1

### for with continue and else
>>> b = 0
>>> for a in 1, 2:
...   if a == 2: continue
...   b = a
... else: b = 3
>>> b
3

### try/finaly
>>> a=0
>>> try:
...   try:
...     assert 0
...   finally:
...     a=1
... except: pass
>>> a
1

### TODO try/except with raise

