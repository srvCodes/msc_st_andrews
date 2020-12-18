package game;

import exceptions.AnimalChessException;
import pieces.Chick;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class to take care of Player's identity: name, number, list of pieces in hand, and if it has won game.
 */
public class Player {
    private String name;
    private int playerNumber;
    private ArrayList<Piece> handPieces;
    private boolean winGame = false;

    /**
     * Constructor.
     *
     * @param name         is name of the player
     * @param playerNumber is ID of player: 0/1
     */
    public Player(String name, int playerNumber) {
        this.name = name;
        this.playerNumber = playerNumber;
        this.handPieces = new ArrayList<>();
    }

    /**
     * Custom equals() method for comparision of instances of Player class.
     *
     * @param o is instance of class Player
     * @return true if o is equal to this instance else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return playerNumber == player.playerNumber
                && Objects.equals(name, player.name);
    }

    /**
     * Generate hash code for the instance.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, playerNumber);
    }

    /**
     * Accessor for the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor for player number.
     *
     * @return the player no.
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Accessor for list of pieces in hand.
     *
     * @return array of pieces in hand
     */
    public ArrayList<Piece> getHand() {
        return handPieces;
    }

    /**
     * Adds piece to the list of pieces in hand.
     *
     * @param piece is Piece captured by the player
     */
    public void addPieceToHand(Piece piece) {
        handPieces.add(piece);
        if (piece instanceof Chick) {
            Chick ch = (Chick) piece;
            if (ch.getIsPromoted()) {
                ch.setPromote(false);
            }
        }
    }

    /**
     * Drop the piece to square if it is in the list of pieces in hand.
     *
     * @param piece  is the piece to be dropped
     * @param square is the square where it should be dropped
     * @throws AnimalChessException if a piece that isn't in hand is thrown.
     */
    public void dropPiece(Piece piece, Square square) throws AnimalChessException {
        int previousSize = handPieces.size();

        try {
            handPieces.removeIf(o ->  o.getClass().equals(piece.getClass()));
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }

        piece.setSquare(square);
    }

    /**
     * Flag winGame for this player if called.
     */
    public void winGame() {
        winGame = true;
    }

    /**
     * Check for winning.
     *
     * @return true if player has won the game.
     */
    public boolean hasWon() {
        return winGame;
    }
}
