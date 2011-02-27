/*
 * Commands.java
 *
 * Created on 3 �������� 2009 �., 23:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package midlet;
import menu.Command;
import locale.SR;
/**
 *
 * @author aqent
 */
public class Commands {
    
    public static Command cmdOk ;
    public static Command cmdCancel;
    
    //MessageList
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
    public static Command cmdActive;
//#ifdef FILE_IO
    public static Command cmdSaveChat;
//#endif

//#ifdef CLIPBOARD    
//#     public static Command cmdSendBuffer;
//#endif
    public static Command cmdAddSearchQuery ; 
    public static Command cmdClrPresences;
//#if BREDOGENERATOR             
//#     public static Command cmdAutoGenON; //MS_BREDO_ON),Command.SCREEN,87);    
//#     public static Command cmdAutoGenOff; //MS_BREDO_OFF),Command.SCREEN,88);    
//#endif       
    public static Command cmdMyService;
    
//#ifdef CLIPBOARD
//#     public static Command cmdCopy ;
//#     public static Command cmdCopyPlus ;
//#endif    
    public static Command cmdxmlSkin ;
    public static Command cmdUrl ;
    public static Command cmdBack ;

//#ifdef HISTORY
//#     public static Command cmdHistory ;
//#endif
    public static Command cmdHistoryRMS ;
    public static Command cmdHistoryFS ;
    public static Command cmdHistorySERVER ;
//#ifdef JUICK.COM   
//#     /*
//#     public static Command cmdJuickLastPopular ; //"", Command.SCREEN, 101);//#
//#     public static Command cmdJuickLastMsgs ; //"", Command.SCREEN, 102);//#+
//#     public static Command cmdJuickSubscribe ; //"", Command.SCREEN, 103);//S #
//#     public static Command cmdJuickUnsubscribe ; //"", Command.SCREEN, 104);//U #
//#     public static Command cmdJuickSendPM ; //"", Command.SCREEN, 105);//PM @nick msg
//#     public static Command cmdJuickUsersMsgs ; //"", Command.SCREEN, 106); //@nick+
//#     */    
//#endif 
    
    
    

    
    public static void initCommands(){
              cmdOk = new Command(SR.get(SR.MS_OK), Command.OK, 1);
              cmdCancel = new Command(SR.get(SR.MS_BACK),Command.BACK, 2);
    
              //MessageList
              cmdTranslate=new Command(SR.get(SR.MS_TRANSLATE), Command.SCREEN /*Command.SCREEN*/,337);    
              cmdSubscribe=new Command(SR.get(SR.MS_SUBSCRIBE), Command.SCREEN, 1);
              cmdUnsubscribed=new Command(SR.get(SR.MS_DECLINE), Command.SCREEN, 2);
              cmdMessage=new Command(SR.get(SR.MS_NEW_MESSAGE),Command.SCREEN,3);
              cmdResume=new Command(SR.get(SR.MS_RESUME),Command.SCREEN,1);
              cmdReply=new Command(SR.get(SR.MS_REPLY),Command.SCREEN,4);
              cmdQuote=new Command(SR.get(SR.MS_QUOTE),Command.SCREEN,5);
//#ifdef ARCHIVE
              cmdArch=new Command(SR.get(SR.MS_ADD_ARCHIVE),Command.SCREEN,6);
//#endif
              cmdPurge=new Command(SR.get(SR.MS_CLEAR_LIST), Command.SCREEN, 7);
              cmdSelect=new Command(SR.get(SR.MS_SELECT), Command.SCREEN, 8);
              cmdActions=new Command(SR.get(SR.MS_CONTACT),Command.SCREEN,9);
              cmdActive=new Command(SR.get(SR.MS_ACTIVE_CONTACTS),Command.SCREEN,10);
//#ifdef FILE_IO
              cmdSaveChat=new Command(SR.get(SR.MS_SAVE_CHAT), Command.SCREEN, 12);
//#endif

//#ifdef CLIPBOARD    
//#               cmdSendBuffer=new Command(SR.get(SR.MS_SEND_BUFFER), Command.SCREEN, 14);
//#endif
              cmdAddSearchQuery = new Command(SR.get(SR.MS_ADD_SEARCH_QUERY), Command.SCREEN, 400);        
              cmdClrPresences=new Command(SR.get(SR.MS_DELETE_ALL_STATUSES), Command.SCREEN,403); 
//#if BREDOGENERATOR             
//#               cmdAutoGenON=new Command(SR.get(SR.MS_BREDO_ON),Command.SCREEN,87);    
//#               cmdAutoGenOff=new Command(SR.get(SR.MS_BREDO_OFF),Command.SCREEN,88);    
//#endif       
              cmdMyService=new Command(SR.get(SR.MS_SERVICE), Command.SCREEN, 31);
    
//#ifdef CLIPBOARD
//#               cmdCopy = new Command(SR.get(SR.MS_COPY), Command.SCREEN, 20);
//#               cmdCopyPlus = new Command("+ "+SR.get(SR.MS_COPY), Command.SCREEN, 30);
//#endif    
              cmdxmlSkin = new Command(SR.get(SR.MS_USE_COLOR_SCHEME), Command.SCREEN, 40);
              cmdUrl = new Command(SR.get(SR.MS_GOTO_URL), Command.SCREEN, 80);
              cmdBack = new Command(SR.get(SR.MS_BACK), Command.BACK, 99);    
  
//#ifdef HISTORY
//#               cmdHistory = new Command(SR.get(SR.MS_HISTORY_OPTIONS), Command.SCREEN, 101);
//#               cmdHistoryRMS = new Command(SR.get(SR.MS_HISTORY_SHOW) + "(rms)", Command.SCREEN, 102);
//#               cmdHistoryFS = new Command(SR.get(SR.MS_HISTORY_SHOW) + "(fs)", Command.SCREEN, 103);
//#               cmdHistorySERVER = new Command(SR.get(SR.MS_HISTORY_SHOW) + "(server)", Command.SCREEN, 104);
//#endif
//#ifdef JUICK.COM   
//#     /*
//#               cmdJuickLastPopular = new Command("", Command.SCREEN, 101);//#
//#               cmdJuickLastMsgs = new Command("", Command.SCREEN, 102);//#+
//#               cmdJuickSubscribe = new Command("", Command.SCREEN, 103);//S #
//#               cmdJuickUnsubscribe = new Command("", Command.SCREEN, 104);//U #
//#               cmdJuickSendPM = new Command("", Command.SCREEN, 105);//PM @nick msg
//#               cmdJuickUsersMsgs = new Command("", Command.SCREEN, 106); //@nick+
//#     */    
//#endif 
          setImages();
    }
    
    private static void setImages(){
        cmdOk.setImg(0x60);
                
        cmdSelect.setImg(0x60);
        cmdResume.setImg(0x80);
        cmdSubscribe.setImg(0x43);
        cmdUnsubscribed.setImg(0x41);
        cmdMessage.setImg(0x81);
        cmdTranslate.setImg(0x73);
        
        cmdClrPresences.setImg(0x76);
        cmdReply.setImg(0x72);
        cmdQuote.setImg(0x63);
        cmdPurge.setImg(0x33);
        cmdAddSearchQuery.setImg(0x83);
        cmdSelect.setImg(0x60);
//#ifdef ARCHIVE
        cmdArch.setImg(0x64);
//#endif
//#ifdef CLIPBOARD
//#         cmdCopy.setImg(0x23);
//#         cmdSendBuffer.setImg(0x84);
//#         cmdCopyPlus.setImg(0x23);
//#endif
        cmdxmlSkin.setImg(0x07);
        cmdUrl.setImg(0x15);
        cmdActions.setImg(0x16);
        cmdSaveChat.setImg(0x44);
        cmdMyService.setImg(0x27);
//#ifdef HISTORY
//#         cmdHistory.setImg(0x64);
//#         cmdHistoryRMS.setImg(0x64);
//#         cmdHistoryFS.setImg(0x64);
//#         cmdHistorySERVER.setImg(0x64);
//#endif
    }
        
    private Commands() {
    }
    
    public static Commands get(){
        if (commands==null) {
            commands=new Commands();
            initCommands();
        }
        return commands;
    }    
    private static Commands commands;    

    
}
