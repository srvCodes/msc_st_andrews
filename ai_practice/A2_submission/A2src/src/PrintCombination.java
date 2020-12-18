import java.util.ArrayList;
import java.util.List;

/**
 * Code taken from https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
 * But modified to print all elements of the array with a negation sign on (n-r) elements.
 */
public class PrintCombination {
    public static List<String> allPropositions = new ArrayList<>();

    public PrintCombination() {
        allPropositions.clear();
    }

    public static String combinationUtil(List<String> arr, String data[], int start,
                                         int end, int index, int r) {
        String result = "";
        List<String> arrCopy = new ArrayList<>();
        arrCopy.addAll(arr);
        // Current combination is ready to be printed, print it
        if (index == r) {
            List<String> temp = new ArrayList<>();
            for (int j = 0; j < r; j++) {
                temp.add(data[j]);
                arrCopy.remove(data[j]);
            }
            for (int i = 0; i < arrCopy.size(); i++) {
                temp.add("!" + arrCopy.get(i));

            }
            result += String.join(" && ", temp);
            return result;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr.get(i);
            String res = combinationUtil(arr, data, i + 1, end, index + 1, r);
            allPropositions.add(res);
        }
        return null;
    }

    static List<String> printCombination(List<String> arr, int n, int r) {
        // A temporary array to store all combination one by one
        String data[] = new String[r];

        // Print all combination using temprary array 'data[]'
        String res = combinationUtil(arr, data, 0, n - 1, 0, r);
        if (res != null) {
            allPropositions.add(res);
        }
        return allPropositions;
    }

    public static void main(String[] args) {
        String arr1[] = {"1", "2", "3", "4"};
        List<String> arr = new ArrayList<>();
        arr.add("1");
        arr.add("2");
        arr.add("3");
        arr.add("4");

        int r = 2;
        int n = arr.size();
        printCombination(arr, n, r);
    }
}
