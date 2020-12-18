import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Main class for the simple HTTP web server.
 * All requirements implemented within the class.
 */
public class WebServerMain {

    private String dirPath = null;
    private ServerSocket serverSocket = null;
    private Socket conn = null;
    private final String protocol = "HTTP/1.1";
    private PrintWriter printWriter = null;
    private BufferedReader urlContent = null;
    private OutputStream out = null;
    private final String logPath;
    private Map<Integer, String> debuggingMessages;
    private static final int ERROR_200 = 200;
    private static final int ERROR_400 = 400;
    private static final int ERROR_505 = 505;
    private static final int ERROR_501 = 501;
    private static final int ERROR_403 = 403;
    private static final int ERROR_500 = 500;
    private static final int ERROR_404 = 404;

    /**
     * Constructor.
     *
     * @param port is the port number.
     * @param path is the URL requested.
     * @throws IOException when content not available.
     */
    public WebServerMain(int port, String path) throws IOException {
        super();
        this.dirPath = path;
        logPath = dirPath + File.separator + ".." + File.separator + "logs";

        debuggingMessages = new HashMap<Integer, String>();
        debuggingMessages.put(ERROR_505, "HTTP version not supported!");
        debuggingMessages.put(ERROR_404, "Not Found");
        debuggingMessages.put(ERROR_400, "Bad request to server!");
        debuggingMessages.put(ERROR_200, "OK");
        debuggingMessages.put(ERROR_501, "Not Implemented");

        serverSocket = new ServerSocket(port);
        // new connection made as long as input is supplied
        while (true) {
            conn = serverSocket.accept();
            InputStreamReader inputStream = new InputStreamReader(conn.getInputStream());
            urlContent = new BufferedReader(inputStream);
            out = conn.getOutputStream();
            printWriter = new PrintWriter(out, true);

            String line = urlContent.readLine();
            System.out.print("\n line: " + line);
            if (isRequestValid(line)) {
                processAndDisplayRequest(line);
            }
            conn.close();
        }
    }

    /**
     * Method to determine valid request types.
     *
     * @return a list of allowed services.
     */
    public List<String> getValidRequestTypes() {
        List<String> allowedRequestTypes = new ArrayList<>();
        allowedRequestTypes.add("GET");
        allowedRequestTypes.add("HEAD");
        allowedRequestTypes.add("DELETE");
        return allowedRequestTypes;
    }

    /**
     * Method to check if service requested is one of allowed services and serve it.
     *
     * @param request is the type of service requested.
     * @return true if request is valid, otherwise false.
     */
    public boolean isRequestValid(String request) {
        String[] splitRequest = request.split(" ");
        List<String> validRequests = getValidRequestTypes();

        // http request has three components: request line, header and body
        if (splitRequest.length != 3) {
            getHtmlResponseForClient(request, ERROR_400, "", true);
            return false;
        } else if (!splitRequest[splitRequest.length - 1].trim().equals(protocol)) {
            getHtmlResponseForClient(request, ERROR_505, "", true);
            return false;
        } else if (!validRequests.contains(splitRequest[0])) {
            getHtmlResponseForClient(request, ERROR_501, "", true);
            return false;
        }
        return true;
    }

    /**
     * Method to determine if request is valid for a given file.
     *
     * @param inputLine  is the input by client.
     * @param fileToRead is the file to be served.
     * @param method     is the service type - HEAD/GET/DELETE
     * @return true if request is valid for the file, otherwise false.
     */
    public boolean isRequestValidForFile(String inputLine, File fileToRead, String method) {
        if (!fileToRead.exists()) {
            getHtmlResponseForClient(inputLine, ERROR_404, "", true);
            return false;
        }
        if (!isRequestAllowedForMethods(method, fileToRead)) {
            getHtmlResponseForClient(inputLine, ERROR_403, "", true);
            return false;
        }
        return true;
    }

    /**
     * Method to check the validity of a request type.
     *
     * @param method     is request type to serve - HEAD/GET/DELETE
     * @param fileToRead is the file requested by the client.
     * @return true if method is valid for the file, otherwise false.
     */
    public boolean isRequestAllowedForMethods(String method, File fileToRead) {
        if (!fileToRead.canRead()) {
            return false;
        }
        if (method.equals("DELETE") || method.equals("PUT")) {
            if (!fileToRead.canWrite()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to check if file is an image based on its extension.
     *
     * @param fileExtension is the file type (jpg, png, html, etc.)
     * @return true if image, otherwise false
     */
    private boolean isImage(String fileExtension) {
        if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("gif")) {
            return true;
        }
        return false;
    }

    /**
     * Method to serve "HEAD", "GET" and "DELETE" requests per the file type.
     *
     * @param inputLine is the input received by server through socket.
     */
    private void processAndDisplayRequest(String inputLine) {
        String[] listOfTokens = inputLine.split(" ");
        String method = listOfTokens[0];
        String filePath = dirPath + "/" + listOfTokens[1];
        String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1);
        File fileToRead = new File(filePath);

        boolean result = isRequestValidForFile(inputLine, fileToRead, method);
        if (!result) {
            return;
        }

        if (method.equals("GET")) {
            if (isImage(fileExtension)) {
                getImageResponseForClient(inputLine, fileToRead, fileExtension, ERROR_200);
            } else {
                String fileContent = readFile(fileToRead);
                getHtmlResponseForClient(inputLine, ERROR_200, fileContent, true);
            }
            return;
        } else if (method.equals("HEAD")) {
            String fileContent = readFile(fileToRead);
            getHtmlResponseForClient(inputLine, ERROR_200, fileContent, false);
            return;
        } else if (method.equals("DELETE")) {
            try {
                Files.delete(fileToRead.toPath());
                getHtmlResponseForClient(inputLine, ERROR_200, "", false);
            } catch (IOException e) {
                getHtmlResponseForClient(inputLine, ERROR_500, "", false);
                System.err.println(e);
            }
            return;
        }

    }

    /**
     * Method to read image and make responseHeader with info about request and the image.
     *
     * @param fileToRead    is the image file object
     * @param fileExtension is the extension of the image
     * @param responseCode  is the type of response to serve
     */
    private void getImageResponseForClient(String inputLine, File fileToRead, String fileExtension, int responseCode) {
        try {
            byte[] fileContent = Files.readAllBytes(fileToRead.toPath());
            makeLogsForRequest(inputLine, responseCode, fileContent.length);
            String responseHeader = getResponseHeader(fileContent.length, fileExtension, responseCode);
            System.out.println(responseHeader);
            out.write(responseHeader.getBytes());
            printWriter.println(responseHeader);
            out.write(fileContent, 0, fileContent.length);
            System.out.println(Arrays.toString(fileContent));
            printWriter.flush();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to write responseHeader and file content to the printWriter.
     *
     * @param inputLine    is the input stream received from server's socket
     * @param responseCode is the type of response
     * @param fileContent  is the body of the file
     * @param serveBody    tells whether to serve body (for "GET") or not (for "HEAD").
     */
    private void getHtmlResponseForClient(String inputLine, int responseCode, String fileContent, boolean serveBody) {
        System.out.print("input line: " + inputLine);
        makeLogsForRequest(inputLine, responseCode, fileContent.length());
        String responseHeader = connectionHandler(responseCode, fileContent);
        System.out.println(responseHeader);
        printWriter.println(responseHeader);
        if (serveBody) {
            printWriter.println(fileContent);
            System.out.println(fileContent);
        }
        printWriter.flush();
    }

    /**
     * Method overloaded for image files (overloading based on parameters).
     *
     * @param imageSize    is the size of the bytes array of the image
     * @param extension    is the file type: jpeg, png or gif
     * @param responseCode is the type of response
     * @return response header containing mimeType, content type/length and server type.
     */
    private String getResponseHeader(int imageSize, String extension, int responseCode) {
        String crlf = "\r\n";
        String mimeType = "image/";
        if (extension.equals("jpg")) {
            mimeType += "jpeg";
        } else {
            mimeType += extension;
        }
        String responseHeader = protocol + " " + Integer.toString(responseCode) + " "
                + debuggingMessages.get(responseCode) + crlf;
        responseHeader += "Server: Http server" + crlf;
        responseHeader += "Content-Type: " + mimeType + crlf;
        responseHeader += "Content-Length: " + imageSize + crlf;
        return responseHeader;
    }

    /**
     * Method overloaded for html files.
     *
     * @param responseCode is the type of response.
     * @param responseBody contains the content of the file
     * @return response header containing mimeType, content type/length and server type.
     */
    private String connectionHandler(int responseCode, String responseBody) {
        String crlf = "\r\n";
        String mimeType = "text/html";
        String responseHeader = protocol + " " + Integer.toString(responseCode) + " "
                + debuggingMessages.get(responseCode) + crlf;
        responseHeader += "Server: Http server" + crlf;
        responseHeader += "Content-Type: " + mimeType + crlf;
        responseHeader += "Content-Length: " + responseBody.length() + crlf;
        return responseHeader;
    }

    /**
     * Function to log requests to files in "logs/".
     *
     * @param requestLine        is the filename
     * @param responseCode       reflects one of the response types
     * @param sizeOfFileReturned is the size of content in the file
     */
    private void makeLogsForRequest(String requestLine, int responseCode, int sizeOfFileReturned) {
        String currentDate = new SimpleDateFormat("dd_MM_YYYY").format(new Date());
        File currentLog = new File(logPath + File.separator + currentDate + ".log");
        File logDir = new File(logPath);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        try {
            currentLog.createNewFile();
            try (PrintWriter logger = new PrintWriter(new FileWriter(currentLog, true));) {
                String timeStamp = new SimpleDateFormat("[dd/MM/YYYY:HH:mm:ss:SSSS z]").format(new Date());
                logger.print(timeStamp + " \"" + requestLine + "\" " + Integer.toString(responseCode) + " "
                        + Integer.toString(sizeOfFileReturned) + "\r\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Function to read html files as text.
     *
     * @param file is the html file
     * @return string with file contents
     */
    private String readFile(File file) {
        String fileContent = "";
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                // \r\n used for carriage return and line feed
                fileContent += sc.nextLine() + "\r\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    /**
     * Main function.
     *
     * @param args : command-line arguments: args[0] is the path to resources directory, args[1] is port no.
     */
    public static void main(String[] args) {
        try {
            File dirPath = new File(args[0]);
            int portNo = Integer.parseInt(args[1]);
            assert dirPath.exists() : dirPath + " does not exist!";
            assert dirPath.isDirectory() : dirPath + " is not a directory!";
            assert portNo > 0 : "Port number must be greater than zero";
            new WebServerMain(portNo, args[0]);
        } catch (Exception e) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }

    }

}