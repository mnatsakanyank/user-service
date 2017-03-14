package com.soundcloud.user.socket;


import com.soundcloud.user.event.handler.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;

@Slf4j
public class SocketServer {

    private static final int READ_ERROR = -1;
    private Selector selector;
    private InetSocketAddress listenAddress;
    private EventHandler eventHandler;
    private final ByteBufferInputStream byteBufferStream = new ByteBufferInputStream();

    public SocketServer(String address, int port, EventHandler eventHandler) {
        listenAddress = new InetSocketAddress(address, port);
        this.eventHandler = eventHandler;
    }

    void startServer() throws IOException {
        setupSocketChannel();
        startConsumingChannel();
    }

    private void startConsumingChannel() throws IOException {
        while (true) {
            // wait for events
            this.selector.select();

            //work on selected keys
            Iterator keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    this.acceptChannel(key);
                } else if (key.isReadable()) {
                    this.readChannel(key);
                }
            }
        }
    }

    private void setupSocketChannel() throws IOException {
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        log.info("Socket port: {} address:{} started", listenAddress.getPort(), listenAddress.getAddress());
    }

    private void acceptChannel(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);
        log.info("Accepted connection: {}", channel);
    }

    private void readChannel(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int numRead = channel.read(buffer);

        if (numRead == READ_ERROR) {
            eventHandler.handleCloseConnection(key);
            return;
        }

        readByteBuffer(channel, buffer);
    }

    private void readByteBuffer(SocketChannel channel, ByteBuffer buffer) {
        buffer.flip();
        byteBufferStream.setmBuf(buffer);
        while (byteBufferStream.hasRemaining()) {
            Optional<String> event = byteBufferStream.readLine();
            event.ifPresent(s -> eventHandler.handleEvent(channel, s));
        }
    }
}
