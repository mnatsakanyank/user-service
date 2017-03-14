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

import static com.soundcloud.user.event.helper.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserSocketApp.class})
public class UnfollowEventSenderUnitTest {

    @Inject
    private ClientStore clientStore;

    @Inject
    private UnfollowEventSender unfollowEventSender;

    @Inject
    private FollowEventSender followEventSender;

    @Inject
    private FollowersStore followersStore;

    @Test
    public void sendEvent() throws Exception {
        SocketChannel mockSocket = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(1);
        Client cl1 = getRegisterAndGetClient(mockSocket, 11L, clientStore);

        SocketChannel mockSocket2 = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(2);
        Client cl2 = getRegisterAndGetClient(mockSocket2, 10L, clientStore);

        UserEvent followerEvent = getFollowUserEvent("followerEvent", cl1.getClientId(), cl2.getClientId());
        followEventSender.sendEvent(followerEvent);
        verify(mockSocket, times(0)).write(any(ByteBuffer.class));
        verify(mockSocket2, times(1)).write(any(ByteBuffer.class));
        assertThat(followersStore.getUserFollowers(cl2.getClientId()).size()).isEqualTo(1);

        UserEvent unfollowerEvent = getUnfollowEvent("unfollowerEvent", cl1.getClientId(), cl2.getClientId());
        unfollowEventSender.sendEvent(unfollowerEvent);

        verify(mockSocket, times(0)).write(any(ByteBuffer.class));
        verify(mockSocket2, times(1)).write(any(ByteBuffer.class));

        assertThat(followersStore.getUserFollowers(cl2.getClientId()).size()).isEqualTo(0);

    }

}