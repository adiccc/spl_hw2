package bgu.spl.mics.handllers;

import bgu.spl.mics.application.objects.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Parser{

        private static CloudPoint deserializeCloudPoints(JsonArray context) {
            if (context != null
                    && context.size() >= 2
                    && context.get(0).isJsonPrimitive() && context.get(0).getAsJsonPrimitive().isNumber()
                    && context.get(1).isJsonPrimitive() && context.get(1).getAsJsonPrimitive().isNumber()) {
                double x = context.get(0).getAsDouble();
                double y = context.get(1).getAsDouble();
                return new CloudPoint(x, y);
            }
            else{
                System.out.println("Error: invalid cloud points structure");
                return null;
            }

        }

    public static StampedCloudPoints deserializeStampedCloudPoints(JsonObject context) {
        // Validate input structure
        if (context == null
                || !context.has("id") || !context.get("id").isJsonPrimitive() || !context.get("id").getAsJsonPrimitive().isString()
                || !context.has("time") || !context.get("time").isJsonPrimitive() || !context.get("time").getAsJsonPrimitive().isNumber()
                || !context.has("cloudPoints") || !context.get("cloudPoints").isJsonArray()) {
           System.out.println("Error: invalid cloud points structure");
           return null;
        }

        // Extract and process data
        String id = context.get("id").getAsString();
        int time = context.get("time").getAsInt();
        JsonArray jsonArray = context.get("cloudPoints").getAsJsonArray();

        List<CloudPoint> cloudPoints = new ArrayList<>();
        try {
            for (JsonElement element : jsonArray) {
                if (!element.isJsonArray()) {
                    System.out.println("Error: invalid cloud points structure");
                    return null;
                }
                JsonArray point = element.getAsJsonArray();
                cloudPoints.add(deserializeCloudPoints(point));
            }
        } catch (Exception e) {
            System.out.println("Error: invalid cloud points structure");
            return null;
        }
        return new StampedCloudPoints(id, time, cloudPoints);
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
                List<StampedDetectedObjects> result=null;
                Gson gson = new Gson();
                List<StampedDetectedObjects> detectedObjectList = new ArrayList<>();
                JsonArray cameraData = o.getAsJsonArray(name);
                try {
                    Type objectListType = new TypeToken<List<StampedDetectedObjects>>() {
                    }.getType();
                    result = gson.fromJson(cameraData, objectListType);

                }catch (Exception e){
                    System.out.println("Error: invalid camera data structure");
                    return null;
                }
                return result;
            }
            return null;

        }

        public static List<Pose> deserializeGPSData(JsonArray jsonArray){
            // Get the objects and parse them into cloudPoints list
            Gson gson = new Gson();
            Type objectListType = new TypeToken<List<Pose>>() {}.getType();
            List<Pose> result=null;
            try {
                result=gson.fromJson(jsonArray, objectListType);
            } catch (Exception e) {
                return null;
            }
            return result;
        }

    public static List<Camera> handleCameras(JsonObject jsonObject, StatisticalFolder statisticalFolder, String folderPath) {
        List<Camera> cameras = new ArrayList<>();

        // Check if the CamerasConfigurations field exists and is an array
        if (!jsonObject.get("CamerasConfigurations").isJsonArray()) {
            System.out.println("Error: CamerasConfigurations is missing or not an array.");
            return null; // Return an empty list or handle it as needed
        }

        JsonArray camerasConfig = jsonObject.getAsJsonArray("CamerasConfigurations");

        // Check if camera data path is present and is a string
        if (!jsonObject.has("camera_datas_path") || !jsonObject.get("camera_datas_path").isJsonPrimitive() || jsonObject.get("camera_datas_path").getAsString().length()<3) {
            System.out.println("Error: camera_datas_path is missing or not a string.");
            return null; // Return an empty list or handle it as needed
        }

        // Get the camera data file path
        String cameraDataPath = folderPath + jsonObject.get("camera_datas_path").getAsString().substring(2);

        // Iterate through the cameras configurations
        for (JsonElement cameraElement : camerasConfig) {
            if (!cameraElement.isJsonObject()) {
                System.out.println("Error: An element in CamerasConfigurations is not a valid object.");
                return null;
            }

            JsonObject cameraObj = cameraElement.getAsJsonObject();

            // Check for the required properties
            if (!cameraObj.has("id") || !cameraObj.has("frequency")) {
                System.out.println("Error: Camera object is missing required properties (id, frequency).");
                return null;
            }

            try {
                // Extract camera properties
                int id = cameraObj.get("id").getAsInt();
                int frequency = cameraObj.get("frequency").getAsInt();

                // Create a Camera object and add it to the list
                Camera camera = new Camera(id, frequency, cameraDataPath, statisticalFolder);
                if(camera.status==STATUS.ERROR){
                    System.out.println("Error: camera data is invalid");
                    return null;
                }
                cameras.add(camera);
            } catch (Exception e) {
                System.out.println("Error: Failed to parse camera properties cause of an incorrect structure input.");
                return null;
            }
        }
        return cameras;
    }

    public static int parseInt(JsonElement element) {
        int result=-1;
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            try {
                result = element.getAsInt();
            } catch (NumberFormatException e) {
            }
        }
        return result;

    }


    public static List<LiDarWorkerTracker> handleLidarWorkers(JsonObject jsonObject, StatisticalFolder statisticalFolder, String folderPath) {
        List<LiDarWorkerTracker> liDarWorkerTrackers = new ArrayList<>();

        // Check if the LidarConfigurations field exists and is an array
        if ( !jsonObject.get("LidarConfigurations").isJsonArray()) {
            System.out.println("Error: LidarConfigurations is missing or not an array.");
            return null; // Return an empty list or handle it as needed
        }

        JsonArray workersConfig = jsonObject.getAsJsonArray("LidarConfigurations");

        // Check if lidar data path is present and is a string
        if (!jsonObject.has("lidars_data_path") || !jsonObject.get("lidars_data_path").isJsonPrimitive() || jsonObject.get("lidars_data_path").getAsString().length()<3) {
            System.out.println("Error: lidars_data_path is missing or not a string.");
            return null; // Return an empty list or handle it as needed
        }

        // Get the lidar database file path
        String filePath = folderPath + jsonObject.get("lidars_data_path").getAsString().substring(2);
        LiDarDataBase.getInstance(filePath).setPath(filePath);
        if(!LiDarDataBase.getInstance(filePath).isDataInitValid()){
            System.out.println("Error: lidars_data_base got wrong input.");
            return null;
        }

        // Iterate through the lidar configurations
        for (JsonElement lidarElement : workersConfig) {
            if (!lidarElement.isJsonObject()) {
                System.out.println("Error: An element in LidarConfigurations is not a valid object.");
                return null;
            }

            JsonObject lidarObj = lidarElement.getAsJsonObject();

            // Check for the required properties
            if (!lidarObj.has("id") || !lidarObj.has("frequency")) {
                System.out.println("Error: Lidar object is missing required properties (id, frequency).");
                return null;
            }

            try {
                // Extract lidar properties
                int id = lidarObj.get("id").getAsInt();
                int frequency = lidarObj.get("frequency").getAsInt();

                // Create a LiDarWorkerTracker object and add it to the list
                LiDarWorkerTracker lidar = new LiDarWorkerTracker(id, frequency, filePath, statisticalFolder);
                liDarWorkerTrackers.add(lidar);
            } catch (Exception e) {
                System.out.println("Error: Failed to parse lidar properties cause of an incorrect structure input. ");
            }
        }
        return liDarWorkerTrackers;
    }

}
