import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class chat_client extends JFrame {
    JTextArea ta = new JTextArea();
    JTextField tf = new JTextField();
    JButton sb = new JButton("Send");
    Socket s;
    PrintWriter o;
    BufferedReader i;
    String ip = "169.254.122.214";
    int p = 444;

    public chat_client() {
        setTitle("Chat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        add(new JScrollPane(ta), BorderLayout.CENTER);
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(tf, BorderLayout.CENTER);
        pnl.add(sb, BorderLayout.EAST);
        add(pnl, BorderLayout.SOUTH);

        sb.addActionListener(e -> send());
        tf.addActionListener(e -> send());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                try { if (s != null) s.close(); } catch (Exception ex) {}
            }
        });

        ip = JOptionPane.showInputDialog(this, "IP:", ip);
        try { p = Integer.parseInt(JOptionPane.showInputDialog(this, "Port:", String.valueOf(p))); } catch (Exception e) {}
        connect();
        setVisible(true);
    }

    void connect() {
        update("Connecting...");
        try {
            s = new Socket(ip, p);
            o = new PrintWriter(s.getOutputStream(), true);
            i = new BufferedReader(new InputStreamReader(s.getInputStream()));
            update("Connected!");
            new Thread(() -> {
                try { String m; while ((m = i.readLine()) != null) update("Server: " + m); }
                catch (Exception e) { update("Disconnected."); }
            }).start();
            tf.setEnabled(true);
            sb.setEnabled(true);
        } catch (Exception e) { update("Connect fail."); }
    }

    void send() {
        String m = tf.getText().trim();
        if (!m.isEmpty() && o != null && s != null && !s.isClosed()) {
            o.println(m);
            tf.setText("");
        }
    }

    void update(String m) {
        SwingUtilities.invokeLater(() -> {
            ta.append(m + "\n");
            ta.setCaretPosition(ta.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(chat_client::new);
    }
}