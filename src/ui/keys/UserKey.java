/*
 * UserKey.java
 * 
 * Created on 14.09.2007, 10:42
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
//#ifdef USER_KEYS
package ui.keys;

import images.RosterIcons;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import ui.IconTextElement;
import ui.VirtualCanvas;

/**
 *
 * @author ad
 */
public class UserKey extends IconTextElement {
    
    public final static String storage="keys_db";
            
    public int commandId = 0;
    public boolean mKey = false;
    public int keyCode = 0;
    public boolean keyLong = false;
    public int secCode = 0;
    public boolean secLong = false;

    public UserKey() {
        super(RosterIcons.getInstance());
    }
    
    public String toString(){
        StringBuffer s=new StringBuffer();
        if (keyLong) s.append("Long ");
        s.append('(').append(getKeyName(keyCode)).append(')');
        if(mKey) {
            s.append('+');
            if (secLong) s.append("Long ");
            s.append('(').append(getKeyName(secCode)).append(')');
        }
        s.append(" -> ").append( UserActions.getActionsList( 1).elementAt( commandId));
        return s.toString();
    }
        
    public static Vector createListFromStorage() {
        Vector list=new Vector(0);
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return list;
        try {
            do {
                if (is.available()==0) {
                    break;
                }
                list.addElement(createFromDataInputStream(is));
            } while (true);
            is.close();
        } catch (Exception e) {
        }
        return list;
    } 
    
    public static UserKey createFromDataInputStream(DataInputStream inputStream){
        UserKey u=new UserKey();
        try {
            u.commandId = inputStream.readInt();
            u.mKey = inputStream.readBoolean();
            u.keyCode = inputStream.readInt();
            u.secCode = inputStream.readInt();
            u.keyLong = inputStream.readBoolean();
            u.secLong = inputStream.readBoolean();
        } catch (IOException e) { /*e.printStackTrace();*/ }
            
        return (u.keyCode==0)?null:u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeInt(commandId);
            outputStream.writeBoolean(mKey);
            outputStream.writeInt(keyCode);
            outputStream.writeInt(secCode);
            outputStream.writeBoolean(keyLong);
            outputStream.writeBoolean(secLong);
        } catch (IOException e) { }
    }

    public static String getKeyName(int keyCode){
        switch( keyCode){
            case VirtualCanvas.NAVIKEY_FIRE: return "Fire";
            case VirtualCanvas.NAVIKEY_UP: return "Up"; 
            case VirtualCanvas.NAVIKEY_DOWN: return"Down";
            case VirtualCanvas.NAVIKEY_LEFT: return"Left";
            case VirtualCanvas.NAVIKEY_RIGHT: return"Right";
            case VirtualCanvas.LEFT_SOFT: return"LSoft";
            case VirtualCanvas.RIGHT_SOFT: return"RSoft";
            case VirtualCanvas.CLEAR_KEY: return"Clear";
            case VirtualCanvas.CLOSE_KEY: return"Close";
            case VirtualCanvas.CALL_KEY: return"Call";
            case VirtualCanvas.CAMERA_KEY: return"Camera";
            case VirtualCanvas.ABC_KEY: return"ABC";
            case VirtualCanvas.VOLPLUS_KEY: return"VolUp";
            case VirtualCanvas.VOLMINUS_KEY: return"VolDown";
            case VirtualCanvas.UNUSED_KEY: return"Unused";
        }// switch
        if( keyCode >0x20 && keyCode <0x7f)
            return String.valueOf((char)keyCode);
        if( (keyCode&0xffff) >0xa020 && (keyCode&0xffff) <0xa07f)
            return String.valueOf((char)(keyCode&0x7f));
        if( (keyCode&0xffff) ==0xa020)
            return "Space";
        return "Key " + java.lang.Integer.toHexString(keyCode);
    }
}
//#endif

