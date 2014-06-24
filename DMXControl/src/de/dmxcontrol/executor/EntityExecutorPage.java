/*
 * EntityDevice.java
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

package de.dmxcontrol.executor;

import android.util.Log;

import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.DMXControlApplication;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.network.ResceivdData;
import de.dmxcontrol.network.UDP.Reader;
import de.dmxcontrol.network.UDP.Sender;

//This is One Executor
public class EntityExecutorPage extends Entity {
    public static int defaultIcon = R.drawable.device_new;
    private ArrayList<String>ExecutorGUIDs;

    public ExecutorCollection getExecutors() {
        ExecutorCollection Executors=new ExecutorCollection();
        for (int i = 0; i <ExecutorGUIDs.size() ; i++) {
            Executors.add(ResceivdData.get().Executors.get(ExecutorGUIDs.get(i)));
        }
        return Executors;
    }

    public static void SendAllRequest(){
        byte[] output=new byte[4];
        output[0]=(byte) Reader.Type.EXECUTORPAGE.ordinal();
        output[1]='A';
        output[2]='L';
        output[3]='L';
        Prefs.get().getUDPSender().addSendData(output);
    }

    public void setExecutorGUIDs(ArrayList<String> executorGUIDs) {
        this.ExecutorGUIDs = executorGUIDs;
    }

    public ArrayList<String> getExecutorGUIDs() {
        return ExecutorGUIDs;
    }

    public EntityExecutorPage(int id) {
        super(id, "ExecutorPage: " + id, Type.EXECUTOR);
        mImage = defaultIcon;
    }

    public EntityExecutorPage(int id, String name) {
        super(id, name, Type.EXECUTOR);
        mImage = defaultIcon;
    }

    public EntityExecutorPage(int id, String name, int image) {
        super(id, name, Type.EXECUTOR);
        mImage = image;
    }

    public EntityExecutorPage(int id, String name, String image) {
        super(id, name, Type.EXECUTOR);
        lImage = image;
    }

    public EntityExecutorPage(byte[] message) {
        super(0,"",Type.EXECUTOR);
        Receive(message);
    }

    public static Entity Receive(byte[] message) {
        int pointer = 1;

        String name = new String(message, pointer + 1, message[pointer]);
        pointer += message[pointer] + 1;

        String guid = new String(message, pointer + 1, message[pointer]);
        pointer += message[pointer] + 1;

        byte[] numberarray = new byte[message[pointer]];
        int number = 0;
        pointer++;
        for (int i = 0; i < numberarray.length; i++) {
            numberarray[i] = message[pointer];
            number += (10 ^ (numberarray.length - i)) * numberarray[i];
            pointer++;
        }

        String executorGuids = new String(message, pointer + 1, message[pointer]*36);
        pointer += message[pointer] + 1;


        EntityExecutorPage entity = new EntityExecutorPage(number, name);

        entity.ExecutorGUIDs=new ArrayList<String>();
        try {
            for (int i = 0; i < executorGuids.length() / 36; i++) {
                int start=i * 36;
                int end=i * 36 + 36;
                entity.ExecutorGUIDs.add(executorGuids.substring(start, end));
            }
        }catch (Exception e) {
            Log.w("",DMXControlApplication.stackTraceToString(e));
            DMXControlApplication.SaveLog();
        }
        entity.guid=guid;
        message=null;
        return entity;
    }

    public void Send() {
        byte[] output,
                name =this.getName().getBytes(),
                guid = this.guid.getBytes(),
                number = (this.getId()+"").getBytes();

        output = new byte[1 + 10 +
                name.length +
                guid.length +
                number.length];

        int position = 0;
        output[position] = (byte) Sender.Type.EXECUTOR.ordinal();
        position++;

        output[position] = (byte)name.length;
        position++;
        for (int i = 0; i < name.length; i++)
        {
            output[position] = name[i];
            position++;
        }

        output[position] = (byte)guid.length;
        position++;
        for (int i = 0; i < guid.length; i++)
        {
            output[position] = guid[i];
            position++;
        }

        output[position] = (byte)number.length;
        position++;
        for (int i = 0; i < number.length; i++)
        {
            output[position] = number[i];
            position++;
        }
        Prefs.get().getUDPSender().addSendData(output);
        output=null;
    }
}
