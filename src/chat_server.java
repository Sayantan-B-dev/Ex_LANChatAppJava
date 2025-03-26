import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class chat_server {
    ArrayList<Socket> SOCKET_LIST = new ArrayList<>();
    ArrayList<String> USER_CONNECTED = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        chat_server SERVER = new chat_server();
        try {
            SERVER.startThreads(SERVER);
        } catch (Exception e) {
            if (e.getMessage().equals("Address already in use: JVM_Bind")) {
                System.out.println("Another instance running. Shut it down.");
            }
        }
    }

    public void startThreads(chat_server SERVER) throws Exception {
        ServerSocket SERVER_SOCK = new ServerSocket(444);
        System.out.println("Waiting for clients...");
        while (true) {
            Socket SOCK = SERVER_SOCK.accept();
            System.out.println("Connection accepted " + SOCK.getLocalAddress());
            new chat_server_background(SERVER, SOCK).start();
        }
    }
}

class chat_server_background extends Thread {
    private Socket SOCK;
    private chat_server SERVER;
    private String MESG, NAME;
    BufferedReader BR;
    PrintStream PS;

    public chat_server_background(chat_server server, Socket X) {
        this.SOCK = X;
        this.SERVER = server;
    }

    public void run() {
        try {
            BR = new BufferedReader(new InputStreamReader(SOCK.getInputStream()));
            PS = new PrintStream(SOCK.getOutputStream());
            NAME = tellMeYourName(SOCK);
            System.out.println("name given " + NAME);
            notifyServer(NAME, SOCK);
            notifyUsers(NAME, true);
            addNameToList(NAME, SOCK);
            System.out.println("Now total users: " + SERVER.USER_CONNECTED.size());
            while ((MESG = BR.readLine()) != null) {
                System.out.println(NAME + " >> " + MESG);
                sendMesgToAllUser(NAME, MESG);
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) {
                try {
                    notifyUsers(NAME, false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        }
    }

    public String tellMeYourName(Socket X) throws IOException {
        while (true) {
            PS.println("SERVER: Tell me your username");
            NAME = BR.readLine();
            if (NAME != null && !NAME.isEmpty()) {
                if (SERVER.USER_CONNECTED.contains(NAME)) {
                    PS.println("Sorry that username is already taken :(");
                } else {
                    break;
                }
            }
        }
        return NAME;
    }

    public void addNameToList(String name, Socket X) {
        SERVER.USER_CONNECTED.add(NAME);
        SERVER.SOCKET_LIST.add(X);
    }

    public void notifyUsers(String name, boolean connectFlag) throws IOException {
        String mesg = connectFlag ? " just connected to chat :)" : " disconnected :(";
        for (Socket X : SERVER.SOCKET_LIST) {
            new PrintStream(X.getOutputStream()).println("SERVER:" + name + mesg);
        }
    }

    public void notifyServer(String name, Socket X) {
        System.out.println("#" + name + " Connected | host: " + X.getLocalAddress().getHostName());
    }

    public void sendMesgToAllUser(String name, String mesg) throws IOException {
        for (Socket X : SERVER.SOCKET_LIST) {
            new PrintStream(X.getOutputStream()).println(">>" + name + ": " + mesg);
        }
    }
}