package de.dmxcontrol.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Qasi on 15.06.2014.
 */
public class ExecutorCollection implements Collection<EntityExecutor> {

    private ArrayList<EntityExecutor> list= new ArrayList<EntityExecutor>();

    public boolean add(EntityExecutor object) {
        if(object==null){return false;}
        if(!contains(object)) {
            return list.add(object);
        }
        else {
            try {

            EntityExecutor obj=list.get(indexOf(object));
            obj.setId(object.getId());
            obj.setName(object.getName(),true);
            obj.setImage(object.getImageName());
            obj.setValue(object.getValue(),true);
            obj.setFlash(object.getFlash(),true);
            }
            catch(Exception e){
                e.toString();
            }
        return false;
        }
    }

    public boolean addAll(int location, Collection<? extends EntityExecutor> collection) {
        return list.addAll(location, collection);
    }

    public boolean addAll(Collection<? extends EntityExecutor> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        if(object!=null)
        for (int i = 0; i <size() ; i++) {
            if(list.get(i)!=null)
            if(((EntityExecutor)object).guid.equals(list.get(i).guid)){return true;}
        }
        return false;
    }

    public boolean contains(String guid) {
        for (int i = 0; i <size() ; i++) {
            if(guid.equals(list.get(i).guid)){return true;}
        }
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    public EntityExecutor get(int location) {
        return list.get(location);
    }

    public EntityExecutor get(String guid) {
        for(EntityExecutor e:this.list) {
            if (e.guid.equals(guid)) {
                return e;
            }
        }
        return null;
    }

    public int indexOf(Object object) {
        for (int i = 0; i <size() ; i++) {
            if(((EntityExecutor)object).guid.equals(list.get(i).guid)){return i;}
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<EntityExecutor> iterator() {
        return null;
    }

    public int lastIndexOf(Object object) {
        int out=Integer.MIN_VALUE;
        for (int i = 0; i <size() ; i++) {
            if(((EntityExecutor)object).guid.equals(list.get(i).guid)){out = i;}
        }
        return out;
    }

    public EntityExecutor remove(int location) {
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
