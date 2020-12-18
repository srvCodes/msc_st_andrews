import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for Cell class
 */
public class CellTest {
    @Test
    public static void testGetRow(){
        Cell cell = new Cell(2,3);
        Assert.assertTrue("Test 5 passed.. ", cell.getRow() == 2);
    }

    @Test
    public static void testGetCol() {
        Cell cell = new Cell(4,5);
        Assert.assertTrue("Test 4 passed.. ", cell.getCol() == 5);
    }

    @Test
    public static void testGetNeighbours() {
        Cell cell = new Cell(2,2);
        List<Cell> neighbours = cell.getNeighbours(3);
        List<Cell> expectedNeighbours = new ArrayList<Cell>();

        // Neighbours should have been added in the order: (row-1) -> (row) -> (row+1), (col -1) -> (col) -> (col+1)
        Cell n1 = new Cell(2,1);
        Cell n2 = new Cell(1,2);
        Cell n3 = new Cell(1,1);
        expectedNeighbours.add(n3);
        expectedNeighbours.add(n2);
        expectedNeighbours.add(n1);

        Assert.assertArrayEquals("Test 3 passed.. ", expectedNeighbours.toArray(), neighbours.toArray());
    }

    @Test
    public static void TestSetRow() {
        Cell cell = new Cell(1,1);
        cell.setRow(2);
        Assert.assertTrue("Test 2 passed.", cell.getRow() == 2);
    }

    @Test
    public static void TestSetCol() {
        Cell cell = new Cell(4,3);
        cell.setCol(1);
        Assert.assertTrue("Test 1 passed for cellTest", cell.getCol() == 1);
    }

    public static void main(String[] args) {
        TestSetRow();
        TestSetCol();
        testGetNeighbours();
        testGetCol();
        testGetRow();
    }
}
