### assign empty tuple to var
>>> a = ()
>>> a
()

### assign explicit tuple to var
>>> a = (1, 2,)
>>> a
(1, 2)

### assign implicit tuple to var
>>> a = 2, 3,
>>> a
(2, 3)

### assign tuple to implicit var tuple
>>> a, b = 1, 2
>>> a, b
(1, 2)

### assign tuple to explicit var tuple
>>> (a, b) = (2, 3) 
>>> a, b
(2, 3)

### assign tuple to var and tuple of vars
>>> a, (b, c), d = (1, (2, 3), 4)
>>> a, b, c, d
(1, 2, 3, 4)

### swap variables
>>> a, b = 3, 4
>>> a, b = b, a
>>> a, b
(4, 3)
>>> a, b, c = 6, 7, 8
>>> a, (b, c) = (c,), (a, b)
>>> a, b, c
((8,), 6, 7)

### assign 1{,} to x{,}
>>> x = 1; x
1
>>> x = 1,; x
(1,)
>>> x, = 1,; x
1
>>> x = (1); x
1
>>> x = (1,); x
(1,)
>>> x, = (1,); x
1

### assign 1{,} to x{,} in parentheses
>>> (x) = 1; x
1
>>> (x) = 1,; x
(1,)
>>> (x,) = 1,; x
1
>>> (x) = (1); x
1
>>> (x) = (1,); x
(1,)
>>> (x,) = (1,); x
1

### assign 1{,} to x{,} in double parentheses
>>> ((x)) = 1,; x
(1,)
>>> ((x,)) = 1,; x
1
>>> ((x),) = 1,; x
1
