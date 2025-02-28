package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private AtomicInteger systemRuntime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandmarks;
    private Map<String,LandMark> landmarks;
    public StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
        this.landmarks = new HashMap<>();
    }

    public void increaseNumDetectedObjects(int numDetectedObjects) {
        this.numDetectedObjects.addAndGet(numDetectedObjects);
    }
    public void increaseNumTrackedObjects(int numTrackedObjects) {
        this.numTrackedObjects.addAndGet(numTrackedObjects);
    }
    public void increaseNumLandmarks(int numLandmarks) {
        this.numLandmarks.addAndGet(numLandmarks);
    }
    public void setSystemRuntime(int systemRuntime) {
        this.systemRuntime.set(systemRuntime);
    }

    public void setLandmarks(ArrayList<LandMark> landmarks) {
       for(LandMark landmark : landmarks) {
           this.landmarks.put(landmark.getId(),landmark);
       }
    }
}
