package bgu.spl.mics;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class ErrorReport {

    private String error;
    private String fualtySensor;
    private List<DetectedObject> cameraDetection;
    private List<TrackedObject> lidarDetection;
    private PoseEvent[] poses;

    public ErrorReport(String error, String fualtySensor, List<DetectedObject> detectedObjects,List<TrackedObject> lidarDetection, PoseEvent[] poses) {
        this.error = error;
        this.fualtySensor = fualtySensor;
        this.cameraDetection = detectedObjects;
        this.poses = poses;
        this.lidarDetection = lidarDetection;
    }

    public void setError(String error) {
        this.error = error;
    }
    public void setFualtySensor(String fualtySensor) {
        this.fualtySensor = fualtySensor;
    }
    public void addDetectedObject(List<DetectedObject> detectedObjects) {
        this.cameraDetection.addAll(detectedObjects);
    }
    public void addTrackedObject(List<TrackedObject> trackedObject) {
        this.lidarDetection.addAll(trackedObject);
    }
    public void setPoses(List<PoseEvent> poses) {
        this.poses = poses.toArray(new PoseEvent[poses.size()]);
    }
    public String getError() {
        return this.error;
    }
}
