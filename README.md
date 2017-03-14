**User Service**

User Service is an application for handling and processing Events via socket connections.

Supported events are
* Follow
* Unfollow
* Broadcast
* Private Message
* Status Update
****
**Implementation details**

On app start two sockets are initialized one for clients another for events.

The events arrive out of order but app should deliver them by order. EventsBuffer class is taking care of this by using BlockingQueue with a comparator. Capacity of the queue is configurable and
is exported to JMX so can be changed during runtime to fit our needs.

Key Classes

* SocketServer basically all functionality related to opening and reading sockets
* EventsBuffer dealing with events order and pushing them to event processors
* ClientStore holding all connected clients and removes them when connection is closed
* FollowerStore holding data about which followers (who follows whom)

****
**Technology stack**

* Java 8
* Spring
* Javaslang
* Lombock
* Guava
* Junit, Mockito, Assertj
****
**Build and Run**

To build and run jar file

`./gradlew build`

`java -jar build/libs/user-service-0.1-SNAPSHOT.jar `

To build and run Docker

`./gradlew buildDocker`

`docker run -p 9090:9090 -p 9099:9099 soundcloud/user-service:1.0-SNAPSHOT`

NOTE: You should have Lombock plugin installed to build and run the app with IDE 
****
**Known issues**

While running with docker follower-maze-2.0.jar is failing while waiting for remaining notifications with
`ಠ_ಠ SOMETHING WENT WRONG ಠ_ಠ`
****
**Configuration**

1. buffer.capacity - default `1000`. Represents capacity of the buffer. When size of buffer gets to the limit half of the buffer is being flushed (events are being send)
2. buffer.scheduleFixedRate - default `2000` (2sec). Used for flushing events buffer if there are no incoming events.
3. socket.address - default localhost. Socket address
4. socket.eventPort - default `9090`. Socket address
5. socket.clientPort - default `9099`. Socket address


The app uses `application.yml` in classpath by default

If you need custom configuration use

`-Dspring.config.location=/path`

For changing log lvl you can use

`-Dlogging.level.org.springframework=DEBUG`
**Finally**

Hope you have enjoyed reviewing my code as much as I writing it :)
