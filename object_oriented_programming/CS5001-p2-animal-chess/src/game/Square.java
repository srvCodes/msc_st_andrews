package game;

import exceptions.AnimalChessException;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class to link all 12 squares to the Game while keeping track of row and column.
 */
public class Square {
    private int row;
    private int col;
    private Piece piece;
    private Game game;
    private ArrayList<Piece> pieces = new ArrayList<Piece>();

    /**
     * Constructor.
     *
     * @param game is the game being played
     * @param row  is the row for the square
     * @param col  is the column for the square
     */
    public Square(Game game, int row, int col) {
        this.row = row;
        this.col = col;
        this.game = game;
        this.piece = null;
    }

    /**
     * custom equals and hashCode methods for comparing equality of instances.
     *
     * @param o is the instance to be compared with
     * @return true if equal else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Square square = (Square) o;
        return row == square.row
                && col == square.col
                && Objects.equals(piece, square.piece)
                && Objects.equals(game, square.game)
                && Objects.equals(pieces, square.pieces);
    }

    /**
     * Create a hash code for the instance.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col, piece, game, pieces);
    }

    /**
     * Method to place piece.
     * @param piece is piece to be placed
     * @throws AnimalChessException when piece is null
     */
    public void placePiece(Piece piece) throws AnimalChessException {
        if (piece == null || (this.piece != null)) {
            throw new AnimalChessException("Piece cannot be placed here.");
        }
        this.piece = piece;
    }

    /**
     * Removes the pointer to piece.
     */
    public void removePiece() {
        piece = null;
    }

    /**
     * Get the current game.
     *
     * @return game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the current piece.
     *
     * @return piece.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Return row for this piece.
     *
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * Return column for this piece.
     *
     * @return column
     */
    public int getCol() {
        return col;
    }

    /**
     * Set the row for this piece.
     *
     * @param row to be set to.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Set the column for this piece.
     *
     * @param col to be set to.
     */
    public void setCol(int col) {
        this.col = col;
    }
}
