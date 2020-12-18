import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Sub-class to implement the satx agent.
 */
public class SATX extends TornadoSweeper {

    public SATX() {
    }

    /**
     * Invoking the call to SAT4J to check entailment for clauses.
     *
     * @param clauses    is list of array of integers denoting all valid clauses for a cell
     * @param cellNumber is the literal for the cell for which entailment is to be made.
     * @return
     */
    public boolean solveForSatisfiability(ArrayList<int[]> clauses, int cellNumber) {
        final int MAXVAR = 1000;
        final int NBCLAUSES = clauses.size();

        ISolver solver = SolverFactory.newDefault();
        // prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);
        // Feed the solver using Dimacs format, using arrays of int
        // (best option to avoid dependencies on SAT4J IVecInt)
        for (int i = 0; i < NBCLAUSES; i++) {
            int[] clause = clauses.get(i);
            try {
                solver.addClause(new VecInt(clause)); // adapt Array to IVecInt
            } catch (ContradictionException e) {
                System.out.print(e.getStackTrace());
            }
        }

        IProblem problem = solver;
        // convert the cellNumber to VecInt which needs an array of integers as input
        int[] cellNumberArray = {cellNumber};
        VecInt assumps = new VecInt(cellNumberArray);
        boolean isSatisfiable = false;
        try {
            if (problem.isSatisfiable(assumps)) {
                isSatisfiable = true;
                System.out.print(" Formula is satisfiable!");
            } else {
                isSatisfiable = false;
                System.out.print(" Formula isn't satisfiable!");
            }
        } catch (TimeoutException e) {
            System.out.print(e.getStackTrace());
        }
        return isSatisfiable;
    }

    /**
     * Invoke call to Tweety library to convert propositional formula to CNF form.
     *
     * @param formula is the proposition for a cell
     * @return array of PlFormula where each element is in CNF.
     */
    public ArrayList<PlFormula> getCnfFromFormula(String formula) {
        PlBeliefSet kb = new PlBeliefSet();
        PlParser parser = new PlParser();
        try {
            kb.add((PlFormula) parser.parseFormula(formula));
        } catch (IOException e) {
            System.out.print(e.getStackTrace());
        }
        Conjunction conj = kb.toCnf();
        ArrayList<PlFormula> clauses = new ArrayList<>();
        ListIterator<PlFormula> iter = conj.listIterator();
        while (iter.hasNext()) {
            PlFormula clause = iter.next();//do something with clause}
            clauses.add(clause);
        }
        return clauses;
    }

    /**
     * Denote a cell by an integer for making propositions.
     *
     * @param cell is the cell to be denoted.
     * @return the string, e.g. D0,3 for cell [0,3]
     */
    public String cellToString(Cell cell) {
        String result = "D";
        result += cell.getRow();
        result += ",";
        result += cell.getCol();
        return result;
    }

    /**
     * Make propositions for list of cells by applying combination nCr where n = size of unCoveredCells, r = clue
     *
     * @param unCoveredCells is the list of uncovered cells in the neighbour
     * @param clue           serves as 'r' for nCr
     * @return
     */
    public String makeProposition(List<Cell> unCoveredCells, int clue) {
        List<String> literals = new ArrayList<>();
        for (Cell cell : unCoveredCells) {
            String s = cellToString(cell);
            if (!literals.contains(s)) {
                literals.add(s);
            }
        }
        PrintCombination p = new PrintCombination();
        // get combination of all elements with a negation on exactly (n-r) elements
        List<String> res = p.printCombination(literals, literals.size(), clue);
        res.removeAll(Collections.singleton(null));
        String formula = "";
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i).length() > 0) {
                formula += "(";
                formula += res.get(i);
                formula += ")";
                if (i != res.size() - 1) {
                    formula += " || ";
                }
            }
        }
        return formula;
    }

    /**
     * Get all uncovered neighbours of cell marked as '?'
     *
     * @param cell is the cell for which neighbours are to be identified
     * @return list of uncovered neighbour cells
     */
    public List<Cell> getUncoveredNeighbours(Cell cell) {
        List<Cell> allNeighbours = cell.getNeighbours(originalWorld.length);
        List<Cell> unCoveredNeighbours = new ArrayList<>();
        for (Cell neighbour : allNeighbours) {
            if (agentWorld[neighbour.getRow()][neighbour.getCol()] == '?') {
                unCoveredNeighbours.add(neighbour);
            }
        }
        return unCoveredNeighbours;
    }

    /**
     * Count the number of flagged neighbours of a cell.
     *
     * @param cell is the cell on the agentWorld
     * @return the no. of flagged neighbours
     */
    public int getNumberOfFlaggedNeighbours(Cell cell) {
        List<Cell> allNeighbours = cell.getNeighbours(originalWorld.length);
        int numOfFlaggedNeighbours = 0;
        for (Cell neighbour : allNeighbours) {
            if (agentWorld[neighbour.getRow()][neighbour.getCol()] == 'F') {
                numOfFlaggedNeighbours++;
            }
        }
        return numOfFlaggedNeighbours;
    }

    /**
     * Map all literals to their indices which serve as unique IDs
     *
     * @param allLiterals is a list of string of literals
     * @return array of integers with unique IDs for corresponding string literals
     */
    public ArrayList<Integer> convertLiteralsToInts(List<String> allLiterals) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < allLiterals.size(); i++) {
            indexes.add(i);
        }
        return indexes;
    }

    /**
     * Extract literals from Tweety formulae.
     *
     * @param clauses is the array of PlFormula returned by Tweety.
     * @return list of string of corresponding literals.
     */
    public List<String> getLiterals(ArrayList<PlFormula> clauses) {
        List<String> allLiterals = new ArrayList<>();
        String literal;
        for (int i = 0; i < clauses.size(); i++) {
            for (PlFormula pl : clauses.get(i).getLiterals()) {
                literal = pl.getLiterals().toString();
                literal = literal.replace("[", "");
                literal = literal.replace("]", "");
                literal = literal.replace("!", "");

                if (!allLiterals.contains(literal)) {
                    allLiterals.add(literal);
                }
            }
        }
        return allLiterals;
    }

    /**
     * Convert PLForumulae of Tweety to individual array of literals for feeding into SAT4J
     *
     * @param clauses is array of PLFormula clauses
     * @param cell    is the current cell whose entailment is to be made.
     * @return an object of DimacsReturnType which contains the result
     */
    public DimacsReturnType clausesToDIMACS(ArrayList<PlFormula> clauses, String cell) {
        ArrayList<int[]> numberCNF = new ArrayList<int[]>();
        List<String> allLiterals = getLiterals(clauses);
        ArrayList<Integer> indices = convertLiteralsToInts(allLiterals);
        int cellNumber = allLiterals.indexOf(cell) + 1;

        for (int i = 0; i < clauses.size(); i++) {
            String clause = clauses.get(i).toString();
            clause = clause.replace("!", "-");
            for (int j = 0; j < indices.size(); j++) {
                clause = clause.replaceAll(allLiterals.get(j), Integer.toString(j + 1));
            }
            String[] arrayofNum = clause.split("\\|\\|");
            int[] arrayOfNumbersCNF = new int[arrayofNum.length];
            for (int j = 0; j < arrayofNum.length; j++) {
                arrayOfNumbersCNF[j] = Integer.parseInt(arrayofNum[j]);
            }
            numberCNF.add(arrayOfNumbersCNF);
        }
        DimacsReturnType result = new DimacsReturnType(numberCNF, cellNumber);
        return result;
    }

    /**
     * Convert proposition to cnf and then to DIMACS.
     *
     * @param formula is the propositional formula
     * @param cell    is the cell being tested for entailment.
     * @return an object containing the cnf mapped to numbers as well as the cell number for the cell.
     */
    public DimacsReturnType getDimacsFromFormula(String formula, String cell) {
        ArrayList<PlFormula> cnfs = getCnfFromFormula(formula);
        DimacsReturnType result = clausesToDIMACS(cnfs, cell);
        return result;
    }

    /**
     * Check entailments KB && ~Dx,y and KB && Dx,y by constructing knowledge base for the cell
     *
     * @param row is row of the cell being evaluated
     * @param col is column of the cell
     * @return true if the cell was visited, i.e. either of the entailments hold. Otherwise, false
     */
    public boolean checkAndVisitNeighbours(int row, int col) {
        boolean isVisited = false;
        Cell cellToTest = new Cell(row, col);
        List<Cell> listOfNeighbours = cellToTest.getNeighbours(originalWorld.length);
        String formula = "";
        List<String> formulae = new ArrayList<>();

        for (int i = 0; i < listOfNeighbours.size(); i++) {
            Cell neighbour = listOfNeighbours.get(i);
            char clue = agentWorld[neighbour.getRow()][neighbour.getCol()];
            if (clue != '?' && clue != 'F') {
                int clueValue = clue - '0';
                List<Cell> allUncoveredNeighbours = getUncoveredNeighbours(neighbour);
                int numOfFlaggedNeighbours = getNumberOfFlaggedNeighbours(neighbour);
                System.out.print("\nVisiting neighbour [" + neighbour.getRow() + "," + neighbour.getCol() + "] with" +
                        " effective clue value: " + (clueValue - numOfFlaggedNeighbours));
                int effectiveClue = clueValue - numOfFlaggedNeighbours;
                if (effectiveClue < 0) {
                    System.out.print("\nClue value should never be negative. Anomalous behaviour!!");
                    System.exit(1);
                } else if (effectiveClue == 0) {
                    continue;
                } else {
                    System.out.print("\nProposition for neighbour [" + neighbour.getRow() + "," + neighbour.getCol() +
                            "]: ");
                    String proposition = makeProposition(allUncoveredNeighbours, effectiveClue);
                    System.out.print(proposition);
                    if (proposition.length() > 0) {
                        String tempFormula = "(" + proposition + ")";
                        formulae.add(tempFormula);
                    }
                }
            }
        }
        // if a formula was found for the cell, convert it to DIMACS and feed into SAT4J for entailment
        if (formulae.size() > 0) {
            printBoard();
            formula = String.join(" && ", formulae);
            System.out.print("\nKnowledge Base for cell [" + row + "," + col + "]: " + formula);
            DimacsReturnType result = getDimacsFromFormula(formula, cellToString(cellToTest));
            ArrayList<int[]> allLiterals = result.cnfInNumbers;
            int cellNumber = result.cellNumber;

            System.out.print("\nCalling SAT4J for probing cell [" + row + "," + col + "]:");
            boolean solverResultsForProbing = solveForSatisfiability(allLiterals, cellNumber);
            if (!solverResultsForProbing) {
                System.out.print("\nSatisfied for probing!");
                updateAgentWorld(cellToTest);
                isVisited = true;
            } else {
                System.out.print("\nCalling SAT4J for flagging cell [" + row + "," + col + "]:");
                boolean solverResultsForFlagging = solveForSatisfiability(allLiterals, cellNumber * -1);
                if (!solverResultsForFlagging) {
                    flagCell(row, col);
                    isVisited = true;
                }
            }
        }
        return isVisited;
    }

    public void printInformation() {
        System.out.print("\nStep: " + step);
        System.out.print("\nNumber of visited cells: " + visited.size() +
                "\nNumber of cells to be visited: " + toBeVisited.size());
    }

    /**
     * Move through the board to check for satisfiability of logic for each cell. Switch to SPX and RPX otherwise.
     */
    public void checkSatisfiabilityAndMove() {
        int noOfRandomProbes = 0;
        gameloop:
        while (toBeVisited.size() > numOfTornadoes) {
            printInformation();
            boolean boardChangeForSATX = false;
            boolean boardChangeForSPX = false;

            if (super.step == 0 || !midCellProbed) {
                Cell cellToProbe = super.getFirstCellsToProbe();
                updateAgentWorld(cellToProbe);
                step += 1;
                printInformation();
                super.printBoard();
                boardChangeForSATX = true;
            } else {
                for (int i = 0; i < originalWorld.length; i++) {
                    for (int j = 0; j < originalWorld.length; j++) {
                        if (agentWorld[i][j] == '?') {
                            step += 1;
                            System.out.print("\n\n\nConsidering to visit cell [" + i + "," + j + "]");
                            boolean visitedCondition = checkAndVisitNeighbours(i, j);
                            // check if cell was visited
                            if (visitedCondition) {
                                printInformation();
                                // mark the board as having changed
                                boardChangeForSATX = true;
                            } else {
                                System.out.print("\nCell didn't satisfy either of SAT knowledge entailments!");
                            }
                        }
                    }
                }
            }
            // switch to SPX for a move
            if (!boardChangeForSATX) {
                System.out.print("\n\n****** Switching to SPX for finding a single point move! *******");
                SPX singlePointAgent = new SPX();
                spxLoop:
                for (int i = 0; i < originalWorld.length; i++) {
                    for (int j = 0; j < originalWorld.length; j++) {
                        if (agentWorld[i][j] == '?') {
                            step += 1;
                            System.out.print("\nConsidering single point strategy to visit the cell in [" + i + ","
                                    + j + "] coordinates");
                            boolean visitedCondition = singlePointAgent.checkSatisfiabilityInNeighbours(i, j);
                            if (visitedCondition) {
                                boardChangeForSPX = true;
                                printInformation();
                                printBoard();
                                break spxLoop;
                            } else {
                                System.out.print("\nCell didn't satisfy single point conditions!");
                            }
                        }
                    }
                }
            }
            // switch to RPX for a move
            if (!boardChangeForSPX && !boardChangeForSATX) {
                System.out.print("\n\n****** Switching to RPX for finding a random move! *******");
                RPX randomGame = new RPX();
                Cell randomCell = randomGame.selectRandomUnprobedAndUnflaggedCell();
                int randomRow = randomCell.getRow();
                int randomCol = randomCell.getCol();
                noOfRandomProbes++;
                boolean cellContainsTornado = randomGame.unCoverandMarkCell(randomRow, randomCol);
                if (cellContainsTornado) {
                    break gameloop;
                }
                printBoard();
            }
        }
        // condition for losing or winning the game
        if (toBeVisited.size() > numOfTornadoes) {
            System.out.println("\n\nGame lost after " + step + " steps!");
        } else if (toBeVisited.size() == numOfTornadoes) {
            System.out.print("\n\nGame won in " + step + " steps!");
            makeFinalBoard();
        } else {
            System.out.print("\nCode showing anomalous behaviour!");
        }
        System.out.println("\nTotal no of Random probes required: " + noOfRandomProbes);
    }
}
