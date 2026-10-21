import java.util.HashMap;
import java.util.Map;

public final class SparseMatrix { 
    private final Map<String, Integer> matrix; 
    private final int rows; 
    private final int cols; 

    
    public static class Pair<A, B> {
        public final A first;
        public final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }


    private boolean inValidCordinate(int r, int c) { 
        return r < 0 || r >= rows || c < 0 || c >= cols; 
    } 

    
    public SparseMatrix(int rows, int cols, Pair<Pair<Integer, Integer>, Integer>... elements) { 
        this.rows = rows; 
        this.cols = cols; 
        this.matrix = new HashMap<>();

        if (elements.length > rows * cols) { 
            throw new IllegalArgumentException("Too many elements for dimensions."); 
        } 

        int nonZeroCount = 0; 
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == null) continue;
            
            int r = elements[i].first.first;
            int c = elements[i].first.second;
            int value = elements[i].second;

            if (inValidCordinate(r, c)) { 
                throw new IllegalArgumentException("Invalid coordinate at index " + i); 
            } 

            if (value != 0) { 
                nonZeroCount++; 
                String key = r + "," + c;
                matrix.put(key, value); 
            } 
        } 

        if (nonZeroCount >= (rows * cols) / 2) { 
            throw new IllegalArgumentException("A sparse matrix must have a majority of zero elements."); 
        }
    }

    // Direct Constructor for internal operations (Addition/Multiplication/Transpose)
    private SparseMatrix(int rows, int cols, Map<String, Integer> matrixMap) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = matrixMap;
    }

    // Constant time O(1) cell lookup
    private int getValueAt(int r, int c) {
        String key = r + "," + c;
        return matrix.getOrDefault(key, 0);
    }

    // Returns a new transposed sparse matrix
    public SparseMatrix transpose() {
        Map<String, Integer> transposedMap = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : matrix.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            
            // Swap row and column
            transposedMap.put(c + "," + r, entry.getValue());
        }
        
        return new SparseMatrix(this.cols, this.rows, transposedMap);
    }

    // Checks symmetry in O(N) where N is non-zero elements
    public boolean isSymmetric() {
        if (this.rows != this.cols) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : matrix.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            
            if (getValueAt(c, r) != entry.getValue()) {
                return false;
            }
        }
        return true;
    }

   
    public SparseMatrix add(SparseMatrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrix dimensions must match for addition.");
        }
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : this.matrix.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : other.matrix.entrySet()) {
            String key = entry.getKey();
            int sum = resultMap.getOrDefault(key, 0) + entry.getValue();
            if (sum != 0) {
                resultMap.put(key, sum);
            } else {
                resultMap.remove(key); 
            }
        }

        return new SparseMatrix(this.rows, this.cols, resultMap);
    }

    public SparseMatrix multiply(SparseMatrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Matrix dimensions incompatible for multiplication.");
        }

        Map<String, Integer> resultMap = new HashMap<>();
        for (Map.Entry<String, Integer> entryA : this.matrix.entrySet()) {

            String[] partsA = entryA.getKey().split(",");

            int rA = Integer.parseInt(partsA[0]);
            int cA = Integer.parseInt(partsA[1]);
            int valA = entryA.getValue();

            // Match current column of A to all relevant row values of B
            for (int colB = 0; colB < other.cols; colB++) {
                int valB = other.getValueAt(cA, colB); // Match row component
                if (valB != 0) {
                    String targetKey = rA + "," + colB;
                    int productContribution = valA * valB;
                        
                    int currentSum = resultMap.getOrDefault(targetKey, 0) + productContribution;
                    if (currentSum != 0) {
                        resultMap.put(targetKey, currentSum);
                    } else {
                        resultMap.remove(targetKey);
                    }
                }
            }
        }
        return new SparseMatrix(this.rows, other.cols, resultMap);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SparseMatrix Details: ").append(rows).append("x").append(cols)
          .append(" (Non-zero elements: ").append(matrix.size()).append(")\n");
        
        for (int r = 0; r < rows; r++) {
            sb.append("[ ");
            for (int c = 0; c < cols; c++) {
                sb.append(String.format("%-4d", getValueAt(r, c)));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) { 
        System.out.println("Hi, Devansh your program Starts here!\n"); 
        
        // Define some core element pairs
        Pair<Pair<Integer, Integer>, Integer> e1 = new Pair<>(new Pair<>(0, 1), 5);
        Pair<Pair<Integer, Integer>, Integer> e2 = new Pair<>(new Pair<>(1, 0), 5);
        Pair<Pair<Integer, Integer>, Integer> e3 = new Pair<>(new Pair<>(1, 1), 3);
        
        SparseMatrix m1 = new SparseMatrix(3, 3, e1, e3);
        SparseMatrix m2 = new SparseMatrix(3, 3, e2, e3);
        
        System.out.println("--- Matrix 1 ---");
        System.out.print(m1);
        
        System.out.println("--- Matrix 2 ---");
        System.out.print(m2);

        System.out.println("--- Matrix 1 + Matrix 2 ---");
        System.out.print(m1.add(m2));

        System.out.println("--- Matrix 1 * Matrix 2 ---");
        System.out.print(m1.multiply(m2));
    } 
}
