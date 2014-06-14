/*
 * XMLDescriptionHandler.java
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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDescriptionHandler extends DefaultHandler {
    private ArrayList<Release> mReleasesParsed;

    private Release mCrrRelease;
    private String mCrrVersion;
    private String mCrrItem;

    private boolean mChangelogTag = false;
    private boolean mReleaseTag = false;
    private boolean mVersionTag = false;
    private boolean mItemTag = false;

    public ArrayList<Release> getReleasesParsed() {
        return mReleasesParsed;
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startDocument() throws SAXException {
        mReleasesParsed = new ArrayList<Release>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        localName = localName.toLowerCase();
        if (localName.equals("changelog"))
            mChangelogTag = true;
        else if (localName.equals("release"))
            mReleaseTag = true;
        else if (localName.equals("version"))
            mVersionTag = true;
        else if (localName.equals("item"))
            mItemTag = true;
        else
            throw new SAXException("Unknown Tag: " + localName);

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        localName = localName.toLowerCase();
        if (localName.equals("changelog"))
            mChangelogTag = false;
        else if (localName.equals("release")) {
            mReleasesParsed.add(mCrrRelease);
            mReleaseTag = false;
        } else if (localName.equals("version")) {
            mCrrRelease = new Release(mCrrVersion);
            mVersionTag = false;
        } else if (localName.equals("item")) {
            mCrrRelease.add(mCrrItem);
            mItemTag = false;
        } else
            throw new SAXException("Unknown Tag: " + localName);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (!mChangelogTag || !mReleaseTag)
            return;

        if (mVersionTag)
            mCrrVersion = new String(ch, start, length);
        else if (mItemTag)
            mCrrItem = new String(ch, start, length);
    }
}
