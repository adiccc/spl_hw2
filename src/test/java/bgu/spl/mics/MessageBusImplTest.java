package bgu.spl.mics;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class MessageBusImplTest {
    MessageBus messageBus;
    StatisticalFolder statisticalFolder;
    CameraService cameraService;
    LiDarService liDarService;
    TimeService timeService;
    PoseService poseService;
    GPSIMU gpsimu;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        statisticalFolder = new StatisticalFolder();
        cameraService= new CameraService(new Camera(1, 2, STATUS.UP, "example_input\\camera_data.json", statisticalFolder));
        liDarService=new LiDarService(new LiDarWorkerTracker(1,2,STATUS.UP,"example_input\\lidar_data.json",statisticalFolder),1);
        timeService=new TimeService(500,10,statisticalFolder);
        gpsimu=new GPSIMU("example_input\\pose_data.json");
        poseService=new PoseService(gpsimu);
        messageBus.subscribeBroadcast(TickBroadcast.class,cameraService);
        messageBus.subscribeBroadcast(TickBroadcast.class,liDarService);
        messageBus.subscribeEvent(DetectedObjectsEvent.class,liDarService);
        Thread cameraServiceThread = new Thread(cameraService);
        Thread liDarServiceThread = new Thread(liDarService);
        Thread timeServiceThread = new Thread(timeService);
        Thread poseServiceThread = new Thread(poseService);
//        timeServiceThread.start();
//        poseServiceThread.start();
//        cameraServiceThread.start();
//        liDarServiceThread.start();
    }

    @Test
    void subscribeEvent() {
        boolean isSubscribed = messageBus.isRegisterToEvent(liDarService, DetectedObjectsEvent.class);
        assertTrue(isSubscribed, "liDarService isn't subscribed to DetectedObjectsEvent.");
    }
    @Test
    void subscribeBroadcast() {
        boolean isSubscribed = messageBus.isRegisterToBrodcast(cameraService, TickBroadcast.class);
        assertTrue(isSubscribed, "CameraService isn't subscribed to TickBroadcast.");
        boolean isSubscribed2 = messageBus.isRegisterToBrodcast(liDarService, TickBroadcast.class);
        assertTrue(isSubscribed2, "liDarService isn't subscribed to TickBroadcast.");
    }

    @Test
    void sendBroadcast() {
        messageBus.sendBroadcast(new TickBroadcast(1));
        try {
            Message m=messageBus.awaitMessage(cameraService);
            assertNotNull(m, "cameraService didn't reseved the TickBrodcast");
        } catch (InterruptedException e) {
        }
        try {
            Message m2=messageBus.awaitMessage(liDarService);
            assertNotNull(m2, "liDarService didn't reseved the TickBrodcast");
        } catch (InterruptedException e) {
        }

    }

    @Test
    void sendEvent() {

    }
    @Test
    void complete() {

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