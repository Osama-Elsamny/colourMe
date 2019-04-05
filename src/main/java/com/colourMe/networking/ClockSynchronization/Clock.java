package com.colourMe.networking.ClockSynchronization;

public class Clock extends Thread {
    private volatile long clock;

    public Clock() { this.clock = System.currentTimeMillis(); }

    public long getTime() { return clock; }

    public void setTime (long time) { this.clock = time; }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clock++;
        }
    }
}
