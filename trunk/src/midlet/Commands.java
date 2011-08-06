/*
 * Commands.java
 *
 * Created on 3 �������� 2009 �., 23:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package midlet;

import images.MenuIcons;
import menu.Command;
import locale.SR;

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
    public static Command cmdJuickLastPopular ; //"", Command.SCREEN, 101);//#
    public static Command cmdJuickLastMsgs ; //"", Command.SCREEN, 102);//#+
    public static Command cmdJuickSubscribe ; //"", Command.SCREEN, 103);//S #
    public static Command cmdJuickUnsubscribe ; //"", Command.SCREEN, 104);//U #
    public static Command cmdJuickSendPM ; //"", Command.SCREEN, 105);//PM @nick msg
    public static Command cmdJuickUsersMsgs ; //"", Command.SCREEN, 106); //@nick+
//#endif

    public static void initCommands() {
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
              cmdJuickLastPopular = new Command(" # ", 101);//#
              cmdJuickLastMsgs = new Command(" #+ ", 102);//#+
              cmdJuickSubscribe = new Command(" S# ", 103);//S #
              cmdJuickUnsubscribe = new Command(" U# ", 104);//U #
              cmdJuickSendPM = new Command(" PM @nick msg ", 105);//PM @nick msg
              cmdJuickUsersMsgs = new Command(" @nick+ ", 106); //@nick+
//#endif
    }
}
