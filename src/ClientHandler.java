import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Server server;
    private Socket socket;
    private String username;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Server server, Socket socket, String username) {
        this.server = server;
        this.socket = socket;
        this.username = username;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String message;
            while ((message = in.readLine()) != null) {
                server.broadcast(username + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("Error handling client " + username + ": " + e.getMessage());
        } finally {
            server.removeClient(this);
            server.broadcast(username + " has left");
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}