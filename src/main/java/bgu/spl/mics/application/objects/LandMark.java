package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String Id;
    private String Description;
    private List<CloudPoint> Coordinates;
    private CloudPoint currentPose;

    public LandMark(String Id, String Description, List<CloudPoint> Coordinates) {
        this.Id = Id;
        this.Description = Description;
        this.Coordinates = Coordinates;
    }
    public String getId() {
        return Id;
    }
    public String getDescription() {
        return Description;
    }
    public void addPoint(CloudPoint point) {
            Coordinates.add(point);
    }

    public String toString(){
        return Id + "{ Description=" + Description + ", Coordinates=" + Coordinates + "}";
    }
}
