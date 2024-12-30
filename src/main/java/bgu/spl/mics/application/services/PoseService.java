package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("Change_This_Name");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    public void initialize() {//was protected changed for tests
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast t) -> {
            PoseEvent p=new PoseEvent(gpsimu.getPose(t.getTime()));
            if(p!=null)
                sendEvent(p);
            else
                this.terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class,(CrashedBroadcast t) -> terminate());
        subscribeBroadcast(TerminatedBroadcast.class,(TerminatedBroadcast t) -> {
            if(t.getSender().getClass()==(TimeService.class)) {
                terminate();
            }
        });
    }
}
