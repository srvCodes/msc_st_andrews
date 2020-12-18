package pieces;

import exceptions.AnimalChessException;
import game.Player;
import game.Square;

import java.util.ArrayList;

/**
 * Subclass of Piece representing a bishop.
 */
public class Elephant extends Piece {

    /**
     * Constructor.
     * @param owner is player
     * @param square is the box
     * @throws AnimalChessException to superclass
     */
    public Elephant(Player owner, Square square) throws AnimalChessException {
        super(owner, square);
    }

    /**
     * Get legal moves for elephant.
     *
     * @return list of squares for all possible diagonal moves.
     */
    public ArrayList<Square> getLegalMoves() {
        ArrayList<Square> result = new ArrayList<>();

        if (getLeftUp() != null && (getLeftUp().getPiece() == null || !getLeftUp().getPiece().getOwner().equals(super.getOwner()))) {
            result.add(getLeftUp());
        }
        if (getRightUp() != null && (getRightUp().getPiece() == null || !getRightUp().getPiece().getOwner().equals(super.getOwner()))) {
            result.add(getRightUp());
        }
        if (getRightDown() != null && (getRightDown().getPiece() == null || !getRightDown().getPiece().getOwner().equals(super.getOwner()))) {
            result.add(getRightDown());
        }
        if (getLeftDown() != null && (getLeftDown().getPiece() == null || !getLeftDown().getPiece().getOwner().equals(super.getOwner()))) {
            result.add(getLeftDown());
        }
        return result;
    }

}
