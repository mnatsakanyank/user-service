package com.soundcloud.user.event;

import com.soundcloud.user.config.EventsConfiguration;
import com.soundcloud.user.event.processor.EventProcessor;
import javaslang.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@Component
@EnableScheduling
@Slf4j
public class EventsBuffer {

    private final EventsConfiguration configuration;
    private final BlockingQueue<Event> queue;
    private final AtomicBoolean flushBuffer;
    private final EventProcessor eventProcessor;

    @Inject
    public EventsBuffer(EventProcessor eventProcessor, EventsConfiguration configuration) {
        this.eventProcessor = eventProcessor;
        this.queue = new PriorityBlockingQueue<>(2000, Comparator.comparing(Event::getId));
        this.flushBuffer = new AtomicBoolean(false);
        this.configuration = configuration;
    }

    public void pushEvent(Event event) {
        queue.offer(event);
        flushBuffer.set(false);

        if (queue.size() > configuration.getCapacity()) {
            log.debug("Flushing buffer sending {} events ", configuration.getCapacity() / 2);
            IntStream.range(0, configuration.getCapacity() / 2)
                    .forEach(v ->
                            Try.of(queue::take)
                                    .getOption()
                                    .toJavaOptional()
                                    .ifPresent(eventProcessor::processEvent));
        }
    }

    @Scheduled(fixedRateString = "${scheduleFixedRate}")
    public void flushBuffer() {
        if (flushBuffer.get()) {
            log.debug("Flushing buffer if there are any {}", queue.size());
            while (queue.size() > 0) {
                if (!flushBuffer.get()) {
                    break;
                }
                Try.of(queue::take)
                        .getOption()
                        .toJavaOptional()
                        .ifPresent(eventProcessor::processEvent);
            }
            flushBuffer.set(false);
        } else {
            flushBuffer.set(true);
        }
    }

}
