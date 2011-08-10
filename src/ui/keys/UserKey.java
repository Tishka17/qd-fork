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
import locale.SR;
import ui.IconTextElement;
import ui.VirtualCanvas;

/**
 *
 * @author ad
 */
public class UserKey extends IconTextElement {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif
    
    public final static String storage="keys_db";
            
    public int    commandId = 0;
    public int    keyCode   = 0;

    public UserKey() {
        super(RosterIcons.getInstance());
    }
    
    public static UserKey loadUserKey(int index){
        UserKey u=UserKey.createFromStorage(index);
        return u;
    }
    
    public String toString(){
        StringBuffer s=new StringBuffer("(").append(getKeyName(keyCode)).append(") -> ").append(COMMANDS_DESC[commandId]);
        return s.toString();
    }
    
    public static UserKey createFromStorage(int index) {
        UserKey u=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {u=null; break;}
                u=createFromDataInputStream(is);
                index--;
            } while (index>-1);
            is.close();
        } catch (Exception e) { }
        return u;
    }    
    
    public static UserKey createFromDataInputStream(DataInputStream inputStream){
        UserKey u=new UserKey();
        try {
            u.commandId  = inputStream.readInt();
            u.keyCode        = inputStream.readInt();
        } catch (IOException e) { /*e.printStackTrace();*/ }
            
        return (u.keyCode==-1)?null:u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeInt(commandId);
            outputStream.writeInt(keyCode);
        } catch (IOException e) { }
    }

    public static final String[] COMMANDS_DESC = {
            SR.get(SR.MS_NO),
            SR.get(SR.MS_OPTIONS),
            SR.get(SR.MS_CLEAN_ALL_MESSAGES),
            SR.get(SR.MS_RECONNECT),
            SR.get(SR.MS_STATS),
            SR.get(SR.MS_STATUS),
            SR.get(SR.MS_FILE_TRANSFERS),
            SR.get(SR.MS_ARCHIVE),
            SR.get(SR.MS_DISCO),
            SR.get(SR.MS_PRIVACY_LISTS),
            SR.get(SR.MS_CUSTOM_KEYS),
            SR.get(SR.MS_CLEAR_POPUPS),
            SR.get(SR.MS_APP_MINIMIZE),
            SR.get(SR.MS_INVERT),
            SR.get(SR.MS_XML_CONSOLE), 
            SR.get(SR.MS_FULLSCREEN)
    };

    public static String getKeyName( int keyCode){
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
            return ""+(char)keyCode;
        if( (keyCode&0xffff) >0xa020 && (keyCode&0xffff) <0xa07f)
            return ""+(char)(keyCode&0x7f);
        if( (keyCode&0xffff) ==0xa020)
            return "Space";
        return "Key " + java.lang.Integer.toHexString(keyCode);
    }
}
//#endif

