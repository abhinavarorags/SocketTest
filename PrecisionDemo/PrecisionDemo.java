import java.math.BigDecimal;
import java.math.MathContext;

public class PrecisionDemo {

    public static void main(String[] args) {
        System.out.println("=== Case 1: Pure BigDecimal chain (String-initialized) ===");
        pureChain();

        System.out.println();

        System.out.println("=== Case 2: Chain poisoned by a double at one step ===");
        doublePoison();
    }

    // All values come from String literals — BigDecimal stays exact throughout
    static void pureChain() {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        BigDecimal c = new BigDecimal("0.3");

        BigDecimal sum        = a.add(b);           // 0.1 + 0.2
        BigDecimal product    = sum.multiply(c);    // 0.3 * 0.3
        BigDecimal subtracted = product.subtract(new BigDecimal("0.09")); // 0.09 - 0.09

        System.out.println("  0.1 + 0.2                     = " + sum);
        System.out.println("  (0.1 + 0.2) * 0.3             = " + product);
        System.out.println("  ((0.1+0.2)*0.3) - 0.09        = " + subtracted);
        System.out.println("  Expected: 0.3, 0.09, 0.00 — exact? " + (subtracted.compareTo(BigDecimal.ZERO) == 0));
    }

    // One intermediate value comes from a double — poisons the entire chain
    static void doublePoison() {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");

        // This is the problem: constructing BigDecimal from a double literal
        // 0.3 as a double is NOT exactly 0.3 in binary floating point
        BigDecimal poisoned = new BigDecimal(0.3);  // <-- the culprit

        BigDecimal sum        = a.add(b);              // still exact here: 0.3
        BigDecimal product    = sum.multiply(poisoned); // multiplied by the corrupted 0.3
        BigDecimal subtracted = product.subtract(new BigDecimal("0.09"));

        System.out.println("  new BigDecimal(0.3) actual value: " + poisoned);
        System.out.println("  0.1 + 0.2                        = " + sum);
        System.out.println("  (0.1 + 0.2) * new BigDecimal(0.3)= " + product);
        System.out.println("  ... - 0.09                       = " + subtracted);
        System.out.println("  Expected 0.00, got: " + subtracted + " — precision lost!");
    }
}
