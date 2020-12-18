package guiDelegate;

import model.MyCanvas;
import model.SimpleModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;


/**
 * The SimpleGuiDelegate class whose purpose is to render relevant state information stored in the model and make changes to the model state based on user events.
 * <p>
 * This class uses Swing to display the model state when the model changes. This is the view aspect of the delegate class.
 * It also listens for user input events (in the listeners defined below), translates these to appropriate calls to methods
 * defined in the model class so as to make changes to the model. This is the controller aspect of the delegate class.
 * The class implements PropertyChangeListener in order to permit it to be added as an observer of the model class.
 * When the model calls notifier.firePropertyChange
 * the propertyChange(...) method below is called in order to update the view of the model.
 *
 * @author jonl
 */
public class SimpleGuiDelegate implements PropertyChangeListener {

    private static final int FRAME_HEIGHT = 800;
    private static final int FRAME_WIDTH = 800;

    private JFrame mainFrame;
    private JToolBar toolbar;

    private JButton rectangleButton;
    private JButton ellipseButton;
    private JButton lineButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton colourButton;
    private JButton traingleButton;
    private JButton parallelogramButton;
    private JButton hexagonButton;
    private JButton diamondButton;
    private JButton clearButton;
    private JRadioButton solidFillButton;

    private JPopupMenu colorMenu;
    private String drawMode;
    private String fillColour = "";
    private boolean solidFill = false;
    private JMenuBar menu;
    private JPanel outputPanel;
    private boolean lockAspectRatio = false;
    private SimpleModel model;
    private Point p1, p2;
    MyCanvas canvas;


    /**
     * Instantiate a new SimpleGuiDelegate object
     *
     * @param model the Model to observe, render, and update according to user events
     */
    public SimpleGuiDelegate(SimpleModel model) {
        this.model = model;
        this.mainFrame = new JFrame("The new paint world!");  // set up the main frame for this GUI
        // set up points
        setUpPoints();
        menu = new JMenuBar();
        toolbar = new JToolBar();
        canvas = new MyCanvas(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        this.model.addInitCanvas(canvas);
        drawMode = "";
        outputPanel = new JPanel();
        outputPanel.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        outputPanel.add(canvas, BorderLayout.CENTER);
        setupComponents();

        // add the delegate UI component as an observer of the model
        // so as to detect changes in the model and update the GUI view accordingly
        model.addObserver(this);

    }

    public static int getFrameHeight() {
        return FRAME_HEIGHT;
    }

    public static int getFrameWidth() {
        return FRAME_WIDTH;
    }

    /**
     * Method to instantiate the points representing vertices of the shapes.
     */
    private void setUpPoints() {
        p1 = new Point();
        p2 = new Point();
    }


    /**
     * Initialises the toolbar to contain the buttons, label, input field, etc. and adds the toolbar to the main frame.
     * Listeners are created for the buttons and text field which translate user events to model object method calls (controller aspect of the delegate)
     */
    private void setupToolbar() {
        colorMenu = new JPopupMenu();
        colorMenu.add(new JMenuItem(new AbstractAction("Red") {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillColour = "red";
            }
        }));
        colorMenu.add(new JMenuItem(new AbstractAction("Green") {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillColour = "green";
            }
        }));
        colorMenu.add(new JMenuItem(new AbstractAction("Blue") {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillColour = "blue";
            }
        }));

        solidFillButton = new JRadioButton("Solid Fill");
        solidFillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle the boolean var
                solidFill = !solidFill;
            }
        });

        colourButton = new JButton("Colour");
        colourButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                colorMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        rectangleButton = new JButton("Rectangle");
        rectangleButton.addActionListener(new ActionListener() {     // to translate event for this button into appropriate model method call
            public void actionPerformed(ActionEvent e) {
                lockAspectRatio = false;
                drawMode = "rectangle";
            }
        });
        // add listener for shift key pressed (this locks the aspect ratio for drawing squares).
        rectangleButton.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    lockAspectRatio = !lockAspectRatio;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        ellipseButton = new JButton("Ellipse");
        ellipseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lockAspectRatio = false;
                drawMode = "ellipse";
            }
        });
        // add listener for shift key pressed (this locks the aspect ratio for drawing circles)
        ellipseButton.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    lockAspectRatio = !lockAspectRatio;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        lineButton = new JButton("Line");
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawMode = "line";
            }
        });

        undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.undo(canvas);
            }
        });

        redoButton = new JButton("Redo");
        redoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.redo(canvas);
            }
        });

        traingleButton = new JButton("Triangle");
        traingleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawMode = "triangle";
            }
        });

        hexagonButton = new JButton("Hexagon");
        hexagonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawMode = "hexagon";
            }
        });

        parallelogramButton = new JButton("Parallelogram");
        parallelogramButton.addActionListener(new ActionListener() {  // to translate event for this button into appropriate model method call
            public void actionPerformed(ActionEvent e) {
                drawMode = "parallelogram";
            }
        });

        diamondButton = new JButton("Diamond");
        diamondButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawMode = "diamond";
            }
        });

        clearButton = new JButton(("Clear"));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.clear(canvas);
            }
        });
        // add buttons and frame to the toolbar
        toolbar.add(solidFillButton);
        toolbar.add(colourButton);
        toolbar.add(lineButton);
        toolbar.add(rectangleButton);
        toolbar.add(ellipseButton);
        toolbar.add(undoButton);
        toolbar.add(redoButton);
        toolbar.add(traingleButton);
        toolbar.add(hexagonButton);
        toolbar.add(parallelogramButton);
        toolbar.add(diamondButton);
        toolbar.add(clearButton);
        // add toolbar to north of main frame
        mainFrame.add(toolbar, BorderLayout.NORTH);
    }

    /**
     * Sets up File menu with Load and Save entries
     * The Load and Save actions would normally be translated to appropriate model method calls similar to the way the code does this
     * above in @see #setupToolbar(). However, load and save functionality is not implemented here, instead the code below merely displays
     * an error message.
     */
    private void setupMenu() {
        JMenu file = new JMenu("File");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem save = new JMenuItem("Save");
        file.add(load);
        file.add(save);
        menu.add(file);
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.loadFile(canvas);
            }
        });
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.saveFile(canvas);
            }
        });
        // add menubar to frame
        mainFrame.setJMenuBar(menu);
    }

    /**
     * Method to setup the menu and toolbar components, and mouse event listeners.
     */
    private void setupComponents() {
        setupMenu();
        setupToolbar();

        outputPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                p1 = mouseEvent.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                p2 = mouseEvent.getPoint();
                model.drawShape(canvas, drawMode, fillColour, solidFill, p1, p2, lockAspectRatio);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        outputPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                p2 = e.getPoint();
//                model.drawShapeOnDrag(canvas, drawMode, fillColour, solidFill, p1, p2, lockAspectRatio);
//                outputPanel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        mainFrame.add(outputPanel, BorderLayout.CENTER);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * This method contains code to update the GUI view when the model changes.
     * The method is called when the model changes (i.e. when the model executes notifier.firePropertyChange)
     * <p>
     * The code in propertyChange is sent the property that has changed and can display it appropriately.
     * For this simple example, the only state information we need from the model is what is in the model's text buffer and the
     * only GUI view element we need to update is the text area used for output.
     * <p>
     * NOTE: In a more complex program, the model may hold information on a variety of objects.
     */
    public void propertyChange(final PropertyChangeEvent event) {

        if (event.getSource() == model && event.getPropertyName().equals("canvasUpdated")) {
            // Tell the SwingUtilities thread to update the canvas and remove all previous components.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    canvas = (MyCanvas) event.getNewValue();
                    outputPanel.removeAll();
                    outputPanel.add(canvas, BorderLayout.CENTER);
                    outputPanel.revalidate();
                    outputPanel.repaint();
                }
            });
        }
    }

}