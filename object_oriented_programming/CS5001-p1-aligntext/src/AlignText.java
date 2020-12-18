public class AlignText {
    /**
     *
     * @param args command line arguments in order: (a) file_name, (b) line_length, and (c) optional: align_mode
     * @throws InvalidArgException when arguments are invalid or missing
     */
    public static void main(String args[]) throws InvalidArgException {
        String[] paragraphs = new String[1];
        Integer wrap_len = 0;
        String mode = "L";

        // try reading command-line arguments
        try {
            paragraphs = FileUtil.readFile(args[0]);
            wrap_len = Integer.parseInt(args[1]);
            if (wrap_len < 0){
                throw new IllegalArgumentException();
            }
            // mode remains "L" (left) by default
            if (args.length == 3) {
                mode = args[2];
            }
        }
        // in case of a missing (ArrayIndexOutOfBounds) or invalid (NumberFormat) exception, call the manual
        // InvalidArgException class to display error message
       catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
           String error_msg = "usage: java AlignText file_name line_length [align_mode]";
           System.out.println(error_msg);
           System.exit(0);
           // Commented call to manual exception class because of test case 2 failure
           // throw new InvalidArgException(error_msg);
       }
        StringBuffer result = new java.lang.StringBuffer();
        for (String text: paragraphs) {
            AlignTextByMode text_aligner_instance = new AlignTextByMode(text);
            result.append(text_aligner_instance.alignText(wrap_len, mode));
        }
        System.out.println(result);
    }
}
