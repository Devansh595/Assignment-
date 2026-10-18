import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntSetTest {

    private IntSet a;      // {2, 4, 6, 8, 10}
    private IntSet b;      // {1, 2, 3, 4, 5}
    private IntSet empty;  // {}

    // Runs before EVERY test, so each test gets fresh sets
    @BeforeEach
    void setUp() {
        a = new IntSet(2, 4, 6, 8, 10);
        b = new IntSet(1, 2, 3, 4, 5);
        empty = new IntSet();
    }

    // ---------- helpers ----------

    private static IntSet fullSet() {
        int[] all = new int[1000];
        for (int i = 0; i < 1000; i++) {
            all[i] = i + 1;
        }
        return new IntSet(all);
    }

    private static void assertSameMembers(IntSet expected, IntSet actual) {
        for (int i = 1; i <= 1000; i++) {
            assertEquals(expected.isMember(i), actual.isMember(i), "difference at value " + i);
        }
    }

    // ---------- constructor ----------

    @Test
    void constructor_noArgs_createsEmptySet() {
        assertEquals(0, new IntSet().size());
    }

    @Test
    void constructor_singleElement_isMember() {
        assertTrue(new IntSet(5).isMember(5));
    }

    @Test
    void constructor_duplicates_storedOnce() {
        assertEquals(1, new IntSet(4, 4, 4).size());
    }

    @Test
    void constructor_boundaries_accepted() {
        IntSet s = new IntSet(1, 1000);
        assertTrue(s.isMember(1));
        assertTrue(s.isMember(1000));
    }

    @Test
    void constructor_acceptsIntArray() {
        assertEquals(2, new IntSet(new int[]{7, 8}).size());
    }

    @Test
    void constructor_zero_throws() {
        assertThrows(IllegalArgumentException.class, () -> new IntSet(0));
    }

    @Test
    void constructor_1001_throws() {
        assertThrows(IllegalArgumentException.class, () -> new IntSet(1001));
    }

    @Test
    void constructor_negative_throws() {
        assertThrows(IllegalArgumentException.class, () -> new IntSet(-5));
    }

    @Test
    void constructor_integerMax_throws() {
        assertThrows(IllegalArgumentException.class, () -> new IntSet(Integer.MAX_VALUE));
    }

    @Test
    void constructor_oneBadValueAmongGood_throws() {
        assertThrows(IllegalArgumentException.class, () -> new IntSet(1, 2, 5000, 3));
    }

    @Test
    void constructor_nullArray_throws() {
        assertThrows(NullPointerException.class, () -> new IntSet((int[]) null));
    }

    @Test
    void constructor_doesNotKeepCallersArray() {
        int[] input = {1, 2, 3};
        IntSet s = new IntSet(input);
        input[0] = 999;
        assertTrue(s.isMember(1));
        assertFalse(s.isMember(999));
    }

    // ---------- isMember ----------

    @Test
    void isMember_presentValue_true() {
        assertTrue(a.isMember(4));
    }

    @Test
    void isMember_absentValue_false() {
        assertFalse(a.isMember(5));
    }

    @Test
    void isMember_emptySet_false() {
        assertFalse(empty.isMember(1));
    }

    @Test
    void isMember_outOfRange_false() {
        assertFalse(a.isMember(0));
        assertFalse(a.isMember(1001));
        assertFalse(a.isMember(-1));
        assertFalse(a.isMember(Integer.MIN_VALUE));
        assertFalse(a.isMember(Integer.MAX_VALUE));
    }

    // ---------- size ----------

    @Test
    void size_empty_isZero() {
        assertEquals(0, empty.size());
    }

    @Test
    void size_normalSet() {
        assertEquals(5, a.size());
    }

    @Test
    void size_fullSet_is1000() {
        assertEquals(1000, fullSet().size());
    }

    // ---------- isSubSet ----------

    @Test
    void isSubSet_properSubset_true() {
        assertTrue(a.isSubSet(new IntSet(2, 4)));
    }

    @Test
    void isSubSet_equalSets_true() {
        assertTrue(a.isSubSet(new IntSet(2, 4, 6, 8, 10)));
    }

    @Test
    void isSubSet_itself_true() {
        assertTrue(a.isSubSet(a));
    }

    @Test
    void isSubSet_emptyIsSubsetOfAnySet() {
        assertTrue(a.isSubSet(empty));
        assertTrue(empty.isSubSet(empty));
    }

    @Test
    void isSubSet_everythingIsSubsetOfFullSet() {
        assertTrue(fullSet().isSubSet(a));
    }

    @Test
    void isSubSet_partialOverlap_false() {
        assertFalse(a.isSubSet(new IntSet(2, 3)));
    }

    @Test
    void isSubSet_disjoint_false() {
        assertFalse(a.isSubSet(new IntSet(1, 3, 5)));
    }

    @Test
    void isSubSet_superset_false() {
        assertFalse(new IntSet(2, 4).isSubSet(a));
    }

    @Test
    void isSubSet_nonEmptyIsNotSubsetOfEmpty() {
        assertFalse(empty.isSubSet(new IntSet(1)));
    }

    @Test
    void isSubSet_null_throws() {
        assertThrows(NullPointerException.class, () -> a.isSubSet(null));
    }

    // ---------- getComplement ----------

    @Test
    void complement_sizeIs1000MinusSize() {
        assertEquals(995, a.getComplement().size());
    }

    @Test
    void complement_excludesOriginalMembers() {
        IntSet c = a.getComplement();
        assertFalse(c.isMember(2));
        assertFalse(c.isMember(10));
    }

    @Test
    void complement_includesOtherNumbers() {
        IntSet c = a.getComplement();
        assertTrue(c.isMember(1));
        assertTrue(c.isMember(3));
        assertTrue(c.isMember(1000));
    }

    @Test
    void complement_ofEmpty_isFullSet() {
        assertSameMembers(fullSet(), empty.getComplement());
    }

    @Test
    void complement_ofFullSet_isEmpty() {
        assertEquals(0, fullSet().getComplement().size());
    }

    @Test
    void complement_twice_givesOriginal() {
        assertSameMembers(a, a.getComplement().getComplement());
    }

    @Test
    void complement_isDisjointFromOriginal_andTogetherFormUniverse() {
        IntSet c = a.getComplement();
        assertEquals(0, a.intersection(c).size());
        assertEquals(1000, a.union(c).size());
    }

    // ---------- union ----------

    @Test
    void union_sheetExample() {
        assertSameMembers(new IntSet(1, 2, 3, 4, 5, 6, 8, 10), a.union(b));
    }

    @Test
    void union_isCommutative() {
        assertSameMembers(a.union(b), b.union(a));
    }

    @Test
    void union_withEmpty_givesSameSet() {
        assertSameMembers(a, a.union(empty));
    }

    @Test
    void union_emptyWithEmpty_isEmpty() {
        assertEquals(0, empty.union(new IntSet()).size());
    }

    @Test
    void union_withItself_givesSameSet() {
        assertSameMembers(a, a.union(a));
    }

    @Test
    void union_withFullSet_isFull() {
        assertEquals(1000, a.union(fullSet()).size());
    }

    @Test
    void union_null_throws() {
        assertThrows(NullPointerException.class, () -> a.union(null));
    }

    // ---------- intersection ----------

    @Test
    void intersection_sheetExample() {
        assertSameMembers(new IntSet(2, 4), a.intersection(b));
    }

    @Test
    void intersection_isCommutative() {
        assertSameMembers(a.intersection(b), b.intersection(a));
    }

    @Test
    void intersection_disjoint_isEmpty() {
        assertEquals(0, a.intersection(new IntSet(1, 3, 5)).size());
    }

    @Test
    void intersection_withEmpty_isEmpty() {
        assertEquals(0, a.intersection(empty).size());
    }

    @Test
    void intersection_withItself_givesSameSet() {
        assertSameMembers(a, a.intersection(a));
    }

    @Test
    void intersection_withFullSet_givesSameSet() {
        assertSameMembers(a, a.intersection(fullSet()));
    }

    @Test
    void intersection_null_throws() {
        assertThrows(NullPointerException.class, () -> a.intersection(null));
    }

    // ---------- difference ----------

    @Test
    void difference_AminusB_sheetExample() {
        assertSameMembers(new IntSet(6, 8, 10), a.difference(b));
    }

    @Test
    void difference_BminusA() {
        assertSameMembers(new IntSet(1, 3, 5), b.difference(a));
    }

    @Test
    void difference_minusItself_isEmpty() {
        assertEquals(0, a.difference(a).size());
    }

    @Test
    void difference_minusEmpty_givesSameSet() {
        assertSameMembers(a, a.difference(empty));
    }

    @Test
    void difference_emptyMinusAnything_isEmpty() {
        assertEquals(0, empty.difference(a).size());
    }

    @Test
    void difference_disjoint_givesSameSet() {
        assertSameMembers(a, a.difference(new IntSet(1, 3)));
    }

    @Test
    void difference_null_throws() {
        assertThrows(NullPointerException.class, () -> a.difference(null));
    }

    // ---------- immutability ----------

    @Test
    void operations_doNotChangeOperands() {
        a.union(b);
        a.intersection(b);
        a.difference(b);
        a.getComplement();
        assertSameMembers(new IntSet(2, 4, 6, 8, 10), a);
        assertSameMembers(new IntSet(1, 2, 3, 4, 5), b);
    }

    @Test
    void union_returnsNewObject() {
        assertNotSame(a, a.union(b));
        assertNotSame(b, a.union(b));
    }

    // ---------- toString ----------

    @Test
    void toString_emptySet() {
        assertEquals("{}", empty.toString());
    }

    @Test
    void toString_singleElement() {
        assertEquals("{7}", new IntSet(7).toString());
    }

    @Test
    void toString_isAscendingRegardlessOfInputOrder() {
        assertEquals("{1, 2, 3}", new IntSet(3, 1, 2).toString());
    }
}