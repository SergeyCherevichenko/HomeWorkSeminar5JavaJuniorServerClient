package ru.cherevichenko.client;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private JLabel labelName;
    private JLabel labelPassword;
    private JLabel labelIp;
    private JLabel labelPort;
    private JTextField userName;
    private JTextField userPassword;
    private JTextField ip;
    private JTextField port;
    private JTextArea fieldChat;
    private JTextArea message;
    private JButton btnLogin;
    private JButton btnSend;
    private boolean serverConnected;
    private JButton weather;
    private JButton allOnlineClient;
    private JButton chatWithOneOnlineClient;
    private JButton registry;
    private JPanel topPanel;

    private ClientInterface clientInterface;

    public ClientGUI(ClientInterface clientInterface) {
        this.clientInterface = clientInterface;
        initGUI();
        this.serverConnected = false;
    }

    private void initGUI() {
        setTitle("Client Chat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(300, 200);
        setSize(400, 400);
        setLayout(new BorderLayout());

        add(topPanel(), BorderLayout.NORTH);
        add(chatField(), BorderLayout.CENTER);
        add(messageAndSend(), BorderLayout.SOUTH);

        setVisible(true);

    }

    private JPanel topPanel() {
        topPanel = new JPanel(new GridLayout(6, 2));
        labelName = new JLabel("User name: ");
        userName = new JTextField();
        labelPassword = new JLabel("User password:");
        userPassword = new JTextField();
        labelIp = new JLabel("IP address:");
        ip = new JTextField();
        labelPort = new JLabel("Port:");
        port = new JTextField();
        btnLogin = new JButton("Login");

        topPanel.add(labelName);
        topPanel.add(userName);
        topPanel.add(labelPassword);
        topPanel.add(userPassword);
        topPanel.add(labelIp);
        topPanel.add(ip);
        topPanel.add(labelPort);
        topPanel.add(port);
        topPanel.add(btnLogin);

        return topPanel;
    }

    private JPanel chatField() {
        JPanel panel = new JPanel(new BorderLayout());
        fieldChat = new JTextArea(15, 35);
        fieldChat.setLineWrap(true);
        fieldChat.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(fieldChat);
        scrollPane.setPreferredSize(new Dimension(350, 250)); // Устанавливаем размер окна чата
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel messageAndSend() {
        JPanel panel = new JPanel(new BorderLayout());
        message = new JTextArea(3, 25);
        btnSend = new JButton("Отправить");

        panel.add(new JScrollPane(message), BorderLayout.CENTER);
        panel.add(btnSend, BorderLayout.EAST);

        return panel;
    }

    public void displayMessage(String message) {
        fieldChat.append(message + "\n");
    }

    public JTextArea getMessage() {
        return message;
    }

    public JTextField getUserName() {
        return userName;
    }

    public JTextField getUserPassword() {
        return userPassword;
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JTextField getIp() {
        return ip;
    }

    public JTextField getPort() {
        return port;
    }
}
