package bgu.spl.mics.application.objects;

import bgu.spl.mics.FileReaderUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> PoseList;

    public GPSIMU(String filePath) {
        PoseList = new ArrayList<>();
        JsonArray jsonArray = FileReaderUtil.readJson(filePath).getAsJsonArray();
        // Iterate over each JSON object in the array
        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();

            // Extract fields
            int time = obj.get("time").getAsInt();
            float x = obj.get("x").getAsFloat();
            float y = obj.get("y").getAsFloat();
            float yaw = obj.get("yaw").getAsFloat();

            // Create and add Pose object
            Pose pose = new Pose(x, y, yaw, time);
            PoseList.add(pose);
        }
    }

    public Pose getPose(){
        return PoseList.get(currentTick);
    }
    public void setCurrentTick(int tick){
        currentTick = tick;
    }
}
