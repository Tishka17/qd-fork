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
    public static Command cmdCancel;

    public static Command cmdTranslate;
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

//#ifdef FILE_IO
    public static Command cmdSaveChat;
//#endif

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
    public static Command cmdBack;

//#ifdef HISTORY
    public static Command cmdHistory;
//#endif
    
//#ifdef JUICK.COM
    /*
    public static Command cmdJuickLastPopular ; //"", Command.SCREEN, 101);//#
    public static Command cmdJuickLastMsgs ; //"", Command.SCREEN, 102);//#+
    public static Command cmdJuickSubscribe ; //"", Command.SCREEN, 103);//S #
    public static Command cmdJuickUnsubscribe ; //"", Command.SCREEN, 104);//U #
    public static Command cmdJuickSendPM ; //"", Command.SCREEN, 105);//PM @nick msg
    public static Command cmdJuickUsersMsgs ; //"", Command.SCREEN, 106); //@nick+
    */
//#endif

    public static void initCommands() {
        cmdOk = new Command(SR.get(SR.MS_OK), Command.OK, 1);
        cmdOk.setImg(0x60);

        cmdSelect = new Command(SR.get(SR.MS_SELECT), Command.SCREEN, 8);
        cmdSelect.setImg(0x60);

        cmdMessage = new Command(SR.get(SR.MS_NEW_MESSAGE), Command.SCREEN, 3);
        cmdMessage.setImg(0x81);

        cmdResume = new Command(SR.get(SR.MS_RESUME), Command.SCREEN, 1);
        cmdResume.setImg(0x80);

        cmdCancel = new Command(SR.get(SR.MS_BACK), Command.BACK, 2);

        cmdTranslate = new Command(SR.get(SR.MS_TRANSLATE), Command.SCREEN, 337);
        cmdTranslate.setImg(0x73);

        cmdSubscribe = new Command(SR.get(SR.MS_SUBSCRIBE), Command.SCREEN, 1);
        cmdSubscribe.setImg(0x43);

        cmdUnsubscribed = new Command(SR.get(SR.MS_DECLINE), Command.SCREEN, 2);
        cmdUnsubscribed.setImg(0x41);

        cmdReply = new Command(SR.get(SR.MS_REPLY), Command.SCREEN, 4);
        cmdReply.setImg(0x72);

        cmdQuote = new Command(SR.get(SR.MS_QUOTE), Command.SCREEN, 5);
        cmdQuote.setImg(0x63);

        cmdPurge = new Command(SR.get(SR.MS_CLEAR_LIST), Command.SCREEN, 7);
        cmdPurge.setImg(0x33);

        cmdActions = new Command(SR.get(SR.MS_CONTACT), Command.SCREEN, 9);
        cmdActions.setImg(0x16);

//#ifdef FILE_IO
        cmdSaveChat = new Command(SR.get(SR.MS_SAVE_CHAT), Command.SCREEN, 12);
        cmdSaveChat.setImg(0x44);
//#endif

        cmdAddSearchQuery = new Command(SR.get(SR.MS_ADD_SEARCH_QUERY), Command.SCREEN, 400);
        cmdAddSearchQuery.setImg(MenuIcons.ICON_SEARCH);

        cmdClrPresences = new Command(SR.get(SR.MS_DELETE_ALL_STATUSES), Command.SCREEN, 403);
        cmdClrPresences.setImg(0x76);

        cmdMyService = new Command(SR.get(SR.MS_SERVICE), Command.SCREEN, 31);
        cmdMyService.setImg(0x27);

        cmdxmlSkin = new Command(SR.get(SR.MS_USE_COLOR_SCHEME), Command.SCREEN, 40);
        cmdxmlSkin.setImg(MenuIcons.ICON_COLOR_TUNE);

        cmdUrl = new Command(SR.get(SR.MS_GOTO_URL), Command.SCREEN, 80);
        cmdUrl.setImg(MenuIcons.ICON_GOTO_URL);

        cmdBack = new Command(SR.get(SR.MS_BACK), Command.BACK, 99);

//#ifdef CLIPBOARD
        cmdCopy = new Command(SR.get(SR.MS_COPY), Command.SCREEN, 20);
        cmdCopy.setImg(MenuIcons.ICON_COPY);

        cmdCopyPlus = new Command("+ " + SR.get(SR.MS_COPY), Command.SCREEN, 30);
        cmdCopyPlus.setImg(MenuIcons.ICON_COPY);

        cmdSendBuffer = new Command(SR.get(SR.MS_SEND_BUFFER), Command.SCREEN, 14);
        cmdSendBuffer.setImg(0x84);

        cmdPaste = new Command(SR.get(SR.MS_PASTE), Command.SCREEN, 31);
        cmdPaste.setImg(0x84);
//#endif

//#ifdef HISTORY
        cmdHistory = new Command(SR.get(SR.MS_HISTORY), Command.SCREEN, 101);
        cmdHistory.setImg(0x64);
//#endif

//#ifdef ARCHIVE
        cmdArch = new Command(SR.get(SR.MS_ADD_ARCHIVE), Command.SCREEN, 6);
        cmdArch.setImg(0x64);
//#endif

//#ifdef JUICK.COM
    /*
              cmdJuickLastPopular = new Command("", Command.SCREEN, 101);//#
              cmdJuickLastMsgs = new Command("", Command.SCREEN, 102);//#+
              cmdJuickSubscribe = new Command("", Command.SCREEN, 103);//S #
              cmdJuickUnsubscribe = new Command("", Command.SCREEN, 104);//U #
              cmdJuickSendPM = new Command("", Command.SCREEN, 105);//PM @nick msg
              cmdJuickUsersMsgs = new Command("", Command.SCREEN, 106); //@nick+
    */
//#endif
    }
}
