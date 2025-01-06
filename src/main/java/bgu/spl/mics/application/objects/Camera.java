package bgu.spl.mics.application.objects;

import bgu.spl.mics.handllers.Parser;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;

import bgu.spl.mics.handllers.FileHandelUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    public STATUS status;
    private List<StampedDetectedObjects> detectedObjectList;
    private StampedDetectedObjects lastDetectedObjects;
    private StatisticalFolder statisticalFolder;

    public Camera(int id, int frequency,String filePath, StatisticalFolder statisticalFolder) {
        this.status=STATUS.UP;
        this.id = id;
        this.frequency = frequency;
        initDetectedObjects(filePath);
        this.statisticalFolder = statisticalFolder;
        this.lastDetectedObjects = null;
    }

    public boolean isLeftData(){
        return (detectedObjectList.size()>0);
    }

    public int getId(){
        return this.id;
    }

    public STATUS getStatus(){ return this.status; }

    public List<DetectedObjectsEvent> Detect(int time) {
        List<StampedDetectedObjects> l = new ArrayList<>();
        List<StampedDetectedObjects> toRemove = new ArrayList<>();
        for (StampedDetectedObjects detectedObjects : detectedObjectList) {//adds the new detected object
            if (detectedObjects.getTime() + frequency <= time) {
                l.add(detectedObjects);
                toRemove.add(detectedObjects);
            }
        }
        for(StampedDetectedObjects detectedObjects : toRemove) {//removes the send detected objects
            this.detectedObjectList.remove(detectedObjects);
        }
        if (l.size() > 0){
            List<DetectedObjectsEvent> events=new ArrayList<>();
            for (StampedDetectedObjects detectedObjects : l) {
                DetectedObjectsEvent currentEvent=new DetectedObjectsEvent(detectedObjects.getDetectedObjects(), detectedObjects.getTime());
                for(int i=0;i<detectedObjects.getDetectedObjects().size();i++){
                    if(detectedObjects.getDetectedObjects().get(i).getId().equals("ERROR")) {
                        this.status=STATUS.ERROR;
                        currentEvent.setDetectedError(detectedObjects.getDetectedObjects().get(i).getDescription());
                    }
                }
                events.add(currentEvent);
            }
            if(this.status!=STATUS.ERROR) {
                lastDetectedObjects = l.get(l.size()-1);
                int sumDetectedObjects=0;
                for (StampedDetectedObjects detectedObjects : l) {
                    sumDetectedObjects+=detectedObjects.getDetectedObjects().size();
                }
                statisticalFolder.increaseNumDetectedObjects(sumDetectedObjects);
            }
            return events;
        }
        return null;
    }
        private void initDetectedObjects (String path){
            System.out.println(path);
            JsonObject o = FileHandelUtil.readJsonObject(path);
            String name = "camera" + id;
            this.detectedObjectList= Parser.deserializeCameraData(name,o);
            if(this.detectedObjectList==null)
                this.status=STATUS.ERROR;
        }
        public StampedDetectedObjects getLastDetectedObjects(){
            return lastDetectedObjects;
        }
    }
