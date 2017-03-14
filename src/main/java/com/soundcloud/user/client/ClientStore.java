package com.soundcloud.user.client;

import org.springframework.stereotype.Component;

import java.nio.channels.SelectableChannel;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

@Component
public class ClientStore {

    private ConcurrentHashMap<Long, Client> clients = new ConcurrentHashMap<>();

    public void registerClient(Client client) {
        clients.put(client.getClientId(), client);
    }

    public Optional<Client> getClientById(Long clientId) {
        return ofNullable(clients.get(clientId));
    }

    public Collection<Client> getAllClients() {
        return clients.values();
    }

    public void deleteClientByChannel(SelectableChannel selectableChannel) {
        clients.values()
                .stream()
                .filter(client -> client.getSocketChannel().equals(selectableChannel))
                .findFirst()
                .ifPresent(client -> clients.remove(client.getClientId()));
    }
}
