package de.dmxcontrol.device;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class DevicePropertyCollection implements Collection<DeviceProperty> {

    public static String NetworkID = "Propertys";
    private ArrayList<DeviceProperty> list = new ArrayList<DeviceProperty>();

    public DevicePropertyCollection(JSONObject o) throws Exception {
        if(!o.has("Type")) {
            throw new Exception("Type not found!");
        }

        if(o.get(NetworkID) == null) {
            throw new Exception("Type isn't " + NetworkID);
        }

        if(o.has(NetworkID)) {
            JSONArray array = o.getJSONArray(NetworkID);

            for(int i = 0; i < array.length(); i++) {
                list.add(new DeviceProperty(array.getJSONObject(i)));
            }
        }
    }

    public boolean add(DeviceProperty object) {
        if(object == null) {
            return false;
        }
        if(!contains(object)) {
            return list.add(object);
        }
        else {
            DeviceProperty obj = list.get(indexOf(object));
            obj.setValue(object.getValue());
            return false;
        }
    }

    public boolean addAll(int location, Collection<? extends DeviceProperty> collection) {
        return list.addAll(location, collection);
    }

    public boolean addAll(Collection<? extends DeviceProperty> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((DeviceProperty) object).getGUID().equals(list.get(i).getGUID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public DeviceProperty get(int location) {
        if(location >= list.size()) {
            return null;
        }
        return list.get(location);
    }

    public int indexOf(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((DeviceProperty) object).getGUID().equals(list.get(i).getGUID())) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<DeviceProperty> iterator() {
        return null;
    }

    public int lastIndexOf(Object object) {
        int out = Integer.MIN_VALUE;
        for(int i = 0; i < size(); i++) {
            if(((DeviceProperty) object).getGUID().equals(list.get(i).getGUID())) {
                out = i;
            }
        }
        return out;
    }

    public DeviceProperty remove(int location) {
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
