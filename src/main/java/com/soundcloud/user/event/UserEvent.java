package com.soundcloud.user.event;

import lombok.Data;

import static javaslang.API.*;
import static javaslang.Predicates.isIn;

@Data
public class UserEvent extends Event {

    private EventType eventType;
    private Long fromUserId;
    private Long toUserId;
    private String payload;

    public boolean isValid() {

        if (null == eventType || getId() == null) {
            return false;
        }

       return Match(eventType).of(
                Case(isIn(EventType.U, EventType.F,EventType.P),e -> fromUserId != null && toUserId != null),
                Case(EventType.B, fromUserId == null && toUserId == null),
                Case(EventType.S, fromUserId != null && toUserId == null),
                Case($() ,false)
        );

    }
}
