package com.soundcloud.user.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.inject.Inject;

@Configuration
@PropertySource(value = "application.yml")
@ConfigurationProperties(prefix = "buffer")
@ManagedResource(objectName = "user-service:name=eventsConfiguration")
@EnableMBeanExport
public class EventsConfiguration {

    @Value("${capacity}")
    private int capacity;

    @ManagedAttribute
    public int getCapacity() {
        return capacity;
    }

    @ManagedOperation
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
