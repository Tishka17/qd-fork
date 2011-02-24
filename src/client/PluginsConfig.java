/*
 * PluginsConfig.java
 *
 * Created on 28.07.2009, 15:47
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
import xmpp.EntityCaps;
import colors.ColorSelector;
import colors.ColorTheme;
import javax.microedition.midlet.MIDlet;
import ui.VirtualList;
import ui.controls.form.PluginBox;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import locale.SR;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Displayable;
import menu.MenuListener;
import ui.GMenu;
import ui.controls.ScrollBar;
import ui.GMenuConfig;
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
import util.StringLoader;
import java.util.Vector;
import images.SmilesIcons;
import ui.ImageList;
import history.HistoryConfig;
import java.util.*;
import ui.MainBar;
/**
 *
 * @author aqent
 */
public class PluginsConfig extends DefForm implements MenuListener
{
    private Display display;
    private int pos = -1;
    private static Config cf = midlet.BombusQD.cf;

    private CheckBox showOfflineContacts;
    private CheckBox selfContact;
    private CheckBox showTransports;
    private CheckBox ignore;
    private CheckBox collapsedGroups;
    private CheckBox autoFocus;
    private CheckBox showResources;
    private CheckBox useBoldFont;
    private CheckBox rosterStatus;
//#ifdef CLIENTS_ICONS
    private CheckBox showClientIcon;
//#endif
    private CheckBox dont_loadMC;
    private CheckBox gradient_cursor;  
//#ifdef SMILES
    private CheckBox smiles;
//#endif    
    private CheckBox eventComposing;
    private CheckBox capsState;
    private CheckBox storeConfPresence;
    private CheckBox createMessageByFive;
    private CheckBox showCollapsedPresences;
    private CheckBox timePresence;
    private CheckBox autoScroll;
    private CheckBox useTabs;
//#ifdef DETRANSLIT
//#     private CheckBox autoDetranslit;
//#endif
    
//#ifdef PEP
//#     private CheckBox sndrcvmood;
//#     private CheckBox rcvtune;
//#     private CheckBox rcvactivity;
//#endif
    
//#ifdef RUNNING_MESSAGE
//#     private CheckBox runningMessage;
//#endif
//#ifdef POPUPS
    private CheckBox popUps;
//#endif

   // private CheckBox graphicsMenu;
    private CheckBox showBaloons;
    private CheckBox animatedSmiles;
    private CheckBox eventDelivery;
    private CheckBox networkAnnotation;
    //private CheckBox metaContacts;
    private CheckBox executeByNum;
    private CheckBox sendMoodInMsg;
    private CheckBox savePos;
    private CheckBox boldNicks;    
    private CheckBox selectOutMessages;
//#ifdef CLIPBOARD
//#     private CheckBox useClipBoard;
//#endif
    
    private CheckBox autoLogin;
    private CheckBox useLowMemory_userotator;  
  //private CheckBox animateMenuAndRoster;
    private CheckBox useLowMemory_iconmsgcollapsed;
    private CheckBox iconsLeft;    
    private CheckBox autoJoinConferences;
    
    private CheckBox nokiaReconnectHack;
//#ifdef FILE_TRANSFER
    private CheckBox fileTransfer;
//#endif
//#ifdef ADHOC
//#     private CheckBox adhoc;
//#endif
    
    private CheckBox fullscr;
//#ifdef MEMORY_MONITOR
//#     private CheckBox memMon;
//#endif
    private CheckBox enableVersionOs;
    private CheckBox queryExit;
    private CheckBox popupFromMinimized;
    private CheckBox drawScrollBgnd; 
    private CheckBox gradientBarLigth;
    private CheckBox shadowBar;
    private CheckBox autoLoadTransports;
//#ifdef AUTOSTATUS
//#     private CheckBox awayStatus;
//#endif
    private CheckBox drawMenuCommand;
    private CheckBox showNickNames;
    private CheckBox oldSE;
    //private CheckBox useClassicChat; 
    private CheckBox use_phone_theme;
    private CheckBox simpleContacts;
    

    private TrackItem gradientBarLight1; 
    private TrackItem gradientBarLight2; 
    

    
    
    
    private DropChoiceBox subscr;
    private DropChoiceBox nil;
    private DropChoiceBox msgEditType;
//#if LOGROTATE
//#     private NumberInput messageCountLimit;
//#endif
    private NumberInput messageLimit;
    private NumberInput msglistLimit;
    
    private NumberInput reconnectCount;
    private NumberInput reconnectTime;
    private NumberInput fieldGmt; 
    private NumberInput scrollWidth; 
	private NumberInput minItemHeight;
    private DropChoiceBox textWrap;
    private DropChoiceBox langFiles;
    private DropChoiceBox bgnd_image;    
    private DropChoiceBox graphicsMenuPosition;  
//#ifdef AUTOSTATUS
//#     private DropChoiceBox autoAwayType;
//#     private NumberInput fieldAwayDelay; 
//#endif
    private DropChoiceBox panels;
    private NumberInput classic_chat_height;     
    private NumberInput line_count;    
    
    private static Vector langs[];
    

    private PluginBox contacts;    
    private PluginBox messages;    
    private PluginBox network;
    private PluginBox graphics;    
    private PluginBox app;
    private PluginBox userKeys;    
    private PluginBox autostatus;
    private PluginBox classicchat;
    private PluginBox debug;
    private PluginBox theme;
    private PluginBox cashe;
    
    private PluginBox history;
    private PluginBox fonts; 
//#ifdef IMPORT_EXPORT
//#     private PluginBox ie;  
//#endif
    private PluginBox notify; 
    private PluginBox tasks;
    private PluginBox avatars;  

    
    private SimpleString str;
    private SimpleString appLevel;    
    private CheckBox appLevelName;    
    private SimpleString appLevelID;
    private SimpleString gradient_light1;
    private SimpleString gradient_light2;
    private StringBuffer sb = new StringBuffer(0);

    private void destroyItems() {
        //#ifdef CONSOLE
//#         midlet.BombusQD.debug.add("::destroy options..", 10);
        //#endif
         showOfflineContacts = null;
         selfContact = null;
         showTransports = null;
         ignore = null;
         collapsedGroups = null;
         autoFocus = null;
         showResources = null;
         useBoldFont = null;
         rosterStatus = null;
//#ifdef CLIENTS_ICONS
         showClientIcon = null;
//#endif
         //dont_loadMC;
         gradient_cursor = null;
         smiles = null;
         eventComposing = null;
         capsState = null;
         storeConfPresence = null;
         createMessageByFive = null;
         showCollapsedPresences = null;
         timePresence = null;
         autoScroll = null;
         useTabs = null;
         autoDetranslit = null;
//#ifdef PEP        
//#           sndrcvmood = null;
//#           rcvtune = null;
//#           rcvactivity = null;
//#endif
          runningMessage = null;
          popUps = null;
          //graphicsMenu = null;
          showBaloons = null;
          animatedSmiles = null;
          eventDelivery = null;
          networkAnnotation = null;
          //metaContacts = null;
          executeByNum = null;
          //sendMoodInMsg = null;
          savePos = null;
          boldNicks = null;
          selectOutMessages = null;
          useClipBoard = null;
          autoLogin = null;
          useLowMemory_userotator = null;
          useLowMemory_iconmsgcollapsed = null;
          iconsLeft = null;
          autoJoinConferences = null;
          nokiaReconnectHack = null;
//#ifdef FILE_TRANSFER
          fileTransfer = null;
//#endif   
//#ifdef ADHOC
//#           adhoc = null;
//#endif
          fullscr = null;
//#ifdef MEMORY_MONITOR
//#           memMon = null;
//#endif
          enableVersionOs = null;
          queryExit = null;
          popupFromMinimized = null;
          drawScrollBgnd = null;
          gradientBarLigth = null;
          shadowBar = null;
          autoLoadTransports = null;
//#ifdef AUTOSTATUS
//#           awayStatus = null;
//#endif
          drawMenuCommand = null;
          showNickNames = null;
          oldSE = null;
          use_phone_theme = null;
          simpleContacts = null;
          gradientBarLight1 = null;
          gradientBarLight2 = null;
          appLevel = null;
          appLevel = null;
          str = null;
          gradient_light1 = null;
          gradient_light2 = null;
          
          
          contacts = null;
          messages = null;
          notify = null;
          network = null;
          app = null;
          graphics = null;
          theme = null;
          fonts = null;
          autostatus = null;
          userKeys = null;
          avatars = null;
          history = null;
 //#ifdef IMPORT_EXPORT         
//#           ie = null;
//#endif
          tasks = null;
          classicchat = null;
          debug = null;
          
          if ( subscr != null ) subscr = null;
          if ( nil != null ) nil = null;
          if ( msgEditType != null ) msgEditType = null;
          if ( textWrap != null ) textWrap = null;
          if ( langFiles != null ) langFiles = null;
          if ( bgnd_image != null ) bgnd_image = null;    
          if ( graphicsMenuPosition != null ) graphicsMenuPosition = null;
    
          if ( messageLimit != null ) messageLimit = null;
          if ( msglistLimit != null ) msglistLimit = null;
          if ( reconnectCount != null ) reconnectCount = null;
          if ( reconnectTime != null ) reconnectTime = null;
          if ( fieldGmt != null ) fieldGmt = null; 
          if ( scrollWidth != null ) scrollWidth = null; 
		  if ( minItemHeight != null ) minItemHeight = null;
  
//#ifdef AUTOSTATUS
//#           if ( autoAwayType != null ) autoAwayType = null;
//#           if ( fieldAwayDelay != null ) fieldAwayDelay = null; 
//#endif
          if ( panels != null ) panels = null;
          if ( classic_chat_height != null ) classic_chat_height = null;     
          if ( line_count != null ) line_count = null;          
          
          Runtime.getRuntime().gc();
          //#ifdef CONSOLE
//#           midlet.BombusQD.debug.add("::destroy options..ok", 10);
          //#endif
    }
    
    
    private void initItems() {
//#ifdef DEBUG
//#         System.out.println("init items");
//#endif
         showOfflineContacts = new CheckBox(SR.get(SR.MS_OFFLINE_CONTACTS), cf.showOfflineContacts);
         selfContact = new CheckBox(SR.get(SR.MS_SELF_CONTACT), cf.selfContact);
         showTransports = new CheckBox(SR.get(SR.MS_TRANSPORTS), cf.showTransports);
         ignore = new CheckBox(SR.get(SR.MS_IGNORE_LIST), cf.ignore);
         collapsedGroups = new CheckBox(SR.get(SR.MS_COLLAPSED_GROUPS), cf.collapsedGroups);
         autoFocus = new CheckBox(SR.get(SR.MS_AUTOFOCUS), cf.autoFocus);
         showResources = new CheckBox(SR.get(SR.MS_SHOW_RESOURCES), cf.showResources);
         useBoldFont = new CheckBox(SR.get(SR.MS_BOLD_FONT), cf.useBoldFont);
         rosterStatus = new CheckBox(SR.get(SR.MS_SHOW_STATUSES), cf.rosterStatus);
//#ifdef CLIENTS_ICONS
         showClientIcon = new CheckBox(SR.get(SR.MS_SHOW_CLIENTS_ICONS), cf.showClientIcon);
//#endif
         //dont_loadMC;
         gradient_cursor = new CheckBox(SR.get(SR.MS_GRADIENT_CURSOR),cf.gradient_cursor);    
         smiles = new CheckBox(SR.get(SR.MS_SMILES), cf.smiles);

         eventComposing = new CheckBox(SR.get(SR.MS_COMPOSING_EVENTS), cf.eventComposing); 
         capsState = new CheckBox(SR.get(SR.MS_CAPS_STATE), cf.capsState); 
         storeConfPresence = new CheckBox(SR.get(SR.MS_STORE_PRESENCE), cf.storeConfPresence); 
         createMessageByFive = new CheckBox(SR.get(SR.MS_USE_FIVE_TO_CREATEMSG), cf.createMessageByFive); 
         showCollapsedPresences = new CheckBox(SR.get(SR.MS_COLLAPSE_PRESENCE), cf.showCollapsedPresences); 
         timePresence = new CheckBox(SR.get(SR.MS_SHOW_PRS_TIME), cf.timePresence);
         autoScroll = new CheckBox(SR.get(SR.MS_AUTOSCROLL), cf.autoScroll);
         useTabs = new CheckBox(SR.get(SR.MS_EMULATE_TABS), cf.useTabs); 
         autoDetranslit = new CheckBox(SR.get(SR.MS_AUTODETRANSLIT), cf.autoDeTranslit); 
//#ifdef PEP        
//#           sndrcvmood = new CheckBox(SR.get(SR.MS_USERMOOD), cf.sndrcvmood);
//#           rcvtune = new CheckBox(SR.get(SR.MS_USERTUNE), cf.rcvtune); 
//#           rcvactivity = new CheckBox(SR.get(SR.MS_USERACTIVITY), cf.rcvactivity);
//#endif
          runningMessage = new CheckBox(SR.get(SR.MS_RUNNING_MESSAGE), cf.runningMessage);//ticker obj
          popUps = new CheckBox(SR.get(SR.MS_POPUPS), cf.popUps); 

          //graphicsMenu = new CheckBox(SR.get(SR.MS_GR_MENU), cf.graphicsMenu);
          showBaloons = new CheckBox(SR.get(SR.MS_SHOW_BALLONS), cf.showBalloons); 
          animatedSmiles = new CheckBox(SR.get(SR.MS_ANI_SMILES), cf.animatedSmiles); 
          eventDelivery = new CheckBox(SR.get(SR.MS_DELIVERY), cf.eventDelivery); 
          networkAnnotation = new CheckBox(SR.get(SR.MS_CONTACT_ANNOTATIONS), cf.networkAnnotation);
          //metaContacts = new CheckBox(SR.get(SR.MS_METACONTACTS) +"[FROZEN]", cf.metaContacts);
          executeByNum = new CheckBox(SR.get(SR.MS_EXECUTE_MENU_BY_NUMKEY), cf.executeByNum); 
          //sendMoodInMsg = new CheckBox(SR.get(SR.MS_MOOD_IN_MSG, cf.sendMoodInMsg);
          savePos = new CheckBox(SR.get(SR.MS_SAVE_CURSOR), cf.savePos);
//	  //#ifdef COLOR_TUNE
           boldNicks = new CheckBox(SR.get(SR.MS_BOLD_AND_COLORS_NICKS), cf.boldNicks); 
//	  //#endif
          selectOutMessages = new CheckBox(SR.get(SR.MS_SELECT_OUT_MESSAGES), cf.selectOutMessages);
          useClipBoard = new CheckBox(SR.get(SR.MS_CLIPBOARD), cf.useClipBoard); 

          autoLogin = new CheckBox(SR.get(SR.MS_AUTOLOGIN), cf.autoLogin); 
          useLowMemory_userotator = new CheckBox(SR.get(SR.MS_ANIMATION), cf.useLowMemory_userotator);
          useLowMemory_iconmsgcollapsed = new CheckBox(SR.get(SR.MS_ICON_COLP), cf.useLowMemory_iconmsgcollapsed);
          iconsLeft = new CheckBox(SR.get(SR.MS_CLIENT_ICONS_LEFT), cf.iconsLeft);
          autoJoinConferences = new CheckBox(SR.get(SR.MS_AUTO_CONFERENCES), cf.autoJoinConferences); 
          nokiaReconnectHack = new CheckBox(SR.get(SR.MS_NOKIA_RECONNECT_HACK), cf.nokiaReconnectHack);
//#ifdef FILE_TRANSFER
          fileTransfer = new CheckBox(SR.get(SR.MS_FILE_TRANSFERS), cf.fileTransfer); 
//#endif   
//#ifdef ADHOC
//#           adhoc = new CheckBox(SR.get(SR.MS_ADHOC), cf.adhoc); 
//#endif
          fullscr = new CheckBox(SR.get(SR.MS_FULLSCREEN), cf.fullscreen); 
//#ifdef MEMORY_MONITOR
//#           memMon = new CheckBox(SR.get(SR.MS_HEAP_MONITOR), cf.memMonitor);
//#endif
          enableVersionOs = new CheckBox(SR.get(SR.MS_SHOW_HARDWARE), cf.enableVersionOs);
          queryExit = new CheckBox(SR.get(SR.MS_CONFIRM_EXIT), cf.queryExit); 
          popupFromMinimized = new CheckBox(SR.get(SR.MS_ENABLE_POPUP), cf.popupFromMinimized);
          drawScrollBgnd = new CheckBox(SR.get(SR.MS_BGND_SCROLL),cf.drawScrollBgnd); 
          gradientBarLigth = new CheckBox(SR.get(SR.MS_USE_LIGHT_TO_DRWPANELS), cf.gradientBarLigth);
          shadowBar = new CheckBox(SR.get(SR.MS_SHADOW_BAR), cf.shadowBar);
          autoLoadTransports = new CheckBox(SR.get(SR.MS_AUTOCONNECT_TRANSPORTS), cf.autoLoadTransports);
//#ifdef AUTOSTATUS
//#           awayStatus=new CheckBox(SR.get(SR.MS_AUTOSTATUS_MESSAGE), cf.setAutoStatusMessage);
//#endif
          drawMenuCommand = new CheckBox(SR.get(SR.MS_SHOW_TIME_TRAFFIC), cf.showTimeTraffic); 
          showNickNames = new CheckBox(SR.get(SR.MS_SHOW_NACKNAMES), cf.showNickNames); 
          oldSE = new CheckBox(SR.get(SR.MS_KEYS_FOR_OLD_SE), cf.oldSE);
          use_phone_theme = new CheckBox(SR.get(SR.MS_CLCHAT_BGNG_PHONE), cf.use_phone_theme);
          simpleContacts = new CheckBox(SR.get(SR.MS_SIMPLE_CONTACTS_DRAW), cf.simpleContacts);
          
          
          gradientBarLight1=new TrackItem(cf.gradientBarLight1/10, 20, null, false);
          gradientBarLight2=new TrackItem(cf.gradientBarLight2/10, 20, null, false);
          appLevel = null;
          appLevel = new SimpleString(SR.get(SR.MS_USER_APP_LEVEL));
          str = new SimpleString(SR.get(SR.MS_ADVANCED_OPT), true);
          gradient_light1 = new SimpleString(SR.get(SR.MS_MAINBAR_GRADIENTLIGHT)+"1", true);
          gradient_light2 = new SimpleString(SR.get(SR.MS_MAINBAR_GRADIENTLIGHT)+"2", true);
    }
    
    public void reloadItems() {
//#ifdef DEBUG
//#        System.out.println("reload items");
//#endif
       mainbar = new MainBar(SR.get(SR.MS_MODULES_CONFIG));
       setMainBarItem(new MainBar(mainbar));
          infobar=new MainBar("", true);
          infobar.addElement(null); //1
          infobar.addRAlign();
          infobar.addElement(null); //3
          setInfoBarItem(infobar);
       
       itemsList.removeAllElements();

       itemsList.addElement(contacts);
       itemsList.addElement(messages);
       itemsList.addElement(notify); 
       itemsList.addElement(network);
       itemsList.addElement(app); 
       itemsList.addElement(graphics);
       itemsList.addElement(theme);
       itemsList.addElement(fonts);
       
       itemsList.addElement(str);
       itemsList.addElement(history); 
       itemsList.addElement(autostatus); 
       itemsList.addElement(avatars);

       itemsList.addElement(userKeys);
       
//#ifdef IMPORT_EXPORT         
//#        itemsList.addElement(ie);
//#endif
       if(midlet.BombusQD.cf.userAppLevel == 1) {        
               itemsList.addElement(classicchat);
               itemsList.addElement(tasks);
               itemsList.addElement(debug);
       }
    }

    public PluginsConfig(Display display, Displayable pView) {
        super(display, pView, null);
        this.display=display;
        initItems();

          contacts = new PluginBox(SR.get(SR.MS_contactStr), cf.module_contacts, 0);
          messages = new PluginBox(SR.get(SR.MS_msgStr), cf.module_messages, 0);
          notify = new PluginBox(SR.get(SR.MS_notifyStr), cf.module_notify, 0);
          network = new PluginBox(SR.get(SR.MS_netStr), cf.module_network, 0);
          app = new PluginBox(SR.get(SR.MS_appStr), cf.module_app, 0);
          graphics = new PluginBox(SR.get(SR.MS_grStr), cf.module_graphics, 0);
          theme = new PluginBox(SR.get(SR.MS_COLOR_TUNE), cf.module_theme, 0);
          fonts = new PluginBox(SR.get(SR.MS_fontsStr), cf.module_fonts, 0);
          autostatus = new PluginBox(SR.get(SR.MS_astatusStr), cf.module_autostatus, 1);
          userKeys = new PluginBox(SR.get(SR.MS_hotkeysStr), cf.userKeys, 2);
          avatars = new PluginBox(SR.get(SR.MS_avatarStr), cf.module_avatars, 3);
          history = new PluginBox(SR.get(SR.MS_historyStr), cf.module_history, 4);
 //#ifdef IMPORT_EXPORT         
//#           ie = new PluginBox(SR.get(SR.MS_ieStr), cf.module_ie, 5);    
//#endif
          tasks = new PluginBox(SR.get(SR.MS_taskstr), cf.module_tasks, 6);
          classicchat = new PluginBox(SR.get(SR.MS_clchatStr), cf.module_classicchat, 7);
          debug = new PluginBox(SR.get(SR.MS_DEBUG_MENU), cf.debug, 8);

        reloadItems();  
        
        setCommandListener(this);
        attachDisplay(display);
        this.parentView=pView;        
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if(command==midlet.BombusQD.commands.cmdOk){
            cmdOk();
        } else super.commandAction(command, displayable);
    }

    public String touchRightCommand(){ return SR.get(SR.MS_BACK); }    
    public String touchLeftCommand(){ 
         try {
             String text=getFocusedObject().toString();
             if(text==SR.get(SR.MS_contactStr)){ return cf.module_contacts?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_msgStr)){ return cf.module_messages?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_netStr)){ return cf.module_network?SR.get(SR.MS_config):""; } 
             else if(text==SR.get(SR.MS_grStr)){ return cf.module_graphics?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_appStr)){ return cf.module_app?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_hotkeysStr)){ return cf.userKeys?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_astatusStr)){ return cf.module_autostatus?SR.get(SR.MS_config):""; } 
             else if(text==SR.get(SR.MS_clchatStr)){ return cf.module_classicchat?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_DEBUG_MENU)){ return ""; }           
             else if(text==SR.get(SR.MS_COLOR_TUNE)){ return cf.module_theme?SR.get(SR.MS_config):""; }
             
             //else if(text==SR.get(SR.MS_casheStr)){ return cf.module_cashe?"Clear":""; }
             else if(text==SR.get(SR.MS_historyStr)){ return cf.module_history?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_fontsStr)){ return cf.module_fonts?SR.get(SR.MS_config):""; }
//#ifdef IMPORT_EXPORT
//#              else if(text==SR.get(SR.MS_ieStr)){ return cf.module_ie?SR.get(SR.MS_config):""; } 
//#endif
             else if(text==SR.get(SR.MS_notifyStr)){ return cf.module_notify?SR.get(SR.MS_config):""; } 
             else if(text==SR.get(SR.MS_taskstr)){ return cf.module_tasks?SR.get(SR.MS_config):""; }
             else if(text==SR.get(SR.MS_avatarStr)){ return cf.module_avatars?SR.get(SR.MS_config):""; }
             
        } catch (Exception e) { } 
      return "";
    }
    
    
    public void eventLongOk() {
	    touchLeftPressed();
    }
    public void cmdOk() {
        try {
          //String type = touchLeftCommand();
          String type=getFocusedObject().toString();
          if(touchLeftCommand()=="") return;
//#ifdef USER_KEYS
//#           if(type==SR.get(SR.MS_hotkeysStr)){
//#            display.setCurrent(new ui.keys.UserKeysList(display));
//#           }
//#endif
          else if(type==SR.get(SR.MS_COLOR_TUNE)){
           display.setCurrent(new colors.ColorConfigForm(display, this));
          }         
          else if(type==SR.get(SR.MS_historyStr)){
           display.setCurrent(new HistoryConfig(display, this));
          }
          else if(type==SR.get(SR.MS_fontsStr)){
           display.setCurrent(new font.ConfigFonts(display, this));
          }
//#ifdef IMPORT_EXPORT          
//#           else if(type==SR.get(SR.MS_ieStr)){
//#            display.setCurrent(new impexp.IEMenu(display, this));
//#           }  
//#endif
          else if(type==SR.get(SR.MS_notifyStr)){
           display.setCurrent(new alert.AlertCustomizeForm(display, this));
          }
          else if(type==SR.get(SR.MS_taskstr)){
           display.setCurrent(new autotask.AutoTaskForm(display, this));
          }
          else if(type==SR.get(SR.MS_avatarStr)){
           display.setCurrent(new ConfigAvatar(display,this));
          }  
          else {
           new ConfigModule(display, this, type);
          }          
        } catch (Exception e) {
            e.printStackTrace();

        }
        
    }
    
    

    public void destroyView(){
       display.setCurrent(parentView);
       destroyItems();
       midlet.BombusQD.sd.roster.pluginsConfig = null;
       cf.saveToStorage();
    }

    
    
    
 ////Configure Module
 class ConfigModule extends DefForm implements MenuListener
 {
    private String type="";
    public ConfigModule(Display display, Displayable pView,String type) {
        super(display, pView, type);
        this.display=display;
        this.type=type;
         if(type==SR.get(SR.MS_contactStr)) {//
          subscr=new DropChoiceBox(display, SR.get(SR.MS_AUTH_NEW));
          subscr.append(SR.get(SR.MS_SUBSCR_AUTO));
          subscr.append(SR.get(SR.MS_SUBSCR_ASK));
          subscr.append(SR.get(SR.MS_SUBSCR_DROP));
          subscr.append(SR.get(SR.MS_SUBSCR_REJECT));
          subscr.setSelectedIndex(cf.autoSubscribe);
          itemsList.addElement(subscr);

          itemsList.addElement(new SpacerItem(2));
          nil=new DropChoiceBox(display, SR.get(SR.MS_NOT_IN_LIST));
          nil.append(SR.get(SR.MS_NIL_DROP_MP));
          nil.append(SR.get(SR.MS_NIL_DROP_P));
          nil.append(SR.get(SR.MS_NIL_ALLOW_ALL));
          nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel);
          itemsList.addElement(nil);
          itemsList.addElement(new SpacerItem(2));
          
          if(midlet.BombusQD.cf.userAppLevel == 1) {
             itemsList.addElement(simpleContacts);
             itemsList.addElement(showOfflineContacts);
             itemsList.addElement(selfContact);
             itemsList.addElement(showTransports);
             itemsList.addElement(showResources);
             itemsList.addElement(useBoldFont);
             itemsList.addElement(showClientIcon);
             itemsList.addElement(iconsLeft);
             itemsList.addElement(autoFocus);
             itemsList.addElement(ignore);
             itemsList.addElement(rosterStatus);
			 itemsList.addElement(minItemHeight);
          } else {
             itemsList.addElement(showOfflineContacts);
             itemsList.addElement(showTransports);
             itemsList.addElement(useBoldFont);
             itemsList.addElement(autoFocus);
             itemsList.addElement(rosterStatus);
          }
         }
         else if(type==SR.get(SR.MS_msgStr)){//or chat
            msgEditType=new DropChoiceBox(display, SR.get(SR.MS_MSG_EDIT_TYPE));
            msgEditType.append(SR.get(SR.MS_MES_EDIT_OLD));//0
            msgEditType.append(SR.get(SR.MS_MES_EDIT_ALT));//1
	    msgEditType.setSelectedIndex(cf.msgEditType);
            itemsList.addElement(msgEditType);
            itemsList.addElement(new SpacerItem(3));  
            itemsList.addElement(runningMessage);                               
            
                  itemsList.addElement(new SpacerItem(3));
                  textWrap=new DropChoiceBox(display, SR.get(SR.MS_TEXTWRAP));
                  textWrap.append(SR.get(SR.MS_TEXTWRAP_CHARACTER));
                  textWrap.append(SR.get(SR.MS_TEXTWRAP_WORD));
	          textWrap.setSelectedIndex(cf.textWrap);
                  itemsList.addElement(textWrap);
                  itemsList.addElement(new SpacerItem(3));
                  messageLimit=new NumberInput(display, SR.get(SR.MS_MESSAGE_COLLAPSE_LIMIT), Integer.toString(cf.messageLimit), 200, 1000);
                  if(midlet.BombusQD.cf.userAppLevel == 1) {
                     itemsList.addElement(messageLimit);
                  }
                  
                  msglistLimit=new NumberInput(display, SR.get(SR.MS_MESSAGE_COUNT_LIMIT), Integer.toString(cf.msglistLimit), 10, 1000);
                  itemsList.addElement(msglistLimit);
                  
                  itemsList.addElement(new SpacerItem(3));
                
                  if(midlet.BombusQD.cf.userAppLevel == 1) {
                     itemsList.addElement(createMessageByFive);
                     itemsList.addElement(storeConfPresence);
                     itemsList.addElement(showCollapsedPresences);
                     itemsList.addElement(autoScroll);
                     itemsList.addElement(timePresence);
                     itemsList.addElement(autoDetranslit);
                     itemsList.addElement(showNickNames);
                     itemsList.addElement(savePos); 
                     itemsList.addElement(boldNicks);
                     itemsList.addElement(selectOutMessages);                         
                     itemsList.addElement(useLowMemory_iconmsgcollapsed);
                     itemsList.addElement(smiles);
                     if(cf.ANIsmilesDetect) itemsList.addElement(animatedSmiles);                                
                     itemsList.addElement(capsState);
                     itemsList.addElement(useTabs);
                     itemsList.addElement(useClipBoard);
                  } else {
                       itemsList.addElement(createMessageByFive);
                       itemsList.addElement(autoScroll);
                       itemsList.addElement(autoDetranslit);
                       itemsList.addElement(selectOutMessages);
                       itemsList.addElement(smiles);
                       if(cf.ANIsmilesDetect) itemsList.addElement(animatedSmiles);
                       itemsList.addElement(capsState);
                       itemsList.addElement(useTabs);
                  }
         }
         else if(type==SR.get(SR.MS_netStr)){//
//#ifdef PEP        
//#                    if(midlet.BombusQD.cf.userAppLevel == 1) {
//#                      itemsList.addElement(autoLoadTransports);
//#                      itemsList.addElement(new SimpleString(SR.get(SR.MS_PEP), true));
//#                      itemsList.addElement(sndrcvmood);
//#                      itemsList.addElement(rcvtune);
//#                      itemsList.addElement(rcvactivity);
//#                      itemsList.addElement(new SpacerItem(10));
//#                    }
//#endif
                   itemsList.addElement(new SimpleString(SR.get(SR.MS_MESSAGES), true));
                   itemsList.addElement(eventComposing);
                   itemsList.addElement(eventDelivery);
                   itemsList.addElement(networkAnnotation);
                   //itemsList.addElement(metaContacts);
                   //itemsList.addElement(sendMoodInMsg);
                          
                   itemsList.addElement(new SpacerItem(10));
                   itemsList.addElement(new SimpleString(SR.get(SR.MS_RECONNECT), true));//����
        
	           reconnectCount=new NumberInput(display, SR.get(SR.MS_RECONNECT_COUNT_RETRY), Integer.toString(cf.reconnectCount), 0, 100);
                   itemsList.addElement(reconnectCount);
                   reconnectTime=new NumberInput(display, SR.get(SR.MS_RECONNECT_WAIT), Integer.toString(cf.reconnectTime), 1, 60 ); 
                   itemsList.addElement(reconnectTime);
                   itemsList.addElement(nokiaReconnectHack);
                   
                   if(midlet.BombusQD.cf.userAppLevel == 1) {
//#ifdef FILE_TRANSFER
                     itemsList.addElement(fileTransfer);
//#endif
//#ifdef ADHOC
//#                      itemsList.addElement(adhoc);
//#endif
                   }

         } 
         else if(type==SR.get(SR.MS_grStr)){
          panels=new DropChoiceBox(display, SR.get(SR.MS_PANELS));
          panels.append(SR.get(SR.MS_NO_BAR)+" : "+SR.get(SR.MS_NO_BAR));
          panels.append(SR.get(SR.MS_MAIN_BAR)+" : "+SR.get(SR.MS_NO_BAR));
          panels.append(SR.get(SR.MS_MAIN_BAR)+" : "+SR.get(SR.MS_INFO_BAR));
          panels.append(SR.get(SR.MS_NO_BAR)+" : "+SR.get(SR.MS_INFO_BAR));
          panels.append(SR.get(SR.MS_INFO_BAR)+" : "+SR.get(SR.MS_NO_BAR));
          panels.append(SR.get(SR.MS_INFO_BAR)+" : "+SR.get(SR.MS_MAIN_BAR));
          panels.append(SR.get(SR.MS_NO_BAR)+" : "+SR.get(SR.MS_MAIN_BAR));
    	  panels.setSelectedIndex(cf.panelsState);
          itemsList.addElement(panels);

           itemsList.addElement(gradientBarLigth); 
           itemsList.addElement(gradient_light1);
           itemsList.addElement(new SpacerItem(2));
           itemsList.addElement(gradientBarLight1);
           
           itemsList.addElement(gradient_light2);
           itemsList.addElement(new SpacerItem(2));
           itemsList.addElement(gradientBarLight2);
           //itemsList.addElement(graphicsMenu);
          
           graphicsMenuPosition=new DropChoiceBox(display, SR.get(SR.MS_GRAPHICSMENU_POS));
           graphicsMenuPosition.append(SR.get(SR.MS_GRMENU_CENTER)); //0
           graphicsMenuPosition.append(SR.get(SR.MS_GRMENU_LEFT));//1
           graphicsMenuPosition.append(SR.get(SR.MS_GRMENU_RIGHT));//2
           graphicsMenuPosition.setSelectedIndex(cf.graphicsMenuPosition);
           itemsList.addElement(graphicsMenuPosition);  

             
           itemsList.addElement(new SpacerItem(3));
           bgnd_image=new DropChoiceBox(display, "*"+SR.get(SR.MS_TYPE_BACKGROUND));
           bgnd_image.append(SR.get(SR.MS_BGND_NONE)); //0
           bgnd_image.append(SR.get(SR.MS_BGND_IMAGE));//1
           bgnd_image.append(SR.get(SR.MS_BGND_GRADIENT_));//2
           bgnd_image.append(SR.get(SR.MS_MY_BGND_IMAGE));//3
           bgnd_image.setSelectedIndex(cf.bgnd_image);
           itemsList.addElement(bgnd_image);
           
           itemsList.addElement(new SpacerItem(3));                   
	   scrollWidth=new NumberInput(display, SR.get(SR.MS_SCROLL_WIDTH), Integer.toString(cf.scrollWidth), 3, 25); 
           itemsList.addElement(scrollWidth);  
			minItemHeight = new NumberInput(display, "Min item height", Integer.toString(cf.minItemHeight), 0, 100);
			itemsList.addElement(minItemHeight);
           itemsList.addElement(new SpacerItem(3));      
           
           if(midlet.BombusQD.cf.userAppLevel == 1) {
             itemsList.addElement(useLowMemory_userotator);
             itemsList.addElement(shadowBar);
             itemsList.addElement(gradient_cursor);
//#ifdef MEMORY_MONITOR
//#              itemsList.addElement(memMon);
//#endif
             itemsList.addElement(drawScrollBgnd);  
             itemsList.addElement(drawMenuCommand);
             itemsList.addElement(popUps);
             itemsList.addElement(showBaloons);               
           } else {
               itemsList.addElement(gradient_cursor);
               itemsList.addElement(drawMenuCommand);
               itemsList.addElement(popUps);
           }
           
         }
         else if(type==SR.get(SR.MS_appStr)){
            
            itemsList.addElement(new SimpleString(SR.get(SR.MS_STARTUP_ACTIONS), true));
            itemsList.addElement(autoLogin);
            itemsList.addElement(autoJoinConferences);

            if(midlet.BombusQD.cf.userAppLevel == 1) {
            itemsList.addElement(collapsedGroups);
            itemsList.addElement(fullscr);
            itemsList.addElement(enableVersionOs);
            itemsList.addElement(queryExit);
            } else {
             itemsList.addElement(fullscr);
            }
            if (phoneManufacturer==cf.SONYE) itemsList.addElement(oldSE);
            if (cf.allowMinimize) {
                               itemsList.addElement(popupFromMinimized);
            }
            itemsList.addElement(executeByNum);
                               
            itemsList.addElement(new SpacerItem(10));
            itemsList.addElement(new SimpleString(SR.get(SR.MS_TIME_SETTINGS), true));
            fieldGmt=new NumberInput(display, SR.get(SR.MS_GMT_OFFSET), Integer.toString(cf.gmtOffset), -12, 12);
            itemsList.addElement(fieldGmt);

            langs=new StringLoader().stringLoader("/lang/res.txt",3);
            if (langs[0].size()>1) {
               itemsList.addElement(new SpacerItem(10));
               langFiles=new DropChoiceBox(display, "*"+SR.get(SR.MS_LANGUAGE));
               String tempLang=cf.lang;
               if (tempLang==null) { //not detected
                   String locale=System.getProperty("microedition.locale");
                   if (locale!=null) {
                       tempLang=locale.substring(0, 2).toLowerCase();
                   }
               }

               for (int i=0; i<langs[0].size(); i++) {
                   String label=(String) langs[2].elementAt(i);
                   String langCode=(String) langs[0].elementAt(i);
                   langFiles.append(label);
                   if (tempLang.equals(langCode))
                       langFiles.setSelectedIndex(i);
               }
               itemsList.addElement(langFiles);
            }
         }
//#ifdef AUTOSTATUS
//#          else if(type==SR.get(SR.MS_astatusStr)){
//#            autoAwayType=new DropChoiceBox(display, SR.get(SR.MS_AWAY_TYPE));
//#            autoAwayType.append(SR.get(SR.MS_AWAY_OFF));
//#            autoAwayType.append(SR.get(SR.MS_AWAY_LOCK));
//#            autoAwayType.append(SR.get(SR.MS_MESSAGE_LOCK));
//#            autoAwayType.append(SR.get(SR.MS_IDLE));
//#            autoAwayType.setSelectedIndex(cf.autoAwayType);
//#            itemsList.addElement(autoAwayType);
//#            fieldAwayDelay=new NumberInput(display, "*"+SR.get(SR.MS_AWAY_PERIOD), Integer.toString(cf.autoAwayDelay), 1, 60);
//#            itemsList.addElement(fieldAwayDelay);
//#            itemsList.addElement(awayStatus);
//# 
//#          }
//#endif
         else if(type==SR.get(SR.MS_clchatStr)){
           itemsList.addElement(new SimpleString(SR.get(SR.MS_CLCHAT_ON), true));
           //itemsList.addElement(useClassicChat);
           itemsList.addElement(use_phone_theme);        
           classic_chat_height=new NumberInput(display,SR.get(SR.MS_CLCHAT_HEIGHT), Integer.toString(cf.classic_chat_height), 80, 320);
           itemsList.addElement(classic_chat_height);
           line_count=new NumberInput(display,SR.get(SR.MS_CLCHAT_MSGLIMIT), Integer.toString(cf.line_count), 1, 1000);
           itemsList.addElement(line_count);
           itemsList.addElement(new SpacerItem(10));            
         } 
      setCommandListener(this);
      attachDisplay(display);    
      this.parentView=pView;
    }

    
    public void cmdOk() {
         if(type==SR.get(SR.MS_contactStr)){
             
            if(null != simpleContacts) cf.simpleContacts=simpleContacts.getValue();
            if(null != showOfflineContacts) cf.showOfflineContacts=showOfflineContacts.getValue();
            if(null != selfContact) cf.selfContact=selfContact.getValue();
            if(null != showTransports) cf.showTransports=showTransports.getValue();
            if(null != showResources) cf.showResources=showResources.getValue();
            if(null != useBoldFont) cf.useBoldFont=useBoldFont.getValue();
            if(null != showClientIcon) cf.showClientIcon=showClientIcon.getValue();
            if(null != iconsLeft) cf.iconsLeft=iconsLeft.getValue();
            if(null != autoFocus) cf.autoFocus=autoFocus.getValue();
            
            if(null != subscr) cf.autoSubscribe=subscr.getSelectedIndex();
            if(null != nil) cf.notInListDropLevel=nil.getSelectedIndex();
            
            if(null != rosterStatus) cf.rosterStatus=rosterStatus.getValue(); 
            if(null != ignore) cf.ignore=ignore.getValue();
            
         }
         else if(type==SR.get(SR.MS_msgStr)){
             
            if(null != msgEditType) cf.msgEditType=msgEditType.getSelectedIndex();
            if(null != runningMessage){
              if(cf.runningMessage != runningMessage.getValue()){
               cf.runningMessage=!cf.runningMessage;
               midlet.BombusQD.sd.roster.createMessageEdit(true);
              }
              cf.runningMessage=runningMessage.getValue();
            }
            if(null != textWrap) cf.textWrap=textWrap.getSelectedIndex();                              
            if(null != messageLimit) cf.messageLimit=Integer.parseInt(messageLimit.getValue());
            if(null != msglistLimit) cf.msglistLimit=Integer.parseInt(msglistLimit.getValue());
      
            if(null != createMessageByFive) cf.createMessageByFive=createMessageByFive.getValue();
            if(null != storeConfPresence) cf.storeConfPresence=storeConfPresence.getValue();
            if(null != showCollapsedPresences) cf.showCollapsedPresences=showCollapsedPresences.getValue();
            if(null != autoScroll) cf.autoScroll=autoScroll.getValue();
            if(null != timePresence) cf.timePresence=timePresence.getValue();
            if(null != autoDetranslit) cf.autoDeTranslit=autoDetranslit.getValue();
            if(null != showNickNames) cf.showNickNames=showNickNames.getValue();
            if(null != savePos) cf.savePos=savePos.getValue();
            if(null != boldNicks) cf.boldNicks=boldNicks.getValue();
            if(null != selectOutMessages) cf.selectOutMessages=selectOutMessages.getValue();
     
            if(null != useLowMemory_iconmsgcollapsed) cf.useLowMemory_iconmsgcollapsed=useLowMemory_iconmsgcollapsed.getValue();
            if(null != smiles) cf.smiles=smiles.getValue(); 
            if(cf.ANIsmilesDetect) {
                if(null != animatedSmiles) cf.animatedSmiles=animatedSmiles.getValue();
            }
             boolean aniSmiles = message.MessageParser.animated;
             if( !aniSmiles && cf.animatedSmiles || aniSmiles && !cf.animatedSmiles ){
                 message.MessageParser.restart();
             }
            if(null != capsState) cf.capsState=capsState.getValue(); 
            if(null != useTabs) cf.useTabs=useTabs.getValue();
            if(null != useClipBoard) cf.useClipBoard=useClipBoard.getValue();
             
         }
         else if(type==SR.get(SR.MS_netStr)){
             
            if(null != autoLoadTransports) cf.autoLoadTransports=autoLoadTransports.getValue();
//#ifdef PEP             
//#             if(null != sndrcvmood) cf.sndrcvmood=sndrcvmood.getValue();
//#             if(null != rcvtune) cf.rcvtune=rcvtune.getValue();
//#             if(null != rcvactivity) cf.rcvactivity=rcvactivity.getValue(); 
//#endif            
            if(null != eventComposing) cf.eventComposing=eventComposing.getValue();
            if(null != eventDelivery) cf.eventDelivery=eventDelivery.getValue();
            if(null != networkAnnotation) cf.networkAnnotation=networkAnnotation.getValue();
            //if(null != metaContacts) cf.metaContacts=metaContacts.getValue();
            //if(null != sendMoodInMsg) cf.sendMoodInMsg=sendMoodInMsg.getValue();
            
            if(null != reconnectCount) cf.reconnectCount=Integer.parseInt(reconnectCount.getValue());
            if(null != reconnectTime) cf.reconnectTime=Integer.parseInt(reconnectTime.getValue());
            if(null != nokiaReconnectHack) cf.nokiaReconnectHack=nokiaReconnectHack.getValue();  
//#ifdef FILE_TRANSFER
            if(null != fileTransfer) cf.fileTransfer=fileTransfer.getValue();
//#endif
//#ifdef ADHOC
//#             if(null != adhoc) {
//#                 cf.adhoc=adhoc.getValue();
//#             }
//#endif
            EntityCaps.initCaps();
            
         } 
         else if(type==SR.get(SR.MS_grStr)){
             
           if(null != panels) cf.panelsState=panels.getSelectedIndex(); 
           if(null != gradientBarLigth) cf.gradientBarLigth=gradientBarLigth.getValue();
           if(null != gradientBarLight1) cf.gradientBarLight1=gradientBarLight1.getValue()*10;
           if(null != gradientBarLight2) cf.gradientBarLight2=gradientBarLight2.getValue()*10;
           
           //boolean oldValue = cf.graphicsMenu;
           //cf.graphicsMenu=graphicsMenu.getValue();
           //System.out.println(oldValue + "/" +cf.graphicsMenu);
           
           if(null != graphicsMenuPosition) cf.graphicsMenuPosition=graphicsMenuPosition.getSelectedIndex();
           if(null != bgnd_image) cf.bgnd_image=bgnd_image.getSelectedIndex();
           if(null != scrollWidth) cf.scrollWidth=Integer.parseInt(scrollWidth.getValue());
		   if(null != minItemHeight) cf.minItemHeight = Integer.parseInt(minItemHeight.getValue());
           if(null != useLowMemory_userotator) cf.useLowMemory_userotator=useLowMemory_userotator.getValue();
           if(null != shadowBar) cf.shadowBar=shadowBar.getValue();
           if(null != gradient_cursor) cf.gradient_cursor=gradient_cursor.getValue();
//#ifdef MEMORY_MONITOR
//#            if(null != memMon) ui.VirtualList.memMonitor=cf.memMonitor=memMon.getValue();
//#endif
           if(null != drawScrollBgnd) cf.drawScrollBgnd=drawScrollBgnd.getValue();      
           
           ui.VirtualList.changeOrient(cf.panelsState);   
           
           if(null != drawMenuCommand) ui.VirtualList.showTimeTraffic=cf.showTimeTraffic=drawMenuCommand.getValue();
           if(null != popUps) cf.popUps=popUps.getValue();
           if(null != showBaloons) cf.showBalloons=showBaloons.getValue();
           
//#ifdef BACK_IMAGE
//#            VirtualList.createImage(false);
//#endif   
           /*
           if(oldValue != cf.graphicsMenu && oldValue == false) {
              midlet.BombusQD.sd.roster.pluginsConfig = null;
              cf.saveToStorage();
              midlet.BombusQD.sd.roster.clearMenu();
           }      
            */     
         }
         else if(type==SR.get(SR.MS_appStr)){
             
            if(null != autoLogin) cf.autoLogin=autoLogin.getValue();
            if(null != autoJoinConferences) cf.autoJoinConferences=autoJoinConferences.getValue();
            if(null != collapsedGroups) cf.collapsedGroups=collapsedGroups.getValue();
            
            if(null != fullscr) ui.VirtualList.fullscreen=cf.fullscreen=fullscr.getValue();
            if(null != enableVersionOs) cf.enableVersionOs=enableVersionOs.getValue();
            if(null != queryExit) cf.queryExit=queryExit.getValue();  
            if(null != oldSE) {
              if (phoneManufacturer==cf.SONYE) cf.oldSE=oldSE.getValue();
            }
            if(null != popupFromMinimized) {
                if (cf.allowMinimize) cf.popupFromMinimized=popupFromMinimized.getValue();
            }
            if(null != executeByNum) cf.executeByNum=executeByNum.getValue();
            if(null != fieldGmt) cf.gmtOffset=Integer.parseInt(fieldGmt.getValue());
            
            if (langs[0].size()>1 && null != langFiles) {
              String lang_ = (String) langs[0].elementAt( langFiles.getSelectedIndex() );
              if(!cf.lang.equals(lang_)) {
                cf.lang= lang_;
                SR.changeLocale();
                midlet.BombusQD.commands.initCommands(); //other Commands
                midlet.BombusQD.sd.roster.initCommands(); //roster Commands
                midlet.BombusQD.sd.roster.showRoster();
		//#ifdef COLOR_TUNE
//#                 ColorTheme.reInitNames();
		//#endif
                midlet.BombusQD.cf.saveUTF();
                return;
              }
              cf.lang= lang_;
            }   
         }
         else if(type==SR.get(SR.MS_hotkeysStr)){
 
         }
//#ifdef AUTOSTATUS
//#          else if(type==SR.get(SR.MS_astatusStr)){
//#             if(null != autoAwayType) cf.autoAwayType=autoAwayType.getSelectedIndex();
//#             if(null != fieldAwayDelay) cf.autoAwayDelay=Integer.parseInt(fieldAwayDelay.getValue());
//#             if(null != awayStatus) cf.setAutoStatusMessage=awayStatus.getValue();
//#          }
//#endif
         else if(type==SR.get(SR.MS_clchatStr)){
            //cf.useClassicChat=useClassicChat.getValue();
            if(null != use_phone_theme) cf.use_phone_theme=use_phone_theme.getValue();
            if(null != classic_chat_height) cf.classic_chat_height=Integer.parseInt(classic_chat_height.getValue());
            if(null != line_count) cf.line_count=Integer.parseInt(line_count.getValue()); 
         }          
      reloadItems();
      destroyView();
    }
    
    public void destroyView(){
        int size = itemsList.size();
        Object obj;
        for(int i = 0; i < size; ++i){
            obj = (Object)itemsList.elementAt(i);
            if(obj instanceof DropChoiceBox) ((DropChoiceBox)obj).destroy();
            if(obj instanceof NumberInput) ((NumberInput)obj).destroy();
        }
        itemsList.removeAllElements();
        display.setCurrent(parentView);
        reloadItems();
    }    
 }
    
  
}
