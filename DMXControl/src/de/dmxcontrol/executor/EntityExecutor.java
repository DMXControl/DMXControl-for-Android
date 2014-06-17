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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import de.dmxcontrol.android.R;
import de.dmxcontrol.app.Prefs;
import de.dmxcontrol.device.Entity;
import de.dmxcontrol.device.EntityManager.Type;
import de.dmxcontrol.network.UDP.Sender;

//This is One Executor
public class EntityExecutor extends Entity {
    public static int defaultIcon = R.drawable.device_new;
    private float value;
    private boolean toggle;
    private boolean doGO;
    private boolean doStop;
    private boolean doBreakBack;
    private boolean flash;

    private ArrayList<ValueChangedListener> ValueChangedListeners = new ArrayList<ValueChangedListener>();

    public void setValueChangedListener(ValueChangedListener listener) {
        this.ValueChangedListeners.add(listener);
    }

    public interface ValueChangedListener {
        void onValueChanged(float value);
    }

    private ArrayList<FlashChangedListener> FlashChangedListeners = new ArrayList<FlashChangedListener>();

    public void setFlashChangedListener(FlashChangedListener listener) {
        this.FlashChangedListeners.add(listener);
    }

    public interface FlashChangedListener {
        void onFlashChanged(float value);
    }

    public void BreakBack() {
        doBreakBack=true;
        Send();
    }
    public void GO() {
        doGO=true;
        Send();
    }
    public void Stop(){
        doStop=true;
        Send();}

    public boolean getToggle() {
        return toggle;
    }

    public EntityExecutor(int id) {
        super(id, "Executor: " + id, Type.EXECUTOR);
        mImage = defaultIcon;
    }

    public EntityExecutor(int id, String name) {
        super(id, name, Type.EXECUTOR);
        mImage = defaultIcon;
    }

    public EntityExecutor(int id, String name, int image) {
        super(id, name, Type.EXECUTOR);
        mImage = image;
    }

    public EntityExecutor(int id, String name, String image) {
        super(id, name, Type.EXECUTOR);
        lImage = image;
    }

    public EntityExecutor(byte[]  message) {
        super(0,"",Type.EXECUTOR);
        Receive(message);
    }

    public static Entity Receive(byte[] message) {
        int pointer = 1;

        String name = new String(message, pointer + 1, message[pointer]);
        pointer += message[pointer] + 1;

        String guid = new String(message, pointer + 1, message[pointer]);
        pointer += message[pointer] + 1;

        String svalue = new String(message, pointer + 1, message[pointer]);
        pointer += message[pointer] + 1;

        String flash = new String(message, pointer + 1, message[pointer]);
        pointer += message[pointer] + 1;

        byte[] numberarray = new byte[message[pointer]];
        int number = 0;
        pointer++;
        for (int i = 0; i < numberarray.length; i++) {
            numberarray[i] = message[pointer];
            number += (10 ^ (numberarray.length - i)) * numberarray[i];
            pointer++;
        }

        EntityExecutor entity = new EntityExecutor(number, name);
        entity.guid=guid;
        entity.value=Float.parseFloat(svalue.replace(",", "."));
        entity.flash=Boolean.parseBoolean(flash);
        return entity;
    }

    public void Send() {
        byte[] output,
                name =this.getName().getBytes(),
                guid = this.guid.getBytes(),
                value =(this.value+"").getBytes(),
                flash = (this.flash+"").replace("f","F").replace("t","T").getBytes(),
                number = (this.getId()+"").getBytes(),
                GO = (this.doGO+"").replace("f","F").replace("t","T").getBytes(),
                BREAKBACK = (this.doBreakBack+"").replace("f","F").replace("t", "T").getBytes(),
                STOP = (this.doStop+"").replace("f","F").replace("t","T").getBytes(),
                BREAK = (false+"").replace("f","F").replace("t","T").getBytes(),
                BACK = (false+"").replace("f","F").replace("t","T").getBytes();
        this.doGO=false;
        this.doBreakBack=false;
        this.doStop=false;

        output = new byte[1 + 10 +
                name.length +
                guid.length +
                value.length +
                flash.length +
                number.length+
                GO.length+
                BREAKBACK.length+
                STOP.length+
                BREAK.length+
                BACK.length];

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

        output[position] = (byte)value.length;
        position++;
        for (int i = 0; i < value.length; i++)
        {
            output[position] = value[i];
            position++;
        }

        output[position] = (byte)flash.length;
        position++;
        for (int i = 0; i < flash.length; i++)
        {
            output[position] = flash[i];
            position++;
        }

        output[position] = (byte)number.length;
        position++;
        for (int i = 0; i < number.length; i++)
        {
            output[position] = number[i];
            position++;
        }

        output[position] = (byte)GO.length;
        position++;
        for (int i = 0; i < GO.length; i++)
        {
            output[position] = GO[i];
            position++;
        }

        output[position] = (byte)BREAKBACK.length;
        position++;
        for (int i = 0; i < BREAKBACK.length; i++)
        {
            output[position] = BREAKBACK[i];
            position++;
        }

        output[position] = (byte)STOP.length;
        position++;
        for (int i = 0; i < STOP.length; i++)
        {
            output[position] = STOP[i];
            position++;
        }

        output[position] = (byte)BREAK.length;
        position++;
        for (int i = 0; i < BREAK.length; i++)
        {
            output[position] = BREAK[i];
            position++;
        }

        output[position] = (byte)BACK.length;
        position++;
        for (int i = 0; i < BACK.length; i++)
        {
            output[position] = BACK[i];
            position++;
        }

        Prefs.get().getUDPSender().addSendData(output);
    }

    public void setValue(float value,boolean fromReader) {
        int comp = Float.compare(this.value, value);
        boolean isEqual = 0 == comp;
        this.value = value;
        if(!isEqual&&fromReader) {
            for (ValueChangedListener listener : ValueChangedListeners) {
                listener.onValueChanged(value);
            }
            return;
        }
        else if(!isEqual&&!fromReader)
            Send();
    }

    public float getValue() {
        return value;
    }
    public void setFlash(boolean flash,boolean fromReader) {
        boolean isEqual=this.flash==flash;
        this.flash = flash;
        if(!isEqual&&fromReader) {
            for (FlashChangedListener listener : FlashChangedListeners) {
                listener.onFlashChanged(value);
            }
            return;
        }
        if(!isEqual&&!fromReader)
        Send();
        //Prefs.get().getUDPSender().addSendData(new byte[]{(byte)0xff});
        //Send();
    }

    public boolean getFlash() {
        return flash;
    }
}
