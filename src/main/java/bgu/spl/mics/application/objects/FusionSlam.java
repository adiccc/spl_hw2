package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        // TODO: Implement singleton instance logic.
    }

    private static FusionSlam instance=null;
    private ConcurrentHashMap<Integer,StampedDetectedObjects> maps;

    private FusionSlam() {
        //TODO
    }
    public static FusionSlam getInstance() {
        if(instance==null)
            instance = new FusionSlam();
        return instance;
    }

    public void updateMap(TrackedObjectsEvent trackedObjectsEvent) {
        // TODO
    }
    public void updatePose(PoseEvent p) {
        // TODO
    }
}
