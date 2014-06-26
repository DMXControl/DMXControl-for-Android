package de.dmxcontrol.network;

import de.dmxcontrol.cuelist.CuelistCollection;
import de.dmxcontrol.device.DeviceCollection;
import de.dmxcontrol.device.GroupCollection;
import de.dmxcontrol.executor.EntityExecutorPage;
import de.dmxcontrol.executor.ExecutorCollection;
import de.dmxcontrol.executor.ExecutorPageCollection;
import de.dmxcontrol.executor.ExecutorPageView;
import de.dmxcontrol.preset.PresetCollection;

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
    public final GroupCollection Groups = new GroupCollection();
    public final ExecutorCollection Executors = new ExecutorCollection();
    public final ExecutorPageCollection ExecutorPages = new ExecutorPageCollection();
    public final PresetCollection Presets = new PresetCollection();
    public final CuelistCollection Cuelists = new CuelistCollection();

    public EntityExecutorPage SelectedExecutorPage;
    public ExecutorPageView executorPageView;
}
