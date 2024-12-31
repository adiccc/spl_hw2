package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker workerTracker;
    private final ConcurrentHashMap<Event, Future> futures;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, int id) {
        super("LiDarService");
        this.workerTracker = LiDarWorkerTracker;
        futures = new ConcurrentHashMap<>();
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    public void TrackDetected(DetectedObjectsEvent e){

    }
    @Override
    public void initialize() {//was protected changed for tests
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {
        TrackedObjectsEvent e = workerTracker.fetchData(t);
        if (e != null) {
            if(e.isDetectedError())
                sendBroadcast(new CrashedBroadcast(this,"Sensor Lidar disconnected"));
            else
                futures.put(e,sendEvent(e));
        }
        if(!workerTracker.isLeftData(t.getTime()))
            this.terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            workerTracker.status = STATUS.DOWN;
            terminate();
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
            if (TimeService.class==c.getSender().getClass()) {
                workerTracker.status=STATUS.DOWN;
                terminate();
            }
        });
        subscribeEvent(DetectedObjectsEvent.class,(DetectedObjectsEvent t) -> workerTracker.processDetectedObjects(t));
    }

    public List<TrackedObject> getLastTrackedObjects(){
        return workerTracker.getLastTrackedObjects();
    }

}
