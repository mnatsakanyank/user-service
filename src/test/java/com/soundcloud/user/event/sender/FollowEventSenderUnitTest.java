package com.soundcloud.user.event.sender;

import org.junit.Test;

import com.soundcloud.user.event.follower.FollowersStore;

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
import static org.assertj.core.api.Assertions.assertThat;
import static com.soundcloud.user.event.helper.TestHelper.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserSocketApp.class})
public class FollowEventSenderUnitTest {

    @Inject
    private ClientStore clientStore;

    @Inject
    private FollowersStore followersStore;

    @Inject
    private FollowEventSender followEventSender;

    @Test
    public void sendEvent() throws Exception {
        SocketChannel mockSocket = mock(SocketChannel.class);
        when(mockSocket.write(any(ByteBuffer.class))).thenReturn(1);

        Client cl1 = getRegisterAndGetClient(mockSocket, 100L, clientStore);
        Client cl2 = getRegisterAndGetClient(mockSocket, 102L, clientStore);
        Client cl3 = getRegisterAndGetClient(mockSocket, 103L, clientStore);

        UserEvent followerEvent = getFollowUserEvent("Payload", cl1.getClientId(), cl2.getClientId());

        followEventSender.sendEvent(followerEvent);

        verify(mockSocket, times(1)).write(any(ByteBuffer.class));
        assertThat(followersStore.getUserFollowers(cl2.getClientId()).size()).isEqualTo(1);
        assertThat(followersStore.getUserFollowers(cl2.getClientId())).contains(cl1.getClientId());

        UserEvent followerEvent2 = getFollowUserEvent("Payload2", cl3.getClientId(), cl2.getClientId());

        followEventSender.sendEvent(followerEvent2);

        verify(mockSocket, times(2)).write(any(ByteBuffer.class));
        assertThat(followersStore.getUserFollowers(cl2.getClientId()).size()).isEqualTo(2);
        assertThat(followersStore.getUserFollowers(cl2.getClientId())).contains(cl3.getClientId());

        //Duplicate events should not have impact
        followEventSender.sendEvent(followerEvent);
        assertThat(followersStore.getUserFollowers(cl2.getClientId()).size()).isEqualTo(2);
    }


}