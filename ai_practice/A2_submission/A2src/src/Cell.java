import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to represent a cell.
 */
public class Cell {
    private int row;
    private int col;

    /**
     * Constructor.
     *
     * @param row is the row of the cell
     * @param col is the column of the cell
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row &&
                col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * Get neighbours of the cell on the board.
     *
     * @param worldLength is the length of the board.
     * @return list of valid neighbouring cells.
     */
    public List<Cell> getNeighbours(int worldLength) {
        List<Cell> neighbours = new ArrayList<>();

        if (row > 0) {
            if (col > 0) {
                Cell n1 = new Cell(row - 1, col - 1);
                neighbours.add(n1);
            }
            Cell n2 = new Cell(row - 1, col);
            neighbours.add(n2);
        }

        if (col > 0) {
            Cell n3 = new Cell(row, col - 1);
            neighbours.add(n3);
        }

        if (col < worldLength - 1) {
            Cell n4 = new Cell(row, col + 1);
            neighbours.add(n4);
        }

        if (row < worldLength - 1) {
            Cell n5 = new Cell(row + 1, col);
            neighbours.add(n5);
            if (col < worldLength - 1) {
                Cell n6 = new Cell(row + 1, col + 1);
                neighbours.add(n6);
            }
        }
        return neighbours;
    }
}
