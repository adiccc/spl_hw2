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

    public LiDarWorkerTracker(int id,  int frequency, String filePath, StatisticalFolder statisticalFolder ) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new ArrayList<>();
        this.detectedEvents = new ArrayList<>();
        this.filePath = filePath;
        this.statisticalFolder = statisticalFolder;
    }

    public boolean isLeftData(int time){
        return LiDarDataBase.getInstance(filePath).isLeftData(time);
    }
    public TrackedObjectsEvent fetchData(TickBroadcast t){
        for(DetectedObjectsEvent e: detectedEvents){
                if(e.getStampedDetectedObjects().getTime()+frequency<=t.getTime()){
                    detectedEvents.remove(e);
                    return sendTrackedEvent(e,e.getStampedDetectedObjects().getTime());
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
        TrackedObjectsEvent tEvent= new TrackedObjectsEvent(lastTrackedObjects);
        if(lastTrackedObjects.size()!=dec.getDetectedObjects().size()){
            MessageBusImpl.getInstance().complete(e, false);
            tEvent.setDetectedError(true);
            this.status=STATUS.ERROR;
        }
        else if(lastTrackedObjects.size()>0){
            statisticalFolder.increaseNumTrackedObjects(lastTrackedObjects.size());
            MessageBusImpl.getInstance().complete(e, true);
        }
        else{
            MessageBusImpl.getInstance().complete(e, false);
            tEvent= null;
        }
        return tEvent;
    }
    public void processDetectedObjects(DetectedObjectsEvent e){//create tracked object event
        detectedEvents.add(e);
    }
    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }
    public int getId(){return id;}
}
