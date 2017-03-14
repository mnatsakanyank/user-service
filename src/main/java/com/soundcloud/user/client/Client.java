package com.soundcloud.user.client;

import java.nio.channels.SocketChannel;

public class Client {

    private Long clientId;
    private SocketChannel socketChannel;

    public Client(Long clientId, SocketChannel socketChannel) {
        this.clientId = clientId;
        this.socketChannel = socketChannel;
    }

    public Long getClientId() {
        return clientId;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
