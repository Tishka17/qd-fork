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
    //public int    key       = -1;
    public int    keyCode       = -1;
    //public boolean active    = false;

    public UserKey() {
        super(RosterIcons.getInstance());
    }
    
    public static UserKey loadUserKey(int index){
        UserKey u=UserKey.createFromStorage(index);
        return u;
    }
    
    public String toString(){
        //StringBuffer s=new StringBuffer("(* + ").append(KEYS_NAME[key]).append(") ").append(COMMANDS_DESC[commandId]);
        StringBuffer s=new StringBuffer("* ").append(getKeyName(keyCode)).append(" ").append(COMMANDS_DESC[commandId]);
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
            //u.keyCode        = inputStream.readInt();
            // Indian code :) for save current storage structure
            u.keyCode        = inputStream.readInt();
            //u.active     = inputStream.readBoolean();
        } catch (IOException e) { /*e.printStackTrace();*/ }
            
        return (u.keyCode==-1)?null:u;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeInt(commandId);
            //outputStream.writeInt(key);
            outputStream.writeInt(keyCode);
            //outputStream.writeBoolean(active);
        } catch (IOException e) { }
    }

    public int getImageIndex() {return 0;}

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
        String retval= "" +keyCode +"=" +java.lang.Integer.toHexString( keyCode);
        switch( keyCode){
            case VirtualCanvas.NAVIKEY_FIRE: retval= "Fire"; break;
            case VirtualCanvas.NAVIKEY_UP: retval= "Up"; break;
            case VirtualCanvas.NAVIKEY_DOWN: retval= "Down"; break;
            case VirtualCanvas.NAVIKEY_LEFT: retval= "Left"; break;
            case VirtualCanvas.NAVIKEY_RIGHT: retval= "Right"; break;
            case VirtualCanvas.LEFT_SOFT: retval= "LSoft"; break;
            case VirtualCanvas.RIGHT_SOFT: retval= "RSoft"; break;
            case VirtualCanvas.CLEAR_KEY: retval= "Clear"; break;
            case VirtualCanvas.CLOSE_KEY: retval= "Close"; break;
            case VirtualCanvas.CALL_KEY: retval= "Call"; break;
            case VirtualCanvas.CAMERA_KEY: retval= "Camera"; break;
            case VirtualCanvas.ABC_KEY: retval= "ABC"; break;
            case VirtualCanvas.VOLPLUS_KEY: retval= "VolUp"; break;
            case VirtualCanvas.VOLMINUS_KEY: retval= "VolDown"; break;
            case VirtualCanvas.UNUSED_KEY: retval= "Unused"; break;
        }// switch
        if( keyCode >0x20 && keyCode <0x7f)
            retval= "" +(char)keyCode;
        if( (keyCode&0xffff) >0xa020 && (keyCode&0xffff) <0xa07f)
            retval= "" +(char)(keyCode&0x7f);
        if( (keyCode&0xffff) ==0xa020)
            retval= "Space";
        return "(" +retval +")";
    }
}
//#endif

