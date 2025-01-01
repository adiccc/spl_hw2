package bgu.spl.mics.application.objects;

import java.util.ArrayList;
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
    public List<CloudPoint> getCoordinates() {
        return Coordinates;
    }
    public String getDescription() {
        return Description;
    }
    public void UpdatePoints(List<CloudPoint> point) {
        List<CloudPoint> newCoordinates = new ArrayList<CloudPoint>();
        for(int i=0;i<point.size();i++) {
            if(Coordinates.size()>i)
                newCoordinates.add(new CloudPoint((point.get(i).getX()+Coordinates.get(i).getX())/2,(point.get(i).getY()+Coordinates.get(i).getY())/2));
            else{
                newCoordinates.add(point.get(i));
            }
            }
        Coordinates=newCoordinates;
    }

    public String toString(){
        return Id + "{ Description=" + Description + ", Coordinates=" + Coordinates + "}";
    }
}
