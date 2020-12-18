package game;

import exceptions.AnimalChessException;
import pieces.Chick;
import pieces.Elephant;
import pieces.Giraffe;
import pieces.Lion;

/**
 * Class to define the 4*3 board.
 */
public class Game {
    /*HEIGHT and WIDTH specify the size of the board to be created.*/
    private final int height = 4;
    private final int width = 3;
    private Square[][] board = new Square[height][width];
    private Player p0;
    private Player p1;

    /**
     * getter method for HEIGHT of board.
     *
     * @return HEIGHT
     */
    public int getHeight() {
        return height;
    }

    /**
     * getter method for WIDTH of board.
     *
     * @return WIDTH
     */
    public int getWidth() {
        return width;
    }

    /**
     * Constructor to assign two players to the game.
     *
     * @param p0 is first player
     * @param p1 is second player
     */
    public Game(Player p0, Player p1) {
        this.p0 = p0;
        this.p1 = p1;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                /* each element of the board[][] array is a square */
                board[i][j] = new Square(this, i, j);
            }
        }

        try {
            Giraffe g0 = new Giraffe(p0, board[0][0]);
            Giraffe g1 = new Giraffe(p1, board[3][2]);

            Lion l0 = new Lion(p0, board[0][1]);
            Lion l1 = new Lion(p1, board[3][1]);

            Elephant e0 = new Elephant(p0, board[0][2]);
            Elephant e1 = new Elephant(p1, board[3][0]);

            Chick c0 = new Chick(p0, board[1][1]);
            Chick c1 = new Chick(p1, board[2][1]);
        } catch (AnimalChessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get instance of Player based on playerNumber.
     *
     * @param playerNumber is ID for player.
     * @return player with playerNumber.
     * @throws IllegalArgumentException if player no. other than 0 or 1
     */
    public Player getPlayer(int playerNumber) throws IllegalArgumentException {
        if (p0.getPlayerNumber() == playerNumber) {
            return p0;
        } else if (p1.getPlayerNumber() == playerNumber) {
            return p1;
        } else {
            throw new IllegalArgumentException("Player number should be 0 or 1.");
        }
    }

    /**
     * Check hasWon() attribute of player to decide winner.
     *
     * @return Player who has won the game.
     */
    public Player getWinner() {
        if (p0.hasWon()) {
            return getPlayer(0);
        } else if (p1.hasWon()) {
            return getPlayer(1);
        } else {
            return null;
        }
    }

    /**
     * Get instance of Square based on row and col attributes.
     *
     * @param row is row of square.
     * @param col is col of square,
     * @return the board array
     */
    public Square getSquare(int row, int col) {
        return board[row][col];
    }
}
