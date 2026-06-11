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

## Cases

**Case 1 — Pure BigDecimal (String-init):** chain `(0.1 + 0.2) * 0.3 - 0.09`. All values from String literals. Result is exactly `0.00`.

**Case 2 — Double-poisoned (short chain):** same chain, but `0.3` is `new BigDecimal(0.3)`. Result is not `0.00`.

**Case 3 — Long chain, all BigDecimal:** `10.0 - 3.1 * 2.0 + 1.2 - 5.0`. All String-init. Result is exactly `10.0`.

**Case 4 — Long chain, one double in the middle:** same chain, but `1.2` is `new BigDecimal(1.2)`. Every step after that carries the error. Result is `9.9999999999999999...` instead of `10.0`.

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
  Expected 0.00 — exact? true

=== Case 2: Chain poisoned by a double at one step ===
  new BigDecimal(0.3) actual value  : 0.299999999999999988897769753748434595763683319091796875
  0.1 + 0.2                         = 0.3
  (0.1 + 0.2) * new BigDecimal(0.3) = 0.0899999999999999966693309261245303787291049957275390625
  ... - 0.09                        = -3.3306690738754696212708950042724609375E-18
  Expected 0.00 — exact? false

=== Case 3: Long chain — all BigDecimal, no loss ===
  10.0 - 3.1 = 6.9
  6.9  * 2.0 = 13.8
  13.8 + 1.2 = 15.0
  15.0 - 5.0 = 10.00
  Expected 10.0 — exact? true

=== Case 4: Same long chain — one double in the middle ===
  new BigDecimal(1.2) actual value: 1.1999999999999999555910790149937383830547332763671875
  10.0 - 3.1 = 6.9
  6.9  * 2.0 = 13.8
  13.8 + new BigDecimal(1.2) = 14.9999999999999999555910790149937383830547332763671875
  ... - 5.0  = 9.9999999999999999555910790149937383830547332763671875
  Expected 10.0 — exact? false
```
