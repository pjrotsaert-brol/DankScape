package dankscape.misc;

/**
 * Created by Kyle on 11/16/2015.
 */
public class Timer {
    private long startTime;

    public Timer() {
        start();
    }

    public void start() {
        startTime = System.nanoTime() / 1000000;
    }

    public long ellapsed() {
        return (System.nanoTime() / 1000000) - startTime;
    }

}
