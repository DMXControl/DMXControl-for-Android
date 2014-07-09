package de.dmxcontrol.file;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ImageWithKeyCollection implements Collection<ImageWithKey> {

    private String[] guids;

    private ArrayList<ImageWithKey> list = new ArrayList<ImageWithKey>();

    public void setGUIDsList(JSONArray a) throws JSONException {
        guids = new String[a.length()];

        for(int i = 0; i < a.length(); i++) {
            guids[i] = a.getString(i);
        }

        removeDeletedDevices();
    }

    public void removeDeletedDevices() {
        for(int i = 0; i < size(); i++) {
            if(!Arrays.asList(guids).contains(list.get(i).getKey())) {

                // Null out object. Maybe remove this. GC should do this alone
                ImageWithKey dev = list.get(i);
                if(dev != null) {
                    dev = null;
                    if(dev != null) {
                        dev = null;
                    }
                }

                // Remove device from ArrayList
                list.remove(i);
            }
        }
    }

    public boolean add(ImageWithKey object) {
        if(object == null) {
            return false;
        }
        if(!contains(object)) {
            return list.add(object);
        }
        return false;
    }

    public boolean addAll(int location, Collection<? extends ImageWithKey> collection) {
        return list.addAll(location, collection);
    }

    public boolean addAll(Collection<? extends ImageWithKey> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((ImageWithKey) object).getKey().equals(list.get(i).getKey())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String key) {
        for(int i = 0; i < size(); i++) {
            if(key.equals(list.get(i).getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public ImageWithKey get(int location) {
        if(location >= list.size()) {
            return null;
        }
        return list.get(location);
    }

    public ImageWithKey get(String key) {
        for(int i = 0; i < size(); i++) {
            if(key.equals(list.get(i).getKey())) {
                return list.get(i);
            }
        }
        return null;
    }

    public int indexOf(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((ImageWithKey) object).getKey().equals(list.get(i).getKey())) {
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
    public Iterator<ImageWithKey> iterator() {
        return null;
    }

    public int lastIndexOf(Object object) {
        int out = Integer.MIN_VALUE;
        for(int i = 0; i < size(); i++) {
            if(((ImageWithKey) object).getKey().equals(list.get(i).getKey())) {
                out = i;
            }
        }
        return out;
    }

    public ImageWithKey remove(int location) {
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
