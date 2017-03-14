package com.soundcloud.user.event.handler;

import com.soundcloud.user.event.EventsBuffer;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.nio.channels.SocketChannel;

import static com.soundcloud.user.event.EventParser.parseUserEvent;

@Component
public class UserEventHandler implements EventHandler {

    private final EventsBuffer eventsBuffer;

    @Inject
    public UserEventHandler(@Nonnull EventsBuffer eventsBuffer) {
        this.eventsBuffer = eventsBuffer;
    }

    @Override
    public void handleEvent(@Nonnull SocketChannel channel, @Nonnull String event) {
        parseUserEvent(event).ifPresent(eventsBuffer::pushEvent);
    }
}
