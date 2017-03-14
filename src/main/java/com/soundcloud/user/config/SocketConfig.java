package com.soundcloud.user.config;

import com.soundcloud.user.event.handler.EventHandler;
import com.soundcloud.user.socket.SocketServer;
import com.soundcloud.user.socket.SocketStarter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "socket")
public class SocketConfig {

    @Value("${address}")
    private String address;

    @Value("${eventPort}")
    private int eventPort;

    @Value("${clientPort}")
    private int clientPort;


    private final EventHandler clientEventHandler;
    private final EventHandler userEventHandler;

    @Inject
    public SocketConfig(@Qualifier(value = "userEventHandler")
                                         EventHandler userEventHandler,
                        @Qualifier(value = "clientEventHandler")
                                     EventHandler clientEventHandler) {
        this.userEventHandler = userEventHandler;
        this.clientEventHandler = clientEventHandler;
    }

    @PostConstruct
    public void startSockets() throws IOException {
        getEventSocketStarter().start();
        getUserSocketStarter().start();
    }

    private SocketStarter getEventSocketStarter() {
        return getSocketStarter(address, eventPort, userEventHandler);
    }

    private SocketStarter getUserSocketStarter() {
        return getSocketStarter(address, clientPort, clientEventHandler);
    }

    private SocketStarter getSocketStarter(String address, int port, EventHandler eventHandler) {
        return new SocketStarter(new SocketServer(address, port, eventHandler));
    }


}
