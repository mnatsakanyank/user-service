package com.soundcloud.user.event.processor;

import com.soundcloud.user.event.Event;
import com.soundcloud.user.event.UserEvent;
import com.soundcloud.user.event.sender.EventSenderProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.Optional;

import static com.google.common.base.Predicates.instanceOf;


@Component
@Slf4j
public class UserEventProcessor implements EventProcessor {

    private final EventSenderProvider eventSenderProvider;

    @Inject
    public UserEventProcessor(@Nonnull EventSenderProvider eventSenderProvider) {
        this.eventSenderProvider = eventSenderProvider;
    }

    @Override
    public void processEvent(@Nonnull Event event) {
        if(instanceOf(UserEvent.class).apply(event)) {
            UserEvent userEvent = (UserEvent) event;
            eventSenderProvider.getEventSender(userEvent.getEventType()).sendEvent(userEvent);
        } else {
            log.warn("Unknown event was received: {}", event);
        }
    }
}
