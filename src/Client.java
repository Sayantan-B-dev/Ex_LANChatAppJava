import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JTextArea chatArea;
    private JTextField messageField;
    private String username;

    public Client(String serverAddress) throws IOException {
        // Prompt for username
        username = JOptionPane.showInputDialog(null, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous";
        }
        username = username.trim();

        // Connect to the server
        socket = new Socket(serverAddress, 5000);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send username to server as the first message
        out.println(username);

        // Setup UI
        setupUI();

        // Start a thread to read messages from server
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    appendMessage(message);
                }
            } catch (IOException e) {
                appendMessage("Disconnected from server");
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void setupUI() {
        // Create the main frame
        JFrame frame = new JFrame("LAN Chat Client - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Chat display area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        // Send message on button click
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage()); // Send on Enter key

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            if (message.equals("/quit")) {
                closeConnection();
                System.exit(0);
            } else {
                out.println(message);
                messageField.setText(""); // Clear input field
            }
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll
        });
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java Client <server_ip>");
            System.exit(1);
        }
        String serverAddress = args[0];
        new Client(serverAddress);
    }
}