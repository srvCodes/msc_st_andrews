package model;

import javax.swing.*;
import java.awt.*;

/**
 * Superclass with constructor for all shapes.
 */
public abstract class DrawingShapes extends JComponent {
    // only visible to subclasses
    protected int x1, y1, x2, y2;

    public DrawingShapes(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        setPreferredSize(new Dimension(500, 500));
    }

    public int getEuclideanDistance(int x1, int y1, int x2, int y2) {
        int xDist = Math.abs(x2 - x1);
        int yDist = Math.abs(y2 - y1);
        int distance = (int) Math.sqrt(xDist*xDist + yDist*yDist);
        return distance;
    }
}