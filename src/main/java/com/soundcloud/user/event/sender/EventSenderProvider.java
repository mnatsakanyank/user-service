package com.soundcloud.user.event.sender;

import com.soundcloud.user.event.EventType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;

@Component
public class EventSenderProvider {

    private HashMap<EventType, EventSender> eventSenders = new HashMap<>();

    private final EventSender broadcastEventSender;
    private final EventSender followEventSender;
    private final EventSender privateMsgEventSender;
    private final EventSender statusUpdateEventSender;
    private final EventSender unfollowEventSender;

    @Inject
    public EventSenderProvider(EventSender unfollowEventSender,
                               EventSender statusUpdateEventSender,
                               EventSender privateMsgEventSender,
                               EventSender followEventSender,
                               EventSender broadcastEventSender) {
        this.unfollowEventSender = unfollowEventSender;
        this.statusUpdateEventSender = statusUpdateEventSender;
        this.privateMsgEventSender = privateMsgEventSender;
        this.followEventSender = followEventSender;
        this.broadcastEventSender = broadcastEventSender;
    }


    @PostConstruct
    public void initEventSenderService() {
        eventSenders.put(EventType.B, broadcastEventSender);
        eventSenders.put(EventType.F, followEventSender);
        eventSenders.put(EventType.P, privateMsgEventSender);
        eventSenders.put(EventType.S, statusUpdateEventSender);
        eventSenders.put(EventType.U, unfollowEventSender);
    }


    public EventSender getEventSender(EventType type) {
        return eventSenders.get(type);
    }
}
