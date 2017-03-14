package com.soundcloud.user;

import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.follower.FollowersStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserSocketApp.class})
public class FollowAndUnfollowItegrationTest {

    private final int millisToWait = 5000;
    @Value("${address}")
    private String address;

    @Value("${eventPort}")
    private int eventPort;

    @Value("${clientPort}")
    private int clientPort;

    @Inject
    private ClientStore clientStore;

    @Inject
    private FollowersStore followersStore;

    SocketChannel client;
    SocketChannel client2;
    SocketChannel client3;
    SocketChannel client4;
    SocketChannel events;

    @Before
    public void setupSockets() throws IOException {
        InetSocketAddress hostAddress = new InetSocketAddress(address, clientPort);
        client = openSocketAndRegisterClient(hostAddress, "200\r\n");
        client2 = openSocketAndRegisterClient(hostAddress, "300\r\n");
        client3 = openSocketAndRegisterClient(hostAddress, "400\r\n");
        client4 = openSocketAndRegisterClient(hostAddress, "500\r\n");

        InetSocketAddress eventHostAddress = new InetSocketAddress(address, eventPort);
        events = SocketChannel.open(eventHostAddress);
    }


    @Test
    public void clientsRegisteredProperly() throws IOException, InterruptedException {
        Thread.sleep(3000);
        assertThat(clientStore.getClientById(200L).isPresent()).isTrue();
        assertThat(clientStore.getClientById(300L).isPresent()).isTrue();
        assertThat(clientStore.getClientById(400L).isPresent()).isTrue();

        assertThat(clientStore.getClientById(200L).get().getSocketChannel().getLocalAddress()).isEqualTo(client.getRemoteAddress());
        assertThat(clientStore.getClientById(300L).get().getSocketChannel().getLocalAddress()).isEqualTo(client2.getRemoteAddress());
        assertThat(clientStore.getClientById(400L).get().getSocketChannel().getLocalAddress()).isEqualTo(client3.getRemoteAddress());

        assertThat(clientStore.getClientById(200L).get().getSocketChannel().getRemoteAddress()).isEqualTo(client.getLocalAddress());
        assertThat(clientStore.getClientById(300L).get().getSocketChannel().getRemoteAddress()).isEqualTo(client2.getLocalAddress());
        assertThat(clientStore.getClientById(400L).get().getSocketChannel().getRemoteAddress()).isEqualTo(client3.getLocalAddress());
    }

    @Test
    public void followAndUnfollowEventPublishing() throws IOException, InterruptedException {

        //Publish follow event
        writeMessageToSocket("1|F|200|300\r\n", events);

        Thread.sleep(millisToWait);
        assertThat(followersStore.getUserFollowers(300L)).containsOnly(200L);

        //Publish follow event
        writeMessageToSocket("2|F|400|300\r\n", events);

        Thread.sleep(millisToWait);
        assertThat(followersStore.getUserFollowers(300L)).containsOnly(200L, 400L);


        //Publish unfollow event
        writeMessageToSocket("3|U|400|300\r\n", events);
        writeMessageToSocket("3|U|200|300\r\n", events);

        Thread.sleep(millisToWait);
        assertThat(followersStore.getUserFollowers(300L).size()).isEqualTo(0);

    }

    @Test
    public void highLoadMessagePublishing() throws IOException, InterruptedException {
        //publish 9995 follow events
        IntStream.range(5, 10000)
                .forEach(v -> {
                    try {
                        writeMessageToSocket(v + "|F|"+v+"00|500\r\n", events);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


        Thread.sleep(millisToWait);
        assertThat(followersStore.getUserFollowers(500L).size()).isEqualTo(9995);

        //publish 9995 unfollow events
        IntStream.range(5, 10000)
                .forEach(v -> {
                    try {
                        writeMessageToSocket(v + "|U|"+v+"00|500\r\n", events);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        Thread.sleep(millisToWait);
        assertThat(followersStore.getUserFollowers(500L).size()).isEqualTo(0);

    }

    private SocketChannel openSocketAndRegisterClient(InetSocketAddress hostAddress, String clientIdMessage) throws IOException {
        SocketChannel client = SocketChannel.open(hostAddress);

        // Send messages to server
        writeMessageToSocket(clientIdMessage, client);

        return client;
    }

    private void writeMessageToSocket(String clientIdMessage, SocketChannel client) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(clientIdMessage.getBytes());
        client.write(buffer);
        buffer.clear();
    }
}