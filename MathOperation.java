/**
 * Mathematical utilities implemented using recursion.
 */
public class MathOperation {

    private static void requirePositive(int x, int y) {
        if (x <= 0 || y <= 0) {
            throw new IllegalArgumentException("x and y must be positive integers");
        }
    }

    /**
     * Computes the H.C.F. (G.C.D.) of two numbers using Euclid's algorithm:
     * hcf(x, 0) = x, otherwise hcf(x, y) = hcf(y, x % y).
     *
     * <pre>
     * hcf(12, 18) -> 6
     * hcf(7, 13)  -> 1
     * </pre>
     *
     * @param x first positive integer
     * @param y second positive integer
     * @return highest common factor of x and y
     * @requires x > 0 and y > 0
     * @effects  returns the largest positive integer that divides both x and y;
     *           throws IllegalArgumentException if x or y is not positive
     */
    public static int hcf(int x, int y) {
        requirePositive(x, y);
        return hcfRecursive(x, y);
    }

    private static int hcfRecursive(int x, int y) {
        if (y == 0) {
            return x;                        // base case
        }
        return hcfRecursive(y, x % y);       // recursive case
    }

    /**
     * Computes the L.C.M. of two numbers using
     * lcm(x, y) = (x / hcf(x, y)) * y.
     *
     * <pre>
     * lcm(4, 6)  -> 12
     * lcm(7, 13) -> 91
     * </pre>
     *
     * @param x first positive integer
     * @param y second positive integer
     * @return least common multiple of x and y
     * @requires x > 0 and y > 0
     * @effects  returns the smallest positive integer divisible by both x and y;
     *           throws IllegalArgumentException if x or y is not positive;
     *           throws ArithmeticException if the result does not fit in an int
     */
    public static int lcm(int x, int y) {
        requirePositive(x, y);
        return Math.multiplyExact(x / hcfRecursive(x, y), y);
    }
}
