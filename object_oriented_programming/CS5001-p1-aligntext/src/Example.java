import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Example {
//    public static StringBuffer alignText(String text, Integer max_line_len, String mode) {
//        List<String> list_of_words = splitParagraphOnLineLen(text, max_line_len);
//        ListIterator<String> i = list_of_words.listIterator();
//
//        StringBuffer result = new java.lang.StringBuffer("");
//        while (i.hasNext()) {
//            String nextLine = i.next();
//            int noOfSpaces = 0;
//            if (nextLine.length() <= max_line_len)
//                noOfSpaces = max_line_len - nextLine.length() + 1;
//            switch (mode) {
//                case "L":
//                    result.append(nextLine);
//                    appendSpaces(result, noOfSpaces);
//                    break;
//                case "R":
//                    appendSpaces(result, noOfSpaces);
//                    result.append(nextLine);
//                    break;
//                case "C":
//                    appendSpaces(result, noOfSpaces / 2);
//                    result.append(nextLine);
//                    appendSpaces(result, noOfSpaces / 2);
//                    break;
//            }
//            result.append("\n");
//        }
//        return result;
//    }

    public static StringBuffer alignText(String text, Integer max_line_len, String mode) {
        String[] text_to_words = text.split("\\s+");
        List<String> list_of_words = addWordsToLine(text_to_words, max_line_len);
        ListIterator<String> i = list_of_words.listIterator();

        StringBuffer result = new java.lang.StringBuffer("");
        while (i.hasNext()) {
            String nextLine = i.next();
            int noOfSpaces = 0;
            if (nextLine.length() == 0)
                continue;
            if (nextLine.length() <= max_line_len)
                noOfSpaces = max_line_len - nextLine.length();
            switch (mode) {
                case "L":
                    result.append(nextLine);
                    appendSpaces(result, noOfSpaces);
                    break;
                case "R":
                    appendSpaces(result, noOfSpaces);
                    result.append(nextLine);
                    break;
                case "C":
                    appendSpaces(result, noOfSpaces / 2);
                    result.append(nextLine);
                    appendSpaces(result, noOfSpaces / 2);
                    break;
            }
            result.append("\n");
        }
        return result;
    }

    private static void appendSpaces(StringBuffer text, int noOfSpaces) {
        text.append(" ".repeat(noOfSpaces));
    }

    public static List<String> addWordsToLine(String[] array_of_words, Integer max_line_len){
        List<String> result = new ArrayList<>();
        System.out.println(Arrays.toString(array_of_words));
        String substring = "";
        if ((array_of_words.length == 0) || (array_of_words == null))
            return result;
        for (int i = 0; i < array_of_words.length; ){
//            System.out.println(array_of_words[i]);
//            System.out.println(substring.length());
            if (array_of_words[i].length() > max_line_len){
                if (substring.length() > 0)
                    result.add(substring); /*case: if first word > maxlinelen then, first element = null*/
                substring = "";
                result.add(array_of_words[i]);
                i++;

                continue;
            }
//            else if ((substring.length() + array_of_words[i].length()) <= max_line_len) {
//                if ((substring.length() + array_of_words[i].length() + array_of_words[i+1].length() + 2) <= max_line_len) {
//                    if (substring.length() > 0)
//                        substring += " ";
//                    substring += array_of_words[i] + " ";
//                    substring += array_of_words[i+1];
//                }
//                else{
//                    if (substring.length() > 0)
//                        substring += " ";
//                    substring += array_of_words[i];
//                }
//                i++;
//                continue;
//            }
            else if ((substring.length() == 0) && (array_of_words[i].length() <= max_line_len)){
                substring += array_of_words[i];
                i++;
                continue;
            }
            else if ((substring.length() > 0) && ((array_of_words[i].length() + substring.length() + 1) <= max_line_len)){
                substring += " ";
                substring += array_of_words[i];
                i++;
                continue;
            }
            else if ((substring.length() + array_of_words[i].length()) > max_line_len){
                result.add(substring);
//                System.out.println(substring);
                substring = "";
            }
        }
        if (substring.length() > 0)
            result.add(substring);
        return result;
    }

    public static List<String> splitParagraphOnLineLen(String text, Integer max_line_len) {
        List<String> all_lines = new ArrayList<String>();
        int lastSpaceIdx = 0;
        if (text == null)
            return all_lines;
        for (int i = 0; i < text.length(); ) {
            i = lastSpaceIdx;
            if ((i < text.length() -1) && (text.charAt(i) == ' '))
                i++;
            int lastCharIdx = Math.min(i + max_line_len +1, text.length());
            lastSpaceIdx = lastCharIdx;
            CharSequence lastChar = text.subSequence(lastCharIdx-1, lastCharIdx);

            String punctuation_marks = ";,'!?@#^`:";
            if (Character.isLetterOrDigit(text.charAt(lastCharIdx - 1))){
                while (text.charAt(lastSpaceIdx - 1) != ' ') {
                    lastSpaceIdx -= 1;
                }
            }

            if ((lastCharIdx - lastSpaceIdx) >= max_line_len) {
                lastSpaceIdx = lastCharIdx-1;
                while (lastSpaceIdx  < text.length()) {
                    if (text.charAt(lastSpaceIdx) == ' ') {
//                        if ((lastSpaceIdx+1) < text.length())
//                            lastSpaceIdx++;
                        break;
                    }
                    lastSpaceIdx++;
                }
            }
            all_lines.add(text.substring(i, lastSpaceIdx));
        }
        return all_lines;
    }
}
