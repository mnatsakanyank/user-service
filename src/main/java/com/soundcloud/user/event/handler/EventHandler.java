package com.soundcloud.user.event.handler;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface EventHandler {

    void handleEvent(@Nonnull SocketChannel channel,@Nonnull String event);

    default void handleCloseConnection(@Nonnull SelectionKey selectionKey) throws IOException {
        selectionKey.channel().close();
        selectionKey.cancel();
    }
}
