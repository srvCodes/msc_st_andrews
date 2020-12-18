package pieces;

import exceptions.AnimalChessException;
import game.Player;
import game.Square;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Abstract class to keep track of owner and square of each type of pieces.
 */
public abstract class Piece {
    /**
     * Attributes left public to be set directly by each of the subclass.
     */
    private Player owner = null;
    private Square square = null;

    /**
     * Constructor.
     * @param owner is player
     * @param square is the box
     * @throws AnimalChessException when illegal box
     */
    public Piece(Player owner, Square square) throws AnimalChessException {
        if (square.getPiece() instanceof Elephant) {
            if (!(square.getRow() == 0 && square.getRow() == 2) && !(square.getRow() == 3 && square.getRow() == 0)) {
                throw new AnimalChessException("Elephant cannot be put here.");
            }
        } else if (square.getPiece() instanceof Chick) {
            if (!(square.getRow() == 2 && square.getRow() == 1) && !(square.getRow() == 1 && square.getRow() == 1)) {
                throw new AnimalChessException("Chick cannot be put here.");
            }
        } else if (square.getPiece() instanceof Giraffe) {
            if (!(square.getRow() == 0 && square.getRow() == 0) && !(square.getRow() == 3 && square.getRow() == 2)) {
                throw new AnimalChessException("Giraffe cannot be put here.");
            } else if (square.getPiece() instanceof Lion) {
                if (!(square.getRow() == 0 && square.getRow() == 1) && !(square.getRow() == 3 && square.getRow() == 1)) {
                    throw new AnimalChessException("Lion cannot be put here.");
                }
            }
        }
        this.owner = owner;
        this.square = square;
        this.square.placePiece(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Piece piece = (Piece) o;
        return Objects.equals(owner, piece.owner)
                && Objects.equals(square, piece.square);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, square);
    }

    /**
     * Get horizontally right square from player 1's point of view.
     *
     * @return the square
     */
    public Square getRight() {
        Square sq = null;
        if (square.getCol() < square.getGame().getWidth() - 1) {
            sq = square.getGame().getSquare(square.getRow(), square.getCol() + 1);
        }
        return sq;
    }

    /**
     * Get horizontally left square from player 1's point of view.
     *
     * @return the square
     */
    public Square getLeft() {
        Square sq = null;
        if (square.getCol() > 0) {
            sq = square.getGame().getSquare(square.getRow(), square.getCol() - 1);
        }
        return sq;
    }

    /**
     * Get vertically up square from player 1's point of view.
     *
     * @return the square
     */
    public Square getUp() {
        Square sq = null;
        if (square.getRow() > 0) {
            sq = square.getGame().getSquare(square.getRow() - 1, square.getCol());
        }
        return sq;
    }

    /**
     * Get vertically down square from player 1's point of view.
     *
     * @return the square
     */
    public Square getDown() {
        Square sq = null;
        if (square.getRow() < square.getGame().getHeight() - 1) {
            sq = square.getGame().getSquare(square.getRow() + 1, square.getCol());
        }
        return sq;
    }

    /**
     * Get diagonally left-upper square from player 1's point of view.
     *
     * @return the square
     */
    public Square getLeftUp() {
        Square sq = null;
        if (square.getRow() > 0 && square.getCol() > 0) {
            sq = square.getGame().getSquare(square.getRow() - 1, square.getCol() - 1);
        }
        return sq;
    }


    /**
     * Get diagonally left-down square from player 1's point of view.
     *
     * @return the square
     */
    public Square getLeftDown() {
        Square sq = null;
        if (square.getRow() < square.getGame().getHeight() - 1 && square.getCol() > 0) {
            sq = square.getGame().getSquare(square.getRow() + 1, square.getCol() - 1);
        }
        return sq;
    }


    /**
     * Get diagonally right-upper square from player 1's point of view.
     *
     * @return the square
     */
    public Square getRightUp() {
        Square sq = null;
        if (square.getRow() > 0 && square.getCol() < square.getGame().getWidth() - 1) {
            sq = square.getGame().getSquare(square.getRow() - 1, square.getCol() + 1);
        }
        return sq;
    }


    /**
     * Get diagonally right-down square from player 1's point of view.
     *
     * @return the square
     */
    public Square getRightDown() {
        Square sq = null;
        if (square.getRow() < square.getGame().getHeight() - 1 && square.getCol() < square.getGame().getWidth() - 1) {
            sq = square.getGame().getSquare(square.getRow() + 1, square.getCol() + 1);
        }
        return sq;
    }

    /**
     * Computes all possible moves in straight horizontal and vertical directions.
     *
     * @return list of possible horizontal and vertical squares.
     */
    public ArrayList<Square> getAllStraightMoves() {
        ArrayList<Square> allSquares = new ArrayList<>();
        if (getDown() != null && (getDown().getPiece() == null || !getDown().getPiece().getOwner().equals(owner))) {
            allSquares.add(getDown());
        }
        if (getUp() != null && (getUp().getPiece() == null || !getUp().getPiece().getOwner().equals(owner))) {
            allSquares.add(getUp());
        }
        if (getRight() != null && (getRight().getPiece() == null || !getRight().getPiece().getOwner().equals(owner))) {
            allSquares.add(getRight());
        }

        if (getLeft() != null && (getLeft().getPiece() == null || !getLeft().getPiece().getOwner().equals(owner))) {
            allSquares.add(getLeft());
        }
        return allSquares;
    }

    /**
     * Abstract method overridden by each piece.
     *
     * @return list of legal squares to move to.
     */
    public abstract ArrayList<Square> getLegalMoves();

    /**
     * Method to move.
     * @param toSquare is the square to be moved to
     */
    public void move(Square toSquare) {
        try {
            if (toSquare.getPiece() != null) {
                owner.addPieceToHand(toSquare.getPiece());
                toSquare.getPiece().beCaptured(owner);
                toSquare.removePiece();

                if (toSquare.getPiece() instanceof Lion) {
                    owner.winGame();
                }
            }
            square.removePiece();
            square = toSquare;
            toSquare.placePiece(this);

        } catch (AnimalChessException e) {
            System.out.println(e.getMessage());
        }
    }

//    public void move(Square toSquare) {
//        ArrayList<Square> legalMoves = getLegalMoves();
//        try {
//            if (legalMoves.stream().anyMatch(o -> o.getRow() == toSquare.getRow()
//                    && o.getCol() == toSquare.getCol())) {
//                if (!toSquare.getPiece().getOwner().equals(owner)) {
//                    toSquare.getPiece().beCaptured(owner);
//                    owner.addPieceToHand(toSquare.getPiece());
//                    if (toSquare.getPiece() instanceof Lion) {
//                        owner.winGame();
//                    }
//                } else {
//                    throw new Exception("not a legal move");
//                }
//                square.removePiece();
//                square.placePiece(this);
//            } else {
//                throw new Exception("not a legal move");
//            }
//        } catch(Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }

    /**
     * Change the owner to capturing player.
     *
     * @param capturer of the piece
     */
    public void beCaptured(Player capturer) {
        square = null;
        owner = capturer;
    }

    /**
     * Get the current square instance for the piece.
     *
     * @return square
     */
    public Square getSquare() {
        return square;
    }

    /**
     * Get owner player of the piece.
     *
     * @return owner
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Set owner of the instance.
     *
     * @param owner is the player who owns the piece.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Assign square to the piece.
     *
     * @param square where the piece is to be put.
     */
    public void setSquare(Square square) {
        this.square = square;
    }

}
