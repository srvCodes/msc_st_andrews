import java.util.Random;

public class RPX extends TornadoSweeper {

    public RPX() {
    }

    /**
     * Random number generator method.
     *
     * @param limit is the range in which to generate
     * @return the random no.
     */
    public static int generateRandomNumberInRange(int limit) {
        Random rand = new Random();
        int num = rand.nextInt(limit);
        return num;
    }

    /**
     * Select a random cell which is not probed yet
     *
     * @return the cell
     */
    public Cell selectRandomUnprobedCell() {
        while (true) {
            int rowToProbe = generateRandomNumberInRange(worldLength);
            int colToProbe = generateRandomNumberInRange(worldLength);
            if (!isCellProbed(rowToProbe, colToProbe)) {
                Cell newCell = new Cell(rowToProbe, colToProbe);
                return newCell;
            }
        }
    }

    /**
     * Selecte a random cell which has not been probed or flagged yet
     *
     * @return the cell
     */
    public Cell selectRandomUnprobedAndUnflaggedCell() {
        while (true) {
            int rowToProbe = generateRandomNumberInRange(worldLength);
            int colToProbe = generateRandomNumberInRange(worldLength);
            if (!isCellProbed(rowToProbe, colToProbe) && super.agentWorld[rowToProbe][colToProbe] != 'F') {
                Cell newCell = new Cell(rowToProbe, colToProbe);
                return newCell;
            }
        }
    }

    /**
     * Uncover a cell, mark it if tornado else update the agentWorld
     *
     * @param rowToProbe is row of cell
     * @param colToProbe is column of cell
     * @return true if cell was a tornado, false otherwise
     */
    public boolean unCoverandMarkCell(int rowToProbe, int colToProbe) {
        boolean isTornado = false;
        char cellContent = super.getContentOfCell(rowToProbe, colToProbe);
        System.out.print("\nUncovering the cell in [" + rowToProbe + "," + colToProbe
                + "] coordinates with content: '" + cellContent + "'");
        if (isTornado(cellContent)) {
            super.agentWorld[rowToProbe][colToProbe] = cellContent;
            isTornado = true;
        } else {
            Cell cellToProbe = new Cell(rowToProbe, colToProbe);
            updateAgentWorld(cellToProbe);
        }
        return isTornado;
    }

    /**
     * Play the game for RPX agent.
     */
    public void randomProbe() {
        int rowToProbe;
        int colToProbe;
        Cell cellToProbe = null;
        int noOfRandomProbes = 0;

        gameLoop:
        while (super.toBeVisited.size() > super.numOfTornadoes) {
            System.out.print("\nStep: " + super.step);
            System.out.print("\nNumber of visited cells: " + super.visited.size() +
                    "\nNumber of cells to be visited: " + super.toBeVisited.size());
            if (super.step > 0 && midCellProbed) {
                noOfRandomProbes += 1;
                cellToProbe = selectRandomUnprobedCell();
            } else {
                cellToProbe = super.getFirstCellsToProbe();
            }
            step += 1;
            rowToProbe = cellToProbe.getRow();
            colToProbe = cellToProbe.getCol();
            boolean cellContainsTornado = unCoverandMarkCell(rowToProbe, colToProbe);
            printBoard();
            if (cellContainsTornado) {
                break gameLoop;
            }
        }
        // condition for losing or winning the game
        if (super.toBeVisited.size() > super.numOfTornadoes) {
            System.out.println("\n\nGame lost after " + step + " steps!");
        } else if (super.toBeVisited.size() == super.numOfTornadoes) {
            System.out.print("\n\nGame won in " + step + " steps!");
            makeFinalBoard();
        } else {
            System.out.print("\nCode showing anomalous behaviour!");
        }
        System.out.print("\nTotal no of random probes: " + noOfRandomProbes);
    }

}
