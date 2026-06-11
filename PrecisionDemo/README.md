# PrecisionDemo

Demonstrates why `BigDecimal` must **never** be initialized from a `double` literal, even when all other operations in the chain use `BigDecimal`.

## The Rule

| Init method | Result |
|---|---|
| `new BigDecimal("0.3")` | Exactly 0.3 — safe |
| `new BigDecimal(0.3)` | 0.299999999999999988897... — broken |

`BigDecimal` preserves whatever value it is given with perfect precision.  
The problem is that `double` cannot represent most decimal fractions exactly in binary.  
Constructing a `BigDecimal` from a `double` bakes in that binary error before `BigDecimal` ever sees it.

## Demo

`PrecisionDemo.java` runs two cases on the same chain of operations: `(0.1 + 0.2) * 0.3 - 0.09`, which should equal exactly `0.00`.

**Case 1 — Pure BigDecimal (String-init):** all values from String literals. Result is exactly `0.00`.

**Case 2 — Double-poisoned:** the value `0.3` is passed as a `double` at one step. The imprecision of `double(0.3)` propagates through every subsequent operation. Result is not `0.00`.

## Build & Run

```bash
javac PrecisionDemo.java
java PrecisionDemo
```

## Actual Output

```
=== Case 1: Pure BigDecimal chain (String-initialized) ===
  0.1 + 0.2                     = 0.3
  (0.1 + 0.2) * 0.3             = 0.09
  ((0.1+0.2)*0.3) - 0.09        = 0.00
  Expected: 0.3, 0.09, 0.00 — exact? true

=== Case 2: Chain poisoned by a double at one step ===
  new BigDecimal(0.3) actual value: 0.299999999999999988897769753748434595763683319091796875
  0.1 + 0.2                        = 0.3
  (0.1 + 0.2) * new BigDecimal(0.3)= 0.0899999999999999966693309261245303787291049957275390625
  ... - 0.09                       = -3.3306690738754696212708950042724609375E-18
  Expected 0.00, got: -3.3306690738754696212708950042724609375E-18 — precision lost!
```
