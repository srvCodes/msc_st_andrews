package tests;

import model.MyCanvas;
import model.SimpleModel;
import org.junit.Test;
import org.junit.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class SimpleModelTest {
    private final int FRAME_WIDTH = 800;
    private final int FRAME_HEIGHT = 800;
    private int numberOfTestCases = 0;
    private int numberOfFailedCases = 0;
    private String failureMessage = "Failed the test case: ";

    @Test
    public void testDrawShape() {
        SimpleModel model = new SimpleModel();
        Point p1 = new Point(30, 150);
        Point p2 = new Point(45, 50);
        String shape = "rectangle";
        String colour = "red";
        boolean solidFill = false;
        boolean lockAspectRatio = false;
        MyCanvas canvas = new MyCanvas(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        model.drawShape(canvas, shape, colour, solidFill, p1, p2, lockAspectRatio);

        // redo stack should be cleared when user wants to draw new shape
        numberOfTestCases++;
        Assert.assertEquals(failureMessage+ numberOfFailedCases++, model.getRedoStack().size(), 0);
        numberOfTestCases++;
        Assert.assertEquals(failureMessage+ numberOfTestCases, model.getUndoStack().size(), 1);
        // for the first time the user enters a shape, the undo stack should have an empty image on the top
        BufferedImage img = model.getUndoStack().peek();
        numberOfTestCases++;
        Assert.assertNotEquals(failureMessage+ numberOfTestCases, canvas.copyImage(null), img);
        System.out.println("Passed " + numberOfTestCases + " out of 3 test cases for drawShape().");
    }

    @Test
    public void testLoadAndSaveFile() throws IOException {
        SimpleModel model = new SimpleModel();
        Point p1 = new Point(30, 15);
        Point p2 = new Point(45, 50);
        String shape = "rectangle";
        String colour = "red";
        boolean solidFill = false;
        boolean lockAspectRatio = false;
        MyCanvas canvas = new MyCanvas(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        model.drawShape(canvas, shape, colour, solidFill, p1, p2, lockAspectRatio);
        // the first shape should now have been pushed to undo stack
        numberOfTestCases++;
        Assert.assertEquals(failureMessage + numberOfTestCases, model.getUndoStack().size(), 1);
        //store the image of canvas
        BufferedImage imageToSave = canvas.getImg();
        byte[] expectedImageArray = ((DataBufferByte) imageToSave.getData().getDataBuffer()).getData();
        model.saveFile(canvas);
        shape = "line";
        // draw a line just to change the canvas
        model.drawShape(canvas, shape, colour, solidFill, p1, p2, lockAspectRatio);
        // undo stack should now also contain the line
        numberOfTestCases++;
        Assert.assertEquals(failureMessage + numberOfTestCases, model.getUndoStack().size(), 2);
        // now load the saved image
        model.loadFile(canvas);
        byte[] actualImageArray = ((DataBufferByte) canvas.getImg().getData().getDataBuffer()).getData();
        // the saved image should be equal to the one we have stored
        numberOfTestCases++;
        Assert.assertArrayEquals(failureMessage + numberOfTestCases, expectedImageArray, actualImageArray);
        // loading an image should not alter undoStack contents as the user might still wish to undo the load operation
        numberOfTestCases++;
        Assert.assertEquals(failureMessage + numberOfTestCases, model.getUndoStack().size(), 2);
        model.undo(canvas);
        numberOfTestCases++;
        Assert.assertEquals(failureMessage + numberOfTestCases, model.getRedoStack().size(), 1);
        System.out.println("Passed " + numberOfTestCases + " out of 8 test cases for loading and saving files.");
    }

    public static void main(String[] args) throws IOException {
        SimpleModelTest tester = new SimpleModelTest();
        tester.testDrawShape();
        tester.testLoadAndSaveFile();
    }
}
