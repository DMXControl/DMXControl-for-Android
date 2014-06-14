/*
 * Range.java
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

public class Range {
    private String mOscRange;
    private String mReadableRange;

    public Range(int begin, int end) {
        if (end > begin) {
            mOscRange = String.format("[%d-%d]", begin, end);
            mReadableRange = String.format("%d-%d", begin, end);
        } else {
            mOscRange = String.format("[%d]", begin);
            mReadableRange = String.format("%d", begin);
        }
    }

    public String getOSCRange() {
        return mOscRange;
    }

    public String getReadableRange() {
        return mReadableRange;
    }

}