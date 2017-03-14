package com.soundcloud.user.event.sender;

import com.soundcloud.user.UserSocketApp;
import com.soundcloud.user.client.ClientStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.*;
import static com.soundcloud.user.event.helper.TestHelper.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserSocketApp.class})
public class BroadcastEventSenderUnitTest {

    @Inject
    private ClientStore clientStore;

    @Inject
    private BroadcastEventSender broadcastEventSender;

    @Test
    public void sendEvent_eventShouldBeDeliveredToAllClients() throws Exception {
        SocketChannel mockSocket = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(1);

        getRegisterAndGetClient(mockSocket, 1L, clientStore);
        getRegisterAndGetClient(mockSocket, 2L, clientStore);;
        getRegisterAndGetClient(mockSocket, 3L, clientStore);;

        broadcastEventSender.sendEvent(getStatusUpdateEvent("Payload"));
        verify(mockSocket, times(3)).write(any(ByteBuffer.class));
    }

    @Test
    public void sendEvent_faultTolerance() throws Exception {
        SocketChannel mockSocket = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenThrow(NullPointerException.class);

        getRegisterAndGetClient(mockSocket, 1L, clientStore);

        broadcastEventSender.sendEvent(getStatusUpdateEvent("Payload"));
    }

}