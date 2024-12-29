package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

public class DetectedObjectsEvent implements Event<Boolean> {
    private StampedDetectedObjects s;
    private boolean detectedError;
    private String errorMessage;


    public DetectedObjectsEvent(List<DetectedObject> detectedObjects, int time){
        s=new StampedDetectedObjects(time, detectedObjects);
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


    public StampedDetectedObjects getStampedDetectedObjects() {
        return s;
    }
}

