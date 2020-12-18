import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AlignTextByMode {
    /**
     * Aligns the text in each paragraph based on left (L), right (R) and center (C) modes.
     *
     * @param text: Each paragraph of the input file
     */
    private String[] text_to_words;

    public AlignTextByMode(String text) {
        //Splits texts into words
        text_to_words = text.split("\\s+");
    }

    public StringBuffer alignText(Integer max_line_len, String mode) {
        /**
         * @param max_line_len: no. of characters allowed on a line
         * @param mode: 'L', 'R' or 'C'
         * @return string aligned accordingly with spaces appended
         */

        // addMaxCharsToLine() contains the core logic for filtering what content should be added to each line
        List<String> words_for_each_line = addMaxCharsToLine(text_to_words, max_line_len);
        // line_iterator iterates through words_for_each_line to append spaces per the mode of alignment
        ListIterator<String> line_iterator = words_for_each_line.listIterator();

        StringBuffer result = new java.lang.StringBuffer("");
        while (line_iterator.hasNext()) {
            String nextLine = line_iterator.next();
            int noOfSpaces = 0;
            // if a line has no content to display, skip it
            if (nextLine.length() == 0) {
                continue;
            }
            // compute the no. of spaces to be displayed
            if (nextLine.length() <= max_line_len) {
                noOfSpaces = max_line_len - nextLine.length();
            }
            switch (mode) {
                case "L":
                    // for left alignment / default, append text first, then populate remaining characters with spaces
                    result.append(nextLine);
                    appendSpaces(result, noOfSpaces);
                    break;
                case "R":
                    // for right alignment, first populate remaining characters with spaces then append text
                    appendSpaces(result, noOfSpaces);
                    result.append(nextLine);
                    break;
                case "C":
                    // for center alignment, divide spaces into two, populate at the begin and the end of text
                    // math.ceil() guarantees that for odd noOfSpaces, (noOfSpaces/2 + 1) appended at beginning
                    appendSpaces(result, (int) Math.ceil(noOfSpaces / 2.0));
                    result.append(nextLine);
                    appendSpaces(result, (int) Math.floor(noOfSpaces / 2.0));
                    break;
            }
            // begin next iteration of while loop on a new line
            result.append("\n");
        }
        return result;
    }

    private void appendSpaces(StringBuffer text, int noOfSpaces) {
        /**
         * Appends spaces to the string text.
         * @param text: string to be aligned
         * @param noOfSpaces: spaces to be filled with till max_line_len is reached
         */
        text.append(" ".repeat(noOfSpaces));
    }

    private List<String> addMaxCharsToLine(String[] array_of_words, Integer max_line_len) {
        /**
         * Arranges words of a text into lines with each line containing allowed length of words.
         * @param array_of_words: words of text split up on spaces
         * @param max_line_len: max no. of characters allowed in a line
         * @return list of strings whose each element corresponds to words allowed in a line
         */
        // result = a list of strings whose each element corresponds to the content of one line
        List<String> result = new ArrayList<>();
        String content_of_line = "";
        // if a text has no words, then return a blank list
        if ((array_of_words.length == 0) || (array_of_words == null)) {
            return result;
        }
        for (int i = 0; i < array_of_words.length; ) {
            // Case 1: this handles the case that if a single word is longer than line_length then it may go over
            // the limit
            if (array_of_words[i].length() > max_line_len) {
                // if content_of_line is already populated, push these to result to reflect contents of previous line
                if (content_of_line.length() > 0) {
                    result.add(content_of_line);
                    // empty content_of_line since we have already pushed its contents
                    content_of_line = "";
                }
                // now push the word to result to reflect so that there is a line dedicated for this out-of-limit word
                result.add(array_of_words[i]);
                i++;
            }
            // Case 2: if the length of next word + current content of line is lesser than max_line_len then we have
            // 4 sub-cases:
            else if ((content_of_line.length() + array_of_words[i].length()) <= max_line_len) {
                // Case 2A: if the lines has no current content, simply append the current word to it, increase counter
                if (content_of_line.length() == 0) {
                    content_of_line += array_of_words[i];
                    i++;
                } else {
                    // Case 2B: if line has non-zero content, we also need to take a space (i.e., separation between the
                    // current line content and the word we're checking for) into account: check if adding the space and
                    // the word still makes length of content lesser than max_line_len
                    if ((content_of_line.length() + array_of_words[i].length() + 1) < max_line_len) {
                        content_of_line += " " + array_of_words[i];
                        i++;
                    }
                    // Case 2C: if adding the current word and space to content of line makes its length equal to
                    // max_line_len, this means we reached the limit; simply add the line's content to result now
                    // and increase the loop counter
                    else if ((content_of_line.length() + array_of_words[i].length() + 1) == max_line_len) {
                        content_of_line += " " + array_of_words[i];
                        result.add(content_of_line);
                        content_of_line = "";
                        i++;
                    }
                    // Case 2D: if adding the current word and space surpasses max_line_len, this word can't be added to
                    // current line; we first add the content_of_line to result and then empty the string for next
                    // iteration of the loop
                    else if ((content_of_line.length() + array_of_words[i].length() + 1) > max_line_len) {
                        result.add(content_of_line);
                        content_of_line = "";
                    }
                }
            }
            // Case 3: if the length of next word + current content of line exceeds max_line_len, we first need to
            // push the content_of_line to the result (w/o adding the word) and flush it for the next iteration
            else if ((content_of_line.length() + array_of_words[i].length()) > max_line_len) {
                result.add(content_of_line);
                content_of_line = "";
            }
        }
        // Since the last iteration of for loop doesn't add content_of_line to result, we check for it here
        if (content_of_line.length() > 0) {
            result.add(content_of_line);
        }
        // result now contains elements corresponding to the content of each line
        return result;
    }
}
