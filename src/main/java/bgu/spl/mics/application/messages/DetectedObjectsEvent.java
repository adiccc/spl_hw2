package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

public class DetectedObjectsEvent implements Event<Boolean> {
    private StampedDetectedObjects StampedDetectedObjects;
    private boolean detectedError;
    private String errorMessage;


    public DetectedObjectsEvent(List<DetectedObject> detectedObjects, int time){
        StampedDetectedObjects=new StampedDetectedObjects(time, detectedObjects);
        detectedError=false;
        errorMessage="";
    }
    public void setDetectedError(String description){
        errorMessage=description;
        this.detectedError=true;
    }
    public String getDetectedError(){
        return errorMessage;
    }

    public boolean isDetectedError(){
        return detectedError;
    }

    public boolean equals(DetectedObjectsEvent d) {
        boolean b=this.StampedDetectedObjects.equals(d.StampedDetectedObjects) && this.detectedError==d.detectedError && this.errorMessage.equals(d.errorMessage);
        return this.StampedDetectedObjects.equals(d.StampedDetectedObjects) && this.detectedError==d.detectedError && this.errorMessage.equals(d.errorMessage);
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return StampedDetectedObjects;
    }
}

