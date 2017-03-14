package com.soundcloud.user.event.helper;

import com.soundcloud.user.client.Client;
import com.soundcloud.user.client.ClientStore;
import com.soundcloud.user.event.EventType;
import com.soundcloud.user.event.UserEvent;

import java.nio.channels.SocketChannel;

public class TestHelper {

    public static UserEvent getFollowUserEvent(String payload2, Long fromClient, Long toClient) {
        return getUserEvent(payload2, fromClient, toClient, EventType.F);
    }

    public static UserEvent getPrivateMessageEvent(String payload2, Long fromClient, Long toClient) {
        return getUserEvent(payload2, fromClient, toClient, EventType.P);
    }

    public static UserEvent getUnfollowEvent(String payload, Long fromClient, Long toClient) {
        return getUserEvent(payload, fromClient, toClient, EventType.U);
    }

    public static UserEvent getStatusUpdateEvent(String payload, Long clientId) {
        return getUserEvent(payload, clientId, null, EventType.S);
    }

    public static UserEvent getStatusUpdateEvent(String payload) {
        return getUserEvent(payload, null, null, EventType.B);
    }

    private static UserEvent getUserEvent(String payload, Long fromClient, Long toClient, EventType p) {
        UserEvent followerEvent = new UserEvent();
        followerEvent.setPayload(payload);
        followerEvent.setEventType(p);
        followerEvent.setFromUserId(fromClient);
        followerEvent.setToUserId(toClient);
        return followerEvent;
    }

    public static Client getRegisterAndGetClient(SocketChannel mockSocket, Long clientId, ClientStore clientStore) {
        Client cl3 = new Client(clientId, mockSocket);
        clientStore.registerClient(cl3);
        return cl3;
    }
}
