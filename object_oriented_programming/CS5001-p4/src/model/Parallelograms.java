package model;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Subclass to create parallelograms.
 */
public class Parallelograms extends DrawingShapes {

    public Parallelograms(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    // check if point p2 is in first quadrant of the image.
    public boolean isPointTwoInQuadOne() {
        return x1 >= x2 && y1 >= y2;
    }

    public Point getCoordinatesForVertexTwo() {
        int x = x2;
        int y = y1;
        return new Point(x, y);
    }

    public Point getCoordinatesForVertexThree() {
        int x = x2 + ((x2 - x1) / 2);
        int y = y2;
        return new Point(x, y);
    }

    public Point getCoordinatesForVertexFour() {
        int x = x1 + ((x2 - x1) / 2);
        int y = y2;
        return new Point(x, y);
    }

    public Path2D getParallelogram() {
        Path2D.Double parallelogram = new Path2D.Double();
        parallelogram.moveTo(x1, y1);
        parallelogram.lineTo(getCoordinatesForVertexTwo().getX(), getCoordinatesForVertexTwo().getY());
        parallelogram.lineTo(getCoordinatesForVertexThree().getX(), getCoordinatesForVertexThree().getY());
        parallelogram.lineTo(getCoordinatesForVertexFour().getX(), getCoordinatesForVertexFour().getY());
        parallelogram.closePath();
        return parallelogram;
    }

}