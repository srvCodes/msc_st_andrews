package model;

import java.awt.geom.Line2D;

/**
 * Subclass to create lines.
 */
public class Lines extends DrawingShapes {

    public Lines(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    public Line2D getLine() {
        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        return line;
    }

    @Override
    public String toString() {
        return "Line{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}