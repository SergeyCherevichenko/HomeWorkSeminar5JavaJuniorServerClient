package ru.cherevichenko.bd;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import ru.cherevichenko.entity.Client;

import java.util.ArrayList;
import java.util.List;

public class ServerClientBD implements DataAcesObject {


    private List<Client> onlineClients;
    private boolean onlineClient;
    Configuration configuration;

    public ServerClientBD() {

        onlineClient = false;
        onlineClients = new ArrayList<>();
        configuration = new Configuration();
        configuration.configure();
        createTableClient();

    }

    private void createTableClient() {
        try {
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("CREATE TABLE if not exists Client (" +
                    "id BIGINT PRIMARY KEY, " +
                    "name VARCHAR(256)," +
                    "password varchar(256))").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            System.out.println("Не удалось создать таблицу client." + e.getMessage());
        }

    }


    @Override
    public void addClient(String name, String password) {

        try {
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("INSERT INTO Client (name, password) VALUES (:name, :password)")
                    .setParameter("name", name)
                    .setParameter("password", password)
                    .executeUpdate();

            onlineClient = true;
            Client client = new Client(name, password);
            onlineClients.add(client);
            tx.commit();


        } catch (Exception e) {
            System.out.println("не удалось добавить клиента." + e.getMessage());
        }

    }


    @Override
    public boolean isClient(String clientName, String clientPassword) {
        try {
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Query<Client> query = session.createQuery("FROM Client WHERE name = :name AND password = :password", Client.class);
            query.setParameter("name", clientName);
            query.setParameter("password", clientPassword);

// Получаем результат запроса
            Client foundClient = query.uniqueResult();
            if (foundClient != null) {
                onlineClients.add(foundClient);
                return true;
            }


        } catch (Exception e) {
            System.out.println("при поиске клиента в базе произошла ошибка." + e.getMessage());
        }
        return false;
    }

    @Override
    public String getOnlineClients() {
        StringBuilder str = new StringBuilder();
        for (Client client : onlineClients) {
            str.append(client.getName()).append(" \n");
        }
        return str.toString();
    }
}








