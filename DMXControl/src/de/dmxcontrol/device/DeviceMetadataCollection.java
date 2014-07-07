package de.dmxcontrol.device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class DeviceMetadataCollection implements Collection<DeviceMetadata> {

    public class VendorCollection {
        private String vendor;
        private ArrayList<DeviceMetadata> list = new ArrayList<DeviceMetadata>();

        public VendorCollection(String vendor, DeviceMetadata deviceMetadata) {
            this.vendor = vendor;
            this.list.add(deviceMetadata);
        }

        public String getVendor() {
            return this.vendor;
        }

        public DeviceMetadata getDeviceMetadata(int index) {
            return list.get(index);
        }

        public void addDeviceMetadata(DeviceMetadata deviceMetadata) {
            this.list.add(deviceMetadata);
        }

        public int getCount() {
            return list.size();
        }
    }

    private ArrayList<DeviceMetadata> list = new ArrayList<DeviceMetadata>();

    private ArrayList<VendorCollection> listVendors = new ArrayList<VendorCollection>();

    public Object getVendors() {
        return listVendors;
    }

    public void FillByJSON(JSONObject o) throws JSONException {
        this.clear();
        JSONArray a = o.getJSONArray("Devices");
        for(int i = 0; i < a.length(); i++) {
            DeviceMetadata device = new DeviceMetadata(a.getJSONObject(i));
            VendorCollection col = this.containsVendor(device.getVendor());
            if(col == null) {

                listVendors.add(new VendorCollection(device.getVendor(), device));
            }
            else {
                col.addDeviceMetadata(device);
            }
            this.list.add(device);
        }
    }

    private VendorCollection containsVendor(String obj) {
        for(int i = 0; i < listVendors.size(); i++) {
            if(listVendors.get(i).vendor.equals(obj)) {
                return listVendors.get(i);
            }
        }
        return null;
    }

    public boolean add(DeviceMetadata object) {
        if(object == null) {
            return false;
        }
        if(!contains(object)) {
            return list.add(object);
        }
        return false;
    }

    public boolean addAll(int location, Collection<? extends DeviceMetadata> collection) {
        return list.addAll(location, collection);
    }

    public boolean addAll(Collection<? extends DeviceMetadata> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
        listVendors.clear();
    }

    @Override
    public boolean contains(Object object) {
        return false;
    }

    public boolean contains(String guid) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public DeviceMetadata get(int location) {
        if(location >= list.size()) {
            return null;
        }
        return list.get(location);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<DeviceMetadata> iterator() {
        return null;
    }

    public DeviceMetadata remove(int location) {
        return list.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return list.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return list.toArray(array);
    }
}