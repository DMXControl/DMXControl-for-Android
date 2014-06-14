/*
 * EntitySelectionManager.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {
    private List<List<Entity>> mEntities = new ArrayList<List<Entity>>();
    private List<Map<Integer, Entity>> mLookups = new ArrayList<Map<Integer, Entity>>();
    private Map<Integer, EntitySelection> mEntitySelections = new HashMap<Integer, EntitySelection>();

    public enum Type {
        DEVICE, GROUP
    }

    ;

    public static final Integer CENTRAL_ENTITY_SELECTION = 0;
    private static EntityManager INSTANCE;

    public static EntityManager get() {
        if (INSTANCE == null)
            INSTANCE = new EntityManager();
        return INSTANCE;
    }

    private EntityManager() {
        mEntities.add(new ArrayList<Entity>());
        mEntities.add(new ArrayList<Entity>());

        mLookups.add(new HashMap<Integer, Entity>());
        mLookups.add(new HashMap<Integer, Entity>());

        int id = CENTRAL_ENTITY_SELECTION;
        EntitySelection entity = new EntitySelection(id, this);
        mEntitySelections.put(id, entity);
        createMockDevices();
    }

    private List<Entity> getEntities(Type type) {
        return mEntities.get(type.ordinal());
    }

    private Map<Integer, Entity> getLookup(Type type) {
        return mLookups.get(type.ordinal());
    }

    private void addEntity(Entity entity) {
        getEntities(entity.getType()).add(entity);
        getLookup(entity.getType()).put(entity.getId(), entity);
    }

    public int getSizeForType(Type type) {
        return getEntities(type).size();
    }

    public Entity getItemForType(Type type, int position) {
        return getEntities(type).get(position);
    }

    public boolean isInEntitySelection(Type type, int selectionsId, int entityId) {
        return mEntitySelections.get(selectionsId).contains(type, entityId);
    }

    private void createMockDevices() {
        EntityGroup groupZero = new EntityGroup(1);
        addEntity(groupZero);

        EntityGroup groupOne = new EntityGroup(2);
        addEntity(groupOne);
        EntityGroup groupTwo = new EntityGroup(3);
        addEntity(groupTwo);
        EntityGroup groupThree = new EntityGroup(4);
        addEntity(groupThree);

        addEntity(new EntityDevice(1, "", "Clay Paky Sharpy.png"));
        addEntity(new EntityDevice(2, "", "Clay Paky Sharpy Wash.png"));
        addEntity(new EntityDevice(3, "", "Clay Paky A_LEDA B-EYE K20.png"));
        addEntity(new EntityDevice(4, "", "Blinder 2x1.png"));
        addEntity(new EntityDevice(5, "", "Blinder 2x2.png"));
        addEntity(new EntityDevice(6, "", "Blinder 2x4.png"));
        addEntity(new EntityDevice(7, "", "Clay Paky CP Color MH AE.png"));
        addEntity(new EntityDevice(8, "", "Clay Paky Alpha Spot QWO 800.png"));
        addEntity(new EntityDevice(9, "", "mac700profile.png"));
        addEntity(new EntityDevice(10, ""));
        addEntity(new EntityDevice(11, ""));
        addEntity(new EntityDevice(12, ""));
        addEntity(new EntityDevice(13, ""));
        addEntity(new EntityDevice(14, ""));
        addEntity(new EntityDevice(15, ""));
        addEntity(new EntityDevice(16, ""));
        addEntity(new EntityDevice(17, ""));
        addEntity(new EntityDevice(18, ""));
        addEntity(new EntityDevice(19, ""));
        addEntity(new EntityDevice(20, ""));
        addEntity(new EntityDevice(21, ""));
        addEntity(new EntityDevice(22, ""));
        addEntity(new EntityDevice(23, ""));
        addEntity(new EntityDevice(24, ""));
        addEntity(new EntityDevice(25, ""));
        addEntity(new EntityDevice(26, ""));
        addEntity(new EntityDevice(27, ""));
        addEntity(new EntityDevice(28, ""));
        addEntity(new EntityDevice(29, ""));
        addEntity(new EntityDevice(30, ""));
        addEntity(new EntityDevice(31, ""));
        addEntity(new EntityDevice(32, ""));
        addEntity(new EntityDevice(33, ""));
        addEntity(new EntityDevice(34, ""));
        addEntity(new EntityDevice(35, ""));
        addEntity(new EntityDevice(36, ""));
        addEntity(new EntityDevice(37, ""));
        addEntity(new EntityDevice(38, ""));
        addEntity(new EntityDevice(39, ""));
        addEntity(new EntityDevice(40, ""));
        addEntity(new EntityDevice(41, ""));
        addEntity(new EntityDevice(42, ""));
    }

    public EntitySelection getEntitySelection(int id) {
        return mEntitySelections.get(id);
    }

    public int getMinId(Type type) {
        return 0;
    }

    public int getMaxId(Type type) {
        return getEntities(type).size();
    }

    Entity lookupEntity(Type type, int id) {
        return getLookup(type).get(id);
    }

    void createRanges(Type type, EntitySelection es) {
        List<Entity> entities = getEntities(type);
        es.createRanges(entities, type);
    }

}
