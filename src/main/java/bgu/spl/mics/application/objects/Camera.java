package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;

import bgu.spl.mics.FileReaderUtil;
import com.google.gson.JsonArray;
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
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectList;

    public Camera(int id, int frequency, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
    }
    public void Detect(int time) {
        List<DetectedObject> l = null;
        for (StampedDetectedObjects detectedObjects : detectedObjectList) {
            if (detectedObjects.getTime() == time) {
                l = detectedObjects.getDetectedObjects();
            }
        }
        if (l != null)
            MessageBusImpl.getInstance().sendEvent(new DetectedObjectsEvent(l, time));
    }
        private void initDetectedObjects (String path){
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
                    JsonArray detectedObjectsArray = cameraEntry.getAsJsonArray("detectedObjects");
                    List<DetectedObject> detectedObjects = new ArrayList<>();
                    for (int j = 0; j < detectedObjectsArray.size(); j++) {
                        JsonObject detectedObjectJson = detectedObjectsArray.get(j).getAsJsonObject();
                        String id = detectedObjectJson.get("id").getAsString();
                        String description = detectedObjectJson.get("description").getAsString();

                        // Create a new DetectedObject and add it to the list
                        DetectedObject detectedObject = new DetectedObject(id, description);
                        detectedObjects.add(detectedObject);
                    }

                    // Create StampedDetectedObjects instance and add it to the list
                    StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(time, detectedObjects);
                    detectedObjectList.add(stampedDetectedObjects);
                }
            }
        }
    }
