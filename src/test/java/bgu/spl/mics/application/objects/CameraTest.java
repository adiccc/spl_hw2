package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.services.CameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {
    StatisticalFolder statisticalFolder;
    Camera camera;
    List<DetectedObjectsEvent> res1;
    List<DetectedObjectsEvent> res2;
    @BeforeEach
    void setUp() {
        statisticalFolder = new StatisticalFolder();
        camera=new Camera(1, 2, STATUS.UP, "example_input\\camera_data.json", statisticalFolder);
        res1= new ArrayList<>();
        List<DetectedObject> detectedObjects1 = new ArrayList<>();
        detectedObjects1.add(new DetectedObject("Wall_1","Wall"));
        res1.add(new DetectedObjectsEvent(detectedObjects1,2));
        res2= new ArrayList<>();
        List<DetectedObject> detectedObjects2 = new ArrayList<>();
        detectedObjects2.add(new DetectedObject("Wall_3","Wall"));
        detectedObjects2.add(new DetectedObject("Chair_Base_1","Chair Base"));
        res2.add(new DetectedObjectsEvent(detectedObjects2,4));
    }

    @Test
    void detect() {
        List<DetectedObjectsEvent> check1= camera.Detect(4);
        List<DetectedObjectsEvent> check2= camera.Detect(7);
        assertTrue(check1.get(0).equals(res1.get(0)));
        assertTrue(check2.get(0).equals(res2.get(0)));
    }
}