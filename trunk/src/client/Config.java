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
import java.io.*;
import java.util.*;
import midlet.BombusQD;
import font.FontCache;
import util.StringLoader;
import ui.Time;
import ui.VirtualList;
import io.NvStorage;

/**
 *
 * @author Eugene Stahov,aqent
 */

public class Config {
    private static Config instance;

    public static int KEY_BACK = -11;
    public static int SOFT_LEFT = -6;
    public static int SOFT_RIGHT = -7;

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
    public final static byte WTK=50;
    public final static byte OTHER=99;
    public final static byte NOKIA_5800=44;
//#ifdef TOUCH
    public boolean isTouchPhone;
//#endif

    public boolean flagQuerySign = false;

    public boolean ghostMotor = false;
    public boolean muc119=true;	// before muc 1.19 use muc#owner instead of muc#admin

    public char keyLock='*';
    public char keyVibra='#';

//#ifdef AUTOSTATUS
    public final static int AWAY_OFF = 0;
    public final static int AWAY_LOCK = 1;
    public final static int AWAY_MESSAGE = 2;
    public final static int AWAY_IDLE = 3;

    public int autoAwayType = 0;
    public int autoAwayDelay = 5; //5 minutes
    public boolean setAutoStatusMessage = true;
//#endif

    public boolean cp1251 = true;

//#ifndef WMUC
    public static String defConference = BombusQD.getStrProperty("Def-Room", "qd@conference.jabber.ru");

    public boolean storeConfPresence=false;
    public boolean autoJoinConferences=false;
    public int confMessageCount=20;
//#endif

    // non-volatile values
    public int accountIndex=-1;
    public int def_profile=0;

    public static boolean fullscreen = true;

//#ifdef SMILES
    public boolean smiles = true;
//#endif

    public boolean showOfflineContacts=false;
    public boolean showTransports=true;
    public boolean selfContact=true;
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

    public String lang;
    public boolean capsState=false;
    public int textWrap=1;
    public int autoSubscribe=SUBSCR_ASK;

    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    public int lastProfile=0;
    public boolean lightState=false;

    //default state both panels show, reverse disabled
    public static int panelsState = 2;

//#ifdef POPUPS
    public boolean popUps = false;
//#endif

    public boolean rosterStatus = false;
    public boolean showResources = true;
    public boolean enableVersionOs = true;
    public boolean collapsedGroups = true;
    public boolean eventDelivery = true;
    public int messageLimit = 512;

    // TODO use DETRANSLIT directive
    public boolean transliterateFilenames=false;
    public boolean autoDeTranslit=false;

//#ifdef PEP
    public boolean sndrcvmood = true;
    public boolean rcvtune = true;
    public boolean rcvactivity = true;
//#endif

    public boolean queryExit = false;
    public int notInListDropLevel = NotInListFilter.ALLOW_ALL;
    public boolean showBalloons = false;

    public int msglistLimit = 50;

    public boolean useTabs = false;
    public boolean notifyBlink = true;
    public boolean notifySound = false;
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
    public boolean showClientIcon=true;
//#endif

    public int reconnectCount = 30;
    public int reconnectTime = 10;

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

    public String add_contact_name="@.";

    public boolean savePos=true;

    public int scrollWidth = 5;
    public int minItemHeight = rosterFont * 3;
    public static int contactXOffset = 10;

    public int[] cursorPos = {
                            1,  //RosterToolsMenu 0
                            0,  //RosterItemActions 1
                            1,  //ActivityMenu 2
                            1,  //MoodList 3
                            -1, //...
                            1,
                            1,
                            1,
                            1
                        };

    public boolean isMinimized = false;
    public boolean iconsLeft = true;
    public String path_skin = "";
    public int width_classic = -1;

    //classic chat
    public int classicChatHeight = 140;
    public int lineCount = 300;
    public boolean usePhoneTheme = false;
    public boolean gradient_cursor = false;

    public String langpair="ru==>en";

    public int argb_bgnd = 0;
    public int gmenu_bgnd = 0;
    public int popup_bgnd = 0;
    public int cursor_bgnd = 0;

    public String msgAvatarPath = "";
    public int avatar_cashe_size = 0;
    public boolean auto_queryPhoto = false;
    public boolean autoSaveVcard = false;
    public boolean showAvatarRect = false;
    public boolean autoload_FSPhoto = false;
    public int maxAvatarHeight = 35;
    public int maxAvatarWidth = 35;

    public boolean nokiaReconnectHack = false;
    public static boolean showTimeInMsgs = false;
    public boolean isStatusFirst = false;

    public static boolean animatedSmiles = true;

    public int msgEditType = 0;
    public boolean runningMessage = false;

    public static boolean debug = false;

    public static boolean hasAniSmiles = true;
    public boolean showCollapsedPresences = false;
    public boolean networkAnnotation = true;
    public boolean graphicsMenu = true;
    public int graphicsMenuPosition = 0;

    public boolean createMessageByFive = true;
    public boolean gradientBarLigth = false;
    public boolean shadowBar = true;
    public int gradientBarLight1=120;
    public int gradientBarLight2=5;

    public boolean simpleContacts = false;
    public int userAppLevel = BombusQD.getIntProperty("Diff-Level", 0);

    public static boolean swapSendAndSuspend = false;
    public static boolean cleanConfContacts = false;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            instance.loadFromStorage();

            FontCache.roster = rosterFont;
            FontCache.baloon = baloonFont;
            FontCache.menu = menuFont;
            FontCache.msg = msgFont;
            FontCache.bar = barFont;
        }
        return instance;
    }

    private Config() {
        getPhoneManufacturer();
        VirtualList.phoneManufacturer=phoneManufacturer;

	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	gmtOffset=gmtloc;

	short greenKeyCode=-1000;

        switch (phoneManufacturer) {
            case SONYE:
             //prefetch images
                RosterIcons.getInstance();
                ActionsIcons.getInstance();
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
                try { Thread.sleep(50); } catch (InterruptedException e){}
                allowMinimize=true;
                greenKeyCode=-10;
                break;
            case SONYE_M600:
                KEY_BACK=-11;
                break;
            case WTK:
                greenKeyCode=-10;
                break;
            case NOKIA:
                KEY_BACK=VirtualList.NOKIA_PEN;
                greenKeyCode=-10;
                allowMinimize=true;
                nokiaReconnectHack=true;
                break;
            case SIEMENS:
            case SIEMENS2:
                keyLock='#';
                keyVibra='*';
                KEY_BACK=-4;
                greenKeyCode=VirtualList.SIEMENS_GREEN;
                break;
            case WINDOWS:
                greenKeyCode=-5;
                VirtualList.keyClear=8;
                graphicsMenu = false;
                break;
            case MOTO:
                ghostMotor=true;
                greenKeyCode=-10;
                break;
            case MOTOEZX:
                VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
                KEY_BACK=VirtualList.MOTOE680_REALPLAYER;
		greenKeyCode=-31;
                break;
        }
	VirtualList.greenKeyCode=greenKeyCode;
    }

    protected void loadFromStorage(){
        loadBoolean();
        loadBoolean_();
        loadInt();
        loadUTF();

        lastProfile = profile = def_profile;
        if (lastProfile == AlertProfile.VIBRA) {
            lastProfile = 0;
        }
	updateTime();
//#ifdef MEMORY_MONITOR
//# 	VirtualList.memMonitor=memMonitor;
//#endif
    }


//#ifdef TOUCH
    public void menuFont() {
        if (isTouchPhone) {
            graphicsMenuPosition = 1; //left

            rosterFont = FontCache.LARGE;
            baloonFont = FontCache.LARGE;
            menuFont = FontCache.LARGE;
            msgFont = FontCache.LARGE;
            barFont = FontCache.LARGE;

            FontCache.roster = rosterFont;
            FontCache.baloon = baloonFont;
            FontCache.menu = menuFont;
            FontCache.msg = msgFont;
            FontCache.bar = barFont;

            scrollWidth = 12;

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
        for (int i=0; i<files[0].size(); i++) {
            String langCode=(String) files[0].elementAt(i);
            if (lang.equals(langCode))
        	return (String) files[1].elementAt(i);
        }
        return null;
        //return "/lang/ru.txt"; //unknown language ->en
    }

    // TODO remove "minItemHeight" loading
    protected void loadBoolean() {
        DataInputStream inputStream = NvStorage.ReadFileRecord("confBoolean", 0);
        try {
            showOfflineContacts = inputStream.readBoolean();
            fullscreen = inputStream.readBoolean();
//#ifdef SMILES
            smiles = inputStream.readBoolean();
//#endif
            showTransports = inputStream.readBoolean();
            selfContact = inputStream.readBoolean();
            collapsedGroups = inputStream.readBoolean();
            ignore = inputStream.readBoolean();
            eventComposing = inputStream.readBoolean();
            autoLogin = inputStream.readBoolean();
            autoJoinConferences = inputStream.readBoolean();
            popupFromMinimized = inputStream.readBoolean();
            notifyBlink = inputStream.readBoolean();
//#ifdef MEMORY_MONITOR
//# 	    memMonitor=inputStream.readBoolean();
//#endif
            autoFocus = inputStream.readBoolean();
            storeConfPresence = inputStream.readBoolean();
            capsState = inputStream.readBoolean();

            cp1251 = inputStream.readBoolean();

            fileTransfer = inputStream.readBoolean(); //newMenu
            lightState = inputStream.readBoolean();
            notifySound = inputStream.readBoolean();
//#ifdef AUTOSTATUS
            setAutoStatusMessage = inputStream.readBoolean();
//#endif

            cleanConfContacts = inputStream.readBoolean();
//#ifdef POPUPS
            popUps = inputStream.readBoolean();
//#endif
            showResources = inputStream.readBoolean();
            enableVersionOs = inputStream.readBoolean();
            eventDelivery = inputStream.readBoolean();

            transliterateFilenames = inputStream.readBoolean();
            rosterStatus = inputStream.readBoolean();
            queryExit = inputStream.readBoolean();
            notifyPicture = inputStream.readBoolean();
            showBalloons = inputStream.readBoolean();
            useTabs = inputStream.readBoolean();
            useBoldFont = inputStream.readBoolean();

            IQNotify = inputStream.readBoolean();
//#ifdef PEP
            sndrcvmood = inputStream.readBoolean();
//#endif
//#ifdef CLIPBOARD
            useClipBoard = inputStream.readBoolean();
//#endif
//#ifdef PEP
            rcvtune = inputStream.readBoolean();
//#endif
            autoDeTranslit = inputStream.readBoolean();
//#ifdef CLIENTS_ICONS
            showClientIcon = inputStream.readBoolean();
//#endif
            executeByNum = inputStream.readBoolean();
            showNickNames = inputStream.readBoolean();
            adhoc = inputStream.readBoolean();
            createMessageByFive = inputStream.readBoolean();
            gradientBarLigth = inputStream.readBoolean();
            shadowBar = inputStream.readBoolean();

            // free
            inputStream.readBoolean();

            simpleContacts = inputStream.readBoolean();
            minItemHeight = inputStream.readInt();

            swapSendAndSuspend = inputStream.readBoolean();

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

    // TODO merge with loadBoolean;
    protected void loadBoolean_(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("confBoolean_", 0);
	try {
//#ifdef PEP
            rcvactivity = inputStream.readBoolean();
//#endif
            oldSE = inputStream.readBoolean();
            showTimeTraffic = inputStream.readBoolean();

            // free
            inputStream.readBoolean();
            inputStream.readBoolean();

            hideMessageIcon = inputStream.readBoolean();
            auto_queryPhoto = inputStream.readBoolean();

            // free
            inputStream.readBoolean();
            inputStream.readBoolean();

            savePos = inputStream.readBoolean();

            // free
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();

            iconsLeft=inputStream.readBoolean();
            usePhoneTheme=inputStream.readBoolean();
            gradient_cursor=inputStream.readBoolean();
            autoSaveVcard=inputStream.readBoolean();
            showAvatarRect=inputStream.readBoolean();

            autoload_FSPhoto=inputStream.readBoolean();
            nokiaReconnectHack=inputStream.readBoolean();
            showTimeInMsgs=inputStream.readBoolean();

            // free
            inputStream.readBoolean();

            useItalic=inputStream.readBoolean();

            // free
            inputStream.readBoolean();
            inputStream.readBoolean();

            //modules
            module_contacts=inputStream.readBoolean();
            module_messages=inputStream.readBoolean();
            module_network=inputStream.readBoolean();
            module_graphics=inputStream.readBoolean();
            module_app=inputStream.readBoolean();
            userKeys=inputStream.readBoolean();
            module_autostatus=inputStream.readBoolean();
            module_classicchat=inputStream.readBoolean();
            module_theme=inputStream.readBoolean();
            module_history=inputStream.readBoolean();
            module_fonts=inputStream.readBoolean();
            module_ie=inputStream.readBoolean();
            module_notify=inputStream.readBoolean();
            module_tasks=inputStream.readBoolean();
            module_avatars=inputStream.readBoolean();

            animatedSmiles=inputStream.readBoolean();
            runningMessage=inputStream.readBoolean();

            debug=inputStream.readBoolean();

            // free
            inputStream.readBoolean();

            showCollapsedPresences = inputStream.readBoolean();
            networkAnnotation = inputStream.readBoolean();

            // free
            inputStream.readBoolean();

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

    protected void loadInt(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("confInt", 0);
	try {
	    accountIndex=inputStream.readInt();
	    def_profile=inputStream.readInt();
	    gmtOffset=inputStream.readInt();
            rosterFont=inputStream.readInt();
            msgFont=inputStream.readInt();
            notInListDropLevel=inputStream.readInt();
	    textWrap=inputStream.readInt();
            loginstatus=inputStream.readInt();
//#ifdef AUTOSTATUS
            autoAwayDelay=inputStream.readInt();
//#endif
            panelsState=inputStream.readInt();
            confMessageCount=inputStream.readInt();
//#ifdef AUTOSTATUS
            autoAwayType=inputStream.readInt();
//#endif
            messageLimit=inputStream.readInt();
            msglistLimit=inputStream.readInt();
            autoSubscribe=inputStream.readInt();
            barFont=inputStream.readInt();
            baloonFont=inputStream.readInt();
            resolvedPort=inputStream.readInt();
            reconnectCount=inputStream.readInt();
            reconnectTime=inputStream.readInt();
            maxAvatarHeight=inputStream.readInt();
//#ifdef BACK_IMAGE
            backImgType=inputStream.readInt();
//#endif
            scrollWidth=inputStream.readInt();
            classicChatHeight=inputStream.readInt();
            lineCount=inputStream.readInt();
            argb_bgnd=inputStream.readInt();
            gmenu_bgnd=inputStream.readInt();
            popup_bgnd=inputStream.readInt();
            cursor_bgnd=inputStream.readInt();
            avatar_cashe_size=inputStream.readInt();
            maxAvatarWidth=inputStream.readInt();
            msgEditType=inputStream.readInt();
            graphicsMenuPosition=inputStream.readInt();
            gradientBarLight1=inputStream.readInt();
            gradientBarLight2=inputStream.readInt();
            userAppLevel = inputStream.readInt();
            menuFont = inputStream.readInt();
            contactXOffset = inputStream.readInt();
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

    public static boolean module_contacts = true;
    public static boolean module_messages = true;
    public static boolean module_network = true;
    public static boolean module_graphics = true;
    public static boolean module_app = true;
    public static boolean userKeys = false;
    public static boolean module_autostatus = false;
    public static boolean module_classicchat = false;
    public static boolean module_theme = true;
    public static boolean module_history = false;
    public static boolean module_fonts = true;
    public static boolean module_ie = false;
    public static boolean module_notify = true;
    public static boolean module_tasks = false;
    public static boolean module_avatars = false;

    private boolean saveBoolean() {
       	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	try {
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
//#ifdef SMILES
	    outputStream.writeBoolean(smiles);
//#endif
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(collapsedGroups);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
            outputStream.writeBoolean(popupFromMinimized);
	    outputStream.writeBoolean(notifyBlink);
//#ifdef MEMORY_MONITOR
//# 	    outputStream.writeBoolean(memMonitor);
//#endif
            outputStream.writeBoolean(autoFocus);
            outputStream.writeBoolean(storeConfPresence);
            outputStream.writeBoolean(capsState);

            outputStream.writeBoolean(cp1251);

            outputStream.writeBoolean(fileTransfer); //newMenu
            outputStream.writeBoolean(lightState);
            outputStream.writeBoolean(notifySound);

//#ifdef AUTOSTATUS
            outputStream.writeBoolean(setAutoStatusMessage);
//#endif

            outputStream.writeBoolean(cleanConfContacts);
//#ifdef POPUPS
            outputStream.writeBoolean(popUps);
//#endif
            outputStream.writeBoolean(showResources);
            outputStream.writeBoolean(enableVersionOs);
            outputStream.writeBoolean(eventDelivery);

            outputStream.writeBoolean(transliterateFilenames);
            outputStream.writeBoolean(rosterStatus);
            outputStream.writeBoolean(queryExit);
            outputStream.writeBoolean(notifyPicture);
            outputStream.writeBoolean(showBalloons);

            outputStream.writeBoolean(useTabs);
            outputStream.writeBoolean(useBoldFont);

            outputStream.writeBoolean(IQNotify);
//#ifdef PEP
            outputStream.writeBoolean(sndrcvmood);
//#endif
//#ifdef CLIPBOARD
            outputStream.writeBoolean(useClipBoard);
//#endif
//#ifdef PEP
            outputStream.writeBoolean(rcvtune);
//#endif
            outputStream.writeBoolean(autoDeTranslit);
//#ifdef CLIENTS_ICONS
            outputStream.writeBoolean(showClientIcon);
//#endif
            outputStream.writeBoolean(executeByNum);
            outputStream.writeBoolean(showNickNames);
            outputStream.writeBoolean(adhoc);
            outputStream.writeBoolean(createMessageByFive);
            outputStream.writeBoolean(gradientBarLigth);
            outputStream.writeBoolean(shadowBar);

           // free
            outputStream.writeBoolean(false);

            outputStream.writeBoolean(simpleContacts);
			outputStream.writeInt(minItemHeight);

            outputStream.writeBoolean(swapSendAndSuspend);

	} catch (IOException e) { }
	return NvStorage.writeFileRecord(outputStream, "confBoolean", 0, true);
   }

   private boolean saveBoolean_(){
       	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	try {
//#ifdef PEP
            outputStream.writeBoolean(rcvactivity);
//#endif
            outputStream.writeBoolean(oldSE);
            outputStream.writeBoolean(showTimeTraffic);

            // free
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);

            outputStream.writeBoolean(hideMessageIcon);
            outputStream.writeBoolean(auto_queryPhoto);

            // free
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);

            outputStream.writeBoolean(savePos);

            // free
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);

            outputStream.writeBoolean(iconsLeft);
            outputStream.writeBoolean(usePhoneTheme);
            outputStream.writeBoolean(gradient_cursor);
            outputStream.writeBoolean(autoSaveVcard);
            outputStream.writeBoolean(showAvatarRect);

            outputStream.writeBoolean(autoload_FSPhoto);
            outputStream.writeBoolean(nokiaReconnectHack);
            outputStream.writeBoolean(showTimeInMsgs);

            // free
            outputStream.writeBoolean(false);

            outputStream.writeBoolean(useItalic);

            // free
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);

            //modules
            outputStream.writeBoolean(module_contacts);
            outputStream.writeBoolean(module_messages);
            outputStream.writeBoolean(module_network);
            outputStream.writeBoolean(module_graphics);
            outputStream.writeBoolean(module_app);
            outputStream.writeBoolean(userKeys);
            outputStream.writeBoolean(module_autostatus);
            outputStream.writeBoolean(module_classicchat);
            outputStream.writeBoolean(module_theme);
            outputStream.writeBoolean(module_history);
            outputStream.writeBoolean(module_fonts);
            outputStream.writeBoolean(module_ie);
            outputStream.writeBoolean(module_notify);
            outputStream.writeBoolean(module_tasks);
            outputStream.writeBoolean(module_avatars);

            outputStream.writeBoolean(animatedSmiles);
            outputStream.writeBoolean(runningMessage);

            outputStream.writeBoolean(debug);

            // free
            outputStream.writeBoolean(false);

            outputStream.writeBoolean(showCollapsedPresences);
            outputStream.writeBoolean(networkAnnotation);

            // free
            outputStream.writeBoolean(false);

	} catch (IOException e) { }
	return NvStorage.writeFileRecord(outputStream, "confBoolean_", 0, true);
   }

   public boolean saveInt(){
       	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeInt(def_profile);
	    outputStream.writeInt(gmtOffset);

            outputStream.writeInt(rosterFont);
            outputStream.writeInt(msgFont);
            outputStream.writeInt(notInListDropLevel);
	    outputStream.writeInt(textWrap);
            outputStream.writeInt(loginstatus);
//#ifdef AUTOSTATUS
            outputStream.writeInt(autoAwayDelay);
//#endif
            outputStream.writeInt(panelsState);
            outputStream.writeInt(confMessageCount);
//#ifdef AUTOSTATUS
            outputStream.writeInt(autoAwayType);
//#endif
            outputStream.writeInt(messageLimit);
            outputStream.writeInt(msglistLimit);
            outputStream.writeInt(autoSubscribe);
            outputStream.writeInt(barFont);
            outputStream.writeInt(baloonFont);
            outputStream.writeInt(resolvedPort);
            outputStream.writeInt(reconnectCount);
            outputStream.writeInt(reconnectTime);
            outputStream.writeInt(maxAvatarHeight);
//#ifdef BACK_IMAGE
            outputStream.writeInt(backImgType);
//#endif
            outputStream.writeInt(scrollWidth);
            outputStream.writeInt(classicChatHeight);
            outputStream.writeInt(lineCount);
            outputStream.writeInt(argb_bgnd);
            outputStream.writeInt(gmenu_bgnd);
            outputStream.writeInt(popup_bgnd);
            outputStream.writeInt(cursor_bgnd);
            outputStream.writeInt(avatar_cashe_size);
            outputStream.writeInt(maxAvatarWidth);
            outputStream.writeInt(msgEditType);
            outputStream.writeInt(graphicsMenuPosition);
            outputStream.writeInt(gradientBarLight1);
            outputStream.writeInt(gradientBarLight2);
            outputStream.writeInt(userAppLevel);
            outputStream.writeInt(menuFont);
            outputStream.writeInt(contactXOffset);
	} catch (IOException e) { }
	return NvStorage.writeFileRecord(outputStream, "confInt", 0, true);
    }

    public boolean saveUTF() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
        try {
            outputStream.writeUTF(msgAvatarPath);
            outputStream.writeUTF(defConference);
            outputStream.writeUTF(lang);
            outputStream.writeUTF(verHash);
            outputStream.writeUTF(resolvedHost);
            outputStream.writeUTF(moodText);
            outputStream.writeUTF(moodName);
            outputStream.writeUTF(actText);
            outputStream.writeUTF(actDescr);
            outputStream.writeUTF(actCat);

            // free
	    //#ifdef FILE_IO
            outputStream.writeUTF(backImgPath);
	    //#endif

            outputStream.writeUTF(path_skin);
        } catch (IOException e) {
        }
        return NvStorage.writeFileRecord(outputStream, "confUtf", 0, true);
    }

    protected void loadUTF() {
        DataInputStream inputStream = NvStorage.ReadFileRecord("confUtf", 0);
        try {
            msgAvatarPath = inputStream.readUTF();
            defConference = inputStream.readUTF();
            lang = inputStream.readUTF();
            verHash = inputStream.readUTF();
            resolvedHost = inputStream.readUTF();
            moodText = inputStream.readUTF();
            moodName = inputStream.readUTF();
            actText = inputStream.readUTF();
            actDescr = inputStream.readUTF();
            actCat = inputStream.readUTF();

            // free
	    //#ifdef FILE_IO
            backImgPath = inputStream.readUTF();
	    //#endif

            path_skin = inputStream.readUTF();
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

    private Timer timer = null;

    private class Saver extends TimerTask {
        private int count = 0;
        public void run() {
            count++;
            switch (count) {
                case 1:
                    break;
                case 2:
                    saveBoolean();
                    break;
                case 3:
                    saveBoolean_();
                    break;
                case 4:
                    saveUTF();
                    break;
                case 5:
                    saveInt();
                    break;
                case 6:
                    timer.cancel();
                    timer = null;
                    count = 0;
                    break;
            }
        }
    }

    public void saveToStorage() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new Saver(), 10, 100);
        } else {
            timer.cancel();
        }
    }

    public void updateTime(){
	Time.setOffset(gmtOffset);
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
                try { FileIO f=FileIO.createConnection(""); } catch (Exception ex) { }
//#endif
                return;
              } else if (platformName.indexOf("Android") > 0) {
                phoneManufacturer=MICROEMU;
            }else {
                phoneManufacturer=OTHER;
            }
        }
    }

    public void platformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");

            String sonyJava=System.getProperty("com.sonyericsson.java.platform");
            if (sonyJava!=null) platformName=platformName+"/"+sonyJava;

            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            String id=System.getProperty("device.id");
            if (platformName.startsWith("microemu")) {
                if (device != null) {
                    platformName=device+"|Android OS "+firmware+"|build "+id;
                }
            }
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
         int index = platformName.lastIndexOf('/');
            if (index != -1) {
            platformName = platformName.substring(0, index);
      }
    }
}
