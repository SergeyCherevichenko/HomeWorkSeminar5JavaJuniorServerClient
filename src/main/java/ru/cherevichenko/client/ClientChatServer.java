package ru.cherevichenko.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class ClientChatServer implements ClientInterface {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private ClientGUI clientGUI;

    public ClientChatServer() throws IOException {
        clientGUI = new ClientGUI(this);
        clientSocket = new Socket("localhost", 8888);

        // Инициализация потоков
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        clientGUI.getIp().setText("localhost");
        clientGUI.getPort().setText("8888");

        // Получаем приветствие с сервера
        new Thread(() -> {
            try {
                String msgFromServer;
                while ((msgFromServer = in.readLine()) != null) {
                    clientGUI.displayMessage(msgFromServer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Добавление слушателей действий
        setupActionListeners();
    }

    private void setupActionListeners() {
        clientGUI.getBtnLogin().addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = clientGUI.getUserName().getText();
                String userPassword = clientGUI.getUserPassword().getText();
                if (userName.isEmpty() || userPassword.isEmpty()) {
                    clientGUI.displayMessage("Поле пароль или имя пустые");
                } else {
                    try {
                        sendMessageToServer(userName);
                        sendMessageToServer(userPassword);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        clientGUI.getBtnSend().addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessageToServer(clientGUI.getMessage().getText());
                    clientGUI.getMessage().setText(""); // Очистить поле после отправки
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void sendMessageToServer(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        new ClientChatServer();
    }
}
