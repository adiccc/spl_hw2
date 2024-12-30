package bgu.spl.mics;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageBusImplTest {
    MessageBus messageBus;
    StatisticalFolder statisticalFolder;
    CameraService cameraService;
    LiDarService liDarService;
    TimeService timeService;
    PoseService poseService;
    FusionSlamService fusionSlamService;
    GPSIMU gpsimu;
    Future<Boolean> detectedObjectsFuture = null;
    DetectedObjectsEvent detectedObjectsEvent = null;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        statisticalFolder = new StatisticalFolder();
        cameraService = new CameraService(new Camera(1, 2, STATUS.UP, "example_input\\camera_data.json", statisticalFolder));
        liDarService = new LiDarService(new LiDarWorkerTracker(1, 2, STATUS.UP, "example_input\\lidar_data.json", statisticalFolder), 1);
        timeService = new TimeService(500, 10, statisticalFolder);
        gpsimu = new GPSIMU("example_input\\pose_data.json");
        poseService = new PoseService(gpsimu);
        fusionSlamService=new FusionSlamService(FusionSlam.getInstance(statisticalFolder,"example_input"));
        MessageBusImpl.latch=new CountDownLatch(5);
        cameraService.initialize();
        liDarService.initialize();
        timeService.initialize();
        poseService.initialize();
        fusionSlamService.initialize();
        LinkedList<DetectedObject> l = new LinkedList<>();
        l.add(new DetectedObject("1", "apple"));
        l.add(new DetectedObject("2", "banana"));
        detectedObjectsEvent = new DetectedObjectsEvent(l, 1);
        detectedObjectsFuture = messageBus.sendEvent(detectedObjectsEvent);
    }

    @Test
    @Order(1)
    void subscribeEvent() {
        boolean isSubscribed = messageBus.isRegisterToEvent(liDarService, DetectedObjectsEvent.class);
        assertTrue(isSubscribed, "liDarService isn't subscribed to DetectedObjectsEvent.");
    }

    @Test
    @Order(2)
    void subscribeBroadcast() {
        boolean isSubscribed = messageBus.isRegisterToBrodcast(cameraService, TickBroadcast.class);
        assertTrue(isSubscribed, "CameraService isn't subscribed to TickBroadcast.");
        boolean isSubscribed2 = messageBus.isRegisterToBrodcast(liDarService, TickBroadcast.class);
        assertTrue(isSubscribed2, "liDarService isn't subscribed to TickBroadcast.");
    }

    @Test
    @Order(3)
    void sendBroadcast() {
        messageBus.sendBroadcast(new TickBroadcast(1));
        try {
            Message m = messageBus.awaitMessage(cameraService);
            assertNotNull(m, "cameraService didn't receive the TickBroadcast");
        } catch (InterruptedException e) {
            fail("Exception occurred while awaiting message for cameraService");
        }
        try {
            Message m2 = messageBus.awaitMessage(liDarService);
            assertNotNull(m2, "liDarService didn't receive the TickBroadcast");
        } catch (InterruptedException e) {
            fail("Exception occurred while awaiting message for liDarService");
        }
    }

    @Test
    @Order(5)
    void sendEvent() {
        try {
            detectedObjectsFuture = messageBus.sendEvent(detectedObjectsEvent);
            Message m = messageBus.awaitMessage(liDarService);
            assertNotNull(m, "liDarService didn't receive the detected objects event");
        } catch (InterruptedException e) {
            fail("Exception occurred while awaiting message for liDarService");
        }
    }

    @Test
    @Order(6)
    void complete() {
        messageBus.complete(detectedObjectsEvent,true);
        assertTrue(detectedObjectsFuture.get(), "future of detetctedObjectEvent didnt resolved");
    }
    @Test
    void register() {
        assertTrue(messageBus.isMicroServiceRegistered(cameraService), "cameraService didn't register to MessageBus");
        assertTrue(messageBus.isMicroServiceRegistered(liDarService), "liDarService didn't register to MessageBus");
        assertTrue(messageBus.isMicroServiceRegistered(fusionSlamService), "fusionSlamService didn't register to MessageBus");
        assertTrue(messageBus.isMicroServiceRegistered(poseService), "poseService didn't register to MessageBus");
        assertTrue(messageBus.isMicroServiceRegistered(timeService), "timeService didn't register to MessageBus");
    }

    @Test
    void unregister() {
        messageBus.unregister(cameraService);
        messageBus.unregister(liDarService);
        messageBus.unregister(fusionSlamService);
        messageBus.unregister(poseService);
        messageBus.unregister(timeService);
        assertTrue(!messageBus.isMicroServiceRegistered(cameraService), "cameraService didn't unregister to MessageBus");
        assertTrue(!messageBus.isMicroServiceRegistered(liDarService), "liDarService didn't unregister to MessageBus");
        assertTrue(!messageBus.isMicroServiceRegistered(fusionSlamService), "fusionSlamService didn't unregister to MessageBus");
        assertTrue(!messageBus.isMicroServiceRegistered(poseService), "poseService didn't unregister to MessageBus");
        assertTrue(!messageBus.isMicroServiceRegistered(timeService), "timeService didn't unregister to MessageBus");
    }
     @Test
     @Order(5)
    void awaitMessage() {
        try {
            Message m = messageBus.awaitMessage(liDarService);
            assertNotNull(m, "liDarService didn't receive the detected objects event");
        } catch (InterruptedException e) {
            fail("Exception occurred while awaiting message for liDarService");
        }
		}
}