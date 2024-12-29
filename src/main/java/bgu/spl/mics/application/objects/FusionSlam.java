package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.ArrayList;
import java.util.LinkedList;
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
    private ConcurrentHashMap<Integer, StampedDetectedObjects> maps;
    private StatisticalFolder statisticalFolder;
    public static int NumberOfSensors;

    //TODO use statisticalFolder.increaseNumLandmarks before we add a new landmark to the map
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance;

        private static void init(StatisticalFolder statisticalFolder) {
            instance = new FusionSlam(statisticalFolder);
        }
    }

    public synchronized static void addNumberOfSensors() {
        FusionSlam.NumberOfSensors = FusionSlam.NumberOfSensors + 1;
    }

    public synchronized static void decreaseNumberOfSensors() {
        FusionSlam.NumberOfSensors--;
    }

    public synchronized static int getNumberOfSensors() {
        return FusionSlam.NumberOfSensors;
    }

    private FusionSlam(StatisticalFolder statisticalFolder) {
        //TODO - check if its ok
        trackedObjectsevents = new ArrayList<>();
        poses = new ArrayList<>();
        landMarks = new ArrayList<>();
        maps = new ConcurrentHashMap<>();
        this.statisticalFolder = statisticalFolder;
    }

    public static FusionSlam getInstance(StatisticalFolder statisticalFolder) {
        if (FusionSlamHolder.instance == null) {
            synchronized (FusionSlamHolder.class) {
                if (FusionSlamHolder.instance == null) {
                    FusionSlamHolder.init(statisticalFolder);
                }
            }
        }
        return FusionSlamHolder.instance;
    }

    public void updateMap(TrackedObjectsEvent trackedObjectsEvent) {
        boolean found = false;
        for (PoseEvent poseEvent : poses) {
            if (poseEvent.getPose().getTime() == trackedObjectsEvent.getTrackedObjects().get(0).getTime()) {
                updateMap(trackedObjectsEvent, poseEvent);
                found = true;
            }
        }
        if (!found) {
            trackedObjectsevents.add(trackedObjectsEvent);
        }
    }

    public void updatePose(PoseEvent p) {
        boolean found = false;
        for (TrackedObjectsEvent event : trackedObjectsevents) {
            if (p.getPose().getTime() == event.getTrackedObjects().get(0).getTime()) {
                updateMap(event, p);
                found = true;
            }
        }
        if (!found) {
            poses.add(p);
        }
    }

    public void updateMap(TrackedObjectsEvent trackedObjectsEvent, PoseEvent poseEvent) {
        List<LandMark> newLandMarks = new ArrayList<>();
        for (LandMark landMark : landMarks) {
            for (TrackedObject trackedObject : trackedObjectsEvent.getTrackedObjects()) {
                if (landMark.getId().equals(trackedObject.getId())) {
                    landMark.addPoint(convertToChargingStation(trackedObject.getCoordinates(),poseEvent.getPose()));
                } else {
                    List<CloudPoint> n=new LinkedList<>();
                    n.add(convertToChargingStation(trackedObject.getCoordinates(), poseEvent.getPose()));
                    newLandMarks.add(new LandMark(trackedObject.getId(), trackedObject.getDescription(),n));
                }
            }
        }
        if (!newLandMarks.isEmpty()) {
            landMarks.addAll(newLandMarks);
        }
    }

    public CloudPoint convertToChargingStation(CloudPoint cloudPoint, Pose p) {
        double newX,newY;
            newX = p.getX() + cloudPoint.getDistance() * Math.cos(p.getYaw() * Math.PI / 180);
            newY = p.getY() + cloudPoint.getDistance() * Math.sin(p.getYaw() * Math.PI / 180);
        return new CloudPoint(newX,newY);
    }
}
