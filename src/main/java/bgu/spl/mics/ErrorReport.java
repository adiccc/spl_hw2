package bgu.spl.mics;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorReport {

    private String error;
    private String fualtySensor;
    private Map<String,List<DetectedObject>> cameraDetection;
    private Map<String,List<TrackedObject>> lidarDetection;
    private PoseEvent[] poses;

    public ErrorReport(String error, String fualtySensor) {
        this.error = error;
        this.fualtySensor = fualtySensor;
        this.cameraDetection = new HashMap<>();
        this.poses = null;
        this.lidarDetection =new HashMap<>();
    }

    public void setError(String error) {
        this.error = error;
    }
    public void setFualtySensor(String fualtySensor) {
        this.fualtySensor = fualtySensor;
    }
    public void addDetectedObject(String name, List<DetectedObject> detectedObjects) {
        this.cameraDetection.put(name,detectedObjects);
    }
    public void addTrackedObject(String name,List<TrackedObject> trackedObject) {
        this.lidarDetection.put(name,trackedObject);
    }
    public void setPoses(List<PoseEvent> poses) {
        this.poses = poses.toArray(new PoseEvent[poses.size()]);
    }
    public String getError() {
        return this.error;
    }
}
