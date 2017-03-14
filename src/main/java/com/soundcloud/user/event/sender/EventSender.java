package com.soundcloud.user.event.sender;


import com.soundcloud.user.event.UserEvent;
import javaslang.control.Try;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface EventSender {

    void sendEvent(@Nonnull UserEvent event);

    default Try<Integer> writeToSocket(SocketChannel channel, String event) {
        return Try.of(() -> channel.write(ByteBuffer.wrap(event.getBytes())));
    }
}
