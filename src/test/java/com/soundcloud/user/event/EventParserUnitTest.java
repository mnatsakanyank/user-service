package com.soundcloud.user.event;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EventParserUnitTest {

    @Test
    public void parseNewClientEvent_invalidClientId() throws Exception {
        Optional<Long> invalidClient = EventParser.parseNewClientEvent("invalidClientId");
        assertThat(invalidClient.isPresent()).isFalse();
    }

    @Test
    public void parseNewClientEvent_validClientId() throws Exception {
        Optional<Long> invalidClient = EventParser.parseNewClientEvent("1111");
        assertThat(invalidClient.isPresent()).isTrue();
        assertThat(invalidClient.get()).isEqualTo(1111);
    }

    @Test
    public void parseNewClientEvent_validClientIdWithCRLF() throws Exception {
        Optional<Long> invalidClient = EventParser.parseNewClientEvent("\r\n222\n\r");
        assertThat(invalidClient.isPresent()).isTrue();
        assertThat(invalidClient.get()).isEqualTo(222);
    }

    @Test
    public void parseFollowUserEvent() throws Exception {
        Optional<UserEvent> event = EventParser.parseUserEvent("666|F|60|50\r\n");
        assertThat(event.isPresent()).isTrue();
        assertThat(event.get().getEventType()).isEqualTo(EventType.F);
        assertThat(event.get().getToUserId()).isEqualTo(50L);
        assertThat(event.get().getFromUserId()).isEqualTo(60L);
        assertThat(event.get().getId()).isEqualTo(666L);
    }

    @Test
    public void parseUnfollowUserEvent() throws Exception {
        Optional<UserEvent> event = EventParser.parseUserEvent("1|U|12|9\r\n");
        assertThat(event.isPresent()).isTrue();
        assertThat(event.get().getEventType()).isEqualTo(EventType.U);
        assertThat(event.get().getToUserId()).isEqualTo(9L);
        assertThat(event.get().getFromUserId()).isEqualTo(12L);
        assertThat(event.get().getId()).isEqualTo(1L);
    }

    @Test
    public void parseBroadcastUserEvent() throws Exception {
        Optional<UserEvent> event = EventParser.parseUserEvent("542532|B\r\n");
        assertThat(event.isPresent()).isTrue();
        assertThat(event.get().getEventType()).isEqualTo(EventType.B);
        assertThat(event.get().getToUserId()).isNull();
        assertThat(event.get().getFromUserId()).isNull();
        assertThat(event.get().getId()).isEqualTo(542532L);
    }


    @Test
    public void parseFollowUserEventInvalid() throws Exception {
        Optional<UserEvent> event = EventParser.parseUserEvent("666|F|60|\r\n");
        assertThat(event.isPresent()).isFalse();
    }

    @Test
    public void parseUnfollowUserEventInvalid() throws Exception {
        Optional<UserEvent> event = EventParser.parseUserEvent("666|U|60|\r\n");
        assertThat(event.isPresent()).isFalse();
    }

    @Test
    public void parseUserEventInvalidType() throws Exception {
        Optional<UserEvent> event = EventParser.parseUserEvent("6|60|\r\n");
        assertThat(event.isPresent()).isFalse();
    }
}