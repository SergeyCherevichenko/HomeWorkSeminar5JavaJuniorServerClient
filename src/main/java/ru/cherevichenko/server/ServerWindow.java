package ru.cherevichenko.server;

import javax.swing.*;
import java.awt.*;

public class ServerWindow {
    private JTextArea textField;

    public ServerWindow() {
        JFrame serverWindow = new JFrame();
        serverWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        serverWindow.setLocation(700, 200);
        serverWindow.setSize(400, 400);
        serverWindow.setTitle("Server Chat");
        serverWindow.add(btn(), BorderLayout.NORTH);
        serverWindow.add(text(), BorderLayout.CENTER);
        serverWindow.setVisible(true);
    }

    private JPanel btn() {
        JPanel panel = new JPanel();
        JButton btnStart = new JButton("Start");
        JButton btnStop = new JButton("Stop");
        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }

    private JPanel text() {
        JPanel text = new JPanel();
        textField = new JTextArea(15, 35);
        textField.setLineWrap(true);
        textField.setEditable(false);
        text.add(new JScrollPane(textField));
        return text;
    }

    public void appendMessage(String message) {
        textField.append(message + "\n");
    }
}
