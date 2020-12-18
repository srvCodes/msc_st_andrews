package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A simple model class whose purpose is to display and store shapes entered by a user.
 * <p>
 * The model's state is altered by user input when the delegate calls the drawShape() method below.
 * <p>
 * The model supports change listeners to be notified when it changes. This form
 * of loose coupling permits the Delegate (View) to be updated when the model has changed.
 */
public class SimpleModel {

    private java.util.List<DrawingShapes> shapesList = new ArrayList<DrawingShapes>();

    private List<BufferedImage> imageList = new ArrayList<>();
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();
    private String fileToSave = "vectorDrawing.png";

    public List<DrawingShapes> getShapesList() {
        return shapesList;
    }

    public void setShapesList(List<DrawingShapes> shapesList) {
        this.shapesList = shapesList;
    }

    public List<BufferedImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<BufferedImage> imageList) {
        this.imageList = imageList;
    }

    public Stack<BufferedImage> getUndoStack() {
        return undoStack;
    }

    public void setUndoStack(Stack<BufferedImage> undoStack) {
        this.undoStack = undoStack;
    }

    public Stack<BufferedImage> getRedoStack() {
        return redoStack;
    }

    public void setRedoStack(Stack<BufferedImage> redoStack) {
        this.redoStack = redoStack;
    }

    /**
     * the change support object to help us fire change events at observers
     */
    private PropertyChangeSupport notifier;

    /**
     * Constructs a new SimpleModel instance.
     * Initialises the StringBuffer and property change support.
     */
    public SimpleModel() {
        notifier = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getNotifier() {
        return notifier;
    }

    /**
     * Utility method to permit an observer to add themselves as an observer to the model's change support object.
     *
     * @param listener the listener to add
     */
    public void addObserver(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    /**
     * Method to retrieve and remove shapes from canvas in the reverse order of when they were drawn.
     *
     * @param canvas is object of the class Canvas.
     */
    public void undo(MyCanvas canvas) {
        if (undoStack.size() > 1) {
            redoStack.push(undoStack.pop());
        }
        canvas = new MyCanvas(undoStack.peek());
        notifier.firePropertyChange("canvasUpdated", null, canvas);
    }

    /**
     * Method to make shapes reappear on the cavas as the user presses the redo button.
     *
     * @param canvas is object of the class Canvas.
     */
    public void redo(MyCanvas canvas) {
        if (redoStack.size() > 0) {
            canvas = new MyCanvas(redoStack.peek());
            undoStack.push(redoStack.pop());
            notifier.firePropertyChange("canvasUpdated", null, canvas);
        }

    }

    /**
     * Method to clear the canvas when user presses 'Clear' button.
     *
     * @param canvas is the canvas coupled with OutputPanel.
     */
    public void clear(MyCanvas canvas) {
        canvas.setImg(new BufferedImage(canvas.getHEIGHT(), canvas.getWIDTH(), canvas.getTYPE()));
        ;
    }

    /**
     * Method to save the buffered image of canvas for future use.
     *
     * @param canvas is the canvas coupled with OutputPanel.
     */
    public void saveFile(MyCanvas canvas) {
        try {
            ImageIO.write(canvas.getImg(), "PNG", new File(fileToSave));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to load the buffered image of canvas for use.
     *
     * @param canvas is the canvas coupled with OutputPanel.
     */
    public void loadFile(MyCanvas canvas) {
        try {
            // load the image file and couple it with the canvas
            BufferedImage image = ImageIO.read(new File(fileToSave));
            canvas.setImg(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to copy the image in canvas and add it to undo stack.
     *
     * @param canvas is the current canvas coupled to JPanel.
     */
    public void addInitCanvas(MyCanvas canvas) {
        BufferedImage img = canvas.copyImage(null);
        undoStack.push(img);
    }

    /**
     * Method to trigger the model.
     *
     * @param canvas          is the current canvas coupled with JPanel.
     * @param shape           is the shape user wants to draw: line, rectangle, etc.
     * @param colour          is the colour the user want to draw/fill in.
     * @param solidFill       tells whether to fill the shape with colour or not.
     * @param p1              is the first point pressed.
     * @param p2              is the second point pressed.
     * @param lockAspectRatio tells whether to lock the aspect ratio for drawing circles and squares.
     */
    public void drawShape(MyCanvas canvas, String shape, String colour, boolean solidFill, Point p1, Point p2,
                          boolean lockAspectRatio) {
        // clear the redo stack each time the user wants to draw a new shape; only add it to the undo stack
        redoStack.clear();
        canvas.redraw(shape, colour, solidFill, p1, p2, lockAspectRatio);
        // create a copy of the image in the canvas to be saved to the undo stack
        BufferedImage img = canvas.copyImage(null);
        // add buffered image to undo stack
        undoStack.push(img);
        // notify the delegate
        notifier.firePropertyChange("canvasUpdated", null, canvas);
    }

}