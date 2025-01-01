package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private final ConcurrentHashMap<Event, Future> futures;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("camera"+camera.getId());
        this.camera = camera;
        futures = new ConcurrentHashMap<>();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    public void initialize() {//was protected changed for tests
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {
            List<DetectedObjectsEvent> DetectedObjectsEvents = camera.Detect(t.getTime());
            if (DetectedObjectsEvents != null) {
                for(DetectedObjectsEvent detectedObjectsEvent : DetectedObjectsEvents) {
                    if(detectedObjectsEvent.isDetectedError()){
                        System.out.println("Camera "+camera.getId()+"detected error");
                        sendBroadcast(new CrashedBroadcast(this,detectedObjectsEvent.getDetectedError()));}
                    else {
                        System.out.println("Camera "+camera.getId()+"detecting objects");
                        Future f = sendEvent(detectedObjectsEvent);
                        futures.put(detectedObjectsEvent, f);
                    }
                }
            }
            if(!camera.isLeftData())
                this.terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            camera.status = STATUS.DOWN;
            terminate();
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            if (TimeService.class==c.getSender().getClass()) {
                camera.status=STATUS.DOWN;
                terminate();
            }
        });
            }
        public StampedDetectedObjects getLastDetectedObjects(){
            return camera.getLastDetectedObjects();
        }
}
