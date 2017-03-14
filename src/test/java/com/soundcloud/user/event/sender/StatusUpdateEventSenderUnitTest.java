package com.soundcloud.user.event.sender;

import com.soundcloud.user.UserSocketApp;
import com.soundcloud.user.client.Client;
import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.UserEvent;
import com.soundcloud.user.event.follower.FollowersStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.soundcloud.user.event.helper.TestHelper.getRegisterAndGetClient;
import static com.soundcloud.user.event.helper.TestHelper.getStatusUpdateEvent;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserSocketApp.class})
public class StatusUpdateEventSenderUnitTest {

    @Inject
    private ClientStore clientStore;

    @Inject
    private FollowersStore followersStore;

    @Inject
    private StatusUpdateEventSender statusUpdateEventSender;

    @Test
    public void sendEvent() throws Exception {
        SocketChannel mockSocket = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(1);

        Client cl1 = getRegisterAndGetClient(mockSocket, 1L, clientStore);

        SocketChannel mockSocketFollowers = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(1);

        Client cl2 = getRegisterAndGetClient(mockSocketFollowers, 2L, clientStore);
        Client cl3 = getRegisterAndGetClient(mockSocketFollowers, 3L, clientStore);
        Client cl4 = getRegisterAndGetClient(mockSocketFollowers, 4L, clientStore);

        followersStore.registerFollower(cl1.getClientId(),cl2.getClientId());
        followersStore.registerFollower(cl1.getClientId(),cl3.getClientId());
        followersStore.registerFollower(cl1.getClientId(),cl4.getClientId());

        UserEvent statusUpdateEvent = getStatusUpdateEvent("Payload",cl1.getClientId());

        statusUpdateEventSender.sendEvent(statusUpdateEvent);

        verify(mockSocketFollowers, times(3)).write(any(ByteBuffer.class));

        followersStore.unfollow(cl1.getClientId(),cl4.getClientId());
        statusUpdateEventSender.sendEvent(statusUpdateEvent);

        verify(mockSocketFollowers, times(5)).write(any(ByteBuffer.class));

        clientStore.deleteClientByChannel(cl3.getSocketChannel());

        statusUpdateEventSender.sendEvent(statusUpdateEvent);

        verify(mockSocketFollowers, times(6)).write(any(ByteBuffer.class));
    }

}