package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Parser{

        private static CloudPoint deserializeCloudPoints(JsonArray context) {
            double x = context.get(0).getAsDouble();
            double y = context.get(1).getAsDouble();
            return new CloudPoint(x, y);
        }

        public static StampedCloudPoints deserializeStampedCloudPoints(JsonObject context) {
            List<CloudPoint> cloudPoints = new ArrayList<>();
            String id=context.get("id").getAsString();
            int time=context.get("time").getAsInt();

            JsonArray jsonArray = context.get("cloudPoints").getAsJsonArray();
                // Iterate through each JSON object in the array
                for (JsonElement element : jsonArray) {
                    JsonArray point = element.getAsJsonArray();
                    cloudPoints.add(deserializeCloudPoints(point));
                }
            return new StampedCloudPoints(id,time,cloudPoints);
        }

        public static List<StampedCloudPoints> deserializeDataBase(JsonArray context) {
            List<StampedCloudPoints> database = new ArrayList<>();
            for (JsonElement element : context) {
                JsonObject row = element.getAsJsonObject();
                database.add(deserializeStampedCloudPoints(row));
            }
            return database;
        }

        public static List<StampedDetectedObjects> deserializeCameraData(String name, JsonObject o){

            // Check if the camera exists in the JSON object
            if (o.has(name)) {
                Gson gson = new Gson();
                List<StampedDetectedObjects> detectedObjectList = new ArrayList<>();
                JsonArray cameraData = o.getAsJsonArray(name);

                Type objectListType = new TypeToken<List<StampedDetectedObjects>>() {}.getType();
                return gson.fromJson(cameraData, objectListType);

                // Iterate over the array of camera data
//                for (int i = 0; i < cameraData.size(); i++) {
//                    JsonObject cameraEntry = cameraData.get(i).getAsJsonObject();
//                    int time = cameraEntry.get("time").getAsInt();
//                    // Get the detected objects and parse them into DetectedObject list
//                    Type objectListType = new TypeToken<List<DetectedObject>>() {}.getType();
//                    List<DetectedObject> detectedObjects = gson.fromJson(cameraEntry.getAsJsonArray("detectedObjects"), objectListType);
//                    // Create StampedDetectedObjects instance and add it to the list
//                    StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(time, detectedObjects);
//                    detectedObjectList.add(stampedDetectedObjects);
//                }
//
//                return detectedObjectList;
            }
            return null;

        }

        public static List<Pose> deserializeGPSData(JsonArray jsonArray){
            // Get the objects and parse them into cloudPoints list
            Gson gson = new Gson();
            Type objectListType = new TypeToken<List<Pose>>() {}.getType();
            return gson.fromJson(jsonArray, objectListType);
        }

}
