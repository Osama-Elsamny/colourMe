package com.colourMe.networking.ClockSynchronization;

public class Clock extends Thread {
    private volatile long clock;

    public Clock() { this.clock = System.currentTimeMillis(); }

    public synchronized long getTime() { return clock; }

    public synchronized void setTime(long time) { this.clock = time; }

    public synchronized void increment() { this.clock++; }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            increment();
        }
    }
}
