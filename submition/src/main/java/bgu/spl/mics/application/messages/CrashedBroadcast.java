package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {
    private MicroService sender;
    private String errorMessage;

    public CrashedBroadcast(MicroService sender, String errorMessage) {
        this.sender = sender;
        this.errorMessage = errorMessage;
    }

    public MicroService getSender() {
        return sender;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
