/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.keys;

import client.Config;
import client.ConfigForm;
import client.StaticData;
import colors.ColorTheme;
//#ifdef XML_CONSOLE
import console.xml.XMLConsole;
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

/**
 *
 * @author Mars
 */

public class UserActions {

    public static class userAct {
        int id; // Номер действия в кейсе actionExecute
        int type; // флаг, определяющий, где может
            // использоваться действие:
            // 0 - везде, 1 - только в хоткеях,
            // 2 - только в планировщике
        String text; // Название действия

        public userAct( int id, int type, String text) {
            this.id= id; this.text= text; this.type= type;
        }
    }

    private static UserActions instance;

    public static UserActions getInstance() {
        if (instance==null) instance=new UserActions();
        return instance;
    }

    public UserActions() {
    }

    public static Vector getActionsList( int type) {
        // Заполняем список доступных команд
        // в зависимости от типа списка
        Vector actList= null;
        actList= new Vector(0);

        for( int i=0; i <allActs.length; i++){
            if( allActs[i].type ==0
            || allActs[i].type ==type)
                actList.addElement( allActs[i].text);
        }
        return actList;
    }

    public static boolean doActionByExtIndex( int type, int eInd) {
        // Выполняем действие по внешнему индексу
        // в зависимости от типа списка

        for( int i=0; i <allActs.length; i++){
            if( allActs[i].type ==0
            || allActs[i].type ==type)
                if( eInd-- ==0)
                    return ActionExecute( allActs[i].id, type);
        }
        return false;
    }

    public static final userAct allActs[]= {
        new userAct( 0, 0, SR.get(SR.MS_NO))
        ,new userAct( 1, 1, SR.get(SR.MS_OPTIONS))
        ,new userAct( 2, 1, SR.get(SR.MS_CLEAN_ALL_MESSAGES))
        ,new userAct( 3, 0, SR.get(SR.MS_RECONNECT))
//#ifdef STATS
        ,new userAct( 4, 1, SR.get(SR.MS_STATS))
//#endif
        ,new userAct( 5, 1, SR.get(SR.MS_STATUS))
//#if FILE_IO && FILE_TRANSFER
        ,new userAct( 6, 1, SR.get(SR.MS_FILE_TRANSFERS))
//#endif
//#ifdef ARCHIVE
        ,new userAct( 7, 1, SR.get(SR.MS_ARCHIVE))
//#endif
//#ifdef SERVICE_DISCOVERY
        ,new userAct( 8, 1, SR.get(SR.MS_DISCO))
//#endif
//#ifdef PRIVACY
        ,new userAct( 9, 1, SR.get(SR.MS_PRIVACY_LISTS))
//#endif
        ,new userAct( 10, 1, SR.get(SR.MS_CUSTOM_KEYS))
//#ifdef POPUPS
        ,new userAct( 11, 1, SR.get(SR.MS_CLEAR_POPUPS))
//#endif
        ,new userAct( 12, 0, SR.get(SR.MS_APP_MINIMIZE))
        ,new userAct( 13, 0, SR.get(SR.MS_INVERT))
        ,new userAct( 14, 1, SR.get(SR.MS_FULLSCREEN))
//#ifdef XML_CONSOLE
        ,new userAct( 15, 1, SR.get(SR.MS_XML_CONSOLE))
//#endif
        ,new userAct( 16, 0, SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES))
        ,new userAct( 17, 0, "Quit QD")
        ,new userAct( 18, 0, SR.get(SR.MS_LOGOFF))
        ,new userAct( 19, 0, SR.get(SR.MS_AUTOLOGIN))
        ,new userAct( 19, 0, SR.get(SR.MS_DO_AUTOJOIN))
    };

    private static boolean ActionExecute(int actId, int type) {
        //return false if action not executed

        Config cf= Config.getInstance();
        StaticData sd= StaticData.getInstance();

        switch (actId) {
            case 0: // nop
                return true;
            case 1: // options
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
                if( sd.roster.isLoggedIn())
                    new ServiceDiscovery(null, null, false).show();
                break;
//#endif
//#ifdef PRIVACY
            case 9:
                if( sd.roster.isLoggedIn())
                    new PrivacySelect().show();
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
                if( cf.allowMinimize) BombusQD.hideApp();
                break;
            case 13:
                ColorTheme.invertSkin();
                break;
            case 14:
                Config.fullscreen = !Config.fullscreen;
                sd.canvas.setFullScreenMode(Config.fullscreen);
                cf.saveToStorage();
                break;
//#ifdef XML_CONSOLE
             case 15:
                 new XMLConsole().show();
                 break;
//#endif
            case 16: // Leave MUCs
                sd.roster.leaveAllMUCs();
                break;
            case 17: // Ouit QD
                BombusQD.getInstance().notifyDestroyed();
                break;
            case 18: // LogOff
                sd.roster.logoff( SR.get(SR.MS_LOGOFF));
                return true;
            case 19: // LogOn
                sd.roster.logon( SR.get(SR.MS_AUTOLOGIN));
                return true;
            case 20: //Join MUCs
                sd.roster.MUCsAutoJoin( SR.get(SR.MS_DO_AUTOJOIN));
                return true;
        }
        return false;
    }

}
