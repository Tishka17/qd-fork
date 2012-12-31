/*
 * Commands.java
 *
 * Created on 3.Nov.2009, 23:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package midlet;

import images.MenuIcons;
import menu.Command;
import locale.SR;
//#ifdef TRANSLATE
import xmpp.extensions.IqTranslator;
//#endif

/**
 *
 * @author aqent
 */

public class Commands {
    public static Command cmdOk;

    public static Command cmdSubscribe;
    public static Command cmdUnsubscribed;
    public static Command cmdMessage;
    public static Command cmdResume;
    public static Command cmdReply;
    public static Command cmdQuote;

//#ifdef ARCHIVE
    public static Command cmdArch;
//#endif

    public static Command cmdPurge;
    public static Command cmdSelect;
    public static Command cmdActions;

    public static Command cmdAddSearchQuery;
    public static Command cmdClrPresences;

    public static Command cmdMyService;
    
//#ifdef CLIPBOARD
    public static Command cmdCopy;
    public static Command cmdCopyPlus;
    public static Command cmdSendBuffer;
    public static Command cmdPaste;
//#endif

    public static Command cmdxmlSkin;
    public static Command cmdUrl;

//#ifdef HISTORY
    public static Command cmdHistory;
//#endif

//#ifdef JUICK.COM
    public static Command cmdJuickMenu;
    public static Command cmdJuickLastPopular;
    public static Command cmdJuickLastMsgs;
    public static Command cmdJuickSubscribe;
    public static Command cmdJuickUnsubscribe;
    public static Command cmdJuickSendPM;
    public static Command cmdJuickUsersMsgs;
//#endif
//#if FILE_TRANSFER
    public static Command cmdFileAccept;
    public static Command cmdFileDecline;
//#endif
//#ifdef TRANSLATE
    public static Command cmdTranslate;
//#endif


    public static void initCommands() {
        //TODO: константы вместо чисел
        cmdOk = new Command(SR.get(SR.MS_OK), 0x60);
        cmdSelect = new Command(SR.get(SR.MS_SELECT), 0x60);
        cmdMessage = new Command(SR.get(SR.MS_NEW_MESSAGE), 0x81);
        cmdResume = new Command(SR.get(SR.MS_RESUME), 0x80);
        cmdSubscribe = new Command(SR.get(SR.MS_SUBSCRIBE), 0x43);
        cmdUnsubscribed = new Command(SR.get(SR.MS_DECLINE), 0x41);
        cmdReply = new Command(SR.get(SR.MS_REPLY), 0x72);
        cmdQuote = new Command(SR.get(SR.MS_QUOTE), 0x63);
        cmdPurge = new Command(SR.get(SR.MS_CLEAR_LIST), 0x33);
        cmdActions = new Command(SR.get(SR.MS_CONTACT), 0x16);
        cmdAddSearchQuery = new Command(SR.get(SR.MS_ADD_SEARCH_QUERY), MenuIcons.ICON_SEARCH);
        cmdClrPresences = new Command(SR.get(SR.MS_DELETE_ALL_STATUSES), 0x76);
        cmdMyService = new Command(SR.get(SR.MS_SERVICE), 0x27);
        cmdxmlSkin = new Command(SR.get(SR.MS_USE_COLOR_SCHEME), MenuIcons.ICON_COLOR_TUNE);
        cmdUrl = new Command(SR.get(SR.MS_GOTO_URL), MenuIcons.ICON_GOTO_URL);

//#ifdef CLIPBOARD
        cmdCopy = new Command(SR.get(SR.MS_COPY), MenuIcons.ICON_COPY);
        cmdCopyPlus = new Command("+ " + SR.get(SR.MS_COPY), MenuIcons.ICON_COPY);
        cmdSendBuffer = new Command(SR.get(SR.MS_SEND_BUFFER), 0x84);
        cmdPaste = new Command(SR.get(SR.MS_PASTE), 0x84);
//#endif

//#ifdef HISTORY
        cmdHistory = new Command(SR.get(SR.MS_HISTORY), 0x64);
//#endif

//#ifdef ARCHIVE
        cmdArch = new Command(SR.get(SR.MS_ADD_ARCHIVE), 0x64);
//#endif

//#ifdef JUICK.COM
            cmdJuickMenu = new Command(SR.get(SR.MS_JUICK_COMMANDS), 0x96); 
            cmdJuickLastPopular = new Command(SR.get(SR.MS_JUICK_LP), 0x64);
            cmdJuickLastMsgs = new Command(SR.get(SR.MS_JUICK_LM), 0x64);
            cmdJuickSubscribe = new Command(SR.get(SR.MS_JUICK_S), 0x02);
            cmdJuickUnsubscribe = new Command(SR.get(SR.MS_JUICK_U), 0x06);
            cmdJuickSendPM = new Command(SR.get(SR.MS_JUICK_SPM), 0x14);
            cmdJuickUsersMsgs = new Command(SR.get(SR.MS_JUICK_UM), 0x14);
//#endif
//#if FILE_TRANSFER
            cmdFileAccept = new Command(SR.get(SR.MS_ACCEPT_FILE), 0x43);
            cmdFileDecline = new Command(SR.get(SR.MS_DECLINE), 0x41);
//#endif

//#ifdef TRANSLATE
            cmdTranslate = new Command(SR.get(SR.MS_TRANSLATE) + " " +IqTranslator.sLang + " -> " +IqTranslator.tLang, MenuIcons.ICON_GOTO_URL);
//#endif
    }
}
