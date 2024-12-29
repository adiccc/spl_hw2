package bgu.spl.mics.application.objects;

import bgu.spl.mics.FileReaderUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
        // Get the objects and parse them into cloudPoints list
        Gson gson = new Gson();
        Type objectListType = new TypeToken<List<Pose>>() {}.getType();
        this.PoseList = gson.fromJson(jsonArray, objectListType);
    }

    public Pose getPose(){
        return PoseList.get(currentTick);
    }
    public void setCurrentTick(int tick){
        currentTick = tick;
    }
}
