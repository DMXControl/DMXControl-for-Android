package de.dmxcontrol.preset;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class PresetCollection implements Collection<EntityPreset> {

    private String[] guids;

    private ArrayList<EntityPreset> list = new ArrayList<EntityPreset>();

    public void setGUIDsList(JSONArray a) throws JSONException {
        guids = new String[a.length()];

        for(int i = 0; i < a.length(); i++) {
            guids[i] = a.getString(i);
        }
        a = null;
        if(a == null) {
            ;
        }

        removeDeletedDevices();

        compareGuidListWithInternalGuids();

        if(needSort) {
            sort();
        }
    }

    private boolean needSort = true;

    public void sort() {
        EntityPreset temp;
        for(int i = 1; i < list.size(); i++) {
            for(int j = 0; j < list.size() - i; j++) {
                if(list.get(j).getId() > list.get(j + 1).getId()) {
                    temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        temp = null;
        if(temp == null) {
            needSort = false;
        }
    }

    public void removeDeletedDevices() {
        for(int i = 0; i < size(); i++) {
            if(!Arrays.asList(guids).contains(list.get(i).guid)) {

                // Null out object. Maybe remove this. GC should do this alone
                EntityPreset dev = list.get(i);
                if(dev != null) {
                    dev = null;
                    if(dev != null) {
                        dev = null;
                    }
                }

                // Remove device from ArrayList
                list.remove(i);

                needSort = true;
            }
        }
    }

    public void compareGuidListWithInternalGuids() {
        for(int i = 0; i < guids.length; i++) {
            if(!contains(guids[i])) {
                EntityPreset.SendRequest(EntityPreset.Request_GUID + guids[i]);
            }
        }
    }

    public boolean add(EntityPreset object) {
        if(object == null) {
            return false;
        }
        if(!contains(object)) {
            needSort = true;
            return list.add(object);
        }
        else {
            try {
                EntityPreset obj = list.get(indexOf(object));
                obj.setId(object.getId());
                obj.setName(object.getName(), true);
                obj.setImage(object.getImageName());
            }
            catch(Exception e) {
                e.toString();
            }
            return false;
        }
    }

    public boolean addAll(int location, Collection<? extends EntityPreset> collection) {
        return list.addAll(location, collection);
    }

    public boolean addAll(Collection<? extends EntityPreset> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((EntityPreset) object).guid.equals(list.get(i).guid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public EntityPreset get(int location) {
        return list.get(location);
    }

    public int indexOf(Object object) {
        for(int i = 0; i < size(); i++) {
            if(((EntityPreset) object).guid.equals(list.get(i).guid)) {
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
    public Iterator<EntityPreset> iterator() {
        return null;
    }

    public int lastIndexOf(Object object) {
        int out = Integer.MIN_VALUE;
        for(int i = 0; i < size(); i++) {
            if(((EntityPreset) object).guid.equals(list.get(i).guid)) {
                out = i;
            }
        }
        return out;
    }

    public EntityPreset remove(int location) {
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
