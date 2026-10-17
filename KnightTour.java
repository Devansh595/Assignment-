import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Knight's Tour on an N x N board using recursion and backtracking.
 * Moves are tried in Warnsdorff order (fewest onward moves first), which
 * makes the search fast for normal board sizes.
 */
public class KnightsTour {

    private static final int[] DR = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] DC = {1, 2, 2, 1, -1, -2, -2, -1};

    /**
     * Finds a knight's tour starting from the top-left corner (0, 0).
     *
     * @param n board size
     * @return board of move numbers 1..n*n, or null if no tour exists
     * @requires n >= 1
     * @effects  same as solve(n, 0, 0);
     *           throws IllegalArgumentException if n < 1
     */
    public static int[][] solve(int n) {
        return solve(n, 0, 0);
    }

    /**
     * Finds a knight's tour (visiting every square exactly once).
     *
     * <pre>
     * solve(5, 0, 0) -> 5x5 matrix containing the numbers 1..25
     * solve(3, 0, 0) -> null
     * </pre>
     *
     * @param n        board size
     * @param startRow starting row (0-based)
     * @param startCol starting column (0-based)
     * @return the tour matrix, or null if no tour exists
     * @requires n >= 1, 0 <= startRow < n and 0 <= startCol < n
     * @effects  if a tour exists, returns an n x n matrix in which each cell
     *           holds the move number (1..n*n) on which the knight reaches it,
     *           the start square holds 1, and consecutive numbers are one
     *           knight move apart; otherwise returns null;
     *           throws IllegalArgumentException if n < 1 or the start square
     *           lies outside the board
     */
    public static int[][] solve(int n, int startRow, int startCol) {
        if (n < 1 || startRow < 0 || startRow >= n || startCol < 0 || startCol >= n) {
            throw new IllegalArgumentException("Invalid board size or start position");
        }
        int[][] board = new int[n][n];
        board[startRow][startCol] = 1;
        if (tour(board, n, startRow, startCol, 1)) {
            return board;
        }
        return null;
    }

    private static boolean tour(int[][] board, int n, int row, int col, int move) {
        if (move == n * n) {
            return true;                                    // base case: all squares visited
        }
        List<int[]> next = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            int r = row + DR[k];
            int c = col + DC[k];
            if (isFree(board, n, r, c)) {
                next.add(new int[]{r, c, countOnward(board, n, r, c)});
            }
        }
        next.sort(Comparator.comparingInt(a -> a[2]));      // Warnsdorff: fewest exits first

        for (int[] m : next) {
            board[m[0]][m[1]] = move + 1;                   // make the move
            if (tour(board, n, m[0], m[1], move + 1)) {
                return true;                                // recursive case
            }
            board[m[0]][m[1]] = 0;                          // backtrack
        }
        return false;
    }

    private static boolean isFree(int[][] board, int n, int r, int c) {
        return r >= 0 && r < n && c >= 0 && c < n && board[r][c] == 0;
    }

    private static int countOnward(int[][] board, int n, int row, int col) {
        int count = 0;
        for (int k = 0; k < 8; k++) {
            if (isFree(board, n, row + DR[k], col + DC[k])) {
                count++;
            }
        }
        return count;
    }
}
