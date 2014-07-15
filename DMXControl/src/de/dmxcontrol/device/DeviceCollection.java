package de.dmxcontrol.device;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class DeviceCollection implements Collection<EntityDevice> {
    private ArrayList<ChangedListener> ChangedListeners = new ArrayList<ChangedListener>();

    public void setChangedListener(ChangedListener listener) {
        this.ChangedListeners.add(listener);
    }

    public void removeChangedListeners() {
        this.ChangedListeners.clear();
    }

    public interface ChangedListener {
        void onChanged();
    }

    private void runChangeListener() {
        for(ChangedListener listener : ChangedListeners) {
            listener.onChanged();
        }
    }

    private String[] guids;

    private ArrayList<EntityDevice> list = new ArrayList<EntityDevice>();

    public void setGUIDsList(JSONArray a) throws JSONException {
        guids = new String[a.length()];

        for(int i = 0; i < a.length(); i++) {
            guids[i] = a.getString(i);
        }
        a = null;
        if(a == null) {
            ;
        }

        boolean changed = removeDeletedDevices();

        compareGuidListWithInternalGuids();

        if(needSort) {
            changed = changed == true ? true : sort();
        }
        if(changed) {
            runChangeListener();
        }
    }

    private boolean needSort = true;

    public boolean sort() {
        boolean changed = false;
        EntityDevice temp;
        for(int i = 1; i < list.size(); i++) {
            for(int j = 0; j < list.size() - i; j++) {
                if(list.get(j).getId() > list.get(j + 1).getId()) {
                    temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    changed = true;
                }
            }
        }
        temp = null;
        if(temp == null) {
            needSort = false;
        }
        return changed;
    }

    public boolean removeDeletedDevices() {
        boolean changed = false;
        for(int i = 0; i < size(); i++) {
            if(!Arrays.asList(guids).contains(list.get(i).guid)) {

                // Null out object. Maybe remove this. GC should do this alone
                EntityDevice dev = list.get(i);
                if(dev != null){
                    dev = null;
                    if(dev != null){
                        dev = null;
                    }
                }

                // Remove device from ArrayList
                list.remove(i);

                changed = true;
                needSort = true;
            }
        }
        return changed;
    }

    public void compareGuidListWithInternalGuids() {
        for(int i = 0; i < guids.length; i++) {
            if(!contains(guids[i])) {
                EntityDevice.SendRequest(EntityDevice.Request_GUID + guids[i]);
            }
        }
    }

    public boolean add(EntityDevice object) {
        if(object == null) {
            return false;
        }
        if(!contains(object)) {
            needSort = true;

            boolean result = list.add(object);
            runChangeListener();
            return result;

        }
        else {
            EntityDevice obj = list.get(indexOf(object));
            obj.setId(object.getId());
            obj.setName(object.getName(), true);
            obj.setImage(object.getImageName());
            obj.setChannel(object.getChannel(), true);
            obj.setEnabled(object.getEnabled(), true);
            obj.setChannelCount(object.getChannelCount());
            return false;
        }
    }

    public boolean addAll(int location, Collection<? extends EntityDevice> collection) {
        return list.addAll(location, collection);
    }

    public boolean addAll(Collection<? extends EntityDevice> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((EntityDevice) object).guid.equals(list.get(i).guid)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String guid) {
        for(int i = 0; i < size(); i++) {
            if(guid.equals(list.get(i).guid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public EntityDevice get(int location) {
        if(location >= list.size() && location < 0) {
            return null;
        }
        return list.get(location);
    }

    public int indexOf(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((EntityDevice) object).guid.equals(list.get(i).guid)) {
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
    public Iterator<EntityDevice> iterator() {
        return null;
    }

    public int lastIndexOf(Object object) {
        int out = Integer.MIN_VALUE;
        for(int i = 0; i < size(); i++) {
            if(((EntityDevice) object).guid.equals(list.get(i).guid)) {
                out = i;
            }
        }
        return out;
    }

    public EntityDevice remove(int location) {
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
