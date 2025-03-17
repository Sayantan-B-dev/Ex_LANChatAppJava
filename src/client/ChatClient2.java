import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatClient2 {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 12345;
    private String username;
    private PrintWriter out;
    private static final String LOG_FILE_NAME = ChatClient2.class.getSimpleName() + "_ChatLog.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient2::new);
    }

    public ChatClient2() {
        // Prompt for username
        username = JOptionPane.showInputDialog(null, "Enter your username:", "Welcome!", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) username = "Anonymous";

        // Create main frame
        JFrame frame = new JFrame(username + " Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        // Create chat area
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(Color.WHITE);
        chatArea.setForeground(Color.BLACK);
        chatArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chatPanel.add(new JScrollPane(chatArea));

        // Create input field
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Color.BLACK);
        inputField.setCaretColor(Color.BLACK);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(45, 156, 219));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to frame
        frame.setLayout(new BorderLayout());
        frame.add(chatPanel, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Start connection thread
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    if (!serverMessage.startsWith("[" + username + "]")) {
                        chatArea.append(serverMessage + "\n");
                        logMessage(serverMessage);
                    }
                }
            } catch (IOException e) {
                chatArea.append("Connection error: " + e.getMessage() + "\n");
            }
        }).start();

        // Sending messages
        ActionListener sendListener = e -> {
            String message = inputField.getText();
            if (!message.trim().isEmpty() && out != null) {
                String formattedMessage = "[" + username + "]: " + message;
                chatArea.append(formattedMessage + "\n"); // Append locally
                out.println(formattedMessage); // Send to server
                logMessage(formattedMessage); // Log the message
                inputField.setText("");
            }
        System.out.println(e);
        };
        sendButton.addActionListener(sendListener);
        inputField.addActionListener(sendListener);
    }

    private void logMessage(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_NAME, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to chat log: " + e.getMessage());
        }
    }
}
