package bgu.spl.mics.application.objects;

import bgu.spl.mics.Parser;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;

import bgu.spl.mics.FileHandelUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    private List<DetectedObject> lastDetectedObjects;
    private StatisticalFolder statisticalFolder;

    public Camera(int id, int frequency,String filePath, StatisticalFolder statisticalFolder) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        initDetectedObjects(filePath);
        this.statisticalFolder = statisticalFolder;
        this.lastDetectedObjects = null;
        this.status=STATUS.UP;
    }
    public List<DetectedObjectsEvent> Detect(int time) {
        List<StampedDetectedObjects> l = new ArrayList<>();
        List<StampedDetectedObjects> toRemove = new ArrayList<>();
        System.out.println("********");
        for (StampedDetectedObjects detectedObjects : detectedObjectList) {
            if (detectedObjects.getTime() + frequency <= time) {
                l.add(detectedObjects);
                toRemove.add(detectedObjects);
            }
        }
        for(StampedDetectedObjects detectedObjects : toRemove) {
            this.detectedObjectList.remove(detectedObjects);
        }
        if (l.size() > 0){
            List<DetectedObjectsEvent> events=new ArrayList<>();
            for (StampedDetectedObjects detectedObjects : l) {
                events.add(new DetectedObjectsEvent(detectedObjects.getDetectedObjects(), detectedObjects.getTime()));
                for(DetectedObject d : detectedObjects.getDetectedObjects()){
                    if(d.getId().equals("ERROR")) {
                        events.get(events.size()-1).setDetectedError(d.getDescription());
                        this.status=STATUS.ERROR;
                    }
                }
            }
            if(this.status!=STATUS.ERROR) {
                lastDetectedObjects = l.get(l.size()-1).getDetectedObjects();
                statisticalFolder.increaseNumDetectedObjects(l.size());
            }
            return events;
        }
        return null;
    }
        private void initDetectedObjects (String path){
            JsonObject o = FileHandelUtil.readJsonObject(path);
            String name = "camera" + id;
            this.detectedObjectList= Parser.deserializeCameraData(name,o);
        }
        public List<DetectedObject> getLastDetectedObjects(){
            return lastDetectedObjects;
        }
    }
