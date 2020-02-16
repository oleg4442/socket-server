import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketServer implements Runnable, Callback {

    private static File file;
    private static Set<Long> numbersSet;
    static private List[] lists = new List[2];
    private static int currentList = 0;
    private static int clientCount = 0;
    private static volatile long duplicates;

    public static void main(String[] args) throws IOException {
        file = new File("numbers.log");
        boolean isNew = file.createNewFile();

        if (!isNew) {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        }
        new Thread(new SocketServer()).start();
    }

    @Override
    public void run() {
        int port = 4000;
        numbersSet = Collections.synchronizedSet(new HashSet<>());
        lists[0] = Collections.synchronizedList(new ArrayList<>());
        lists[1] = Collections.synchronizedList(new ArrayList<>());

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(heartBeatRunnable,10, 10, TimeUnit.SECONDS);

            System.out.println("Server listening port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                clientCount++;

                new ServerThread(this, socket, numbersSet, lists[currentList]).start();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            System.out.println("Heart Beat 10");
            try {
                FileWriter fileWriter = new FileWriter(file, true);
                List<Long> numbersToPrint = new ArrayList<>(lists[currentList]);
                currentList = -currentList + 1;
                for (Long l : numbersToPrint) {
                    if (l < 100000000) {
                        String str = Long.toString(100000000 + l).substring(1);
                        fileWriter.append(str + System.lineSeparator());
                    } else {
                        fileWriter.write(l + System.lineSeparator());
                    }
                }
                System.out.println("Connections closed: " + clientCount);
                fileWriter.close();
                System.out.println("Received " + numbersToPrint.size() + " unique numbers, " + duplicates + " duplicates. Unique total = " + numbersSet.size());
                lists[-currentList + 1].clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void incrementDuplicates() {
        synchronized (this) {
            duplicates++;
        }
    }


}