package bgu.spl.mics.application.messages;


import bgu.spl.mics.Broadcast;


public class TickBroadcast implements Broadcast {

    private long currentTick;
    private int speed;
    private long start;

    public long getStart() {
        return start;
    }

    public TickBroadcast(long tick, int speed, long s) {
        currentTick = tick;
        this.speed = speed;
        start = s;
    }

    public long getCurrentTick(){
        return currentTick;
    }

    public int getSpeed(){
        return speed;
    }

}
