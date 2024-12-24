package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    // TODO: Define fields and methods.

    public LiDarWorkerTracker(final int id, final int frequency, final STATUS status, final List<TrackedObject> lastTrackedObjects) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = lastTrackedObjects;
    }
    public void fetchData(TickBroadcast t){
            // TODO:
    }

    public void processDetectedObjects(DetectedObjectsEvent e){
        //TODO
    }
}
