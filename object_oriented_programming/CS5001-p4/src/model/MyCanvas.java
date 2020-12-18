package model;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Class to represent the canvas with the buffered image in which shapes are to be drawn.
 */
public class MyCanvas extends JLabel implements Cloneable {

    private BufferedImage img;
    private int WIDTH = 0;
    private int HEIGHT = 0;
    private int TYPE = 0;

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getTYPE() {
        return TYPE;
    }

    /**
     * Constructor.
     * @param image is the buffered image.
     */
    public MyCanvas(BufferedImage image) {
        img = copyImage(image);
        HEIGHT = img.getHeight();
        WIDTH = img.getWidth();
        TYPE = img.getType();
        setIcon(new ImageIcon(img));
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
        setIcon(new ImageIcon(this.img));
    }

    /**
     * Constructor to initialize buffered image with specified dimensions.
     * @param height is the height of image
     * @param width is th width
     * @param type is the type, e.g. RGB (color) or black-and-white.
     */
    public MyCanvas(int height, int width, int type) {
        WIDTH = width;
        HEIGHT = height;
        TYPE = type;
        setImg(new BufferedImage(HEIGHT, WIDTH, TYPE));
    }

    /**
     * Method to copy the buffered image based on the dimensions and type.
     * @param source is the source image to be copied.
     * @return the copied image.
     */
    public BufferedImage copyImage(BufferedImage source) {
        // if source image is null, copy an empty image (e.g. for case where user presses redo/undo before any button?).
        BufferedImage src = (source == null) ? img : source;
        BufferedImage b = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return b;
    }

    /**
     * Method to check whether to draw or fill colored shape on graphics.
     * @param g2d is the graphics object.
     * @param s is the shape to draw/fill.
     * @param solidFill is parameter saying whether to fill or just draw.
     */
    public void fillColor(Graphics2D g2d, Shape s, boolean solidFill) {
        if (solidFill && !(s instanceof Line2D)) { // g2d.fill() fills line with black as default (invisible).
            g2d.fill(s);
        } else {
            g2d.draw(s);
        }

    }

    /**
     * Method to get shapes corresponding to the user's input and draw them on buffered image.
     * @param shape is the shape to be drawn
     * @param colour is the colour to be drawn/filled
     * @param solidFill tells whether to fill or draw
     * @param p1 is the first point
     * @param p2 is the second point
     * @param lockAspectRatio tells whether to lock the aspect ratio or not.
     */
    public void redraw(String shape, String colour, boolean solidFill, Point p1, Point p2, boolean lockAspectRatio) {
        Graphics2D g2d = img.createGraphics();
        if (colour.length() > 0) {
            if (colour.equals("red")) {
                g2d.setColor(Color.RED);
            } else if (colour.equals("green")) {
                g2d.setColor(Color.GREEN);
            } else if (colour.equals("blue")) {
                g2d.setColor(Color.BLUE);
            }
        }
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x1 = (int) p1.getX();
        int y1 = (int) p1.getY();
        int x2 = (int) p2.getX();
        int y2 = (int) p2.getY();

        Shape s;
        if (shape.equals("rectangle")) {
            Rectangles rect = new Rectangles(x1, y1, x2, y2, lockAspectRatio);
            s = rect.getRectangle();
        } else if (shape.equals("ellipse")) {
            Ellipses ellipse = new Ellipses(x1, y1, x2, y2, lockAspectRatio);
            s = ellipse.getEllipse();
        } else if (shape.equals("line")) {
            Lines line = new Lines(x1, y1, x2, y2);
            s = line.getLine();
        } else if (shape.equals("triangle")) {
            Triangles triangle = new Triangles(x1, y1, x2, y2);
            s = triangle.getTriangle();
        } else if (shape.equals("hexagon")) {
            Hexagons hexagon = new Hexagons(x1, y1, x2, y2);
            s = hexagon.getHexagon();
        } else if (shape.equals("parallelogram")) {
            Parallelograms parallelogram = new Parallelograms(x1, y1, x2, y2);
            s = parallelogram.getParallelogram();
        } else if (shape.equals("diamond")) {
            Diamonds diamond = new Diamonds(x1, y1, x2, y2);
            s = diamond.getDiamond();
        } else {
            // draw a point by default (if no shape specified before click)
            s = new Rectangle2D.Double(x1, y1, 0, 0);
        }
        fillColor(g2d, s, solidFill);
    }

}