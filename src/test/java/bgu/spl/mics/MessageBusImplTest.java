package bgu.spl.mics;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.services.CameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageBusImplTest {
    MessageBus messageBus;
    @BeforeEach
    void setUp() {
         messageBus = MessageBusImpl.getInstance();
    }

    @Test
    void subscribeEvent() {
    }

    @Test
    void subscribeBroadcast() {
        // Arrange
//        Camera c=new Camera();
//        MicroService microService = new CameraService(c);

        // Act
//        messageBus.register(microService);
//        messageBus.subscribeBroadcast(TickBroadcast.class,microService);

//        assertEquals(messageBus.isRegisterToBrodcast(microService,Class()),ture);
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