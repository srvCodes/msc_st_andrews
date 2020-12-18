public class InvalidArgException extends Exception {
    /**
     * Generates an exception message if the command line arguments are invalid or missing
     * @param error_msg is the message to be displayed
     */
    public  InvalidArgException(String error_msg){
        super(error_msg);
    }
}
