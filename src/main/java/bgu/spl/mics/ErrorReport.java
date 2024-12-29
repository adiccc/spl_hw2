package bgu.spl.mics;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class ErrorReport {

    private String error;
    private String fualtySensor;
    private List<DetectedObject> cameraDetection;
    private List<TrackedObject> lidarDetection;
    private Pose[] poses;

    public ErrorReport(String error, String fualtySensor, List<DetectedObject> detectedObjects,List<TrackedObject> lidarDetection, Pose[] poses) {
        this.error = error;
        this.fualtySensor = fualtySensor;
        this.cameraDetection = detectedObjects;
        this.poses = poses;
        this.lidarDetection = lidarDetection;
    }
}
