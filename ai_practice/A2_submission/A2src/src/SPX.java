import java.util.ArrayList;
import java.util.List;

public class SPX extends TornadoSweeper {

    public SPX() {
    }

    /**
     * Get count of flagged and covered cells in the neighbour of a cell
     *
     * @param cell is the cell for which counts are to be extracted
     * @return a list of counts
     */
    public List<Integer> checkNeighboursOfNeighbour(Cell cell) {
        int dangersMarked = 0;
        int coveredCells = 0;
        List<Integer> answer = new ArrayList<>();
        List<Cell> neighboursOfNeighbour = cell.getNeighbours(originalWorld.length);
        for (Cell adjacent : neighboursOfNeighbour) {
            if (agentWorld[adjacent.getRow()][adjacent.getCol()] == 'F') {
                dangersMarked += 1;
            } else if (agentWorld[adjacent.getRow()][adjacent.getCol()] == '?') {
                coveredCells += 1;
            }
        }
        answer.add(dangersMarked);
        answer.add(coveredCells);
        return answer;
    }

    /**
     * Check for AFN for the cell.
     *
     * @param row           is the row of cell to be tested
     * @param col           is the col of cell to be tested
     * @param dangersMarked # of dangers marked in its neighbour
     * @param clueValue     is the clue value of a neighbour
     * @return true if AFN holds, false otherwise.
     */
    public boolean allFreeNeighbours(int row, int col, int dangersMarked, int clueValue) {
        boolean conditionSatisfied = false;
        if (dangersMarked == clueValue) {
            step += 1;
            System.out.print("\nCell [" + row + "," + col + "] satisfies AFN condition. Probing it!");
            conditionSatisfied = true;
            Cell cellToProbe = new Cell(row, col);
            updateAgentWorld(cellToProbe);
        }
        return conditionSatisfied;
    }

    /**
     * Check for AMN for the cell.
     *
     * @param row           is the row of cell to be tested
     * @param col           is the col of cell to be tested
     * @param dangersMarked # of dangers marked in its neighbour
     * @param clueValue     is the clue value of a neighbour
     * @param coveredCells  # of covered cells in its neighbour
     * @return true if AMN holds. False, otherwise
     */
    public boolean allMarkedNeighbours(int row, int col, int dangersMarked, int clueValue, int coveredCells) {
        boolean conditionSatisfied = false;
        if (clueValue - dangersMarked == coveredCells) { // AMN
            step += 1;
            System.out.print("\nCell [" + row + "," + col + "] satisfies AMN condition.");
            System.out.print("\nFlagging the cell with SPX in [" + row + "," + col + "] coordinates for danger!");
            agentWorld[row][col] = 'F';
            conditionSatisfied = true;
        }
        return conditionSatisfied;
    }

    /**
     * Check neighbours of the current cell for AFN and AMN conditions
     *
     * @param row is row of current cell to be tested
     * @param col is column of cell to be tested
     * @return
     */
    public boolean checkSatisfiabilityInNeighbours(int row, int col) {
        Cell cellToProbe = new Cell(row, col);
        List<Cell> allNeighbours = cellToProbe.getNeighbours(originalWorld.length);

        for (Cell cell : allNeighbours) {
            System.out.print("\n\nConsidering neighbour: [" + cell.getRow() + "," + cell.getCol() + "]");
            char clue = agentWorld[cell.getRow()][cell.getCol()];
            if (clue != '?') {
                int clueValue = clue - '0';
                System.out.print("\nClue received from this neighbour: " + clueValue);
                // count no. of flagged and covered cells in the neighbours
                List<Integer> cellCounts = checkNeighboursOfNeighbour(cell);
                int dangersMarked = cellCounts.get(0);
                int coveredCells = cellCounts.get(1);
                System.out.print("\nTotal count of dangers for this neighbour: " + dangersMarked);
                System.out.print("\nToal count of covered cells for this neighbour: " + coveredCells);

                boolean isAllMarked = allMarkedNeighbours(row, col, dangersMarked, clueValue, coveredCells); // AFN
                boolean isAllFree = allFreeNeighbours(row, col, dangersMarked, clueValue); // AMN
                if (isAllMarked || isAllFree) {
                    if (isAllMarked) {
                        tornadoCells.add(cellToProbe);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Play the game with single point strategy.
     */
    public void singlePointProbe() {
        int noOfRandomProbes = 0;
        // break gameloop if condition satisfied or tornado found
        gameloop:
        while (toBeVisited.size() > numOfTornadoes) {
            boolean boardChanged = false;
            System.out.print("Step: " + step);
            System.out.print("\nNumber of visited cells: " + visited.size() +
                    "\nNumber of cells to be visited: " + toBeVisited.size());
            // get [0,0] or the centre cell to probe
            if (super.step == 0 || !midCellProbed) {
                Cell cellToProbe = super.getFirstCellsToProbe();
                updateAgentWorld(cellToProbe);
                step += 1;
                super.printBoard();
                boardChanged = true;
            } else {
                // move through the board to find and check next covered cell
                for (int i = 0; i < originalWorld.length; i++) {
                    for (int j = 0; j < originalWorld.length; j++) {
                        if (agentWorld[i][j] == '?') {
                            System.out.print("\nConsidering single point strategy to probe the cell in [" + i + ","
                                    + j + "] coordinates");
                            boolean visitedCondition = checkSatisfiabilityInNeighbours(i, j);
                            if (visitedCondition) {
                                System.out.print("\n No of nodes to be visited: " + toBeVisited.size() +
                                        "\nVisited nodes: " + visited.size());
                                printBoard();
                                // mark board as changed
                                boardChanged = true;
                                break;
                            }
                        }
                    }
                }
            }
            // if board didn't change, switch to RPX
            if (!boardChanged) {
                System.out.print("\n\n****** Switching to RPX for finding a random cell! *******");
                RPX randomGame = new RPX();
                Cell randomCell = randomGame.selectRandomUnprobedAndUnflaggedCell();
                int randomRow = randomCell.getRow();
                int randomCol = randomCell.getCol();
                boolean cellContainsTornado = randomGame.unCoverandMarkCell(randomRow, randomCol);
                noOfRandomProbes += 1;
                if (cellContainsTornado) {
                    break gameloop;
                }
                printBoard();
            }
        }
        // condition for losing or winning the game
        if (toBeVisited.size() > numOfTornadoes) {
            System.out.println("\n\nGame lost!");
        } else if (toBeVisited.size() == numOfTornadoes) {
            System.out.print("\n\nGame won!");
            makeFinalBoard();
        } else {
            System.out.print("\nCode showing anomalous behaviour!");
        }
        System.out.print("\nTotal no of Random probes required: " + noOfRandomProbes);
    }
}

