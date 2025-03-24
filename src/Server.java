import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String username = in.readLine(); // First message is the username
                if (username == null || username.trim().isEmpty()) {
                    username = "Anonymous" + (clients.size() + 1); // Fallback if invalid
                }
                ClientHandler clientHandler = new ClientHandler(this, socket, username.trim());
                synchronized (clients) {
                    clients.add(clientHandler);
                }
                clientHandler.start();
                clientHandler.sendMessage("You are " + username);
                broadcast(username + " has joined");
            }
        }
    }

    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 5000;
        Server server = new Server(port);
        server.start();
    }
}