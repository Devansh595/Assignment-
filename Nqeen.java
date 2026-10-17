/**
 * N-Queens problem solved with recursion and backtracking.
 * The board is an N x N int matrix: 1 = queen, 0 = empty.
 */
public class NQueens {

    /**
     * Places queens on the board, one per row starting from startRow, so that
     * no two queens share a row, column or diagonal.
     *
     * <pre>
     * nQueen(new int[4][4], 0, 4) -> true, board becomes
     *   {0,1,0,0}
     *   {0,0,0,1}
     *   {1,0,0,0}
     *   {0,0,1,0}
     * </pre>
     *
     * @param board             the board to fill
     * @param startRow          row from which to start placing queens
     * @param dimensionOfMatrix N, the size of the board
     * @return true if a solution exists, otherwise false
     * @requires board is a non-null N x N matrix where N = dimensionOfMatrix >= 1,
     *           filled with 0 from startRow downwards, and
     *           0 <= startRow <= N
     * @effects  if a solution exists, returns true and board holds N queens (1s)
     *           with no two attacking each other;
     *           otherwise returns false and board is left as it was;
     *           throws IllegalArgumentException if board is null, not N x N,
     *           N < 1, or startRow is outside 0..N
     */
    public static Boolean nQueen(int[][] board, int startRow, int dimensionOfMatrix) {
        validate(board, dimensionOfMatrix);
        if (startRow < 0 || startRow > dimensionOfMatrix) {
            throw new IllegalArgumentException("startRow must be between 0 and N");
        }

        if (startRow >= dimensionOfMatrix) {
            return true;                                  // base case: all rows done
        }
        for (int col = 0; col < dimensionOfMatrix; col++) {
            if (isSafe(board, startRow, col, dimensionOfMatrix)) {
                board[startRow][col] = 1;                 // place queen
                if (nQueen(board, startRow + 1, dimensionOfMatrix)) {
                    return true;                          // recursive case
                }
                board[startRow][col] = 0;                 // backtrack
            }
        }
        return false;                                     // no column works in this row
    }

    private static void validate(int[][] board, int n) {
        if (board == null || n < 1 || board.length != n) {
            throw new IllegalArgumentException("Board must be N x N with N >= 1");
        }
        for (int[] row : board) {
            if (row == null || row.length != n) {
                throw new IllegalArgumentException("Board must be N x N with N >= 1");
            }
        }
    }

    // Only rows above 'row' can hold queens (we fill top to bottom).
    private static boolean isSafe(int[][] board, int row, int col, int n) {
        for (int r = 0; r < row; r++) {
            if (board[r][col] == 1) {
                return false;                             // same column
            }
        }
        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
            if (board[r][c] == 1) {
                return false;                             // upper-left diagonal
            }
        }
        for (int r = row - 1, c = col + 1; r >= 0 && c < n; r--, c++) {
            if (board[r][c] == 1) {
                return false;                             // upper-right diagonal
            }
        }
        return true;
    }
}
