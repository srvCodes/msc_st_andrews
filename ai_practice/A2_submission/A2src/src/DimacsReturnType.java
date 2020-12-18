import java.util.ArrayList;

/**
 * Class to return result of DIMACS conversion
 */
public class DimacsReturnType {
    public ArrayList<int[]> cnfInNumbers;
    public int cellNumber;

    /**
     * Constructor.
     *
     * @param cnfInNumbers is the array of string literals mapped into numbers.
     * @param num          is the cell number for the cell currently being evaluated.
     */
    public DimacsReturnType(ArrayList<int[]> cnfInNumbers, int num) {
        this.cnfInNumbers = cnfInNumbers;
        this.cellNumber = num;
    }
}
