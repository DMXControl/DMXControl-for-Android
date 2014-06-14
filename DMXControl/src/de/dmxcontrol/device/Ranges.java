/*
 * Ranges.java
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
import java.util.List;
import java.util.Set;

import android.util.Log;

class Ranges {
    private final static String TAG = "device";

    private List<Range> mRanges = new ArrayList<Range>();
    private String mRangesString;

    void calcRanges(List<Entity> entities, Set<Entity> selectedEntities) {

        int i;
        mRanges.clear();

        boolean beginFound = false, endFound = false;
        int crrRangeBegin = 0, crrRangeEnd = 0;
        for (i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            if (!beginFound && selectedEntities.contains(entity)) {
                crrRangeBegin = entity.getId();
                beginFound = true;
            }

            if (beginFound && !selectedEntities.contains(entity)) {
                crrRangeEnd = entities.get(i - 1).getId();
                endFound = true;
            } else if (beginFound && (i + 1) == entities.size()) {
                crrRangeEnd = entity.getId();
                endFound = true;
            }

            if (beginFound && endFound) {
                beginFound = false;
                endFound = false;
                mRanges.add(new Range(crrRangeBegin, crrRangeEnd));
            }
        }

        calculateString(mRanges);
    }

    private void calculateString(List<Range> ranges) {
        StringBuilder sb = new StringBuilder();

        String appendConcat = "";
        for (Range range : ranges) {
            sb.append(appendConcat);
            sb.append(range.getReadableRange());
            appendConcat = "+";
        }

        mRangesString = sb.toString();
        Log.d(TAG, "ranges string = " + mRangesString);
    }

    public List<Range> get() {
        return mRanges;
    }

    public String getString() {
        return mRangesString;
    }

}
