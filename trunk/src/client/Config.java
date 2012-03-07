/*
 * Config.java
 *
 * Created on 19.03.2005, 18:37
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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
 *
 */

package client;

import alert.AlertProfile;
import images.ActionsIcons;
import images.RosterIcons;
//#ifdef SMILES
import images.SmilesIcons;
//#endif
//#ifdef FILE_IO
import io.file.FileIO;
//#endif
import midlet.BombusQD;
import font.FontCache;
import images.MenuIcons;
import util.StringLoader;
import util.Time;
import ui.VirtualList;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TimeZone;
import java.util.Vector;

/**
 *
 * @author Eugene Stahov,aqent
 */

public class Config {
    private static final String BOOL_STORAGE_NAME = "confBoolean";
    private static final String INT_STORAGE_NAME = "confInt";
    private static final String UTF_STORAGE_NAME = "confUtf";

    private static Config instance;

    public static final int HISTORY_RMS = 0;
    public static final int HISTORY_FS = 1;

    public final static int SUBSCR_AUTO=0;
    public final static int SUBSCR_ASK=1;
    public final static int SUBSCR_DROP=2;
    public final static int SUBSCR_REJECT=3;

    public final static byte NOT_DETECTED=0;
    public final static byte NONE=-1;
    public final static byte SONYE=1;
    public final static byte NOKIA=2;
    public final static byte SIEMENS=3;
    public final static byte SIEMENS2=4;
    public final static byte MOTO=5;
    public final static byte MOTOEZX=6;
    public final static byte WINDOWS=7;
    public final static byte INTENT=8;
    public final static byte J2ME=9;
    public final static byte NOKIA_9XXX=10;
    public final static byte SONYE_M600=11;
//#if !ZLIB
//#     public final static byte XENIUM99=12;
//#endif
    public final static byte SAMSUNG=14;
    public final static byte LG=15;
    public final static byte JBED=16;
    public final static byte MICROEMU = 17;
    public final static byte NOKIA_S40 = 18;
    public final static byte WTK=50;
    public final static byte OTHER=99;
    public final static byte NOKIA_5800=44;

//#ifdef TOUCH
    public static boolean isTouchPhone;
//#endif

    public boolean ghostMotor = false;
    public boolean muc119=true;	// before muc 1.19 use muc#owner instead of muc#admin


//#ifdef AUTOSTATUS
    public final static int AWAY_LOCK = 0;
    public final static int AWAY_MESSAGE = 1;
    public final static int AWAY_IDLE = 2;

    public static int autoAwayType = 0;
    public static int autoAwayDelay = 5; //5 minutes
    public static boolean setAutoStatusMessage = true;
//#endif

//#ifndef WMUC
    public static String defConference = BombusQD.getStrProperty("Def-Room", "qd@conference.jabber.ru");

    public static boolean storeConfPresence=false;
    public boolean autoJoinConferences=false;
    public int confMessageCount=20;
//#endif

    // non-volatile values
    public int accountIndex=-1;
    public int defaultAlertProfile=0;

    public static boolean fullscreen = true;

//#ifdef SMILES
    public boolean smiles = true;
    public static boolean animatedSmiles = true;
//#endif

    public boolean showOfflineContacts=false;
    public boolean showTransports=true;
    public boolean selfContact=true;
    public boolean showGroups=true;
    public boolean ignore=false;
    public boolean eventComposing=true;
    public boolean autoLogin=false;
    public boolean autoFocus=false;
    public int loginstatus=0;
    public int gmtOffset;
    public boolean popupFromMinimized=false;
//#ifdef MEMORY_MONITOR
//#     public boolean memMonitor=false;
//#endif

    public static int rosterFont = FontCache.MEDIUM;
    public static int baloonFont = FontCache.MEDIUM;
    public static int menuFont = FontCache.MEDIUM;
    public static int msgFont = FontCache.MEDIUM;
    public static int barFont = FontCache.MEDIUM;
    public static boolean useItalic = false;

    public static final byte KEYS_ONPRESS_NOLONG=0;
    public static final byte KEYS_ONPRESS_LONG=1;
    public static final byte KEYS_ONRELEASE=2;
    public static byte keymode = KEYS_ONRELEASE;
    
    public String lang;
    public boolean capsState=false;
    public int textWrap=1;
    public int autoSubscribe=SUBSCR_ASK;

    // runtime values
    public boolean allowMinimize=false;
    public int currentAlertProfile=0;
    public int lastProfile=0;
    public boolean lightState=false;

    //default state both panels show, reverse disabled
    public static int panelsState = 2;

//#ifdef POPUPS
    public static boolean popUps = false;
//#endif

    public static boolean rosterStatus = false;
    public static boolean showResources = true;
    public boolean enableVersionOs = true;
    public boolean collapsedGroups = true;
    public boolean eventDelivery = true;
    public int messageLimit = 512;
    
    public boolean showMsgsCount = false;

//#ifdef DETRANSLIT
//#    public boolean autoDeTranslit=false;
//#endif

//#ifdef PEP
    public boolean sndrcvmood = true;
    public boolean rcvtune = true;
    public boolean rcvactivity = true;
//#endif
//#ifdef JUICK.COM
    public boolean juickImages = false;
//#endif

//#ifdef Android
//#     public boolean queryExit = true;
//#else
     public boolean queryExit = false;
//#endif
    
    public int notInListDropLevel = Roster.ALLOW_ALL;
    public static boolean showBalloons = false;

    public int msglistLimit = 100;

    public boolean useTabs = false;
    public boolean notifyBlink = true;
    public boolean notifyPicture = false;
    public boolean useBoldFont = false;

//#ifdef CLIPBOARD
    public static boolean useClipBoard = true;
//#endif

    public String verHash = "";
    public String resolvedHost = "";
    public int resolvedPort = 0;

    public boolean IQNotify = false;
//#ifdef CLIENTS_ICONS
    public static boolean showClientIcon=true;
//#endif

    public static int reconnectCount = 30;
    public static int reconnectTime = 10;

    public static boolean executeByNum = false;
    public static boolean showNickNames = true;
    public boolean fileTransfer = true;
    public boolean adhoc = false;

    public boolean oldSE = false;

    public static boolean showTimeTraffic = false;
    public static boolean hideMessageIcon = false;

    public String moodText="";
    public String moodName="";
    public String actText="";
    public String actDescr="";
    public String actCat="";

    public boolean find_text=false;//fix

//#ifdef BACK_IMAGE
    public static int backImgType = 0;
    public static String backImgPath = "";
//#endif

    public static int scrollWidth = 5;
    public int minItemHeight = 12;
    public static int contactXOffset = 10;

    public boolean iconsLeft = true;

//#ifdef CLASSIC_CHAT
//#     public int classicChatHeight = 140;
//#     public int lineCount = 300;
//#     public boolean usePhoneTheme = false;
//#endif

    public static boolean gradient_cursor = false;

    public String langpair="ru==>en";

//#ifdef AVATARS
    public String msgAvatarPath = "";
    public static boolean auto_queryPhoto = false;
    public static boolean autoSaveVcard = false;
    public static boolean showAvatarRect = false;
    public static boolean autoload_FSPhoto = false;
    public int maxAvatarHeight = 35;
    public int maxAvatarWidth = 35;
//#endif

    public boolean nokiaReconnectHack = false;
    public static boolean showTimeInMsgs = false;
    public static boolean showMsgSep= true;
    public boolean isStatusFirst = false;

    public int msgEditType = 0;

//#ifdef RUNNING_MESSAGE
    public boolean runningMessage = false;
//#endif
//#ifdef DEBUG_CONSOLE
//#     public static boolean debug = false;
//#endif

    public static boolean hasAniSmiles = true;
    public static boolean showCollapsedPresences = false;
    public boolean networkAnnotation = true;
    public boolean graphicsMenu = true;
    public int graphicsMenuPosition = 0;

    public static boolean createMessageByFive = true;
    public boolean gradientBarVertical = false;
    public boolean shadowBar = true;

    public static boolean simpleContacts = false;
    public int userAppLevel = BombusQD.getIntProperty("Diff-Level", 0);

    public static boolean swapSendAndSuspend = false;
    public static boolean cleanConfContacts = false;
    public static boolean autoScroll = true;

//#ifdef HISTORY
    public static int historyTypeIndex = 0;
    public static String historyPath = "";
    public boolean historyMUC = false;
    public boolean historyMUCPrivate = true;
    public boolean historyPresence = false;
    public boolean historyBlogs = true;
    public int loadLastMsgCount = 0;
//#endif

//#ifdef LIGHT_CONTROL
    public static boolean lightControl = false;
    public static boolean oneLight = false;

    public static int lightKeyPressTime = 10;
    public static int lightMessageTime = 2;
    public static int lightPresenceTime = 1;
    public static int lightConnectTime = 5;
    public static int lightErrorTime = 3;
    public static int lightBlinkTime = 1;

    public static int lightIdle = 0;
    public static int lightKeyPress = 50;
    public static int lightMessage = 100;
    public static int lightPresence = 10;
    public static int lightConnect = 100;
    public static int lightError = 50;
    public static int lightBlink = 100;
//#endif
    
//#ifdef USER_KEYS
    public static boolean userKeys = false;
//#endif
//#ifdef AUTOSTATUS
    public static boolean module_autostatus = false;
//#endif
//#ifdef CLASSIC_CHAT
//#     public static boolean module_classicchat = false;
//#endif
//#ifdef HISTORY
    public static boolean module_history = false;
//#endif
//#ifdef IMPORT_EXPORT
    public static boolean module_ie = false;
//#endif
//#ifdef AUTOTASK
    public static boolean module_tasks = false;
//#endif
//#ifdef AVATARS
    public static boolean module_avatars = false;
//#endif

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            instance.loadFromStorage();
        }
        return instance;
    }

    private Config() {
        getPhoneManufacturer();
        VirtualList.phoneManufacturer=phoneManufacturer;

	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	gmtOffset=gmtloc;

        //prefetch images
        RosterIcons.getInstance();
        ActionsIcons.getInstance();
        MenuIcons.getInstance();
//#ifdef SMILES
        if (smiles) {
            if (animatedSmiles) {
                SmilesIcons.getInstance();
            } else {
                SmilesIcons.getStaticInstance();
            }
        }
//#endif
        System.gc();

        switch (phoneManufacturer) {
//#ifndef Android
            case SONYE:
                try { Thread.sleep(50); } catch (InterruptedException e){}
                allowMinimize=true;
            case NOKIA:
                allowMinimize=true;
                nokiaReconnectHack=true;
                break;
            case WINDOWS:
                graphicsMenu = false;
                break;
            case MOTO:
                ghostMotor=true;
                break;
//#endif
        }
    }

//#ifdef TOUCH
    public void menuFont() {
        if (isTouchPhone) {
            graphicsMenuPosition = 1; //left

            rosterFont = FontCache.MEDIUM;
            baloonFont = FontCache.MEDIUM;
            menuFont = FontCache.MEDIUM;
            msgFont = FontCache.MEDIUM;
            barFont = FontCache.MEDIUM;

            scrollWidth = 8;
            
            int h1 = BombusQD.sd.canvas.getHeight();
            int h2 = BombusQD.sd.canvas.getWidth();
            minItemHeight = ((h1<h2)?h2:h1)*40/480;

            midlet.BombusQD.sd.roster.updateBarsFont();
            saveToStorage();
        }
    }
//#endif


    public String langFileName(){
        if (lang==null) {
            //auto-detecting
            lang=System.getProperty("microedition.locale");
//#ifdef DEBUG
//#             System.out.println(lang);
//#endif
            //We will use only language code from locale
            if (lang==null) lang="en"; else lang=lang.substring(0, 2).toLowerCase();
        }

        if (lang.equals("en")) return null;  //english
	Vector files[]=new StringLoader().stringLoader("/lang/res.txt", 3);
        if (files == null) {
            return null; //english
        }
        for (int i=0; i<files[0].size(); i++) {
            String langCode=(String) files[0].elementAt(i);
            if (lang.equals(langCode))
        	return (String) files[1].elementAt(i);
        }
        return null;
        //return "/lang/ru.txt"; //unknown language ->en
    }

    protected void loadBoolean() {
        DataInputStream inputStream = NvStorage.ReadFileRecord(BOOL_STORAGE_NAME, 0);
        try {
            showOfflineContacts = inputStream.readBoolean();
            fullscreen = inputStream.readBoolean();
            showTransports = inputStream.readBoolean();
            selfContact = inputStream.readBoolean();
            collapsedGroups = inputStream.readBoolean();
            ignore = inputStream.readBoolean();
            eventComposing = inputStream.readBoolean();
            autoLogin = inputStream.readBoolean();
            autoJoinConferences = inputStream.readBoolean();
            popupFromMinimized = inputStream.readBoolean();
            notifyBlink = inputStream.readBoolean();
            autoFocus = inputStream.readBoolean();
            storeConfPresence = inputStream.readBoolean();
            capsState = inputStream.readBoolean();
            fileTransfer = inputStream.readBoolean();
            lightState = inputStream.readBoolean();
            cleanConfContacts = inputStream.readBoolean();
            showResources = inputStream.readBoolean();
            enableVersionOs = inputStream.readBoolean();
            eventDelivery = inputStream.readBoolean();
            rosterStatus = inputStream.readBoolean();
            queryExit = inputStream.readBoolean();
            notifyPicture = inputStream.readBoolean();
            showBalloons = inputStream.readBoolean();
            useTabs = inputStream.readBoolean();
            useBoldFont = inputStream.readBoolean();
            IQNotify = inputStream.readBoolean();
            executeByNum = inputStream.readBoolean();
            showNickNames = inputStream.readBoolean();
            adhoc = inputStream.readBoolean();
            createMessageByFive = inputStream.readBoolean();
            gradientBarVertical = inputStream.readBoolean();
            shadowBar = inputStream.readBoolean();
            simpleContacts = inputStream.readBoolean();
            swapSendAndSuspend = inputStream.readBoolean();
            oldSE = inputStream.readBoolean();
            showTimeTraffic = inputStream.readBoolean();
            hideMessageIcon = inputStream.readBoolean();
            iconsLeft=inputStream.readBoolean();
            gradient_cursor=inputStream.readBoolean();
            nokiaReconnectHack=inputStream.readBoolean();
            showTimeInMsgs=inputStream.readBoolean();
            autoScroll = inputStream.readBoolean();
            useItalic=inputStream.readBoolean();
            showCollapsedPresences = inputStream.readBoolean();
            networkAnnotation = inputStream.readBoolean();
            showMsgSep= inputStream.readBoolean();
//#ifdef MEMORY_MONITOR
//# 	    memMonitor=inputStream.readBoolean();
//#endif
//#ifdef AUTOSTATUS
            setAutoStatusMessage = inputStream.readBoolean();
//#endif
//#ifdef POPUPS
            popUps = inputStream.readBoolean();
//#endif
//#ifdef SMILES
            smiles = inputStream.readBoolean();
//#endif
//#ifdef CLASSIC_CHAT
//#             usePhoneTheme=inputStream.readBoolean();
//#endif
//#ifdef LIGHT_CONTROL
            lightControl = inputStream.readBoolean();
            oneLight = inputStream.readBoolean();
//#endif
//#ifdef AVATARS
            auto_queryPhoto = inputStream.readBoolean();
            autoSaveVcard=inputStream.readBoolean();
            showAvatarRect=inputStream.readBoolean();
            autoload_FSPhoto=inputStream.readBoolean();
//#endif
//#ifdef USER_KEYS
            userKeys=inputStream.readBoolean();
//#endif
//#ifdef AUTOSTATUS
            module_autostatus=inputStream.readBoolean();
//#endif
//#ifdef CLASSIC_CHAT
//#             module_classicchat=inputStream.readBoolean();
//#endif
//#ifdef HISTORY
            module_history=inputStream.readBoolean();
//#endif
//#ifdef IMPORT_EXPORT
            module_ie=inputStream.readBoolean();
//#endif
//#ifdef AUTOTASK
            module_tasks=inputStream.readBoolean();
//#endif
//#ifdef DEBUG_CONSOLE
//#             debug=inputStream.readBoolean();
//#endif
//#ifdef AVATARS
            module_avatars=inputStream.readBoolean();
//#endif
//#ifdef SMILES 
            animatedSmiles=inputStream.readBoolean();
//#endif
//#ifdef RUNNING_MESSAGE
            runningMessage=inputStream.readBoolean();
//#endif
//#ifdef PEP
            sndrcvmood = inputStream.readBoolean();
            rcvactivity = inputStream.readBoolean();
            rcvtune = inputStream.readBoolean();
//#endif
//#ifdef CLIPBOARD
            useClipBoard = inputStream.readBoolean();
//#endif
//#ifdef DETRANSLIT
//#             autoDeTranslit = inputStream.readBoolean();
//#endif
//#ifdef CLIENTS_ICONS
            showClientIcon = inputStream.readBoolean();
//#endif
//#ifdef JUICK.COM
            juickImages = inputStream.readBoolean();
//#endif 

//#ifdef HISTORY
            historyMUC = inputStream.readBoolean();
            historyMUCPrivate = inputStream.readBoolean();
 //#endif
            //minItemHeight = inputStream.readInt();
            showMsgsCount = inputStream.readBoolean();
            showGroups = inputStream.readBoolean();
//#ifdef HISTORY
            historyPresence = inputStream.readBoolean();
            historyBlogs = inputStream.readBoolean();
 //#endif
            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException ex) {
            }
        }
    }

    private boolean saveBoolean() {
       	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        try {
            outputStream.writeBoolean(showOfflineContacts);
            outputStream.writeBoolean(fullscreen);
            outputStream.writeBoolean(showTransports);
            outputStream.writeBoolean(selfContact);
            outputStream.writeBoolean(collapsedGroups);
            outputStream.writeBoolean(ignore);
            outputStream.writeBoolean(eventComposing);
            outputStream.writeBoolean(autoLogin);
            outputStream.writeBoolean(autoJoinConferences);
            outputStream.writeBoolean(popupFromMinimized);
            outputStream.writeBoolean(notifyBlink);
            outputStream.writeBoolean(autoFocus);
            outputStream.writeBoolean(storeConfPresence);
            outputStream.writeBoolean(capsState);
            outputStream.writeBoolean(fileTransfer);
            outputStream.writeBoolean(lightState);
            outputStream.writeBoolean(cleanConfContacts);
            outputStream.writeBoolean(showResources);
            outputStream.writeBoolean(enableVersionOs);
            outputStream.writeBoolean(eventDelivery);
            outputStream.writeBoolean(rosterStatus);
            outputStream.writeBoolean(queryExit);
            outputStream.writeBoolean(notifyPicture);
            outputStream.writeBoolean(showBalloons);
            outputStream.writeBoolean(useTabs);
            outputStream.writeBoolean(useBoldFont);
            outputStream.writeBoolean(IQNotify);
            outputStream.writeBoolean(executeByNum);
            outputStream.writeBoolean(showNickNames);
            outputStream.writeBoolean(adhoc);
            outputStream.writeBoolean(createMessageByFive);
            outputStream.writeBoolean(gradientBarVertical);
            outputStream.writeBoolean(shadowBar);
            outputStream.writeBoolean(simpleContacts);
            outputStream.writeBoolean(swapSendAndSuspend);
            outputStream.writeBoolean(oldSE);
            outputStream.writeBoolean(showTimeTraffic);
            outputStream.writeBoolean(hideMessageIcon);
            outputStream.writeBoolean(iconsLeft);
            outputStream.writeBoolean(gradient_cursor);
            outputStream.writeBoolean(nokiaReconnectHack);
            outputStream.writeBoolean(showTimeInMsgs);
            outputStream.writeBoolean(autoScroll);
            outputStream.writeBoolean(useItalic);
            outputStream.writeBoolean(showCollapsedPresences);
            outputStream.writeBoolean(networkAnnotation);
            outputStream.writeBoolean( showMsgSep);
//#ifdef MEMORY_MONITOR
//# 	    outputStream.writeBoolean(memMonitor);
//#endif
//#ifdef AUTOSTATUS
            outputStream.writeBoolean(setAutoStatusMessage);
//#endif
//#ifdef POPUPS
            outputStream.writeBoolean(popUps);
//#endif
//#ifdef SMILES
            outputStream.writeBoolean(smiles);
//#endif
//#ifdef CLASSIC_CHAT
//#             outputStream.writeBoolean(usePhoneTheme);
//#endif
//#if LIGHT_CONTROL
            outputStream.writeBoolean(lightControl);
            outputStream.writeBoolean(oneLight);
//#endif
//#ifdef AVATARS
            outputStream.writeBoolean(auto_queryPhoto);
            outputStream.writeBoolean(autoSaveVcard);
            outputStream.writeBoolean(showAvatarRect);
            outputStream.writeBoolean(autoload_FSPhoto);
//#endif
//#ifdef USER_KEYS
            outputStream.writeBoolean(userKeys);
//#endif
//#ifdef AUTOSTATUS
            outputStream.writeBoolean(module_autostatus);
//#endif
//#ifdef CLASSIC_CHAT
//#             outputStream.writeBoolean(module_classicchat);
//#endif
//#ifdef HISTORY
            outputStream.writeBoolean(module_history);
//#endif
//#ifdef IMPORT_EXPORT
            outputStream.writeBoolean(module_ie);
//#endif
//#ifdef AUTOTASK
            outputStream.writeBoolean(module_tasks);
//#endif
//#ifdef DEBUG_CONSOLE
//#             outputStream.writeBoolean(debug);
//#endif
//#ifdef AVATARS
            outputStream.writeBoolean(module_avatars);
//#endif
//#ifdef SMILES
            outputStream.writeBoolean(animatedSmiles);
//#endif
//#ifdef RUNNING_MESSAGE
            outputStream.writeBoolean(runningMessage);
//#endif
//#ifdef PEP
            outputStream.writeBoolean(sndrcvmood);
            outputStream.writeBoolean(rcvactivity);
            outputStream.writeBoolean(rcvtune);
//#endif
//#ifdef CLIPBOARD
            outputStream.writeBoolean(useClipBoard);
//#endif
//#ifdef DETRANSLIT
//#             outputStream.writeBoolean(autoDeTranslit);
//#endif
//#ifdef CLIENTS_ICONS
            outputStream.writeBoolean(showClientIcon);
//#endif
//#ifdef JUICK.COM
            outputStream.writeBoolean(juickImages);
//#endif
//#ifdef HISTORY
            outputStream.writeBoolean(historyMUC);
            outputStream.writeBoolean(historyMUCPrivate);
//#endif
            outputStream.writeBoolean(showMsgsCount);
            outputStream.writeBoolean(showGroups);
//#ifdef HISTORY
            outputStream.writeBoolean(historyPresence);
            outputStream.writeBoolean(historyBlogs);
//#endif 
	} catch (IOException e) {}
	return NvStorage.writeFileRecord(outputStream, BOOL_STORAGE_NAME, 0, true);
   }

    protected void loadInt(){
        DataInputStream inputStream=NvStorage.ReadFileRecord(INT_STORAGE_NAME, 0);
        try {
            accountIndex=inputStream.readInt();
            defaultAlertProfile=inputStream.readInt();
            gmtOffset=inputStream.readInt();
            rosterFont=inputStream.readInt();
            msgFont=inputStream.readInt();
            notInListDropLevel=inputStream.readInt();
            textWrap=inputStream.readInt();
            loginstatus=inputStream.readInt();
            panelsState=inputStream.readInt();
            confMessageCount=inputStream.readInt();
            messageLimit=inputStream.readInt();
            msglistLimit=inputStream.readInt();
            autoSubscribe=inputStream.readInt();
            barFont=inputStream.readInt();
            baloonFont=inputStream.readInt();
            resolvedPort=inputStream.readInt();
            reconnectCount=inputStream.readInt();
            reconnectTime=inputStream.readInt();
            scrollWidth=inputStream.readInt();
            msgEditType=inputStream.readInt();
            graphicsMenuPosition=inputStream.readInt();
            inputStream.readInt();//gradientBarLight1
            inputStream.readInt();//gradientBarLight2
            userAppLevel = inputStream.readInt();
            menuFont = inputStream.readInt();
            contactXOffset = inputStream.readInt();
//#ifdef AUTOSTATUS
            autoAwayDelay=inputStream.readInt();
            autoAwayType=inputStream.readInt();
//#endif
//#ifdef AVATARS
            maxAvatarHeight=inputStream.readInt();
            maxAvatarWidth=inputStream.readInt();
//#endif
//#ifdef BACK_IMAGE
            backImgType=inputStream.readInt();
//#endif
//#ifdef CLASSIC_CHAT
//#             classicChatHeight=inputStream.readInt();
//#             lineCount=inputStream.readInt();
//#endif
//#ifdef HISTORY
            historyTypeIndex = inputStream.readInt();
//#endif
//#ifdef LIGHT_CONTROL
            lightKeyPressTime = inputStream.readInt();
            lightMessageTime = inputStream.readInt();
            lightPresenceTime = inputStream.readInt();
            lightConnectTime = inputStream.readInt();
            lightErrorTime = inputStream.readInt();
            lightBlinkTime = inputStream.readInt();

            lightIdle = inputStream.readInt();
            lightKeyPress = inputStream.readInt();
            lightMessage = inputStream.readInt();
            lightPresence = inputStream.readInt();
            lightConnect = inputStream.readInt();
            lightError = inputStream.readInt();
            lightBlink = inputStream.readInt();
//#endif
            minItemHeight = inputStream.readInt();
//#ifdef HISTORY
            loadLastMsgCount = inputStream.readInt();
//#endif
            keymode = (byte)inputStream.readInt();
            
            inputStream.close();
            inputStream=null;
        } catch (Exception e) {
            try {
                if (inputStream!=null) {
                    inputStream.close();
                    inputStream=null;
                }
            } catch (IOException ex) { }
        }
    }

   public boolean saveInt(){
       	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        try {
            outputStream.writeInt(accountIndex);
            outputStream.writeInt(defaultAlertProfile);
            outputStream.writeInt(gmtOffset);
            outputStream.writeInt(rosterFont);
            outputStream.writeInt(msgFont);
            outputStream.writeInt(notInListDropLevel);
            outputStream.writeInt(textWrap);
            outputStream.writeInt(loginstatus);
            outputStream.writeInt(panelsState);
            outputStream.writeInt(confMessageCount);
            outputStream.writeInt(messageLimit);
            outputStream.writeInt(msglistLimit);
            outputStream.writeInt(autoSubscribe);
            outputStream.writeInt(barFont);
            outputStream.writeInt(baloonFont);
            outputStream.writeInt(resolvedPort);
            outputStream.writeInt(reconnectCount);
            outputStream.writeInt(reconnectTime);
            outputStream.writeInt(scrollWidth);
            outputStream.writeInt(msgEditType);
            outputStream.writeInt(graphicsMenuPosition);
            outputStream.writeInt(120);//gradientBarLight1
            outputStream.writeInt(5);//gradientBarLight2
            outputStream.writeInt(userAppLevel);
            outputStream.writeInt(menuFont);
            outputStream.writeInt(contactXOffset);
//#ifdef AUTOSTATUS
            outputStream.writeInt(autoAwayDelay);
            outputStream.writeInt(autoAwayType);
//#endif
//#ifdef AVATARS
            outputStream.writeInt(maxAvatarHeight);
            outputStream.writeInt(maxAvatarWidth);
//#endif
//#ifdef BACK_IMAGE
            outputStream.writeInt(backImgType);
//#endif
//#ifdef CLASSIC_CHAT
//#             outputStream.writeInt(classicChatHeight);
//#             outputStream.writeInt(lineCount);
//#endif
//#ifdef HISTORY
            outputStream.writeInt(historyTypeIndex);
//#endif
//#ifdef LIGHT_CONTROL
            outputStream.writeInt(lightKeyPressTime);
            outputStream.writeInt(lightMessageTime);
            outputStream.writeInt(lightPresenceTime);
            outputStream.writeInt(lightConnectTime);
            outputStream.writeInt(lightErrorTime);
            outputStream.writeInt(lightBlinkTime);

            outputStream.writeInt(lightIdle);
            outputStream.writeInt(lightKeyPress);
            outputStream.writeInt(lightMessage);
            outputStream.writeInt(lightPresence);
            outputStream.writeInt(lightConnect);
            outputStream.writeInt(lightError);
            outputStream.writeInt(lightBlink);
//#endif
            outputStream.writeInt(minItemHeight);
//#ifdef HISTORY
            outputStream.writeInt(loadLastMsgCount);
//#endif
            outputStream.writeInt(keymode);

        } catch (IOException e) { }
        return NvStorage.writeFileRecord(outputStream, INT_STORAGE_NAME, 0, true);
    }

    protected void loadUTF() {
        DataInputStream inputStream = NvStorage.ReadFileRecord(UTF_STORAGE_NAME, 0);
        try {
            defConference = inputStream.readUTF();
            lang = inputStream.readUTF();
            verHash = inputStream.readUTF();
            resolvedHost = inputStream.readUTF();
            moodText = inputStream.readUTF();
            moodName = inputStream.readUTF();
            actText = inputStream.readUTF();
            if ("".equals(actText)) actText = null;
            actDescr = inputStream.readUTF();
            if ("".equals(actDescr)) actDescr = null;
            actCat = inputStream.readUTF();
            if ("".equals(actCat)) actCat = null;
//#ifdef AVATARS
            msgAvatarPath = inputStream.readUTF();
//#endif
//#if FILE_IO && BACK_IMAGE
            backImgPath = inputStream.readUTF();
//#endif
//#ifdef HISTORY
            historyPath = inputStream.readUTF();
//#endif

            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException ex) {
            }
        }
    }

    public boolean saveUTF() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
        try {
            outputStream.writeUTF(defConference);
            outputStream.writeUTF(lang);
            outputStream.writeUTF(verHash);
            outputStream.writeUTF(resolvedHost);
            outputStream.writeUTF(moodText);
            outputStream.writeUTF(moodName);       
            if (actText==null) outputStream.writeUTF("");
            else outputStream.writeUTF(actText);
            if (actDescr==null) outputStream.writeUTF("");
            else outputStream.writeUTF(actDescr);
            if (actCat==null) outputStream.writeUTF("");
            else outputStream.writeUTF(actCat);
//#ifdef AVATARS
            outputStream.writeUTF(msgAvatarPath);
//#endif
//#if FILE_IO && BACK_IMAGE
            outputStream.writeUTF(backImgPath);
//#endif
//#ifdef HISTORY
            outputStream.writeUTF(historyPath);
//#endif
        } catch (IOException e) {
        }
        return NvStorage.writeFileRecord(outputStream, UTF_STORAGE_NAME, 0, true);
    }

    private class Saver implements Runnable {
        public void run() {
            saveBoolean();
            saveUTF();
            saveInt();
        }
    }

    private void loadFromStorage(){
        loadBoolean();
        loadInt();
        loadUTF();

        lastProfile = currentAlertProfile = defaultAlertProfile;
        if (lastProfile == AlertProfile.VIBRA) {
            lastProfile = 0;
        }
	Time.setOffset(gmtOffset);
//#ifdef MEMORY_MONITOR
//# 	VirtualList.memMonitor=memMonitor;
//#endif
    }

    public void saveToStorage() {
        new Thread(new Saver()).start();
    }

    public static String platformName;
    public byte phoneManufacturer=NOT_DETECTED;

    private void getPhoneManufacturer() {
        if (phoneManufacturer==NOT_DETECTED) {
            platformName();
            //String platform=getPlatformName();
            phoneManufacturer=NONE;

            if (platformName.endsWith("(NSG)")) {
                phoneManufacturer=SIEMENS;
                return;
            } else if (platformName.startsWith("SIE")) {
                phoneManufacturer=SIEMENS2;
                return;
            } else if (platformName.startsWith("Motorola-EZX")) {
                phoneManufacturer=MOTOEZX;
                return;
            } else if (platformName.startsWith("Moto")) {
                phoneManufacturer=MOTO;
                return;
            } else if (platformName.startsWith("SonyE")) {
                if (platformName.startsWith("SonyEricssonM600")) {
                    phoneManufacturer=SONYE_M600;
                    return;
                }
                phoneManufacturer=SONYE;
                return;
//#if !ZLIB
//#             } else if (platformName.indexOf("9@9")>-1) {
//#                 phoneManufacturer=XENIUM99;
//#                 return;
//#endif
            } else if (platformName.startsWith("Windows")) {
                phoneManufacturer=WINDOWS;
                return;
            } else if (platformName.startsWith("Nokia9500") ||
                platformName.startsWith("Nokia9300") ||
                platformName.startsWith("Nokia9300i")) {
                phoneManufacturer=NOKIA_9XXX;
                return;
            } else if (platformName.startsWith("Nokia")) {
                phoneManufacturer=NOKIA;
                    if (platformName.endsWith("5800")) {
                          phoneManufacturer=NOKIA_5800;
                          return;
                    }
                return;
            } else if (platformName.startsWith("Intent")) {
                phoneManufacturer=INTENT;
                return;
            } else if (platformName.startsWith("wtk") || platformName.endsWith("wtk")) {
                phoneManufacturer=WTK;
                return;
            } else if (platformName.startsWith("Samsung")) {
                phoneManufacturer=SAMSUNG;
                return;
            } else if (platformName.startsWith("LG")) {
                phoneManufacturer=LG;
                return;
            } else if (platformName.startsWith("j2me")) {
                phoneManufacturer=J2ME;
                return;
            } else if (platformName.startsWith("Jbed")) {
                phoneManufacturer=JBED;
//#ifdef FILE_IO
                try { FileIO.createConnection(""); } catch (Exception ex) { }
//#endif
                return;
              } else if (platformName.indexOf("Android") > 0) {
                phoneManufacturer=MICROEMU;
            }else {
                phoneManufacturer=OTHER;
            }
        }
    }
//#if Android
//# 	public static final String compute(){
//# 		final String manufact = System.getProperty("device.manufacturer");
//# 		final String model = System.getProperty("device.model");
//# 		final String manufact_ = manufact.trim().toLowerCase();
//# 		final String model_ = model.trim().toLowerCase();
//# 		String result = "";
//# 		int total = manufact_.length();
//# 		if(total>model_.length()) total = model_.length();
//# 		int matches = 0;
//# 		for(int i=0; i<total; i++){
//# 			if(manufact_.charAt(i) == model_.charAt(i)) matches++;
//# 		}
//# 		final int percentage = matches*100/total;
//# 		if(percentage > 20){
//# 			result = model;
//# 		}else{
//# 			result = manufact+" "+model;
//# 		}
//# 		return result;
//# 	}
//#endif
    public void platformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");

            String sonyJava=System.getProperty("com.sonyericsson.java.platform");
            if (sonyJava!=null) platformName=platformName+"/"+sonyJava;

            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            String id=System.getProperty("device.id");
//#if Android
//#             if (platformName.startsWith("microemu")) {
//#                 if (device != null) {
//#                     platformName=compute()+"/Android "+firmware+"/build: "+id;
//#                 }
//#             }
//#endif
            if (platformName==null) platformName="Motorola";

             if (platformName.startsWith("j2me")) {
                if (device!=null) if (device.startsWith("wtk-emulator")) {
                     platformName=device;
                }
                if (device!=null && firmware!=null)
                    platformName="Motorola"; // buggy v360
		else {
		    // Motorola EZX phones
		    String hostname=System.getProperty("microedition.hostname");
		    if (hostname!=null) {
		        platformName="Motorola-EZX";
		        if (device!=null) {
		    	    // Motorola EZX ROKR
			    hostname=device;
                        }

                        if (hostname.indexOf("(none)")<0)
                         platformName+="/"+hostname;
                    }
		}
             }
 	    //else
		if (platformName.startsWith("Moto")) {
                if (device==null) device=System.getProperty("funlights.product");
                if (device!=null) platformName="Motorola-"+device;
	        try { // thanks vitalyster
		        Class.forName("com.nokia.mid.ui.DeviceControl");
		        platformName="Nokia"; // FS #896
		} catch (Throwable ex) {}
            }

            if (platformName.indexOf("SIE") > -1) {
                platformName=System.getProperty("microedition.platform")+" (NSG)";
            } else if (System.getProperty("com.siemens.OSVersion")!=null) {
                platformName="SIE-"+System.getProperty("microedition.platform")+"/"+System.getProperty("com.siemens.OSVersion");
            }

            try {
                Class.forName("com.samsung.util.Vibration");
                platformName="Samsung";
            } catch (Throwable ex) { }

            try {
                Class.forName("mmpp.media.MediaPlayer");
                platformName="LG";
            } catch (Throwable ex) {
                try {
                    Class.forName("mmpp.phone.Phone");
                    platformName="LG";
                } catch (Throwable ex1) {
                    try {
                        Class.forName("mmpp.lang.MathFP");
                        platformName="LG";
                    } catch (Throwable ex2) {
                        try {
                            Class.forName("mmpp.media.BackLight");
                            platformName="LG";
                        } catch (Throwable ex3) { }
                    }
                }
            }
        }
//#ifndef Android
         int index = platformName.lastIndexOf('/');
            if (index != -1) {
            platformName = platformName.substring(0, index);
      }
//#endif
    }
}
