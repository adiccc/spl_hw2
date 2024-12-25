package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;
    private static LiDarDataBase liDarDataBase = null;
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if(liDarDataBase == null) {
            liDarDataBase = new LiDarDataBase();
        }
        return liDarDataBase;
    }
    private LiDarDataBase() {
        init();
    }
    private void init() {//read from json!!!!!!

    }
    public List<StampedCloudPoints> getCloudPoint(List<DetectedObject> l,int time) {
        List<StampedCloudPoints> c = new LinkedList<>();
        for(DetectedObject o : l) {
            for (StampedCloudPoints cloudPoint : cloudPoints) {
            if (cloudPoint.getId().equals(o.getId())&&cloudPoint.getTime()==time) {
                c.add(cloudPoint);
            }
        }
        }
        return c;
    }
}
