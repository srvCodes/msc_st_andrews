package model;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Subclass to create diamond objects.
 */
public class Diamonds extends DrawingShapes {
    // x2 and y2 here serve for width calculation
    public Diamonds(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
    }

    public Path2D getDiamond() {
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        Path2D.Double diamond = new Path2D.Double();
        diamond.moveTo(x2, y2 + height / 2);
        diamond.lineTo(x2 + width / 2, y2 + height);
        diamond.lineTo(x2 + width, y2 + height / 2);
        diamond.lineTo(x2 + width / 2, y2);
        diamond.closePath();
        return diamond;
    }

}