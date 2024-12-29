package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

public class DetectedObjectsEvent implements Event<Boolean> {
    private StampedDetectedObjects s;


    public DetectedObjectsEvent(List<DetectedObject> detectedObjects, int time){
        s=new StampedDetectedObjects(time, detectedObjects);
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return s;
    }
}

