package com.soundcloud.user.socket;

import javaslang.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.Executors;

@Slf4j
public class SocketStarter {
    private SocketServer socketServer;

    public SocketStarter(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    public void start() {
        Executors.newSingleThreadExecutor().submit(() ->
                Try.run(() -> socketServer.startServer())
                        .onFailure(throwable-> log.error("Cant start socket {}",throwable))
                        .get());
    }
}
