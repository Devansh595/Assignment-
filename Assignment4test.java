import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class Assignment4Test {

    // =====================================================================
    // Helpers
    // =====================================================================

    // "1 2 3" -> {1, 2, 3}
    private int[] toArray(String text) {
        String[] parts = text.trim().split(" ");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }

    // true if board has exactly n queens and none attack each other
    private boolean isValidQueenBoard(int[][] board, int n) {
        int count = 0;
        for (int r1 = 0; r1 < n; r1++) {
            for (int c1 = 0; c1 < n; c1++) {
                if (board[r1][c1] != 1) continue;
                count++;
                for (int r2 = 0; r2 < n; r2++) {
                    for (int c2 = 0; c2 < n; c2++) {
                        if (board[r2][c2] == 1 && !(r1 == r2 && c1 == c2)) {
                            boolean sameRow = r1 == r2;
                            boolean sameCol = c1 == c2;
                            boolean sameDiag = Math.abs(r1 - r2) == Math.abs(c1 - c2);
                            if (sameRow || sameCol || sameDiag) return false;
                        }
                    }
                }
            }
        }
        return count == n;
    }

    // true if 1..n*n each appear once and consecutive numbers are a knight move apart
    private boolean isValidKnightTour(int[][] board, int n) {
        int[] rowOf = new int[n * n + 1];
        int[] colOf = new int[n * n + 1];
        boolean[] seen = new boolean[n * n + 1];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int v = board[r][c];
                if (v < 1 || v > n * n || seen[v]) return false;
                seen[v] = true;
                rowOf[v] = r;
                colOf[v] = c;
            }
        }
        for (int v = 1; v < n * n; v++) {
            int dr = Math.abs(rowOf[v] - rowOf[v + 1]);
            int dc = Math.abs(colOf[v] - colOf[v + 1]);
            if (!((dr == 1 && dc == 2) || (dr == 2 && dc == 1))) return false;
        }
        return true;
    }

    // =====================================================================
    // Question 1 : HCF and LCM
    // =====================================================================

    @ParameterizedTest
    @CsvSource({
        "12, 18, 6",
        "7, 13, 1",
        "5, 5, 5",
        "100, 10, 10",
        "1, 9, 1",
        "18, 12, 6"
    })
    void hcf_validInputs(int x, int y, int expected) {
        assertEquals(expected, MathOperation.hcf(x, y));
    }

    @ParameterizedTest
    @CsvSource({
        "4, 6, 12",
        "7, 13, 91",
        "5, 5, 5",
        "1, 9, 9",
        "21, 6, 42",
        "6, 4, 12"
    })
    void lcm_validInputs(int x, int y, int expected) {
        assertEquals(expected, MathOperation.lcm(x, y));
    }

    @ParameterizedTest
    @CsvSource({"0, 5", "5, 0", "-3, 6", "6, -3"})
    void hcf_nonPositiveInput_throwsException(int x, int y) {
        assertThrows(IllegalArgumentException.class, () -> MathOperation.hcf(x, y));
    }

    @ParameterizedTest
    @CsvSource({"0, 5", "5, 0", "-3, 6", "6, -3"})
    void lcm_nonPositiveInput_throwsException(int x, int y) {
        assertThrows(IllegalArgumentException.class, () -> MathOperation.lcm(x, y));
    }

    @Test
    void hcfTimesLcm_equalsProduct() {
        assertEquals(12 * 18, MathOperation.hcf(12, 18) * MathOperation.lcm(12, 18));
    }

    @Test
    void hcf_largeValues_works() {
        assertEquals(1, MathOperation.hcf(Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
        assertEquals(Integer.MAX_VALUE, MathOperation.hcf(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    void lcm_resultTooLargeForInt_throwsArithmeticException() {
        assertThrows(ArithmeticException.class,
                () -> MathOperation.lcm(Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
    }

    @Test
    void lcm_ofOneAndOne_isOne() {
        assertEquals(1, MathOperation.lcm(1, 1));
    }

    // =====================================================================
    // Question 2 : Linear Search and Binary Search
    // =====================================================================

    @ParameterizedTest
    @CsvSource({
        "'4 2 9 7', 9, 2",     // middle
        "'4 2 9 7', 4, 0",     // first
        "'4 2 9 7', 7, 3",     // last
        "'4 2 9 7', 5, -1",    // absent
        "'8', 8, 0",           // single element found
        "'8', 3, -1",          // single element absent
        "'5 5 5', 5, 0"        // duplicates -> first occurrence
    })
    void linearSearch_validInputs(String text, int target, int expected) {
        assertEquals(expected, Search.linearSearch(toArray(text), target));
    }

    @Test
    void linearSearch_emptyArray_returnsMinusOne() {
        assertEquals(-1, Search.linearSearch(new int[]{}, 5));
    }

    @Test
    void linearSearch_nullArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Search.linearSearch(null, 5));
    }

    @ParameterizedTest
    @CsvSource({
        "'1 3 5 7 9 11', 7, 3",
        "'1 3 5 7 9 11', 1, 0",
        "'1 3 5 7 9 11', 11, 5",
        "'1 3 5 7 9 11', 4, -1",    // absent, inside range
        "'1 3 5 7 9 11', 0, -1",    // smaller than all
        "'1 3 5 7 9 11', 20, -1",   // larger than all
        "'2 4 6', 4, 1",
        "'8', 8, 0",
        "'8', 3, -1"
    })
    void binarySearch_validInputs(String text, int target, int expected) {
        assertEquals(expected, Search.binarySearch(toArray(text), target));
    }

    @Test
    void binarySearch_emptyArray_returnsMinusOne() {
        assertEquals(-1, Search.binarySearch(new int[]{}, 5));
    }

    @Test
    void binarySearch_nullArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Search.binarySearch(null, 5));
    }

    // ---------- Negative numbers inside the array ----------
    @ParameterizedTest
    @CsvSource({
        "'-5 -2 0 3', -2, 1",
        "'-5 -2 0 3', -5, 0",
        "'-5 -2 0 3', 3, 3",
        "'-5 -2 0 3', -1, -1"
    })
    void bothSearches_negativeValues_work(String text, int target, int expected) {
        int[] arr = toArray(text);                  // already sorted
        assertEquals(expected, Search.linearSearch(arr, target));
        assertEquals(expected, Search.binarySearch(arr, target));
    }

    // ---------- Duplicates: binary search may return any matching index ----------
    @Test
    void binarySearch_duplicates_returnsIndexOfTarget() {
        int[] arr = {1, 2, 2, 2, 3};
        int index = Search.binarySearch(arr, 2);
        assertTrue(index >= 1 && index <= 3);
        assertEquals(2, arr[index]);
    }

    @Test
    void searches_doNotModifyArray() {
        int[] arr = {1, 3, 5, 7};
        Search.linearSearch(arr, 5);
        Search.binarySearch(arr, 5);
        assertArrayEquals(new int[]{1, 3, 5, 7}, arr);
    }

    // =====================================================================
    // Question 3 : N Queens
    // =====================================================================

    @Test
    void nQueen_fourQueens_matchesExpectedBoard() {
        int[][] board = new int[4][4];
        boolean result = NQueens.nQueen(board, 0, 4);

        assertTrue(result);
        int[][] expected = {
            {0, 1, 0, 0},
            {0, 0, 0, 1},
            {1, 0, 0, 0},
            {0, 0, 1, 0}
        };
        assertArrayEquals(expected, board);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 5, 6, 7, 8})
    void nQueen_solvableSizes_returnTrueWithValidBoard(int n) {
        int[][] board = new int[n][n];
        assertTrue(NQueens.nQueen(board, 0, n));
        assertTrue(isValidQueenBoard(board, n));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void nQueen_unsolvableSizes_returnFalseAndBoardUnchanged(int n) {
        int[][] board = new int[n][n];
        assertFalse(NQueens.nQueen(board, 0, n));
        assertArrayEquals(new int[n][n], board);
    }

    @Test
    void nQueen_nullBoard_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> NQueens.nQueen(null, 0, 4));
    }

    @Test
    void nQueen_sizeMismatch_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> NQueens.nQueen(new int[3][3], 0, 4));
    }

    @Test
    void nQueen_zeroDimension_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> NQueens.nQueen(new int[0][0], 0, 0));
    }

    @Test
    void nQueen_startRowEqualsN_returnsTrueAndBoardUnchanged() {
        int[][] board = new int[4][4];
        assertTrue(NQueens.nQueen(board, 4, 4));
        assertArrayEquals(new int[4][4], board);
    }

    @Test
    void nQueen_withQueenAlreadyPlaced_completesTheBoard() {
        int[][] board = new int[4][4];
        board[0][1] = 1;                            // queen already in row 0
        assertTrue(NQueens.nQueen(board, 1, 4));
        int[][] expected = {
            {0, 1, 0, 0},
            {0, 0, 0, 1},
            {1, 0, 0, 0},
            {0, 0, 1, 0}
        };
        assertArrayEquals(expected, board);
    }

    @Test
    void nQueen_withBadPlacedQueen_returnsFalse() {
        int[][] board = new int[4][4];
        board[0][0] = 1;
        board[1][1] = 1;                            // diagonal clash already on board
        assertFalse(NQueens.nQueen(board, 2, 4));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 5})
    void nQueen_startRowOutsideRange_throwsException(int startRow) {
        assertThrows(IllegalArgumentException.class,
                () -> NQueens.nQueen(new int[4][4], startRow, 4));
    }

    // =====================================================================
    // Challenging Problem : Knight's Tour
    // =====================================================================

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 6, 8})
    void knightsTour_boardsWithTour_returnValidTour(int n) {
        int[][] board = KnightsTour.solve(n);
        assertNotNull(board);
        assertEquals(1, board[0][0]);
        assertTrue(isValidKnightTour(board, n));
    }

    @ParameterizedTest
    @CsvSource({"5, 2, 2", "5, 4, 4", "6, 3, 3"})
    void knightsTour_differentStartSquares_startsWhereRequested(int n, int row, int col) {
        int[][] board = KnightsTour.solve(n, row, col);
        assertNotNull(board);
        assertEquals(1, board[row][col]);
        assertTrue(isValidKnightTour(board, n));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void knightsTour_boardsWithoutTour_returnNull(int n) {
        assertNull(KnightsTour.solve(n));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void knightsTour_invalidSize_throwsException(int n) {
        assertThrows(IllegalArgumentException.class, () -> KnightsTour.solve(n));
    }

    @Test
    void knightsTour_singleCell_returnsOne() {
        assertArrayEquals(new int[][]{{1}}, KnightsTour.solve(1));
    }

    @Test
    void knightsTour_startOutsideBoard_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> KnightsTour.solve(5, 5, 0));
        assertThrows(IllegalArgumentException.class, () -> KnightsTour.solve(5, 0, -1));
    }
}
