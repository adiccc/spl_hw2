package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CrashedBroadcast;
import bgu.spl.mics.application.objects.TerminatedBroadcast;
import bgu.spl.mics.application.objects.TickBroadcast;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private Camera camera;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Change_This_Name");
        // TODO Implement this
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().subscribeBroadcast(TickBroadcast.class,this);
        MessageBusImpl.getInstance().subscribeBroadcast(CrashedBroadcast.class,this);
        MessageBusImpl.getInstance().subscribeBroadcast(TerminatedBroadcast.class,this);
    }
}
