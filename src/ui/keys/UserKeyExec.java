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
import client.ConfigForm;
import client.StaticData;
import colors.ColorTheme;
//#ifdef XML_CONSOLE
//# import console.xml.XMLConsole;
//#endif
//#ifdef PRIVACY
import privacy.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif
//#if FILE_IO && FILE_TRANSFER
import io.file.transfer.TransferManager;
//#endif
//#ifdef STATS
import stats.StatsWindow;
//#endif
import java.util.Enumeration;
import java.util.Vector;
import locale.SR;
import midlet.BombusQD;
import ui.VirtualList;

public class UserKeyExec {
    public static int noExecSem= 0;
    public static int waitCode= 0;
    public static int waitCmd= 0;

    private static Config cf;
    StaticData sd=StaticData.getInstance();

    private static UserKeyExec instance;
    public static UserKeyExec getInstance(){
	if (instance==null) {
	    instance=new UserKeyExec();
            cf=Config.getInstance();
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
    public boolean getCommandByKey( int key){
        if( noExecSem !=0)
            return false;
        if (waitCode ==key) {
            commandExecute( waitCmd);
            return true;
        }
        waitCode= 0;
        for (Enumeration commands=commandsList.elements(); commands.hasMoreElements(); ) {
            UserKey userKeyItem=(UserKey) commands.nextElement();
            if (userKeyItem.keyCode==key) {
//                waitCode= 0;
                if( userKeyItem.mKey){
                    waitCode= userKeyItem.secCode;
                    waitCmd= userKeyItem.commandId;
                    return true;
                }
                //commandNum=userKeyItem.commandId;
                commandExecute( userKeyItem.commandId);
                // обработали клавишу, прекратить ее
                // дапьнейшее продвижение в VirtualCanvas
                return true;
            }
        }
        return false;
    }

    private boolean commandExecute(int commandId) { //return false if key not executed
        //int commandId=getCommandByKey(command);

        if (commandId<1) return false;

        boolean connected= ( sd.roster.isLoggedIn() );

        switch (commandId) {
            case 1:
                new ConfigForm().show();
                break;
            case 2:
                sd.roster.cmdCleanAllMessages();
                break;
            case 3:
                sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_SIMULATED_BREAK)));
                break;
//#ifdef STATS
            case 4:
                new StatsWindow().show();
                break;
//#endif
            case 5:
                sd.roster.cmdStatus();
                break;
//#if FILE_IO && FILE_TRANSFER
            case 6:
                new TransferManager().show();
                break;
//#endif
//#ifdef ARCHIVE
            case 7:
                sd.roster.cmdArchive();
                break;
//#endif
//#ifdef SERVICE_DISCOVERY
            case 8:
                if (connected) {
                    new ServiceDiscovery(null, null, false).show();
                }
                break;
//#endif
//#ifdef PRIVACY
            case 9:
                if (connected) {
                    new PrivacySelect().show();
                }
                break;
//#endif
            case 10:
                new UserKeysList().show();
                break;
//#ifdef POPUPS
            case 11:
                VirtualList.getPopUp().clear();;

                break;
//#endif
            case 12:
                if (cf.allowMinimize) {
                    BombusQD.hideApp();
                }
                break;
            case 13:
                ColorTheme.invertSkin();
                break;
//#ifdef XML_CONSOLE
//#             case 14:
//#                 new XMLConsole().show();
//#                 break;
//#endif
            case 15:
                Config.fullscreen = !Config.fullscreen;
                sd.canvas.setFullScreenMode(Config.fullscreen);
                cf.saveToStorage();
                break;
        }
        return true;
    }
}
//#endif
