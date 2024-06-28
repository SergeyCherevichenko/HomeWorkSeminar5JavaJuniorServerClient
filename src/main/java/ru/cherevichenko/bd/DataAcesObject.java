package ru.cherevichenko.bd;

public interface DataAcesObject {

    public void addClient(String name, String password);

    public boolean isClient(String clientName, String clientPassword);

    public String getOnlineClients();
}
