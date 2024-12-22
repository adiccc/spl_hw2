package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void subscribeEvent() {
        // Arrange
        MessageBus messageBus = new MessageBusImpl();
        MicroService microService = new MicroService("TestMicroService") {
            @Override
            protected void initialize() {
            }
        };

        // Act
        messageBus.register(microService);
//        messageBus.subscribeEvent(ExampleEvent.class,microService);
    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void complete() {
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
    }

    @Test
    void awaitMessage() {
    }
}