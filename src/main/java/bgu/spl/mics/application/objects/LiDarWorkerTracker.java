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
    public STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    private List<DetectedObjectsEvent> detectedEvents;
    private String filePath;
    private StatisticalFolder statisticalFolder;

    public LiDarWorkerTracker(int id,  int frequency, STATUS status, String filePath, StatisticalFolder statisticalFolder ) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new ArrayList<>();
        this.detectedEvents = new ArrayList<>();
        this.filePath = filePath;
        this.statisticalFolder = statisticalFolder;
    }
    public TrackedObjectsEvent fetchData(TickBroadcast t){
        for(DetectedObjectsEvent e: detectedEvents){
                if(e.getStampedDetectedObjects().getTime()+frequency<=t.getTime()){//fix? maybe not by detectedobject
                    detectedEvents.remove(e);
                    return sendTrackedEvent(e,t.getTime());//check again
                }
            }
        return null;
    }
    public TrackedObjectsEvent sendTrackedEvent(DetectedObjectsEvent e,int time){//fix with lastTrackedObjects
        StampedDetectedObjects dec=e.getStampedDetectedObjects();
        lastTrackedObjects=new ArrayList<>();
        for(DetectedObject d:dec.getDetectedObjects()){
            StampedCloudPoints s=LiDarDataBase.getInstance(filePath).getCloudPoint(d,time);
            if(s!=null){
                lastTrackedObjects.add(new TrackedObject(d.getId(),s.getTime(),d.getDescription(),s.getCloudPoints()));//convert to list<cloudpoints>
            }
        }
        if(lastTrackedObjects.size()>0){
            TrackedObjectsEvent tEvent= new TrackedObjectsEvent(lastTrackedObjects);
            for(TrackedObject t:lastTrackedObjects){
                if (t.getId().equals("ERROR")) {
                    tEvent.setDetectedError(true);
                    this.status=STATUS.ERROR;
                    MessageBusImpl.getInstance().complete(e, false);
                }
            }
            if(this.status!=STATUS.ERROR) {
                statisticalFolder.increaseNumTrackedObjects(lastTrackedObjects.size());
                MessageBusImpl.getInstance().complete(e, true);
            }
            return tEvent;
        }
        MessageBusImpl.getInstance().complete(e, false);
        return null;
    }
    public void processDetectedObjects(DetectedObjectsEvent e){//create tracked object event
        detectedEvents.add(e);
    }
    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }
}
