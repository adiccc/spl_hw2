package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.STATUS;

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
        super("cameraService");
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
            System.out.println("camera got tick *********");
            DetectedObjectsEvent e = camera.Detect(t.getTime());
            if (e != null) {
                if(e.isDetectedError())
                    sendBroadcast(new CrashedBroadcast(this,e.getDetectedError()));
                else
                    futures.put(e,sendEvent(e));
            }
            System.out.println("camera got tick");
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            camera.status = STATUS.DOWN;
            System.out.println("camera going down - terminated, something got terminated");
            terminate();
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            if (TimeService.class.equals(c.getSender().getClass())) {
                camera.status = STATUS.DOWN;
                terminate();
            }
        });
            }
        public List<DetectedObject> getLastDetectedObjects(){
            return camera.getLastDetectedObjects();
        }
}
