/*
 * XMLParsing.java
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

import de.dmxcontrol.android.R;

public class XMLParsing {

    public ArrayList<Release> parse(Context ctx) throws SAXException,
            ParserConfigurationException, IOException {

        Locale l = ctx.getResources().getConfiguration().locale;

        InputStream in;
        if(l.toString().startsWith("de")) {
            in = ctx.getResources().openRawResource(R.raw.de_changelog);
        }
        else {
            in = ctx.getResources().openRawResource(R.raw.en_changelog);
        }

		/* Get a SAXParser from the SAXPArserFactory. */
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

		/* Get the XMLReader of the SAXParser we created. */
        XMLReader xr = sp.getXMLReader();

		/* Create a new ContentHandler and apply it to the XML-Reader */
        XMLDescriptionHandler xmlDescHandler = new XMLDescriptionHandler();
        xr.setContentHandler(xmlDescHandler);
        InputSource inSource = new InputSource(in);
        xr.parse(inSource);

        ArrayList<Release> itemList = xmlDescHandler.getReleasesParsed();
        return itemList;
    }
}
