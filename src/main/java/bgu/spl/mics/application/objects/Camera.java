package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectList;

    public Camera(int id, int frequency, STATUS status) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
    }
    public void Detect(int time){//read from json

    }
}
