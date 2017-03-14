package com.soundcloud.user.event;

import javaslang.control.Try;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.StringTokenizer;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Slf4j
public class EventParser {

    public static Optional<Long> parseNewClientEvent(String event) {
        return getLong(event);
    }

    public static Optional<UserEvent> parseUserEvent(String event) {
        UserEvent userEvent = new UserEvent();
        userEvent.setPayload(event);
        String tmp = event.replaceAll("(\\r|\\n)", "");
        StringTokenizer tok = new StringTokenizer(tmp, "|");

        if (tok.hasMoreTokens()) {
            getLong(tok.nextToken()).ifPresent(userEvent::setId);
        }
        if (tok.hasMoreTokens()) {
            getEventType(tok).ifPresent(userEvent::setEventType);
        }
        if (tok.hasMoreTokens()) {
            getLong(tok.nextToken()).ifPresent(userEvent::setFromUserId);
        }
        if (tok.hasMoreTokens()) {
            getLong(tok.nextToken()).ifPresent(userEvent::setToUserId);
        }
        return userEvent.isValid() ? of(userEvent) : empty();
    }

    private static Optional<EventType>  getEventType(StringTokenizer tok) {
        return Try.of(() -> EventType.valueOf(tok.nextToken()))
                .onFailure(throwable -> log.error("Error while parsing EventType ", throwable))
                .getOption()
                .toJavaOptional();
    }

    private static Optional<Long> getLong(String event) {
        return Try.of(() -> Long.valueOf(event.replaceAll("\\D+", "")))
                .onFailure(throwable -> log.error("Error while parsing event to long", throwable))
                .getOption()
                .toJavaOptional();
    }
}
