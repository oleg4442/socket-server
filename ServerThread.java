import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Set;

/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread {
    private Socket socket;
    private Set numbersSet;
    private List numbersList;
    private Callback callback;

    ServerThread(Callback callback, Socket socket, Set numbersSet, List numbersList) {
        this.callback = callback;
        this.socket = socket;
        this.numbersSet = numbersSet;
        this.numbersList = numbersList;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text = reader.readLine();
            if (text != null && !text.equals("bye")) {
                writer.println("Text: " + text);
                Long number = Long.parseLong(text);
                if (numbersSet.add(number)) {
                    numbersList.add(number);
                } else {
                    callback.incrementDuplicates();
                }
            }
//            writer.println("Set: " + numbers.toString());
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}