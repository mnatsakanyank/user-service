package com.soundcloud.user.event.sender;

import org.junit.Test;

import com.soundcloud.user.UserSocketApp;
import com.soundcloud.user.client.Client;
import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.UserEvent;
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
public class PrivateMsgEventSenderUnitTest {

    @Inject
    private ClientStore clientStore;

    @Inject
    private PrivateMsgEventSender privateMsgEventSender;

    @Test
    public void sendEvent() throws Exception {
        SocketChannel mockSocket = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(1);
        Client cl1 = getRegisterAndGetClient(mockSocket, 1L, clientStore);

        SocketChannel mockSocket2 = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(2);
        Client cl2 = getRegisterAndGetClient(mockSocket2, 2L, clientStore);

        UserEvent followerEvent = getPrivateMessageEvent("Payload", cl1.getClientId(), cl2.getClientId());

        privateMsgEventSender.sendEvent(followerEvent);

        verify(mockSocket, times(0)).write(any(ByteBuffer.class));
        verify(mockSocket2, times(1)).write(any(ByteBuffer.class));


    }
}