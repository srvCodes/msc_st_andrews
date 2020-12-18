package model;

import java.awt.*;

/**
 * Subclass to create a hexagon.
 */
public class Hexagons extends DrawingShapes {

    public Hexagons(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);

    }

    public Polygon getHexagon() {
        Polygon hexagon = new Polygon();
        int radius = getEuclideanDistance(x1, y1, x2, y2);
        for (int i = 0; i < 6; i++) {
            int xVal = (int) (x2 + radius * Math.cos(i * 2 * Math.PI / 6D));
            int yVal = (int) (y2 + radius * Math.sin(i * 2 * Math.PI / 6D));
            hexagon.addPoint(xVal, yVal);
        }
        return hexagon;
    }

    @Override
    public String toString() {
        return "Hexagon{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}