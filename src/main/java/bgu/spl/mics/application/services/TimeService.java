package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private int TickTime;
    private int Duration;
    private int Ticks;
    private StatisticalFolder statFolder;
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration, StatisticalFolder statFolder) {
        super("timer");
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.Ticks = 1;
        this.statFolder = statFolder;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    public void initialize() { //was protected changed for tests
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast broadcast) -> {
            this.terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            if (broadcast.getTime() < Duration) {
                sendBroadcast(new TickBroadcast(Ticks));
                statFolder.setSystemRuntime(this.Ticks);
                try {
                    Thread.sleep(TickTime);
                } catch (InterruptedException e) {
                }
                System.out.println("---time tick " + Ticks);
            } else
                this.terminate();
            this.Ticks++;
        });
        MessageBusImpl.latch.countDown();
        sendBroadcast(new TickBroadcast(Ticks));
    }

}
