package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
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
        this.Ticks = 0;
        this.statFolder = statFolder;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    public void initialize() {
        MessageBusImpl.latch.countDown();
        try {
            MessageBusImpl.latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //was protected changed for tests
        while (Ticks < Duration) {
            Ticks++;
            sendBroadcast(new TickBroadcast(Ticks));
            statFolder.setSystemRuntime(this.Ticks);
            try {
                Thread.sleep(TickTime);
            } catch (InterruptedException e) {}

            System.out.println("---time tick "+Ticks);
        }
        this.terminate();
    }

}
