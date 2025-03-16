
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    private static final List<PrintWriter> clientWriters = new ArrayList<>();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            synchronized (clientWriters) {
                clientWriters.add(out);
            }

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Broadcasting: " + message);
                synchronized (clientWriters) {
                    for (PrintWriter writer : clientWriters) {
                        writer.println(message);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error with client connection: " + e.getMessage());
        } finally {
            synchronized (clientWriters) {
                clientWriters.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
