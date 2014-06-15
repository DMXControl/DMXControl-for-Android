package de.dmxcontrol.network;

import java.util.ArrayList;

import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.DeviceCollection;
import de.dmxcontrol.device.EntityDevice;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.executor.EntityExecutor;
import de.dmxcontrol.executor.ExecutorCollection;
import de.dmxcontrol.executor.ExecutorPage;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ResceivdData {

    private static ResceivdData INSTANCE = null;
    public static ResceivdData get() {
        if (INSTANCE == null) {
            INSTANCE = new ResceivdData();
        }
        return INSTANCE;
    }
    public final DeviceCollection Devices = new DeviceCollection();
    ArrayList<EntityGroup> Groups = new ArrayList<EntityGroup>();
    public final ExecutorCollection Executors = new ExecutorCollection();
    public ExecutorPage executorPage;
    //ArrayList<EntityExecutor> Executors = new ArrayList<EntityExecutor>();
}
