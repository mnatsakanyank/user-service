package com.soundcloud.user.event.sender;

import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Component
@Slf4j
public class PrivateMsgEventSender implements EventSender {

    private final ClientStore clientStore;

    @Inject
    public PrivateMsgEventSender(ClientStore clientStore) {
        this.clientStore = clientStore;
    }

    @Override
    public void sendEvent(@Nonnull UserEvent event) {
        log.debug("Going to send event: {}", event);
        clientStore.getClientById(event.getToUserId())
                .ifPresent(client -> writeToSocket(client.getSocketChannel(),event.getPayload())
                        .onFailure(throwable -> log.error("Error while sending PRIVATE MSG event to client",throwable))
                        .getOption());
    }
}
