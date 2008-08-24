# just an experiment

def abs(x):
  t = type(x)
  if t == 'instance':
    return x.__abs__()
  elif t == 'int' or t == 'long':
    if x < 0: return -x
    return x
  raise 'bad operand type for abs()'

def fib(n):
  if n < 2:
    return 1
  else: 
    return fib(n-1) + fib(n-2) + 1

OverflowError = 'OverflowError'
RuntimeError = 'RuntimeError'
KeyboardInterrupt = 'KeyboardInterrupt'
IndexError = 'IndexError'
ZeroDivisionError = 'ZeroDivisionError'
EOFError = 'EOFError'
TypeError = 'TypeError'
NameError = 'NameError'
KeyError = 'KeyError'