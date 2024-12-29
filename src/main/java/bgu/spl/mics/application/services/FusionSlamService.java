package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("fusion_slam");
        this.fusionSlam = fusionSlam;
    }


    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {});
        subscribeEvent(TrackedObjectsEvent.class,(TrackedObjectsEvent t) -> fusionSlam.updateMap(t));
        subscribeEvent(PoseEvent.class,(PoseEvent t) -> fusionSlam.updatePose(t));
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {//only when all sensors terminate terminates
            if ("cameraService".equals(c.getSender().getName())||"LiDarService".equals(c.getSender().getName())) {
                FusionSlam.decreaseNumberOfSensors();
            }
            if (FusionSlam.getNumberOfSensors() == 0) {
                createOutputFile();
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> terminate());
    }
    private void createOutputFile() {//withouterror

    }
}
