package com.soundcloud.user.event;

public enum EventType {
    F("Follow"),
    U("Unfollow"),
    B("Broadcast"),
    P("Private Msg"),
    S("Status Update");

    private final String name;

    EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
