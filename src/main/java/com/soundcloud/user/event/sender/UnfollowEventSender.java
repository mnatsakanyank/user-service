package com.soundcloud.user.event.sender;

import com.soundcloud.user.event.UserEvent;
import com.soundcloud.user.event.follower.FollowersStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Component
@Slf4j
public class UnfollowEventSender implements EventSender {

    private final FollowersStore followersStore;

    @Inject
    public UnfollowEventSender(FollowersStore followersStore) {
        this.followersStore = followersStore;
    }

    @Override
    public void sendEvent(@Nonnull UserEvent event) {
        log.debug("Sending UNFOLLOW event from user id: {} to user id: {}", event.getFromUserId(), event.getToUserId());
        followersStore.unfollow(event.getToUserId(), event.getFromUserId());
    }
}
