package model;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Subclass to create a triangle.
 */
public class Triangles extends DrawingShapes {
    public Triangles(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);

    }

    /**
     * Forumlae from https://math.stackexchange.com/questions/880135/how-to-get-the-third-point-coordinates-in-isosceles-triangle
     *
     * @return x3: (x3 = y3)
     */

    public Point getCoordinatesForVertexThree() {
        int x = x1 + ((x2 - x1) / 2);
        int y = y2;
        return new Point(x, y);
    }

    public Path2D.Double getTriangle() {
        Path2D.Double triangle = new Path2D.Double();
        triangle.moveTo(x1, y1);
        triangle.lineTo(x2, y2);
        triangle.lineTo(getCoordinatesForVertexThree().getX(), getCoordinatesForVertexThree().getY());
        triangle.closePath();
        return triangle;
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}