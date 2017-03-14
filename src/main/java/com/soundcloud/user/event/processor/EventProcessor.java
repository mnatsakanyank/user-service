package com.soundcloud.user.event.processor;

import com.soundcloud.user.event.Event;

import java.util.Optional;

public interface EventProcessor {

    void processEvent(Event event);
}
