/**
 * Recursive implementations of Linear Search and Binary Search.
 */
public class Search {

    private static void requireArray(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Array must not be null");
        }
    }

    /**
     * Searches for target by checking each element from left to right.
     *
     * <pre>
     * linearSearch([4,2,9,7], 9) -> 2
     * linearSearch([4,2,9,7], 5) -> -1
     * </pre>
     *
     * @param arr    array to search
     * @param target value to find
     * @return index of first occurrence, or -1
     * @requires arr is non-null (it may be empty and need not be sorted)
     * @effects  returns the smallest index i with arr[i] == target,
     *           or -1 if target does not occur in arr;
     *           arr is not modified;
     *           throws IllegalArgumentException if arr is null
     */
    public static int linearSearch(int[] arr, int target) {
        requireArray(arr);
        return linearSearch(arr, target, 0);
    }

    private static int linearSearch(int[] arr, int target, int index) {
        if (index >= arr.length) {
            return -1;                                   // base case: not found
        }
        if (arr[index] == target) {
            return index;                                // base case: found
        }
        return linearSearch(arr, target, index + 1);     // recursive case
    }

    /**
     * Searches for target in a sorted array by comparing with the middle
     * element and discarding the half that cannot contain the target.
     *
     * <pre>
     * binarySearch([1,3,5,7,9,11], 7) -> 3
     * binarySearch([1,3,5,7,9,11], 4) -> -1
     * </pre>
     *
     * @param arr    sorted array to search
     * @param target value to find
     * @return index of target, or -1
     * @requires arr is non-null and sorted in ascending order
     * @effects  returns an index i with arr[i] == target,
     *           or -1 if target does not occur in arr;
     *           arr is not modified;
     *           throws IllegalArgumentException if arr is null
     */
    public static int binarySearch(int[] arr, int target) {
        requireArray(arr);
        return binarySearch(arr, target, 0, arr.length - 1);
    }

    private static int binarySearch(int[] arr, int target, int low, int high) {
        if (low > high) {
            return -1;                                   // base case: empty range
        }
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            return mid;                                  // base case: found
        }
        if (target < arr[mid]) {
            return binarySearch(arr, target, low, mid - 1);   // search left half
        }
        return binarySearch(arr, target, mid + 1, high);      // search right half
    }
}
