/**
 * An immutable set of integers in the range 1 to 1000.
 * Once created, a set never changes; operations return new sets.
 */
public final class IntSet {

    

    private final boolean[] members = new boolean[1001]; // index = the number itself, index 0 unused

    /**
     * Creates a set containing the given numbers. Duplicates are ignored,
     * and no arguments gives the empty set.
     *
     * @param elements the numbers to include
     * @throws IllegalArgumentException if any number is outside 1..1000
     * @throws NullPointerException     if { elements} is null
     */
    public IntSet(int... elements) {
        for (int x : elements) {
            if (x < 1 || x > 1000) {
                throw new IllegalArgumentException("Out of range: " + x);
            }
            members[x] = true;
        }
    }

    /** Internal use only: wraps the result array of an operation. */
    private IntSet(boolean[] bits) {
        for (int i = 1; i <= 1000; i++) {
            members[i] = bits[i];
        }
    }

    /**
     * Checks whether a number is in this set.
     *
     * @param x the number to look for
     * @return true if x is a member; false otherwise (including when x is outside 1..1000)
     */
    public boolean isMember(int x) {
        return x >= 1 && x <= 1000 && members[x];
    }

    /**
     * Returns how many numbers are in this set.
     *
     * @return the size, from 0 to 1000
     */
    public int size() {
        int count = 0;
        for (int i = 1; i <= 1000; i++) {
            if (members[i]) count++;
        }
        return count;
    }

    /**
     * Checks whether every member of {s} is also in this set.
     * The empty set is a subset of every set, and every set is a subset of itself.
     *
     * @param s the candidate subset
     * @return true if s is a subset of this set
     * @throws NullPointerException if s is null
     */
    public boolean isSubSet(IntSet s) {
        for (int i = 1; i <= 1000; i++) {
            if (s.members[i] && !members[i]) return false;
        }
        return true;
    }

    /**
     * Returns the complement of this set, taking 1..1000 as the universal set.
     *
     * @return a new set of all numbers in 1..1000 that are not in this set
     */
    public IntSet getComplement() {
        boolean[] result = new boolean[1000 + 1];
        for (int i = 1; i <= 1000; i++) {
            result[i] = !members[i];
        }
        return new IntSet(result);
    }

    /**
     * Returns the union: numbers in this set or in {@code s}.
     *
     * @param s the other set
     * @return a new set containing the union
     * @throws NullPointerException if s is null
     */
    public IntSet union(IntSet s) {
        boolean[] result = new boolean[1000 + 1];
        for (int i = 1; i <= 1000; i++) {
            result[i] = members[i] || s.members[i];
        }
        return new IntSet(result);
    }

    /**
     * Returns the intersection: numbers in both this set and {@code s}.
     *
     * @param s the other set
     * @return a new set containing the intersection
     * @throws NullPointerException if s is null
     */
    public IntSet intersection(IntSet s) {
        boolean[] result = new boolean[1000 + 1];
        for (int i = 1; i <= 1000; i++) {
            result[i] = members[i] && s.members[i];
        }
        return new IntSet(result);
    }

    /**
     * Returns the difference: numbers in this set but not in {@code s}.
     *
     * @param s the set to subtract
     * @return a new set containing this 1us s
     * @throws NullPointerException if s is null
     */
    public IntSet difference(IntSet s) {
        boolean[] result = new boolean[1000 + 1];
        for (int i = 1; i <= 1000; i++) {
            result[i] = members[i] && !s.members[i];
        }
        return new IntSet(result);
    }

    /**
     * Returns the members in ascending order, such as "{1, 2, 3}"; the empty set is "{}".
     * @return a text form of this set
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 1; i <= 1000; i++) {
            if (members[i]) {
                if (sb.length() > 1) sb.append(", ");
                sb.append(i);
            }
        }
        return sb.append("}").toString();
    }
}