### can we assign 0?
>>> a=0
>>> a
0

### can we assign 1?
>>> a = 1
>>> a
1

### can we compare two numbers?
>>> 0 < 1
1
>>> 1 < 0
0

### can we add to a number?
>>> a = 2
>>> a = a + 1
>>> a
3

### normal try/except
>>> a=0
>>> try: a=1
... except: a=2
>>> a
1

### try/except with else
>>> a=0
>>> try: a=1
... except: a=2
... else: a=3
>>> a
3

### positive asserts
>>> a=0
>>> assert 1
>>> assert 1, 'ok'
>>> a
0

### negative assert
>>> a=0
>>> try:
...   assert 0, 'failure'
... except:
...   a=1
... else:
...   a=2
... a
1
