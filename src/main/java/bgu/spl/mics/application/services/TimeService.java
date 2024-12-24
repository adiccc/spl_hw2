package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private int TickTime;
    private int Duration;
    private int Ticks;
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("Change_This_Name");
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.Ticks = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        while (Ticks < Duration) {
            Ticks++;
            MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast(Ticks));
            try {
                Thread.sleep(TickTime);
            } catch (InterruptedException e) {
            }
        }
        MessageBusImpl.getInstance().sendBroadcast(new TerminatedBroadcast(this));
    }
}
