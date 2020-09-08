package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/** A broadcast messages that is sent at every passed clock tick. This message must contain the current tick (int).
 *
 */

// Needs to have priority on all the other messages?

public class TickBroadcast implements Broadcast {
    private int tick;


    public TickBroadcast(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }
}

