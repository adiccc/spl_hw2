package bgu.spl.mics.application.objects;
import java.lang.reflect.Type;

import bgu.spl.mics.FileHandelUtil;
import bgu.spl.mics.Parser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;

    private static class LidarHolder {
        private static LiDarDataBase instance=new LiDarDataBase("");
        private static void setPath(String path){
            instance=new LiDarDataBase(path);
        }
    }
    public void setPath(String filePath){
        LidarHolder.setPath(filePath);
    }

    public boolean isLeftData(int time){
        return cloudPoints.get(cloudPoints.size()-1).getTime()>=time;
    }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        return LidarHolder.instance;
    }

    private LiDarDataBase(String filePath){
        if(filePath.length()>0)
            initLidarData(filePath);
    }

    private void initLidarData(String filePath){
        JsonArray jsonArray = FileHandelUtil.readJsonArray(filePath);
        this.cloudPoints=Parser.deserializeDataBase(jsonArray);
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
