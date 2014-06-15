package de.dmxcontrol.device;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class DeviceCollection implements Collection<EntityDevice> {

    private ArrayList<EntityDevice> list= new ArrayList<EntityDevice>();

    public boolean add(EntityDevice object) {
        if(!contains(object)) {
            return list.add(object);
        }
        else {
            EntityDevice obj=list.get(indexOf(object));
            obj.setId(object.getId());
            obj.setName(object.getName());
            obj.setImage(object.getBitmapFileName());
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
        for (int i = 0; i <size() ; i++) {
            if(((EntityDevice)object).guid.equals(list.get(i).guid)){return true;}
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public EntityDevice get(int location) {
        return list.get(location);
    }

    public int indexOf(Object object) {
        for (int i = 0; i <size() ; i++) {
            if(((EntityDevice)object).guid.equals(list.get(i).guid)){return i;}
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
        int out=Integer.MIN_VALUE;
        for (int i = 0; i <size() ; i++) {
            if(((EntityDevice)object).guid.equals(list.get(i).guid)){out = i;}
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
