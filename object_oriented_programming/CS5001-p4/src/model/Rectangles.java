package model;

import java.awt.geom.Rectangle2D;

/**
 * Subclass to create rectangle objects.
 */
public class Rectangles extends DrawingShapes {
    private boolean drawSquare = false;

    public Rectangles(int x1, int y1, int x2, int y2, boolean lockAspectRatio) {
        super(x1, y1, x2, y2);
        this.drawSquare = lockAspectRatio;
    }

    // check if point p2 is in first quadrant of the image.
    public boolean isPointTwoInQuadOne() {
        return x1 >= x2 && y1 >= y2;
    }

    public Rectangle2D getRectangle() {
        Rectangle2D rect;
        // create either rectangle or square based on shift key pressed or not.
        if (!drawSquare) {
            rect = new Rectangle2D.Double(x1, y1, x1 - x1, y1 - y1);
            if (isPointTwoInQuadOne()) {
                // allows drawing rectangle in bottom-right to top-left direction as well
                rect.setRect(x2, y2, x1 - x2, y1 - y2);
            } else {
                rect.setRect(x1, y1, x2 - x1, y2 - y1);
            }

        } else {
            int distance = getEuclideanDistance(x1, y1, x2, y2);
            rect = new Rectangle2D.Double(x1, y1, distance, distance);
        }
        return rect;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}