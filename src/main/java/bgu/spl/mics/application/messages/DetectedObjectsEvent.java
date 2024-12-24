package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.List;

public class DetectedObjectsEvent implements Event<Boolean> {
    private Boolean result;
    private List<DetectedObject> detectedObjects;
    private int time;

    public DetectedObjectsEvent(List<DetectedObject> detectedObjects, int time){
        this.result = false;
        this.detectedObjects = detectedObjects;
        this.time = time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
    public int getTime() {
        return time;
    }
    public Boolean getResult() {
        return result;
    }
    public void setResult(Boolean result) {
        this.result = result;
    }
}

