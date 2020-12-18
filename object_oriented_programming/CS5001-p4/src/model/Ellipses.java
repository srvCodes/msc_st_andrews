package model;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Subclass for creating ellipses.
 */
public class Ellipses extends DrawingShapes {
    private boolean drawCircle = false;

    public Ellipses(int x1, int y1, int x2, int y2, boolean lockAspectRatio) {
        super(x1, y1, x2, y2);
        this.drawCircle = lockAspectRatio;
    }

    public Ellipse2D getEllipse() {
        Ellipse2D ellipse;
        // draw either a circle or an ellipse based on shift key pressed or not.
        if (!drawCircle) {
            ellipse = new Ellipse2D.Double(x1, y1, Math.abs(x2-x1), Math.abs(y2-y1));
        } else {
            int radius = getEuclideanDistance(x1, y1, x2, y2);
            ellipse = new Ellipse2D.Double(Math.abs(x1 - radius), Math.abs(y1 - radius), 2 * radius, 2 * radius);
        }
        return ellipse;
    }

    @Override
    public String toString() {
        return "Ellipse{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}