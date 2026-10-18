import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PolyTest{

    private static final float DELTA = 1e-3f;

    private Poly p;     // 3x^2 + 2x - 5
    private Poly q;     // x + 1
    private Poly r;     // 4x^2 + 2
    private Poly zero;  // 0
    private Poly one;   // 1

    // fresh polynomials before each test
    @BeforeEach
    void setUp() {
        p = new Poly(-5, 2, 3);
        q = new Poly(1, 1);
        r = new Poly(2, 0, 4);
        zero = new Poly();
        one = new Poly(1);
    }

    // ---------- constructor ----------

    @Test
    void constructor_noArgs_isZeroPolynomial() {
        assertEquals(0, zero.numTerms());
        assertEquals(0, zero.degree());
    }

    @Test
    void constructor_singleConstant() {
        Poly c = new Poly(5);
        assertEquals(1, c.numTerms());
        assertEquals(5, c.coefficient(0));
        assertEquals(0, c.degree());
    }

    @Test
    void constructor_positionIsExponent() {
        Poly s = new Poly(1, 2, 3);
        assertEquals(1, s.coefficient(0));
        assertEquals(2, s.coefficient(1));
        assertEquals(3, s.coefficient(2));
    }

    @Test
    void constructor_zeroCoefficientsAreNotStored() {
        Poly s = new Poly(0, 0, 5, 0);
        assertEquals(1, s.numTerms());
        assertEquals(2, s.degree());
        assertEquals(5, s.coefficient(2));
    }

    @Test
    void constructor_allZeros_isZeroPolynomial() {
        assertEquals(zero, new Poly(0, 0, 0));
        assertEquals(0, new Poly(0, 0, 0).numTerms());
    }

    @Test
    void constructor_trailingZerosIgnored() {
        Poly s = new Poly(1, 0, 0);
        assertEquals(0, s.degree());
        assertEquals(1, s.numTerms());
    }

    @Test
    void constructor_negativeCoefficientsKept() {
        assertEquals(-5, p.coefficient(0));
    }

    @Test
    void constructor_acceptsIntArray() {
        assertEquals(p, new Poly(new int[]{-5, 2, 3}));
    }

    @Test
    void constructor_null_throws() {
        assertThrows(NullPointerException.class, () -> new Poly((int[]) null));
    }

    @Test
    void constructor_doesNotKeepCallersArray() {
        int[] input = {1, 2, 3};
        Poly s = new Poly(input);
        input[0] = 99;
        assertEquals(1, s.coefficient(0));
    }

    // ---------- term ----------

    @Test
    void term_createsSingleTerm() {
        Poly t = Poly.term(3, 2);
        assertEquals(1, t.numTerms());
        assertEquals(3, t.coefficient(2));
        assertEquals(2, t.degree());
    }

    @Test
    void term_constant() {
        assertEquals(new Poly(5), Poly.term(5, 0));
    }

    @Test
    void term_zeroCoefficient_isZeroPolynomial() {
        assertEquals(zero, Poly.term(0, 7));
    }

    @Test
    void term_negativeExponent_throws() {
        assertThrows(IllegalArgumentException.class, () -> Poly.term(1, -1));
    }

    // ---------- coefficient ----------

    @Test
    void coefficient_presentTerms() {
        assertEquals(3, p.coefficient(2));
        assertEquals(2, p.coefficient(1));
    }

    @Test
    void coefficient_missingExponent_isZero() {
        assertEquals(0, Poly.term(1, 5).coefficient(3));
    }

    @Test
    void coefficient_beyondDegree_isZero() {
        assertEquals(0, p.coefficient(10));
    }

    @Test
    void coefficient_negativeExponent_isZero() {
        assertEquals(0, p.coefficient(-1));
    }

    @Test
    void coefficient_ofZeroPolynomial_isZero() {
        assertEquals(0, zero.coefficient(0));
    }

    // ---------- degree ----------

    @Test
    void degree_normalPolynomial() {
        assertEquals(2, p.degree());
        assertEquals(1, q.degree());
    }

    @Test
    void degree_constant_isZero() {
        assertEquals(0, new Poly(7).degree());
    }

    @Test
    void degree_zeroPolynomial_isZero() {
        assertEquals(0, zero.degree());
    }

    @Test
    void degree_sparseHighExponent() {
        assertEquals(1000, Poly.term(1, 1000).degree());
    }

    @Test
    void degree_dropsWhenLeadingTermsCancel() {
        Poly a = new Poly(1, 0, 1);    // x^2 + 1
        Poly b = new Poly(0, 0, -1);   // -x^2
        assertEquals(0, Poly.addPoly(a, b).degree());
    }

    // ---------- evaluate ----------

    @Test
    void evaluate_atZero_isConstantTerm() {
        assertEquals(-5f, p.evaluate(0f), DELTA);
    }

    @Test
    void evaluate_atOne_isSumOfCoefficients() {
        assertEquals(0f, p.evaluate(1f), DELTA);
    }

    @Test
    void evaluate_atTwo() {
        assertEquals(11f, p.evaluate(2f), DELTA);
    }

    @Test
    void evaluate_atNegativeValue() {
        assertEquals(-4f, p.evaluate(-1f), DELTA);
    }

    @Test
    void evaluate_atFraction() {
        assertEquals(-3.25f, p.evaluate(0.5f), DELTA);
    }

    @Test
    void evaluate_zeroPolynomial_isZeroEverywhere() {
        assertEquals(0f, zero.evaluate(0f), DELTA);
        assertEquals(0f, zero.evaluate(123.5f), DELTA);
    }

    @Test
    void evaluate_constant_ignoresX() {
        assertEquals(7f, new Poly(7).evaluate(0f), DELTA);
        assertEquals(7f, new Poly(7).evaluate(-9f), DELTA);
    }

    @Test
    void evaluate_cube() {
        assertEquals(27f, Poly.term(1, 3).evaluate(3f), DELTA);
    }

    @Test
    void evaluate_oddPowerOfNegativeNumber_isNegative() {
        assertEquals(-8f, Poly.term(1, 3).evaluate(-2f), DELTA);
    }

    @Test
    void evaluate_highExponentTerm() {
        assertEquals(2048f, Poly.term(2, 10).evaluate(2f), DELTA);
    }

    // ---------- addPoly ----------

    @Test
    void add_differentDegrees() {
        assertEquals(new Poly(-4, 3, 3), Poly.addPoly(p, q));
    }

    @Test
    void add_isCommutative() {
        assertEquals(Poly.addPoly(p, q), Poly.addPoly(q, p));
    }

    @Test
    void add_isAssociative() {
        assertEquals(Poly.addPoly(Poly.addPoly(p, q), r), Poly.addPoly(p, Poly.addPoly(q, r)));
    }

    @Test
    void add_zeroOnEitherSide_givesSamePolynomial() {
        assertEquals(p, Poly.addPoly(p, zero));
        assertEquals(p, Poly.addPoly(zero, p));
    }

    @Test
    void add_zeroToZero_isZero() {
        assertEquals(zero, Poly.addPoly(zero, new Poly()));
    }

    @Test
    void add_oppositePolynomials_giveZero() {
        Poly negP = new Poly(5, -2, -3);
        Poly sum = Poly.addPoly(p, negP);
        assertEquals(zero, sum);
        assertEquals(0, sum.numTerms());
    }

    @Test
    void add_cancellingTermIsDropped() {
        Poly a = new Poly(0, 1, 1);    // x^2 + x
        Poly b = new Poly(1, 0, -1);   // -x^2 + 1
        Poly sum = Poly.addPoly(a, b); // x + 1
        assertEquals(new Poly(1, 1), sum);
        assertEquals(2, sum.numTerms());
        assertEquals(1, sum.degree());
    }

    @Test
    void add_withItself_doublesCoefficients() {
        assertEquals(new Poly(-10, 4, 6), Poly.addPoly(p, p));
    }

    @Test
    void add_sparseTerms() {
        Poly sum = Poly.addPoly(Poly.term(1, 1000), Poly.term(1, 0));
        assertEquals(2, sum.numTerms());
        assertEquals(1000, sum.degree());
        assertEquals(0, sum.coefficient(500));
    }

    @Test
    void add_nullArguments_throw() {
        assertThrows(NullPointerException.class, () -> Poly.addPoly(null, p));
        assertThrows(NullPointerException.class, () -> Poly.addPoly(p, null));
    }

    @Test
    void add_coefficientOverflow_throws() {
        Poly big = Poly.term(Integer.MAX_VALUE, 0);
        assertThrows(ArithmeticException.class, () -> Poly.addPoly(big, one));
    }

    @Test
    void add_evaluatesToSumOfValues() {
        assertEquals(p.evaluate(2f) + q.evaluate(2f), Poly.addPoly(p, q).evaluate(2f), DELTA);
    }

    // ---------- multiplyPoly ----------

    @Test
    void multiply_differenceOfSquares() {
        Poly xPlus1 = new Poly(1, 1);
        Poly xMinus1 = new Poly(-1, 1);
        assertEquals(new Poly(-1, 0, 1), Poly.multiplyPoly(xPlus1, xMinus1));
    }

    @Test
    void multiply_cancellingMiddleTermIsDropped() {
        Poly product = Poly.multiplyPoly(new Poly(1, 1), new Poly(-1, 1));
        assertEquals(2, product.numTerms());
        assertEquals(0, product.coefficient(1));
    }

    @Test
    void multiply_square() {
        assertEquals(new Poly(1, 2, 1), Poly.multiplyPoly(q, q));
    }

    @Test
    void multiply_generalCase() {
        assertEquals(new Poly(-5, -3, 5, 3), Poly.multiplyPoly(p, q));
    }

    @Test
    void multiply_isCommutative() {
        assertEquals(Poly.multiplyPoly(p, q), Poly.multiplyPoly(q, p));
    }

    @Test
    void multiply_isAssociative() {
        assertEquals(Poly.multiplyPoly(Poly.multiplyPoly(p, q), r),
                     Poly.multiplyPoly(p, Poly.multiplyPoly(q, r)));
    }

    @Test
    void multiply_distributesOverAddition() {
        Poly left = Poly.multiplyPoly(p, Poly.addPoly(q, r));
        Poly right = Poly.addPoly(Poly.multiplyPoly(p, q), Poly.multiplyPoly(p, r));
        assertEquals(left, right);
    }

    @Test
    void multiply_byZero_isZero() {
        assertEquals(zero, Poly.multiplyPoly(p, zero));
        assertEquals(zero, Poly.multiplyPoly(zero, p));
        assertEquals(zero, Poly.multiplyPoly(zero, zero));
    }

    @Test
    void multiply_byOne_givesSamePolynomial() {
        assertEquals(p, Poly.multiplyPoly(p, one));
        assertEquals(p, Poly.multiplyPoly(one, p));
    }

    @Test
    void multiply_byConstant_scalesCoefficients() {
        assertEquals(new Poly(-15, 6, 9), Poly.multiplyPoly(p, new Poly(3)));
    }

    @Test
    void multiply_degreesAdd() {
        assertEquals(p.degree() + q.degree(), Poly.multiplyPoly(p, q).degree());
    }

    @Test
    void multiply_sparseTerms() {
        assertEquals(Poly.term(1, 1000), Poly.multiplyPoly(Poly.term(1, 500), Poly.term(1, 500)));
    }

    @Test
    void multiply_nullArguments_throw() {
        assertThrows(NullPointerException.class, () -> Poly.multiplyPoly(null, p));
        assertThrows(NullPointerException.class, () -> Poly.multiplyPoly(p, null));
    }

    @Test
    void multiply_coefficientOverflow_throws() {
        Poly a = Poly.term(65536, 0);
        assertThrows(ArithmeticException.class, () -> Poly.multiplyPoly(a, a));
    }

    @Test
    void multiply_exponentOverflow_throws() {
        Poly a = Poly.term(1, Integer.MAX_VALUE);
        assertThrows(ArithmeticException.class, () -> Poly.multiplyPoly(a, q));
    }

    @Test
    void multiply_evaluatesToProductOfValues() {
        assertEquals(p.evaluate(2f) * q.evaluate(2f), Poly.multiplyPoly(p, q).evaluate(2f), DELTA);
    }

    // ---------- immutability ----------

    @Test
    void operations_doNotChangeOperands() {
        Poly.addPoly(p, q);
        Poly.multiplyPoly(p, q);
        assertEquals(new Poly(-5, 2, 3), p);
        assertEquals(new Poly(1, 1), q);
    }

    // ---------- equals / hashCode ----------

    @Test
    void equals_sameTermsBuiltDifferently() {
        Poly built = Poly.addPoly(Poly.term(1, 0), Poly.term(1, 2));
        assertEquals(new Poly(1, 0, 1), built);
    }

    @Test
    void equals_differentCoefficient_notEqual() {
        assertNotEquals(new Poly(1, 2), new Poly(1, 3));
    }

    @Test
    void equals_differentExponent_notEqual() {
        assertNotEquals(Poly.term(1, 2), Poly.term(1, 3));
    }

    @Test
    void equals_null_isFalse() {
        assertFalse(p.equals(null));
    }

    @Test
    void equals_otherType_isFalse() {
        assertNotEquals(p, "3x^2 + 2x - 5");
    }

    @Test
    void equals_itself_isTrue() {
        assertTrue(p.equals(p));
    }

    @Test
    void hashCode_equalPolynomialsHaveSameHash() {
        assertEquals(new Poly(-5, 2, 3).hashCode(), p.hashCode());
    }

    // ---------- toString ----------

    @Test
    void toString_zero() {
        assertEquals("0", zero.toString());
    }

    @Test
    void toString_constants() {
        assertEquals("5", new Poly(5).toString());
        assertEquals("-5", new Poly(-5).toString());
        assertEquals("1", new Poly(1).toString());
    }

    @Test
    void toString_unitCoefficientsHaveNoNumber() {
        assertEquals("x", Poly.term(1, 1).toString());
        assertEquals("-x", Poly.term(-1, 1).toString());
        assertEquals("x^2", Poly.term(1, 2).toString());
    }

    @Test
    void toString_fullPolynomial_highestExponentFirst() {
        assertEquals("3x^2 + 2x - 5", p.toString());
    }

    @Test
    void toString_negativeLeadingTerm() {
        assertEquals("-x^2 + 2", new Poly(2, 0, -1).toString());
    }

    @Test
    void toString_skipsMissingTerms() {
        assertEquals("x^3 - 1", new Poly(-1, 0, 0, 1).toString());
    }

    @Test
    void toString_sparseHighExponent() {
        assertEquals("x^1000", Poly.term(1, 1000).toString());
    }
}