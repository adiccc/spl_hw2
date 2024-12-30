package bgu.spl.mics.application.objects;

import bgu.spl.mics.FileHandelUtil;
import bgu.spl.mics.Parser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
        JsonArray jsonArray = FileHandelUtil.readJsonArray(filePath);
        this.PoseList= Parser.deserializeGPSData(jsonArray);
    }

    public Pose getPose(){
        return PoseList.get(currentTick);
    }
    public void setCurrentTick(int tick){
        currentTick = tick;
    }
}
