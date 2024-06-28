package ru.cherevichenko.server;

import ru.cherevichenko.bd.DataAcesObject;
import ru.cherevichenko.bd.ServerClientBD;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private ServerSocket server;
    private ServerWindow serverWindow;
    private DataAcesObject dataAcesObject;
    private Map<String, ClientHandler> clientHandlers;

    public ChatServer() throws IOException {
        try {
            serverWindow = new ServerWindow();
            dataAcesObject = new ServerClientBD();
            server = new ServerSocket(8888);
            clientHandlers = new HashMap<>();

            serverWindow.appendMessage("Ожидание подключения клиентов...");
            while (true) {
                Socket clientSocket = server.accept();
                serverWindow.appendMessage("Кто-то подключился.");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private BufferedWriter out;
        private String clientUserName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                sendMessageToClient("Привет. Ты подключился к серверу. " +
                        "Для начала работы необходимо пройти авторизацию или зарегистрироваться. " +
                        "Введите логин и пароль:");

                while (true) {
                    String clientUserName = in.readLine();
                    String clientUserPassword = in.readLine();
                    if (dataAcesObject.isClient(clientUserName, clientUserPassword)) {
                        this.clientUserName = clientUserName;
                        synchronized (clientHandlers) {
                            clientHandlers.put(clientUserName, this);
                        }
                        sendMessageToClient("Клиент подтвержден!");
                        sendMessageToClient(clientUserName);
                        serverWindow.appendMessage("Клиент " + clientUserName + " присоединился к чату");
                        break;
                    } else {
                        sendMessageToClient("Клиент не найден.");
                        sendMessageToClient("Зарегистрируйтесь!");
                        sendMessageToClient("Введите логин и пароль в поля");
                        String userName = in.readLine();
                        String userPassword = in.readLine();
                        dataAcesObject.addClient(userName, userPassword);
                        serverWindow.appendMessage("Клиент " + userName + " зарегистрирован");
                        sendMessageToClient("Вы успешно зарегистрированы");
                        this.clientUserName = userName;
                        synchronized (clientHandlers) {
                            clientHandlers.put(userName, this);
                        }
                        break;
                    }
                }

                while (true) {
                    sendMessageToClient("1. Показать список всех клиентов онлайн\n2. Начать чат с определенным клиентом\n3. Отправить сообщение всем онлайн клиентам");
                    String command = in.readLine();
                    if (command != null) {
                        runChoice(command);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    synchronized (clientHandlers) {
                        clientHandlers.remove(clientUserName);
                    }
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void runChoice(String command) throws IOException {
            switch (command) {
                case "1":
                    sendMessageToClient(dataAcesObject.getOnlineClients());
                    serverWindow.appendMessage("Отправлен список онлайн клиентов");
                    break;
                case "2":
                    sendMessageToClient("Введите имя клиента:");
                    String targetClientName = in.readLine();
                    if (clientHandlers.containsKey(targetClientName)) {
                        sendMessageToClient("Соединение с " + targetClientName + " установлено. Вы можете общаться.");
                        clientHandlers.get(targetClientName).sendMessageToClient(clientUserName + " хочет с вами пообщаться.");
                        serverWindow.appendMessage("Начался чат между " + clientUserName + " и " + targetClientName);
                        startChatWithClient(targetClientName);
                    } else {
                        sendMessageToClient("Клиент с именем " + targetClientName + " не найден.");
                    }
                    break;
                case "3":
                    sendMessageToClient("Введите сообщение для рассылки:");
                    String broadcastMessage = in.readLine();
                    broadcastMessageToAll(clientUserName + ": " + broadcastMessage);
                    serverWindow.appendMessage("Клиент " + clientUserName + " сделал рассылку всем");
                    break;
                default:
                    sendMessageToClient("Неверная команда. Попробуйте снова.");
                    break;
            }
        }

        private void startChatWithClient(String targetClientName) throws IOException {
            String message;
            while ((message = in.readLine()) != null) {
                ClientHandler targetClientHandler = clientHandlers.get(targetClientName);
                if (targetClientHandler != null) {
                    targetClientHandler.sendMessageToClient(clientUserName + ": " + message);
                }
            }
        }

        private void broadcastMessageToAll(String message) throws IOException {
            synchronized (clientHandlers) {
                for (ClientHandler handler : clientHandlers.values()) {
                    handler.sendMessageToClient(message);
                }
            }
        }

        private void sendMessageToClient(String msg) throws IOException {
            out.write(msg + "\n");
            out.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer();
    }
}
