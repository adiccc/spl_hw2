package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private String description;
    private List<CloudPoint> coordinates;//can convert to a list

    public TrackedObject(String id, int time, String description, List<CloudPoint> coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }
    public String getId() {
        return id;
    }
    public int getTime() {
        return time;
    }
    public String getDescription() {
        return description;
    }
    public CloudPoint getCoordinates() {
        double x=0,y=0;
        for (CloudPoint point : coordinates) {
            x=(x+point.getX())/2;
            y=(y+point.getY())/2;
        }
        return new CloudPoint(x,y);
    }
}
