package bgu.spl.mics;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorReport {

    private String error;
    private String fualtySensor;
    private Map<String, StampedDetectedObjects> lastCameraFrames;
    private Map<String,List<TrackedObject>> lastLidarFrames;
    private Pose[] poses;

    public ErrorReport(String error, String fualtySensor) {
        this.error = error;
        this.fualtySensor = fualtySensor;
        this.lastCameraFrames = new HashMap<>();
        this.poses=null;
        this.lastLidarFrames =new HashMap<>();
    }

    public void setError(String error) {
        this.error = error;
    }
    public void setFualtySensor(String fualtySensor) {
        this.fualtySensor = fualtySensor;
    }
    public void addDetectedObject(String name, StampedDetectedObjects detectedObjects) {
        this.lastCameraFrames.put(name,detectedObjects);
    }
    public void addTrackedObject(String name,List<TrackedObject> trackedObject) {
        this.lastLidarFrames.put(name,trackedObject);
    }
    public void setPoses(List<PoseEvent> poses) {
        this.poses=new Pose[poses.size()];
        int i=0;
        for (PoseEvent pose : poses) {
            this.poses[i]=pose.getPose();
            i++;
        }
    }

    public String getError() {
        return this.error;
    }
}
