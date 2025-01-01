package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.ErrorReport;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.ArrayList;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;
    private ErrorReport errorReport;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("fusion_slam");
        this.fusionSlam = fusionSlam;
        this.errorReport=new ErrorReport("noErrorDetected","",new ArrayList<>(),new ArrayList<>(),null);
    }


    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    public void initialize() {//was protected changed for tests
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {});
        subscribeEvent(TrackedObjectsEvent.class,(TrackedObjectsEvent t) -> fusionSlam.updateMap(t));
        subscribeEvent(PoseEvent.class,(PoseEvent t) -> fusionSlam.updatePose(t));
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast c) -> {
           handelTermination(c.getSender());
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            this.errorReport.setError(c.getErrorMessage());
            this.errorReport.setFualtySensor(c.getSender().getName());
            handelTermination(c.getSender());
        });
    }

    public void handelTermination(MicroService c){
        System.out.println("#-# hander terminated broadcast from "+c.getClass()+" , sensors left: "+fusionSlam.getNumberOfSensors());
        if (!c.getClass().equals(TimeService.class)) {
            System.out.println("#-# fustion got terminate form camera or lidar");
            if(c.getClass().equals(LiDarService.class)) {
                errorReport.addTrackedObject(((LiDarService)(c)).getLastTrackedObjects());
            }
            else if(c.getClass().equals(CameraService.class)) {
//                errorReport.addDetectedObject(((CameraService) (c)).getLastDetectedObjects());
            }
            fusionSlam.decreaseNumberOfSensors();
        }
        if (!fusionSlam.isLeftSensorOn()) {
            System.out.println("## No sensors found starting writing report to output file");
            this.terminate();
            fusionSlam.finish(errorReport);
        }
    }
}
