package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private List<TrackedObjectsEvent> trackedObjectsevents;
    private List<PoseEvent> poses;
    private ArrayList<LandMark> landMarks;
    private ConcurrentHashMap<Integer,StampedDetectedObjects> maps;

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance=new FusionSlam();
    }

    private FusionSlam() {
        //TODO - check if its ok
        trackedObjectsevents = new ArrayList<>();
        poses = new ArrayList<>();
        landMarks = new ArrayList<>();
        maps = new ConcurrentHashMap<>();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public void updateMap(TrackedObjectsEvent trackedObjectsEvent) {
        boolean found=false;
        for(PoseEvent poseEvent : poses) {
            if(poseEvent.getPose().getTime()==trackedObjectsEvent.getTrackedObjects().get(0).getTime()){
                updateMap(trackedObjectsEvent,poseEvent);
                found=true;
            }
        }
        if(!found){
            trackedObjectsevents.add(trackedObjectsEvent);
        }
    }
    public void updatePose(PoseEvent p) {
        boolean found=false;
        for(TrackedObjectsEvent event : trackedObjectsevents) {
            if(p.getPose().getTime()==event.getTrackedObjects().get(0).getTime()){
                updateMap(event,p);
                found=true;
            }
        }
        if(!found){
            poses.add(p);
        }
    }
    public void updateMap(TrackedObjectsEvent trackedObjectsEvent, PoseEvent poseEvent) {
        for(LandMark landMark : landMarks) {
            for (TrackedObject trackedObject : trackedObjectsEvent.getTrackedObjects()) {
                if(landMark.getDescription().equals(trackedObject.getDescription())){
                    landMark.mergePoints(trackedObject.getCoordinates());
                }
            }
        }
    }
}
