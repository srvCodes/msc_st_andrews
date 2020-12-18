package pieces;

import exceptions.AnimalChessException;
import game.Game;
import game.Player;
import game.Square;

import java.util.ArrayList;

/**
 * Subclass of Piece to represent a chick.
 */
public class Chick extends Piece {
    /* Boolean variable to track if a chick has ever been promoted. */
    private boolean hasBeenPromoted = false;

    /**
     * Constructor.
     * @param owner is the player
     * @param square is the box
     * @throws AnimalChessException to superclass
     */
    public Chick(Player owner, Square square) throws AnimalChessException {
        super(owner, square);
    }

    /**
     * Checks and returns true if the chick has been promoted.
     *
     * @return true/false
     */
    public boolean getIsPromoted() {
        return hasBeenPromoted;
    }

    /**
     * Generate all legal moves for a chick.
     *
     * @return list of squares where chick can possibly move to.
     */
    public ArrayList<Square> getLegalMoves() {
        ArrayList<Square> result = new ArrayList<>();
        int row = super.getSquare().getRow();
        int col = super.getSquare().getCol();
        Game game = super.getSquare().getGame();
        if (!getIsPromoted()) {
            /** If not promoted, the chick can only move one row forward */
            if (super.getOwner().getPlayerNumber() == 0) {
                // check if the last row is reached (for both the players)
                if (row < (game.getHeight() - 1)) {
                    result.add(game.getSquare(row + 1, col));
                }
            }
            if (super.getOwner().getPlayerNumber() == 1) {
                if (row > 0) {
                    result.add(game.getSquare(row - 1, col));
                }
            }
        } else {
            /** if promoted, the chick can now move in six possible ways */
            /** Get all horizontal and vertical moves: */
            result = getAllStraightMoves();
            if (super.getOwner().getPlayerNumber() == 1) {
                /** get additional diagonal moves for player 1 */
                if (getLeftUp() != null && (getLeftUp().getPiece() == null || !getLeftUp().getPiece().getOwner().equals(super.getOwner()))) {
                    result.add(getLeftUp());
                }
                if (getRightUp() != null && (getRightUp().getPiece() == null || !getRightUp().getPiece().getOwner().equals(super.getOwner()))) {
                    result.add(getRightUp());
                }
            } else {
                /** get additional diagonal moves for player 0 */
                if (getRightDown() != null && (getRightDown().getPiece() == null || !getRightDown().getPiece().getOwner().equals(super.getOwner()))) {
                    result.add(getRightDown());
                }
                if (getLeftDown() != null && (getLeftDown().getPiece() == null || !getLeftDown().getPiece().getOwner().equals(super.getOwner()))) {
                    result.add(getLeftDown());
                }
            }
        }
        return result;
    }

    /**
     * check for promotion by looking at row no. for each players' chick.
     */
    public void promote() {
        hasBeenPromoted = true;
    }

    /**
     * Set hasBeenPromoted to the value.
     * @param value true/false
     */
    public void setPromote(boolean value) {
        hasBeenPromoted = value;
    }
    /**
     * overrides the parent class method for performing an extra promote() step.
     *
     * @param toSquare is next square for the move.
     */
    @Override
    public void move(Square toSquare) {
        super.move(toSquare);
        if ((this.getOwner().getPlayerNumber() == 0 && toSquare.getRow() == 3) || (this.getOwner().getPlayerNumber() == 1
         && toSquare.getRow() == 0)) {
            promote();
        }
    }

}
