package bgu.spl.mics.application.objects;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import bgu.spl.mics.FileReaderUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;

    private static class LidarHolder {
        private static LiDarDataBase instance;

        private static void init(String path) {
            instance = new LiDarDataBase(path);
        }
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if (LidarHolder.instance == null) {
            synchronized (LidarHolder.class) {
                if (LidarHolder.instance == null) {
                    LidarHolder.init(filePath);
                }
            }
        }
        return LidarHolder.instance;
    }


    private LiDarDataBase(String filePath){
        initLidarData(filePath);
    }

    private void initLidarData(String filePath){
        JsonArray jsonArray = FileReaderUtil.readJson(filePath).getAsJsonArray();

        // Get the objects and parse them into cloudPoints list
        Gson gson = new Gson();
        Type objectListType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
        this.cloudPoints = gson.fromJson(jsonArray, objectListType);
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
