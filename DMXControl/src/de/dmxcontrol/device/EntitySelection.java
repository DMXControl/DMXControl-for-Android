/*
 * EntitySelection.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2012 DMXControl-For-Android. All rights reserved.
 *
 *      This software is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU General Public License
 *      as published by the Free Software Foundation; either
 *      version 3, june 2007 of the License, or (at your option) any later version.
 *
 *      This software is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *      General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public
 *      License (gpl.txt) along with this software; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *      For further information, please contact info [(at)] dmxcontrol.de
 *
 * 
 */

package de.dmxcontrol.device;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.model.BaseModel;
import de.dmxcontrol.model.BaseModel.OnModelListener;
import de.dmxcontrol.model.ModelManager;

public class EntitySelection implements OnModelListener {
    public final static String TAG = "entityselection";

    private Integer mId;
    private List<Set<Entity>> mEntities = new ArrayList<Set<Entity>>();
    private List<Map<Integer, Entity>> mLookups = new ArrayList<Map<Integer, Entity>>();
    private List<Ranges> mRanges = new ArrayList<Ranges>();

    private EntityManager mEm;

    private Pattern delim = Pattern.compile("\\+");
    private Pattern star = Pattern.compile("\\*");
    private Pattern span = Pattern.compile("\\d+-\\d+");
    private Pattern spanOpen = Pattern.compile("\\d+-\\*?");

    public interface OnEntitySelectionListener {
        public void onEntitySelectionChanged(EntitySelection es);
    }

    private ModelManager mModelManager;
    private Map<OnEntitySelectionListener, Boolean> mListeners;

    public EntitySelection(Integer id, EntityManager em) {
        mId = id;
        mEm = em;
        mListeners = new WeakHashMap<OnEntitySelectionListener, Boolean>();
        mModelManager = new ModelManager(this);
        mEntities.add(new HashSet<Entity>());
        mEntities.add(new HashSet<Entity>());
        mEntities.add(new HashSet<Entity>());
        mLookups.add(new HashMap<Integer, Entity>());
        mLookups.add(new HashMap<Integer, Entity>());
        mLookups.add(new HashMap<Integer, Entity>());
        mRanges.add(new Ranges());
        mRanges.add(new Ranges());
        mRanges.add(new Ranges());

    }

    public EntityManager getEntityManager() {
        return mEm;
    }

    public ModelManager getModelManager() {
        return mModelManager;
    }

    public int getSelectionId() {
        return mId;
    }

    public <T extends BaseModel> T getModel(ModelManager.Type type) {
        return mModelManager.getModel(type);
    }

    private Set<Entity> getEntities(Type t) {
        return mEntities.get(t.ordinal());
    }

    private Map<Integer, Entity> getLookup(Type t) {
        return mLookups.get(t.ordinal());
    }


    public void addEntity(Entity entity) {
        Type t = entity.getType();

        getEntities(t).add(entity);
        getLookup(t).put(entity.getId(), entity);
        updateRanges(t);
        notifyListener();
    }

    public void removeEntity(Entity entity) {
        Type t = entity.getType();

        getEntities(t).remove(entity);
        getLookup(t).remove(entity.getId());
        updateRanges(t);
        notifyListener();
    }

    public void toogleEntity(Entity entity) {
        Set<Entity> entities = getEntities(entity.getType());
        Map<Integer, Entity> lookup = getLookup(entity.getType());

        if(entities.contains(entity)) {
            entities.remove(entity);
            lookup.remove(entity.getId());
        }
        else {
            entities.add(entity);
            lookup.put(entity.getId(), entity);
        }

        updateRanges(entity.getType());
        notifyListener();
    }

    public void clearEntities(Type type) {
        getEntities(type).clear();
        getLookup(type).clear();

        updateRanges(type);
        notifyListener();
    }


    public boolean contains(Type type, int id) {
        return getLookup(type).containsKey(id);
    }


    private Ranges getRanges(Type t) {
        return mRanges.get(t.ordinal());
    }

    private void updateRanges(Type t) {
        mEm.createRanges(t, this);
    }

    public List<Range> getRangesList(Type type) {
        return getRanges(type).get();
    }

    public String getRangesString(Type type) {
        return getRanges(type).getString();
    }

    void createRanges(List<Entity> entities, Type type) {
        getRanges(type).calcRanges(entities, getEntities(type));
    }


    public void addListener(OnEntitySelectionListener listener) {
        mListeners.put(listener, true);
    }

    public void removeListener(OnEntitySelectionListener listener) {
        mListeners.remove(listener);
    }

    public void notifyListener() {
        Iterator<OnEntitySelectionListener> iter = mListeners.keySet().iterator();

        while(iter.hasNext()) {
            OnEntitySelectionListener listener = iter.next();
            listener.onEntitySelectionChanged(this);
        }
    }

    @Override
    public void onModelChanged(BaseModel model) {
        for(Set<Entity> entities : mEntities) {
            for(Entity e : entities) {
                e.setProperty(model.getOSCAttributeName(), model.getOSCAttributes());
            }
        }
    }

    public void parse(Type type, String entityString) {
        Set<Integer> result = findMatching(entityString, mEm.getMinId(type), mEm.getMaxId(type));

        clearEntities(type);
        Log.d(TAG, "entityselection: type = " + type + " entites " + getEntities(type).size());

        for(Integer id : result) {
            Entity e = mEm.lookupEntity(type, id);

            if(e == null) {
                continue;
            }

            getEntities(type).add(e);
            getLookup(type).put(e.getId(), e);
        }

        updateRanges(type);
        notifyListener();

        Log.d(TAG, "entityselection: type = " + type + " entites " + getEntities(type).size());
    }

    private Set<Integer> findMatching(String matchString, int begin, int end) {
        Log.d(TAG, "findMatching: matchString = *" + matchString + "*");
        Scanner scan = new Scanner(matchString);
        scan.useDelimiter(delim);
        Set<Integer> resultSet = new HashSet<Integer>();

        int beginSpan = -1, endSpan = -1;

        while(scan.hasNextInt() || scan.hasNext(span) || scan.hasNext(spanOpen) || scan.hasNext(star)) {
            if(scan.hasNext(star)) {
                scan.next(star);
                addToResultSet(begin, end, resultSet);
                Log.d(TAG, "findMatching: star found");
            }
            else if(scan.hasNext(span)) {
                String spanStrings[] = scan.next(span).split("-");
                beginSpan = Integer.valueOf(spanStrings[0]);
                endSpan = Integer.valueOf(spanStrings[1]);
                addToResultSet(beginSpan, endSpan, resultSet);
                Log.d(TAG, "findMatching: span found");
            }
            else if(scan.hasNext(spanOpen)) {
                String spanStrings[] = scan.next(spanOpen).split("-");
                beginSpan = Integer.valueOf(spanStrings[0]);
                endSpan = end;
                addToResultSet(beginSpan, endSpan, resultSet);
            }
            else if(scan.hasNextInt()) {
                resultSet.add(scan.nextInt());
                Log.d(TAG, "findMatching: num found");
            }
        }

        scan.close();

        Log.d(TAG, " Done: " + setToString(resultSet));
        return resultSet;
    }

    private void addToResultSet(int begin, int end, Set<Integer> resultSet) {
        int index = begin;

        while(index <= end) {
            if(index >= begin && index <= end) {
                resultSet.add(index);
            }
            index++;
        }
    }

    private String setToString(Set<Integer> set) {
        StringBuilder sb = new StringBuilder();
        Iterator<Integer> iter = set.iterator();

        while(iter.hasNext()) {
            sb.append(iter.next() + " ");
        }

        return sb.toString();
    }

}
