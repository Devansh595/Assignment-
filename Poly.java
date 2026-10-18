import java.util.Arrays;
import java.util.Objects;

/**
 * An immutable polynomial with integer coefficients, such as 3x^2 + 2x - 5.
 * Only terms with a non-zero coefficient are stored.
 * Arguments to methods must not be null.
 */
public final class Poly {

    private final int[] coeffs; // non-zero coefficients
    private final int[] exps;   // matching exponents, sorted from smallest to largest

    /**
     * Creates a polynomial where the position is the exponent:
     * {@code new Poly(-5, 2, 3)} is -5 + 2x + 3x^2. Zero coefficients are not stored.
     *
     * @param coefficients coefficients of x^0, x^1, x^2, ...
     */
    public Poly(int... coefficients) {
        int count = 0;
        for (int c : coefficients) {
            if (c != 0) count++;
        }
        coeffs = new int[count];
        exps = new int[count];
        int k = 0;
        for (int e = 0; e < coefficients.length; e++) {
            if (coefficients[e] != 0) {
                coeffs[k] = coefficients[e];
                exps[k] = e;
                k++;
            }
        }
    }

    /** Internal use only: takes ownership of valid, sorted arrays. */
    private Poly(int[] coeffs, int[] exps) {
        this.coeffs = coeffs;
        this.exps = exps;
    }

    /**
     * Creates the single term {@code coeff * x^exp}.
     *
     * @param coeff the coefficient
     * @param exp   the exponent
     * @return the new polynomial (zero polynomial if coeff is 0)
     * @throws IllegalArgumentException if exp is negative
     */
    public static Poly term(int coeff, int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Negative exponent: " + exp);
        }
        if (coeff == 0) {
            return new Poly();
        }
        return new Poly(new int[]{coeff}, new int[]{exp});
    }

    /**
     * Returns the largest exponent with a non-zero coefficient.
     * The zero polynomial has degree 0.
     *
     * @return the degree
     */
    public int degree() {
        return exps.length == 0 ? 0 : exps[exps.length - 1];
    }

    /**
     * Returns how many terms are stored.
     *
     * @return the number of non-zero terms
     */
    public int numTerms() {
        return exps.length;
    }

    /**
     * Returns the coefficient of {@code x^exp}.
     *
     * @param exp the exponent
     * @return the coefficient, or 0 if there is no such term
     */
    public int coefficient(int exp) {
        int index = Arrays.binarySearch(exps, exp);
        return index >= 0 ? coeffs[index] : 0;
    }

    /**
     * Evaluates the polynomial at x.
     *
     * @param x the value of the variable
     * @return the value of the polynomial
     */
    public float evaluate(float x) {
        double sum = 0;
        for (int k = 0; k < exps.length; k++) {
            sum += coeffs[k] * Math.pow(x, exps[k]);
        }
        return (float) sum;
    }

    /**
     * Adds two polynomials. Terms that cancel out are dropped.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return {@code p1 + p2}
     * @throws ArithmeticException if a coefficient overflows
     */
    public static Poly addPoly(Poly p1, Poly p2) {
        Objects.requireNonNull(p1, "p1 must not be null");
        Objects.requireNonNull(p2, "p2 must not be null");

        // the result has at most as many terms as p1 and p2 together
        int[] c = new int[p1.exps.length + p2.exps.length];
        int[] e = new int[c.length];
        int i = 0;
        int j = 0;
        int k = 0;

        while (i < p1.exps.length || j < p2.exps.length) {
            if (j == p2.exps.length || (i < p1.exps.length && p1.exps[i] < p2.exps[j])) {
                c[k] = p1.coeffs[i];            // term only in p1
                e[k] = p1.exps[i];
                k++;
                i++;
            } else if (i == p1.exps.length || p2.exps[j] < p1.exps[i]) {
                c[k] = p2.coeffs[j];            // term only in p2
                e[k] = p2.exps[j];
                k++;
                j++;
            } else {                            // same exponent in both
                int sum = Math.addExact(p1.coeffs[i], p2.coeffs[j]);
                if (sum != 0) {                 // skip terms that cancel out
                    c[k] = sum;
                    e[k] = p1.exps[i];
                    k++;
                }
                i++;
                j++;
            }
        }
        return new Poly(Arrays.copyOf(c, k), Arrays.copyOf(e, k));
    }

    /**
     * Multiplies two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return {@code p1 * p2}
     * @throws ArithmeticException if a coefficient or exponent overflows
     */
    public static Poly multiplyPoly(Poly p1, Poly p2) {
        Objects.requireNonNull(p1, "p1 must not be null");
        Objects.requireNonNull(p2, "p2 must not be null");

        Poly result = new Poly();
        for (int i = 0; i < p1.exps.length; i++) {
            result = addPoly(result, p2.timesTerm(p1.coeffs[i], p1.exps[i]));
        }
        return result;
    }

    /** Multiplies every term of this polynomial by {@code coeff * x^exp}. */
    private Poly timesTerm(int coeff, int exp) {
        int[] c = new int[exps.length];
        int[] e = new int[exps.length];
        for (int k = 0; k < exps.length; k++) {
            c[k] = Math.multiplyExact(coeffs[k], coeff);
            e[k] = Math.addExact(exps[k], exp);
        }
        return new Poly(c, e);
    }

    /**
     * Two polynomials are equal if they have the same terms.
     *
     * @param o the object to compare with
     * @return true if o is a Poly with the same terms
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poly)) return false;
        Poly other = (Poly) o;
        return Arrays.equals(coeffs, other.coeffs) && Arrays.equals(exps, other.exps);
    }

    /**
     * Returns a hash code that matches {@code equals}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(coeffs) + Arrays.hashCode(exps);
    }

    /**
     * Returns text like "3x^2 + 2x - 5", highest exponent first; "0" for the zero polynomial.
     *
     * @return the text form
     */
    @Override
    public String toString() {
        if (exps.length == 0) return "0";

        StringBuilder sb = new StringBuilder();
        for (int k = exps.length - 1; k >= 0; k--) {
            int c = coeffs[k];
            int e = exps[k];

            if (k == exps.length - 1) {
                if (c < 0) sb.append("-");
            } else {
                sb.append(c < 0 ? " - " : " + ");
            }

            long abs = Math.abs((long) c);
            if (e == 0 || abs != 1) sb.append(abs); // write "x", not "1x"
            if (e >= 1) sb.append("x");
            if (e > 1) sb.append("^").append(e);
        }
        return sb.toString();
    }
}