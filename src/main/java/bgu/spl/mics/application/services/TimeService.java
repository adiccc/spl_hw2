package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private int TickTime;
    private int Duration;
    private int Ticks;
    private StatisticalFolder statFolder;
    private AtomicBoolean stopTime=new AtomicBoolean(false);
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in seconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration, StatisticalFolder statFolder) {
        super("timer");
//        this.TickTime = TickTime*1000;
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.Ticks = 1;
        this.statFolder = statFolder;
    }
    public void stopTime(){
        stopTime.set(true);
    }
    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    public void initialize() { //was protected changed for tests
        subscribeBroadcast(CrashedBroadcast.class,(CrashedBroadcast c)->this.terminate());
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            if(broadcast.getSender().getClass().equals(FusionSlamService.class))
                this.terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {
            if(this.terminated.get()||Thread.interrupted())
                return;
            if (broadcast.getTime() < Duration) {
                try {
                    Thread.sleep(TickTime);
                } catch (InterruptedException e) {
                }
                synchronized (this){
                    if(!stopTime.get()) {
                        sendBroadcast(new TickBroadcast(Ticks));
                        statFolder.setSystemRuntime(this.Ticks);
                        System.out.println("---time tick " + Ticks);
                    }
                    else{
                        this.terminate();
                    }
                    notifyAll();
                }
            }
            else
                this.terminate();
            this.Ticks++;
        });
        MessageBusImpl.latch.countDown();
        sendBroadcast(new TickBroadcast(Ticks));
        Ticks++;
    }
}
