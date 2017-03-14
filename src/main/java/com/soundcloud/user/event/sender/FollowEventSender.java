package com.soundcloud.user.event.sender;

import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.UserEvent;
import com.soundcloud.user.event.follower.FollowersStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Slf4j
@Component
public class FollowEventSender implements EventSender {

    private final ClientStore clientStore;

    private final FollowersStore followersStore;

    @Inject
    public FollowEventSender(ClientStore clientStore, FollowersStore followersStore) {
        this.clientStore = clientStore;
        this.followersStore = followersStore;
    }

    @Override
    public void sendEvent(@Nonnull UserEvent event) {
        log.debug("Going to send event: {}", event);
        followersStore.registerFollower(event.getToUserId(), event.getFromUserId());

        log.debug("Registered FOLLOW event from user id: {} to user id: {}", event.getFromUserId(), event.getToUserId());
        clientStore.getClientById(event.getToUserId())
                .ifPresent(client -> writeToSocket(client.getSocketChannel(),event.getPayload())
                        .onFailure(throwable -> log.error("Error while sending FOLLOW event to client",throwable))
                        .getOption());
    }
}
