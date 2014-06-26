/*
 * Changelog.java
 *
 *  DMXControl for Android
 *
 *  Copyright (c) 2011 DMXControl-For-Android. All rights reserved.
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

package de.dmxcontrol.changelog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.Context;

public class Changelog {

    private ArrayList<Release> mReleases = new ArrayList<Release>();

    private XMLParsing mXMLParsing;
    private IItemsUpdateListener mUpdateListener;
    private ChangelogListAdapter ila;
    private Context mCtx;
    private Set<Integer> types = new HashSet<Integer>();

    public Changelog(Context ctx) {
        mXMLParsing = new XMLParsing();
        mCtx = ctx;
    }

    public void setContext(Context ctx) {
        mCtx = ctx;
    }

    public void setUpdateListener(IItemsUpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public ChangelogListAdapter getChangelogListAdapter() {
        if(ila == null) {
            ila = new ChangelogListAdapter(mCtx, this);
        }
        return ila;
    }

    public void setReleases(ArrayList<Release> items) {
        mReleases = items;
    }

    public void addRelease(Release item) {
        mReleases.add(item);
        types.add(mReleases.size());
    }

    public Release getRelease(int index) {
        if(index < 0 || index >= mReleases.size()) {
            throw new IndexOutOfBoundsException();
        }
        return mReleases.get(index);
    }

    public int getSize() {
        return mReleases.size();
    }

    public void clear() {
        mReleases.clear();
        if(mUpdateListener != null) {
            mUpdateListener.notifyUpdate();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Release r : mReleases) {
            sb.append(r.toString());
        }
        return sb.toString();
    }

    public void populateItems(Context ctx) throws SAXException,
            ParserConfigurationException, IOException {
        ArrayList<Release> res = mXMLParsing.parse(ctx);
        for(int i = 0; i < res.size(); i++) {
            this.addRelease(res.get(i));
        }
        if(mUpdateListener != null) {
            mUpdateListener.notifyUpdate();
        }
    }

    public int getTypeCount() {
        return types.size();
    }

}
