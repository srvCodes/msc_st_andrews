package pieces;

import exceptions.AnimalChessException;
import game.Player;
import game.Square;

import java.util.ArrayList;

/**
 * Inherit class Piece for defining moves of Giraffe.
 */
public class Giraffe extends Piece {

    /**
     * Constructor.
     *
     * @param owner is player
     * @param square is the box
     * @throws AnimalChessException to superclass
     */
    public Giraffe(Player owner, Square square) throws AnimalChessException {
        super(owner, square);
    }

    /**
     * Legal moves for giraffe include only four straight moves.
     *
     * @return list of valid squares to move to.
     */
    public ArrayList<Square> getLegalMoves() {
        ArrayList<Square> result;
        result = getAllStraightMoves();
        return result;
    }

}
