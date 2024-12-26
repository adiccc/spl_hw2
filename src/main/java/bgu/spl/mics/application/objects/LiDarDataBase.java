package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.LinkedList;
import bgu.spl.mics.FileReaderUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;
    private static LiDarDataBase liDarDataBase=null;

    private LiDarDataBase(String filePath){
        initLidarData(filePath);
    }
    private void initLidarData(String filePath){
        this.cloudPoints = new ArrayList<>();
        JsonArray jsonArray = FileReaderUtil.readJson(filePath="/lidar_data.json").getAsJsonArray();
        // Iterate over each object in the JSON array
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject cloudPointJson = jsonArray.get(i).getAsJsonObject();
            int time = cloudPointJson.get("time").getAsInt();
            String id = cloudPointJson.get("id").getAsString();

            // Parse cloudPoints array
            JsonArray cloudPointsArray = cloudPointJson.getAsJsonArray("cloudPoints");
            List<CloudPoint> tempCloudPoints = new ArrayList<>();

            // Iterate over each cloudPoint and create CloudPoint objects
            for (int j = 0; j < cloudPointsArray.size(); j++) {
                JsonArray point = cloudPointsArray.get(j).getAsJsonArray();
                double x = point.get(0).getAsDouble();
                double y = point.get(1).getAsDouble();
                double z = point.get(2).getAsDouble();

                // Create a new CloudPoint object and add it to the list
                CloudPoint cloudPoint = new CloudPoint(x, y);
                tempCloudPoints.add(cloudPoint);
            }

            // Create a StampedCloudPoints object and add it to the list
            StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(id, time, tempCloudPoints);
            cloudPoints.add(stampedCloudPoints);
        }
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if(liDarDataBase == null) {
            liDarDataBase = new LiDarDataBase(filePath);
        }
        return liDarDataBase;
    }
    private LiDarDataBase() {
        init();
    }
    private void init() {//read from json!!!!!!

    }
    public StampedCloudPoints getCloudPoint(DetectedObject d,int time) {
            for (StampedCloudPoints cloudPoint : cloudPoints) {
            if (cloudPoint.getId().equals(d.getId())&&cloudPoint.getTime()==time) {
               return cloudPoint;
            }
        }
        return null;
    }
}
