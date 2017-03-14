package com.soundcloud.user.event.handler;

import com.soundcloud.user.client.Client;
import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.follower.FollowersStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import static com.soundcloud.user.event.EventParser.parseNewClientEvent;

@Component
@Slf4j
public class ClientEventHandler implements EventHandler {


    private final ClientStore clientStore;
    private final FollowersStore followersStore;

    @Inject
    public ClientEventHandler(@Nonnull ClientStore clientStore, @Nonnull FollowersStore followersStore) {
        this.clientStore = clientStore;
        this.followersStore = followersStore;
    }

    @Override
    public void handleEvent(@Nonnull SocketChannel channel,
                            @Nonnull String event) {
        Optional<Long> maybeClientId = parseNewClientEvent(event);
        maybeClientId.ifPresent(clientId -> clientStore.registerClient(new Client(clientId, channel)));
    }

    @Override
    public void handleCloseConnection(@Nonnull SelectionKey selectionKey) throws IOException {
        log.info("Connection closed: {}", selectionKey.channel());
        clientStore.deleteClientByChannel(selectionKey.channel());

        if (clientStore.getAllClients().size() == 0) {
            followersStore.cleanAll();
        }

        selectionKey.channel().close();
        selectionKey.cancel();
    }
}
