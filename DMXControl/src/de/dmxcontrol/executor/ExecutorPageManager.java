package de.dmxcontrol.executor;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ExecutorPageManager {
    private static ExecutorPageManager INSTANCE = null;

    public static ExecutorPageManager get() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutorPageManager();
        }
        return INSTANCE;
    }
}
