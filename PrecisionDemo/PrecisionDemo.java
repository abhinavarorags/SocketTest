import java.math.BigDecimal;

public class PrecisionDemo {

    public static void main(String[] args) {
        System.out.println("=== Case 1: Pure BigDecimal chain (String-initialized) ===");
        pureChain();

        System.out.println();

        System.out.println("=== Case 2: Chain poisoned by a double at one step ===");
        doublePoison();

        System.out.println();

        System.out.println("=== Case 3: Long chain — all BigDecimal, no loss ===");
        longChainClean();

        System.out.println();

        System.out.println("=== Case 4: Same long chain — one double in the middle ===");
        longChainPoisoned();
    }

    // All values come from String literals — BigDecimal stays exact throughout
    static void pureChain() {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        BigDecimal c = new BigDecimal("0.3");

        BigDecimal sum        = a.add(b);
        BigDecimal product    = sum.multiply(c);
        BigDecimal subtracted = product.subtract(new BigDecimal("0.09"));

        System.out.println("  0.1 + 0.2                     = " + sum);
        System.out.println("  (0.1 + 0.2) * 0.3             = " + product);
        System.out.println("  ((0.1+0.2)*0.3) - 0.09        = " + subtracted);
        System.out.println("  Expected 0.00 — exact? " + (subtracted.compareTo(BigDecimal.ZERO) == 0));
    }

    // One intermediate value comes from a double — poisons the entire chain
    static void doublePoison() {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        BigDecimal poisoned = new BigDecimal(0.3);  // <-- culprit

        BigDecimal sum        = a.add(b);
        BigDecimal product    = sum.multiply(poisoned);
        BigDecimal subtracted = product.subtract(new BigDecimal("0.09"));

        System.out.println("  new BigDecimal(0.3) actual value  : " + poisoned);
        System.out.println("  0.1 + 0.2                         = " + sum);
        System.out.println("  (0.1 + 0.2) * new BigDecimal(0.3) = " + product);
        System.out.println("  ... - 0.09                        = " + subtracted);
        System.out.println("  Expected 0.00 — exact? " + (subtracted.compareTo(BigDecimal.ZERO) == 0));
    }

    // Longer chain: 10.0 - 3.1 * 2.0 + 1.2 - 5.0 = 10.0  (all String-init)
    static void longChainClean() {
        BigDecimal result = new BigDecimal("10.0")
            .subtract(new BigDecimal("3.1"))   // 6.9
            .multiply(new BigDecimal("2.0"))   // 13.8
            .add(new BigDecimal("1.2"))        // 15.0
            .subtract(new BigDecimal("5.0"));  // 10.0

        System.out.println("  10.0 - 3.1 = 6.9");
        System.out.println("  6.9  * 2.0 = 13.8");
        System.out.println("  13.8 + 1.2 = 15.0");
        System.out.println("  15.0 - 5.0 = " + result);
        System.out.println("  Expected 10.0 — exact? " + (result.compareTo(new BigDecimal("10.0")) == 0));
    }

    // Same chain, but 1.2 in the middle is a double — corrupts the result
    static void longChainPoisoned() {
        BigDecimal poisoned_1_2 = new BigDecimal(1.2);  // <-- culprit, injected mid-chain

        BigDecimal result = new BigDecimal("10.0")
            .subtract(new BigDecimal("3.1"))   // 6.9   — still fine
            .multiply(new BigDecimal("2.0"))   // 13.8  — still fine
            .add(poisoned_1_2)                 // <-- poison enters here
            .subtract(new BigDecimal("5.0"));  // error propagates to final result

        System.out.println("  new BigDecimal(1.2) actual value: " + poisoned_1_2);
        System.out.println("  10.0 - 3.1 = 6.9");
        System.out.println("  6.9  * 2.0 = 13.8");
        System.out.println("  13.8 + new BigDecimal(1.2) = " + new BigDecimal("13.8").add(poisoned_1_2));
        System.out.println("  ... - 5.0  = " + result);
        System.out.println("  Expected 10.0 — exact? " + (result.compareTo(new BigDecimal("10.0")) == 0));
    }
}
