package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class TrackedObjectsEvent implements Event<Boolean> {
    private List<TrackedObject> trackedObjects;
    private boolean detectedError;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjects) {
        this.trackedObjects = trackedObjects;
        this.detectedError = false;
    }
    public void setDetectedError(boolean detectedError) {
        this.detectedError = detectedError;
    }
    public boolean isDetectedError() {
        return this.detectedError;
    }
    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }
}
