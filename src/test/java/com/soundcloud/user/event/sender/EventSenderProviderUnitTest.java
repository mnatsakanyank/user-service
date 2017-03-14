package com.soundcloud.user.event.sender;

import com.soundcloud.user.UserSocketApp;
import com.soundcloud.user.event.EventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserSocketApp.class})
public class EventSenderProviderUnitTest {

    @Inject
    private EventSenderProvider eventSenderProvider;

    @Test
    public void getEventSender() throws Exception {
        EventSender b = eventSenderProvider.getEventSender(EventType.B);
        assertThat(b).isInstanceOf(BroadcastEventSender.class);

        EventSender f = eventSenderProvider.getEventSender(EventType.F);
        assertThat(f).isInstanceOf(FollowEventSender.class);

        EventSender p = eventSenderProvider.getEventSender(EventType.P);
        assertThat(p).isInstanceOf(PrivateMsgEventSender.class);

        EventSender s = eventSenderProvider.getEventSender(EventType.S);
        assertThat(s).isInstanceOf(StatusUpdateEventSender.class);

        EventSender u = eventSenderProvider.getEventSender(EventType.U);
        assertThat(u).isInstanceOf(UnfollowEventSender.class);
    }

}