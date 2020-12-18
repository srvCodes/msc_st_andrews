package exceptions;

/**
 * Custom exception class to display the error message when an illegal move is made.
 */
public class AnimalChessException extends Exception {

    /**
     * constructor.
     * @param message is the string to be displayed
     */
    public AnimalChessException(String message) {
        super(message);
    }

}
