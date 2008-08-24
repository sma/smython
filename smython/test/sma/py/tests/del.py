### del/tuple/subscription

>>> a=0
>>> try: del (1, 2, 3)[1]
... except TypeError: a=1
>>> a
1

### del/string/subscription

>>> a=0
>>> try: del "abc"[1]
... except TypeError: a=1
>>> a
1

### del/list/subscription

>>> l = [1, 2, 3]; del l[1]; l
[1, 3]
>>> l = [1, 2, 3]; del l[-1]; l
[1, 2]

### del/list/subscription/invalid

>>> a = 0
>>> try: del [1, 2][-3]
... except IndexError: a = 1
>>> a
1
>>> try: del [1, 2][3]
... except IndexError: a = 2
>>> a
2

### del/dictionary/subscription

>>> d = {'a': 1, 'b': 2}; del d['a']; d
{'b': 2}

### del/dictionary/subscription/invalid

>>> a = 0
>>> try: del {}['a']
... except KeyError: a = 1
>>> a
1

### del/list/slicing

>>> l=[1, 2, 3]; del l[1:]; l
[1]
>>> l=[1, 2, 3]; del l[:1]; l
[2, 3]
>>> l=[1, 2, 3]; del l[1:1]; l
[1, 2, 3]
>>> l=[1, 2]; del l[-3:6]; l
[]
>>> l=[1, 2]; del l[:]; l
[]