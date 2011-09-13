/*
 * userKeyExecute.java
 *
 * Created on 14.09.2007, 13:38
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

import client.Config;
import java.util.Enumeration;
import java.util.Vector;

public class UserKeyExec {
    public static int noExecSem = 0;
    public static int waitCode = 0;
    public static boolean waitLong = false;

    private static UserKeyExec instance;

    public static UserKeyExec getInstance(){
	if (instance==null) {
	    instance=new UserKeyExec();
            instance.initCommands();
	}
	return instance;
    }

    public Vector commandsList;

    private void initCommands() {
        commandsList=null;
        commandsList=new Vector(0);

        UserKey u = null;

        int index=0;
        do {
            u=UserKey.createFromStorage(index);
            if (u!=null) {
                commandsList.addElement(u);
                index++;
             }
        } while (u!=null);
    }

    public static void stopExecute( ){
        noExecSem++;
    }

    public static void startExecute( ){
        noExecSem--;
    }

    public boolean getCommandByKey(int key, boolean isLong){
        if(noExecSem >0)
            return false;
        if (!Config.userKeys)
            return false;
        for (Enumeration commands=commandsList.elements(); commands.hasMoreElements(); ) {
            UserKey userKeyItem=(UserKey) commands.nextElement();
            if (waitCode!=0) {
                if (userKeyItem.mKey 
                        && userKeyItem.keyCode==waitCode 
                        && userKeyItem.keyLong==waitLong
                        && userKeyItem.secCode==key 
                        && userKeyItem.secLong==isLong) {
                  UserActions.doActionByExtIndex( UserActions.UA_KEYS, userKeyItem.commandId, null);
                  waitCode = 0;
                  return true;
                }  
            } else if (userKeyItem.keyCode==key && userKeyItem.keyLong==isLong) {
                if(userKeyItem.mKey){
                    waitCode = key;
                    waitLong = isLong;
                    return true;
                }
                UserActions.doActionByExtIndex( UserActions.UA_KEYS, userKeyItem.commandId, null);
                waitCode = 0;
                // обработали клавишу, прекратить ее
                // дапьнейшее продвижение в VirtualCanvas
                return true;
            }
        }
        return false;
    }
}
//#endif
