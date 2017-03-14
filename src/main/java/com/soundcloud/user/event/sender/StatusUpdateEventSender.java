package com.soundcloud.user.event.sender;

import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.UserEvent;
import com.soundcloud.user.event.follower.FollowersStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;


@Component
@Slf4j
public class StatusUpdateEventSender implements EventSender {

    private final ClientStore clientStore;

    private final FollowersStore followersStore;

    @Inject
    public StatusUpdateEventSender(ClientStore clientStore, FollowersStore followersStore) {
        this.clientStore = clientStore;
        this.followersStore = followersStore;
    }

    @Override
    public void sendEvent(@Nonnull UserEvent event) {
        log.debug("Going to send event: {}", event);

        followersStore
                .getUserFollowers(event.getFromUserId())
                .stream()
                .map(clientStore::getClientById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .parallel()
                .forEach(client -> writeToSocket(client.getSocketChannel(), event.getPayload())
                        .onFailure(throwable -> log.error("Error while sending STATUS UPDATE event to client",throwable))
                        .getOption());
    }

}
