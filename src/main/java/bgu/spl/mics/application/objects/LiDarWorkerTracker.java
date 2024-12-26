package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.ArrayList;
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
    private List<DetectedObjectsEvent> detectedEvents;
    // TODO: Define fields and methods.

    public LiDarWorkerTracker(final int id, final int frequency, final STATUS status, final List<TrackedObject> lastTrackedObjects) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = lastTrackedObjects;
        this.detectedEvents = new ArrayList<>();
    }
    public void fetchData(TickBroadcast t){
            for(DetectedObjectsEvent e: detectedEvents){
                if(e.getTime()+frequency==t.getTime()){
                    sendTrackedEvent(e,t.getTime());
                    detectedEvents.remove(e);
                }
            }
    }
    public void sendTrackedEvent(DetectedObjectsEvent e,int time){
        List<DetectedObject> dec=e.getDetectedObjects();
        lastTrackedObjects=new ArrayList<>();
        for(DetectedObject d:dec){
            StampedCloudPoints s=LiDarDataBase.getInstance("somePath").getCloudPoint(d,time);
            if(s!=null){
                lastTrackedObjects.add(new TrackedObject(d.getId(),s.getTime(),d.getDescription(),s.getCloudPoints().toArray(new CloudPoint[0])));
            }
        }
        if(lastTrackedObjects.size()>0){
            MessageBusImpl.getInstance().sendEvent(new TrackedObjectsEvent(lastTrackedObjects));
        }
    }
    public void processDetectedObjects(DetectedObjectsEvent e){//create tracked object event
        detectedEvents.add(e);
    }
}
