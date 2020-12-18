package pieces;

import exceptions.AnimalChessException;
import game.Player;
import game.Square;

import java.util.ArrayList;

/**
 * Subclass representing a lion piece.
 */
public class Lion extends Piece {

    /**
     * Constructor.
     * @param owner is player
     * @param square is the box
     * @throws AnimalChessException to superclass
     */
    public Lion(Player owner, Square square) throws AnimalChessException {
        super(owner, square);
    }

    /**
     * Legal moves of a lion include all eight directions (four straight moves + four diagonal ones).
     *
     * @return list of all eight squares that the lion can move to
     */
    public ArrayList<Square> getLegalMoves() {
        ArrayList<Square> result;
        result = getAllStraightMoves();
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

    /**
     * Overrides the super class method by additionally calling the winGame() method.
     */
    @Override
    public void beCaptured(Player capturer) {
        super.setOwner(capturer);
        capturer.winGame();
    }
}
