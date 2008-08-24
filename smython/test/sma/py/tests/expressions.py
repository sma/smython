### literals/numbers

>>> 0
0
>>> 42L
42L
>>> 1000000000000
1000000000000L
>>> 99999999999999999L
99999999999999999L
>>> 011
9
>>> 012L
10L
>>> 0xAB
171
>>> 0xFFFFFFFFFFFFFFFF
18446744073709551615L
>>> 2147483647
2147483647
>>> 2147483648
2147483648L

### literals/strings

>>> ''
''
>>> ""
''
>>> 'Hello'
'Hello'
>>> "World"
'World'
>>> r''
''
>>> r""
''
>>> r'\n'
'\n'
>>> r"\n"
'\n'
>>> """
... hallo
... """
'\nhallo\n'

### atoms/parenthesized forms

>>> ()
()
>>> (1,)
(1,)
>>> (1,"2")
(1, '2')
>>> (1,"2",)
(1, '2')
>>> ((1,), ("2",), )
((1,), ('2',))

### atoms/list displays

>>> []
[]
>>> [1]
[1]
>>> [1,]
[1]
>>> [1,"2"]
[1, '2']
>>> [1,"2",]
[1, '2']
>>> [[1,], ["2",], ]
[[1], ['2']]

### atoms/dictionary displays

>>> {}
{}
>>> {1: "one"}
{1: 'one'}
>>> {1: "one",}
{1: 'one'}
>>> {1: 1, 2: "2", 3: (3,)}
{1: 1, 2: '2', 3: (3,)}
>>> {'a':1, 'b':2}
{'a': 1, 'b': 2}
>>> {(7,):1, (3, 4):2}
{(3, 4): 2, (7,): 1}

### atoms/string conversions

>>> `1`
'1'
>>> `1, 2`
'(1, 2)'
>>> `1, "2", (())`
"(1, '2', ())"

### primary/subscription/tuple

>>> t = (1, 2, 3)
>>> t[0]
1
>>> t[1]
2
>>> t[2]
3
>>> t[-1], t[-2], t[-3]
(3, 2, 1)

# primary/subscription/tuple/invalid

>>> a = 0
>>> try:
...   (1,)[1]
... except IndexError:
...   a = 1
>>> a
1
>>> try:
...   ()[0]
... except IndexError:
...   a = 2
>>> a
2

### primary/subscription/string

>>> s = "ab"
>>> s[0]
'a'
>>> s[1]
'b'
>>> s[-1], s[-2]
('b', 'a')

### primary/subscription/string/invalid

>>> a = 0
>>> try:
...   "abc"[-4]
... except IndexError:
...   a = 1
>>> a
1
>>> try:
...   "abc"[4]
... except IndexError:
...   a = 2
>>> a
2

### primary/subscription/list

>>> l = [3, 4]
>>> l[0], l[1], l[-1], l[-2]
(3, 4, 4, 3)
>>> l[0] = 5; l
[5, 4]
>>> l[1] = 6; l
[5, 6]
>>> l[-1] = 8; l[-2] = 7; l
[7, 8]

### primary/subscription/list/invalid

>>> a = 0
>>> try:
...   [1,2][-3]
... except IndexError:
...   a = 1
>>> a
1
>>> try:
...   [1][1] = 2
... except IndexError:
...   a = 2
>>> a
2

### primary/subscription/dictionary

>>> d = {'one': 1, 'two': 2}
>>> d['one']
1
>>> d['two']
2
>>> d['one'] = -1
>>> d['two'] = -2
>>> d
{'one': -1, 'two': -2}
>>> {1: 'ok'}[1]
'ok'
>>> {(1,): 'ok'}[1,]
'ok'
>>> {(1,2): 'ok'}[1,2]
'ok'
>>> d = {}
>>> d[1] = 'one'
>>> d[2,] = 'two'
>>> d[1], d[(2,)]
('one', 'two')

### primary/subscription/dictionary/invalid

>>> a = 0
>>> try:
...  {'a': 1}['b']
... except KeyError:
...  a = 1
>>> a
1

### primary/slicing/tuple

>>> t = (1, 2, 3, 4, 5)
>>> t[:]
(1, 2, 3, 4, 5)
>>> t[2:]
(3, 4, 5)
>>> t[-2:]
(4, 5)
>>> t[0:1]
(1,)
>>> t[0:0]
()
>>> t[4:8]
(5,)
>>> t[2:1]
()

### primary/slicing/string
>>> s = "abcd"
>>> s[:]
'abcd'
>>> s[2:]
'cd'
>>> s[:2]
'ab'
>>> s[2:2]
''
>>> s[2:3]
'c'
>>> s[3:6]
'd'
>>> s[3:2]
''
>>> s[:-1]
'abc'
>>> s[0:0]
''

### primary/slicing/list

>>> l = [1, 2, 4, 8]
>>> l[:]
[1, 2, 4, 8]
>>> l[:] is not l
1
>>> l[3:]
[8]
>>> l[-2:]
[4, 8]
>>> l[2:-1]
[4]
>>> l[-1:4]
[8]
>>> l[-9:9]
[1, 2, 4, 8]
>>> l[6:2]
[]

### primary/slicing/list/assign

>>> l = [1, 2]; l[:] = [3]; l
[3]
>>> l = [1, 2, 3, 4]; l[:2] = []; l
[3, 4]
>>> l = [1, 2]; l[1:1] = [3]; l
[1, 3, 2]
>>> l = [1]; l[3:2] = [4]; l
[1, 4]
>>> l = [1]; l[0:0] = [4]; l
[4, 1]

### primary/slicing/dictionary

>>> a = 0
>>> try: {}[:]
... except TypeError: a = 1
>>> a
1
>>> try: {}[:] = {'a': 1}
... except TypeError: a = 2
>>> a
2

### expr/power
>>> 2 ** 0
1
>>> 2 ** 8
256
>>> 2 ** 50
1125899906842624L
>>> 2 ** 200
1606938044258990275541962092341162602522202993782792835301376L

### expr/unary arithmetic

>>> -0
0
>>> +0
0
>>> -1
-1
>>> --1
1
>>> +1
1
>>> +-1
-1
>>> ~3
-4
>>> ~-3
2
>>> -1000000000000000000000
-1000000000000000000000L
>>> +1000000000000000000000
1000000000000000000000L
>>> ~1000000000000000000000
-1000000000000000000001L
>>> -2147483648
-2147483648

### expr/binary arithmetic/numbers

>>> 3+4
7
>>> 3-4
-1
>>> 3*4
12
>>> 3/4
0
>>> 3%4
3
>>> 2147483647+1
2147483648L
>>> -2147483648+1
-2147483647
>>> -2147483648-1
-2147483649L
>>> 100000*100000
10000000000L
>>> 1+2*3
7
>>> 1*2+3
5
>>> (1+2)*3
9
>>> 1*(2+4)
6
>>> 1+2+3+4
10
>>> 100/9/9
1
>>> 8589934592L + 4
8589934596L
>>> 4 + 8589934592L
8589934596L
>>> 8589934592L + 8589934592L
17179869184L
>>> 8589934592L * 8589934592L
73786976294838206464L
>>> (-2147483647-1)/-1
2147483648L
>>> 1L - 2, 1 - 2L, 1L - 2L
(-1L, -1L, -1L)
>>> 1L + 2, 1 + 2L, 1L + 2L
(3L, 3L, 3L)
>>> 4L / 3L
1L
>>> 4L % 3L
1L

### expr/binary arithmetic/numbers/div
>>> -3 / 3, -2 / 3, -1 / 3, 0 / 3, 1 / 3, 2 / 3, 3 / 3
(-1, -1, -1, 0, 0, 0, 1)
>>> -3 / -3, -2 / -3, -1 / -3, 0 / -3, 1 / -3, 2 / -3, 3 / -3
(1, 0, 0, 0, -1, -1, -1)
>>> -3L / 3, -2L / 3, -1L / 3, 0L / 3, 1L / 3, 2L / 3, 3L / 3
(-1L, -1L, -1L, 0L, 0L, 0L, 1L)
>>> -3 / -3L, -2 / -3L, -1 / -3L, 0 / -3L, 1 / -3L, 2 / -3L, 3 / -3L
(1L, 0L, 0L, 0L, -1L, -1L, -1L)

### expr/binary arithmetic/numbers/mod
>>> -3 % 3, -2 % 3, -1 % 3, 0 % 3, 1 % 3, 2 % 3, 3 % 3
(0, 1, 2, 0, 1, 2, 0)
>>> -3 % -3, -2 % -3, -1 % -3, 0 % -3, 1 % -3, 2 % -3, 3 % -3
(0, -2, -1, 0, -2, -1, 0)
>>> -3 % 3L, -2 % 3L, -1 % 3L, 0 % 3L, 1 % 3L, 2 % 3L, 3 % 3L
(0L, 1L, 2L, 0L, 1L, 2L, 0L)
>>> -3L % -3, -2L % -3, -1L % -3, 0L % -3, 1L % -3, 2L % -3, 3L % -3
(0L, -2L, -1L, 0L, -2L, -1L, 0L)


### expr/binary arithmetic/strings

>>> 'foo' + "bar"
'foobar'
>>> 'foo' * 0
''
>>> "foo" * 3
'foofoofoo'

### expr/shifts

>>> 1 << 0
1
>>> 1 << 10
1024
>>> 1 << 100
1267650600228229401496703205376L
>>> 256 >> 0
256
>>> 256 >> 3
32
>>> 1L << 2
4L
>>> 4L >> 1
2L
>>> 1267650600228229401496703205376L >> 99
2L

### expr/bit-wise operations

>>> 3 & 1
1
>>> 3 | 4
7
>>> 3 ^ 1
2

### expr/comparisons/ints

>>> 1 < 2
1
>>> 1 > 2
0
>>> 1 <= 2
1
>>> 1 >= 2
0
>>> 1 == 2
0
>>> 1 != 2
1
>>> 1 <> 2
1
>>> 1 < 3 < 5
1
>>> 1 < 5 < 3
0

### expr/comparisons/tuple

>>> () == ()
1
>>> (1,) == (1,)
1
>>> (1,(2,)) == (1,(2,))
1
>>> () != (1,)
1
>>> (1,) != ()
1
>>> (1,2) != (2,1)
1
>>> (1,) < (1,2)
1
>>> (1,) > (1,2)
0

### expr/identity comparisons

>>> t = (1, 2)
>>> t is t
1
>>> t is ()
0
>>> t is (1, 2)
0
>>> t is not t
0
>>> t is not ()
1
>>> t is not (1, 2)
1

### expr/containment/tuple

>>> t = (1, 2)
>>> 2 in t, 3 in t
(1, 0)
>>> 2 not in t, 3 not in t
(0, 1)

### expr/containment/string

>>> s = "foobar"
>>> "o" in s
1
>>> "oob" in s
1
>>> "e" in s
0
>>> "baz" in s
0

### expr/containment/list

>>> l = [1, 2]
>>> 2 in l, 3 in l
(1, 0)
>>> 2 not in l, 3 not in l
(0, 1)

### expr/containment/dictionary

>>> d = {1: 2, 3: 4}
>>> 1 in d, 2 in d, 3 in d, 4 in d
(1, 0, 1, 0)
>>> 1 not in d, 2 not in d, 3 not in d, 4 not in d
(0, 1, 0, 1)

### expr/boolsche operations/not

>>> not ""
1
>>> not ()
1
>>> not []
1
>>> not {}
1
>>> not 0
1
>>> not "true"
0
>>> not (1,)
0
>>> not ["", ()]
0
>>> not {1: ()}
0
>>> not 4711
0
>>> not 1 == 2
1
>>> not 1 == 1
0
>>> not 1 > -1
0
>>> (not 1) > -1
1

### expr/boolsche operations/and

>>> "x" and "y"
'y'
>>> "" and "y"
''

### expr/boolsche operations/or

>>> "x" or "y"
'x'
>>> "" or "y"
'y'

### expr/expression lists
>>> a = 1,
>>> a
(1,)
>>> a = 1, 2,
>>> a
(1, 2)
