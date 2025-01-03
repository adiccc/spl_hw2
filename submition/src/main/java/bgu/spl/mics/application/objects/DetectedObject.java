package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {
    private String id;
    private String description;

    public DetectedObject(String id, String description) {
        this.id = id;
        this.description = description;
    }
    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }

    public boolean equals(DetectedObject d) {
        boolean b=id.equals(d.getId())&& description.equals(d.getDescription());
        return id.equals(d.getId())&& description.equals(d.getDescription());
    }
}
