package de.dmxcontrol.network;

import de.dmxcontrol.device.DeviceCollection;
import de.dmxcontrol.device.GroupCollection;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.executor.ExecutorCollection;
import de.dmxcontrol.executor.ExecutorPageCollection;
import de.dmxcontrol.executor.ExecutorPageView;

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
    public final GroupCollection Groups = new GroupCollection();
    public final ExecutorCollection Executors = new ExecutorCollection();
    public final ExecutorPageCollection ExecutorPages = new ExecutorPageCollection();
    public EntityExecutorPage SelectedExecutorPage;
    public ExecutorPageView executorPageView;
    //ArrayList<EntityExecutor> Executors = new ArrayList<EntityExecutor>();
}
