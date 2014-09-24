package de.dmxcontrol.device;

import de.dmxcontrol.network.ReceivedData;

/**
 * Created by Qasi on 24.09.2014.
 */
public class DeviceGroupWrapper {
    private static DeviceGroupWrapper INSTANCE = null;

    public static DeviceGroupWrapper get() {
        if(INSTANCE == null) {
            INSTANCE = new DeviceGroupWrapper();
        }
        return INSTANCE;
    }

    private EntityManager mEntityManager = null;
    private DeviceCollection devices;
    private EntityDevice wrappedDevice = null;

    public EntityDevice getWrappedDeviceFromSelection() {
        try {
            if(mEntityManager == null) {
                mEntityManager = EntityManager.get();
            }
            devices = new DeviceCollection();
            String models_vendor = null;
            boolean AllTheSameModels = true;
            for(int i = 0; i < ReceivedData.get().Devices.size(); i++) {
                if(mEntityManager.isInEntitySelection(EntityManager.Type.DEVICE, EntityManager.CENTRAL_ENTITY_SELECTION, ReceivedData.get().Devices.get(i).getId())) {
                    devices.add(ReceivedData.get().Devices.get(i));
                    if(models_vendor != null) {
                        if(!models_vendor.equals(ReceivedData.get().Devices.get(i).getModel() + "_" + ReceivedData.get().Devices.get(i).getVendor())) {
                            AllTheSameModels = false;
                        }
                    }
                    else {
                        models_vendor = ReceivedData.get().Devices.get(i).getModel() + "_" + ReceivedData.get().Devices.get(i).getVendor();
                    }
                }
            }
            switch(devices.size()) {
                case 0:
                    break;
                case 1:
                    wrappedDevice = (EntityDevice) devices.get(0).clone();
                    break;
                case 2:
                default:
                    if(AllTheSameModels) {
                        wrappedDevice = (EntityDevice) devices.get(0).clone();
                    }
                    else {
                        wrappedDevice = new EntityDevice();
                        for(int i = 0; i < devices.size(); i++) {
                            //device.;
                        }
                    }
                    break;
            }
            /**for(int i = 0; i < ReceivedData.get().Groups.size(); i++) {
             if(mEntityManager.isInEntitySelection(EntityManager.Type.GROUP,EntityManager.CENTRAL_ENTITY_SELECTION,ReceivedData.get().Groups.get(i).getId())){
             for(int j = 0; j < ReceivedData.get().Groups.get(i).; j++) {
             devices.add(ReceivedData.get().Devices.get(i));
             }
             }**/
            devices.clear();
            devices = null;
        }
        catch(Exception e) {
            e.getMessage();
        }
        return wrappedDevice;
    }

    public EntityDevice getLastWrappedDeviceFromSelection() {
        return wrappedDevice;

    }
}
