package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;

import bgu.spl.mics.FileReaderUtil;
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
    private StatisticalFolder statisticalFolder;

    public Camera(int id, int frequency, STATUS status,String filePath, StatisticalFolder statisticalFolder) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        initDetectedObjects(filePath);
        this.statisticalFolder = statisticalFolder;
    }
    public DetectedObjectsEvent Detect(int time) {
        List<DetectedObject> l = null;
        for (StampedDetectedObjects detectedObjects : detectedObjectList) {
            if (detectedObjects.getTime() + frequency == time) {
                l = detectedObjects.getDetectedObjects();
            }
        }
        if (l != null){
            DetectedObjectsEvent dEvent=new DetectedObjectsEvent(l, time);
            for(DetectedObject detectedObject : l){
                if(detectedObject.getId().equals("ERROR")) {
                    dEvent.setDetectedError(true);
                    this.status=STATUS.ERROR;
                }
            }
            if(this.status!=STATUS.ERROR)
                statisticalFolder.increaseNumDetectedObjects(l.size());
            return dEvent;
        }
        return null;
    }
        private void initDetectedObjects (String path){
            Gson gson = new Gson();
            detectedObjectList = new ArrayList<>();
            JsonObject o = FileReaderUtil.readJson(path);
            String name = "camera" + id;
            // Check if the camera exists in the JSON object
            if (o.has(name)) {
                JsonArray cameraData = o.getAsJsonArray(name);
                // Iterate over the array of camera data
                for (int i = 0; i < cameraData.size(); i++) {
                    JsonObject cameraEntry = cameraData.get(i).getAsJsonObject();
                    int time = cameraEntry.get("time").getAsInt();

                    // Get the detected objects and parse them into DetectedObject list
                    Type objectListType = new TypeToken<List<DetectedObject>>() {}.getType();
                    List<DetectedObject> detectedObjects = gson.fromJson(cameraEntry.getAsJsonArray("detectedObjects"), objectListType);

                    // Create StampedDetectedObjects instance and add it to the list
                    StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(time, detectedObjects);
                    detectedObjectList.add(stampedDetectedObjects);
                }
            }
        }
    }
