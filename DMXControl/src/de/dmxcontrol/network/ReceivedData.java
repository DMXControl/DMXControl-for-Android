package de.dmxcontrol.network;

import java.util.ArrayList;

import de.dmxcontrol.device.DeviceCollection;
import de.dmxcontrol.device.EntityGroup;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.executor.ExecutorCollection;
import de.dmxcontrol.executor.ExecutorPageCollection;
import de.dmxcontrol.executor.ExecutorPageView;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ReceivedData
    {

    private static ReceivedData INSTANCE = null;
    public static ReceivedData get() {
        if (INSTANCE == null) {
            INSTANCE = new ReceivedData();
        }
        return INSTANCE;
    }
    public final DeviceCollection Devices = new DeviceCollection();
    ArrayList<EntityGroup> Groups = new ArrayList<EntityGroup>();
    public final ExecutorCollection Executors = new ExecutorCollection();
    public final ExecutorPageCollection ExecutorPages = new ExecutorPageCollection();
    public EntityExecutorPage SelectedExecutorPage;
    public ExecutorPageView executorPageView;
    //ArrayList<EntityExecutor> Executors = new ArrayList<EntityExecutor>();
}
