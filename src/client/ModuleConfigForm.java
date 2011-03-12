/*
 * ModuleConfigForm.java
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

//#ifdef COLOR_TUNE
import colors.ColorTheme;
//#endif
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import menu.MenuListener;
import message.MessageParser;
import midlet.BombusQD;
import midlet.Commands;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import util.StringLoader;
import xmpp.EntityCaps;

public class ModuleConfigForm extends DefForm implements MenuListener {
    private String type;

    // for contacts options
    private CheckBox simpleContacts;
    private CheckBox selfContact;
    private CheckBox showOfflineContacts;
    private CheckBox showTransports;
//#ifdef CLIENTS_ICONS
    private CheckBox showClientIcon;
//#endif
    private CheckBox iconsLeft;
    private CheckBox ignore;
    private CheckBox autoFocus;
    private CheckBox showResources;
    private CheckBox useBoldFont;
    private CheckBox rosterStatus;
    private DropChoiceBox subscr;
    private DropChoiceBox nil;

    // for chat config
//#ifdef RUNNING_MESSAGE
    private CheckBox runningMessage;
//#endif
    private CheckBox createMessageByFive;
    private CheckBox storeConfPresence;
    private CheckBox showCollapsedPresences;
    private CheckBox showNickNames;
    private CheckBox timePresence;
    private CheckBox savePos;
    private CheckBox capsState;
    private CheckBox useTabs;
    private CheckBox useLowMemory_iconmsgcollapsed;
    private CheckBox swapSendAndSuspend;
//#ifdef CLIPBOARD
    private CheckBox useClipBoard;
//#endif
//#ifdef SMILES
    private CheckBox smiles;
    private CheckBox animatedSmiles;
//#endif
//#ifdef DETRANSLIT
//#     private CheckBox autoDetranslit;
//#endif
    private DropChoiceBox textWrap;
    private DropChoiceBox msgEditType;
    private NumberInput messageLimit;
    private NumberInput msglistLimit;

    // for autostatus config
//#ifdef AUTOSTATUS
    private CheckBox awayStatus;
    private DropChoiceBox autoAwayType;
    private NumberInput fieldAwayDelay;
//#endif

    // for classic chat config
//#ifdef CLASSIC_CHAT
//#     private CheckBox usePhoneTheme;
//#     private NumberInput classicChatHeight;
//#     private NumberInput lineCount;
//#endif

    // for app config
    private CheckBox autoLogin;
    private CheckBox autoJoinConferences;
    private CheckBox cleanConfContacts;
    private CheckBox collapsedGroups;
    private CheckBox enableVersionOs;
    private CheckBox executeByNum;
    private CheckBox fullscr;
    private CheckBox oldSE;
    private CheckBox queryExit;
    private CheckBox popupFromMinimized;
    private DropChoiceBox langFiles;
    private NumberInput fieldGmt;

    // for interface config
    private CheckBox gradient_cursor;
    private CheckBox showBaloons;
//#ifdef POPUPS
    private CheckBox popUps;
//#endif
//#ifdef MEMORY_MONITOR
//#     private CheckBox memMon;
//#endif
    private CheckBox gradientBarLigth;
    private CheckBox shadowBar;
    private CheckBox drawMenuCommand;
    private TrackItem gradientBarLight1;
    private TrackItem gradientBarLight2;
    private NumberInput scrollWidth;
	private NumberInput minItemHeight;
//#ifdef BACK_IMAGE
    private DropChoiceBox bgnd_image;
//#endif
    private DropChoiceBox graphicsMenuPosition;
    private DropChoiceBox panels;

    // for network config
    private CheckBox autoLoadTransports;
    private CheckBox eventComposing;
    private CheckBox networkAnnotation;
    private CheckBox eventDelivery;
    private CheckBox nokiaReconnectHack;
//#ifdef PEP
    private CheckBox sndrcvmood;
    private CheckBox rcvtune;
    private CheckBox rcvactivity;
//#endif
//#ifdef FILE_TRANSFER
    private CheckBox fileTransfer;
//#endif
//#ifdef ADHOC
    private CheckBox adhoc;
//#endif
    private NumberInput reconnectCount;
    private NumberInput reconnectTime;

    public ModuleConfigForm(Display display, Displayable pView, String type) {
        super(display, pView, type);

        this.display = display;
        this.type = type;

        Config config = Config.getInstance();

        if(type.equals(SR.get(SR.MS_contactStr))) {
            subscr = new DropChoiceBox(display, SR.get(SR.MS_AUTH_NEW));
            subscr.append(SR.get(SR.MS_SUBSCR_AUTO));
            subscr.append(SR.get(SR.MS_SUBSCR_ASK));
            subscr.append(SR.get(SR.MS_SUBSCR_DROP));
            subscr.append(SR.get(SR.MS_SUBSCR_REJECT));
            subscr.setSelectedIndex(config.autoSubscribe);
            addControl(subscr);

            nil = new DropChoiceBox(display, SR.get(SR.MS_NOT_IN_LIST));
            nil.append(SR.get(SR.MS_NIL_DROP_MP));
            nil.append(SR.get(SR.MS_NIL_DROP_P));
            nil.append(SR.get(SR.MS_NIL_ALLOW_ALL));
            if (config.notInListDropLevel > NotInListFilter.ALLOW_ALL) {
                nil.setSelectedIndex(NotInListFilter.ALLOW_ALL);
            } else {
                nil.setSelectedIndex(config.notInListDropLevel);
            }
            addControl(nil);

            if(config.userAppLevel == 1) {
                simpleContacts = new CheckBox(SR.get(SR.MS_SIMPLE_CONTACTS_DRAW), config.simpleContacts);
                addControl(simpleContacts);

                selfContact = new CheckBox(SR.get(SR.MS_SELF_CONTACT), config.selfContact);
                addControl(selfContact);
            }

            showOfflineContacts = new CheckBox(SR.get(SR.MS_OFFLINE_CONTACTS), config.showOfflineContacts);
            addControl(showOfflineContacts);

            showTransports = new CheckBox(SR.get(SR.MS_TRANSPORTS), config.showTransports);
            addControl(showTransports);

            if(config.userAppLevel == 1) {
                showResources = new CheckBox(SR.get(SR.MS_SHOW_RESOURCES), config.showResources);
                addControl(showResources);

//#ifdef CLIENTS_ICONS
                showClientIcon = new CheckBox(SR.get(SR.MS_SHOW_CLIENTS_ICONS), config.showClientIcon);
                addControl(showClientIcon);
//#endif

                iconsLeft = new CheckBox(SR.get(SR.MS_CLIENT_ICONS_LEFT), config.iconsLeft);
                addControl(iconsLeft);

                ignore = new CheckBox(SR.get(SR.MS_IGNORE_LIST), config.ignore);
                addControl(ignore);
            }

            rosterStatus = new CheckBox(SR.get(SR.MS_SHOW_STATUSES), config.rosterStatus);
            itemsList.addElement(rosterStatus);

            useBoldFont = new CheckBox(SR.get(SR.MS_BOLD_FONT), config.useBoldFont);
            addControl(useBoldFont);

            autoFocus = new CheckBox(SR.get(SR.MS_AUTOFOCUS), config.autoFocus);
            addControl(autoFocus);
        } else if (type.equals(SR.get(SR.MS_msgStr))) {
            msgEditType = new DropChoiceBox(display, SR.get(SR.MS_MSG_EDIT_TYPE));
            msgEditType.append(SR.get(SR.MS_MES_EDIT_OLD));
            msgEditType.append(SR.get(SR.MS_MES_EDIT_ALT));
            msgEditType.setSelectedIndex(config.msgEditType);
            addControl(msgEditType);

            addControl(new SpacerItem(3));

//#ifdef RUNNING_MESSAGE
            runningMessage = new CheckBox(SR.get(SR.MS_RUNNING_MESSAGE), config.runningMessage);
            addControl(runningMessage);
//#endif

            addControl(new SpacerItem(3));

            textWrap = new DropChoiceBox(display, SR.get(SR.MS_TEXTWRAP));
            textWrap.append(SR.get(SR.MS_TEXTWRAP_CHARACTER));
            textWrap.append(SR.get(SR.MS_TEXTWRAP_WORD));
	    textWrap.setSelectedIndex(config.textWrap);
            addControl(textWrap);

            addControl(new SpacerItem(3));

            if(config.userAppLevel == 1) {
                messageLimit = new NumberInput(display, SR.get(SR.MS_MESSAGE_COLLAPSE_LIMIT), Integer.toString(config.messageLimit), 200, 1000);
                addControl(messageLimit);
            }

            msglistLimit = new NumberInput(display, SR.get(SR.MS_MESSAGE_COUNT_LIMIT), Integer.toString(config.msglistLimit), 10, 1000);
            addControl(msglistLimit);

            addControl(new SpacerItem(3));

            createMessageByFive = new CheckBox(SR.get(SR.MS_USE_FIVE_TO_CREATEMSG), config.createMessageByFive);
            addControl(createMessageByFive);

            if(config.userAppLevel == 1) {
                storeConfPresence = new CheckBox(SR.get(SR.MS_STORE_PRESENCE), config.storeConfPresence);
                addControl(storeConfPresence);

                showCollapsedPresences = new CheckBox(SR.get(SR.MS_COLLAPSE_PRESENCE), config.showCollapsedPresences);
                addControl(showCollapsedPresences);
            }

//#ifdef DETRANSLIT
//#             autoDetranslit = new CheckBox(SR.get(SR.MS_AUTODETRANSLIT), config.autoDeTranslit);
//#             addControl(autoDetranslit);
//#endif
            if(config.userAppLevel == 1) {
                savePos = new CheckBox(SR.get(SR.MS_SAVE_CURSOR), config.savePos);
                addControl(savePos);

                timePresence = new CheckBox(SR.get(SR.MS_SHOW_PRS_TIME), config.timePresence);
                addControl(timePresence);

                showNickNames = new CheckBox(SR.get(SR.MS_SHOW_NACKNAMES), config.showNickNames);
                addControl(showNickNames);
            }

            useTabs = new CheckBox(SR.get(SR.MS_EMULATE_TABS), config.useTabs);
            addControl(useTabs);

            capsState = new CheckBox(SR.get(SR.MS_CAPS_STATE), config.capsState);
            addControl(capsState);

//#ifdef SMILES
            smiles = new CheckBox(SR.get(SR.MS_SMILES), config.smiles);
            addControl(smiles);
            if(Config.hasAniSmiles) {
                animatedSmiles = new CheckBox(SR.get(SR.MS_ANI_SMILES), config.animatedSmiles);
                addControl(animatedSmiles);
            }
//#endif

            if(config.userAppLevel == 1) {
                useLowMemory_iconmsgcollapsed = new CheckBox(SR.get(SR.MS_ICON_COLP), config.useLowMemory_iconmsgcollapsed);
                addControl(useLowMemory_iconmsgcollapsed);
//#ifdef CLIPBOARD
                useClipBoard = new CheckBox(SR.get(SR.MS_CLIPBOARD), config.useClipBoard);
                addControl(useClipBoard);
//#endif
            }

            swapSendAndSuspend = new CheckBox(SR.get(SR.MS_SWAP_SEND_SUSPEND), config.swapSendAndSuspend);
            addControl(swapSendAndSuspend);
        } else if (type.equals(SR.get(SR.MS_netStr))) {
            if (config.userAppLevel == 1) {
                autoLoadTransports = new CheckBox(SR.get(SR.MS_AUTOCONNECT_TRANSPORTS), config.autoLoadTransports);
                addControl(autoLoadTransports);

//#ifdef PEP
                addControl(new SimpleString(SR.get(SR.MS_PEP), true));

                sndrcvmood = new CheckBox(SR.get(SR.MS_USERMOOD), config.sndrcvmood);
                addControl(sndrcvmood);

                rcvtune = new CheckBox(SR.get(SR.MS_USERTUNE), config.rcvtune);
                addControl(rcvtune);

                rcvactivity = new CheckBox(SR.get(SR.MS_USERACTIVITY), config.rcvactivity);
                addControl(rcvactivity);

                addControl(new SpacerItem(10));
//#endif
            }

            addControl(new SimpleString(SR.get(SR.MS_MESSAGES), true));

            eventComposing = new CheckBox(SR.get(SR.MS_COMPOSING_EVENTS), config.eventComposing);
            addControl(eventComposing);

            eventDelivery = new CheckBox(SR.get(SR.MS_DELIVERY), config.eventDelivery);
            addControl(eventDelivery);

            networkAnnotation = new CheckBox(SR.get(SR.MS_CONTACT_ANNOTATIONS), config.networkAnnotation);
            addControl(networkAnnotation);

            addControl(new SpacerItem(10));
            addControl(new SimpleString(SR.get(SR.MS_RECONNECT), true));

            reconnectCount = new NumberInput(display, SR.get(SR.MS_RECONNECT_COUNT_RETRY), Integer.toString(config.reconnectCount), 0, 100);
            addControl(reconnectCount);

            reconnectTime = new NumberInput(display, SR.get(SR.MS_RECONNECT_WAIT), Integer.toString(config.reconnectTime), 1, 60 );
            addControl(reconnectTime);

            nokiaReconnectHack = new CheckBox(SR.get(SR.MS_NOKIA_RECONNECT_HACK), config.nokiaReconnectHack);
            addControl(nokiaReconnectHack);

            if (config.userAppLevel == 1) {
//#ifdef FILE_TRANSFER
                fileTransfer = new CheckBox(SR.get(SR.MS_FILE_TRANSFERS), config.fileTransfer);
                addControl(fileTransfer);
//#endif
//#ifdef ADHOC
            adhoc = new CheckBox(SR.get(SR.MS_ADHOC), config.adhoc);
            itemsList.addElement(adhoc);
//#endif
            }
        } else if (type.equals(SR.get(SR.MS_grStr))) {
            panels = new DropChoiceBox(display, SR.get(SR.MS_PANELS));
            panels.append(SR.get(SR.MS_NO_BAR)+" : "+SR.get(SR.MS_NO_BAR));
            panels.append(SR.get(SR.MS_MAIN_BAR)+" : "+SR.get(SR.MS_NO_BAR));
            panels.append(SR.get(SR.MS_MAIN_BAR)+" : "+SR.get(SR.MS_INFO_BAR));
            panels.append(SR.get(SR.MS_NO_BAR)+" : "+SR.get(SR.MS_INFO_BAR));
            panels.append(SR.get(SR.MS_INFO_BAR)+" : "+SR.get(SR.MS_NO_BAR));
            panels.append(SR.get(SR.MS_INFO_BAR)+" : "+SR.get(SR.MS_MAIN_BAR));
            panels.append(SR.get(SR.MS_NO_BAR)+" : "+SR.get(SR.MS_MAIN_BAR));
            panels.setSelectedIndex(config.panelsState);
            addControl(panels);

            gradientBarLigth = new CheckBox(SR.get(SR.MS_USE_LIGHT_TO_DRWPANELS), config.gradientBarLigth);
            addControl(gradientBarLigth);

            addControl(new SimpleString(SR.get(SR.MS_MAINBAR_GRADIENTLIGHT)+"1", true));

            gradientBarLight1 = new TrackItem(config.gradientBarLight1/10, 20);
            addControl(gradientBarLight1);

            addControl(new SimpleString(SR.get(SR.MS_MAINBAR_GRADIENTLIGHT)+"2", true));

            gradientBarLight2 = new TrackItem(config.gradientBarLight2/10, 20);
            addControl(gradientBarLight2);

            graphicsMenuPosition = new DropChoiceBox(display, SR.get(SR.MS_GRAPHICSMENU_POS));
            graphicsMenuPosition.append(SR.get(SR.MS_GRMENU_CENTER));
            graphicsMenuPosition.append(SR.get(SR.MS_GRMENU_LEFT));
            graphicsMenuPosition.append(SR.get(SR.MS_GRMENU_RIGHT));
            graphicsMenuPosition.setSelectedIndex(config.graphicsMenuPosition);
            addControl(graphicsMenuPosition);

//#ifdef BACK_IMAGE
            itemsList.addElement(new SpacerItem(3));
            bgnd_image = new DropChoiceBox(display, "*"+SR.get(SR.MS_TYPE_BACKGROUND));
            bgnd_image.append(SR.get(SR.MS_BGND_NONE));
            bgnd_image.append(SR.get(SR.MS_BGND_IMAGE));
            bgnd_image.append(SR.get(SR.MS_BGND_GRADIENT_));
            bgnd_image.append(SR.get(SR.MS_MY_BGND_IMAGE));
            bgnd_image.setSelectedIndex(config.bgnd_image);
            addControl(bgnd_image);
//#endif

            addControl(new SpacerItem(3));

            scrollWidth = new NumberInput(display, SR.get(SR.MS_SCROLL_WIDTH), Integer.toString(config.scrollWidth), 3, 25);
            addControl(scrollWidth);

			minItemHeight = new NumberInput(display, SR.get(SR.MS_MIN_ITEM_HEIGHT), Integer.toString(config.minItemHeight), 0, 100);
			addControl(minItemHeight);
            addControl(new SpacerItem(3));

            if(config.userAppLevel == 1) {
//#ifdef MEMORY_MONITOR
//#              memMon = new CheckBox(SR.get(SR.MS_HEAP_MONITOR), config.memMonitor);
//#              addControl(memMon);
//#endif
                shadowBar = new CheckBox(SR.get(SR.MS_SHADOW_BAR), config.shadowBar);
                addControl(shadowBar);
            }

            drawMenuCommand = new CheckBox(SR.get(SR.MS_SHOW_TIME_TRAFFIC), config.showTimeTraffic);
            addControl(drawMenuCommand);

//#ifdef POPUPS
            popUps = new CheckBox(SR.get(SR.MS_POPUPS), config.popUps);
            addControl(popUps);
//#endif

            showBaloons = new CheckBox(SR.get(SR.MS_SHOW_BALLONS), config.showBalloons);
            addControl(showBaloons);

            gradient_cursor = new CheckBox(SR.get(SR.MS_GRADIENT_CURSOR), config.gradient_cursor);
            addControl(gradient_cursor);
        } else if (type.equals(SR.get(SR.MS_appStr))) {
            addControl(new SimpleString(SR.get(SR.MS_STARTUP_ACTIONS), true));

            autoLogin = new CheckBox(SR.get(SR.MS_AUTOLOGIN), config.autoLogin);
            addControl(autoLogin);

            autoJoinConferences = new CheckBox(SR.get(SR.MS_AUTO_CONFERENCES), config.autoJoinConferences);
            addControl(autoJoinConferences);

            cleanConfContacts = new CheckBox("Delete contacts, who leave MUC", Config.cleanConfContacts);
            addControl(cleanConfContacts);

            if (config.userAppLevel == 1) {
                collapsedGroups = new CheckBox(SR.get(SR.MS_COLLAPSED_GROUPS), config.collapsedGroups);
                addControl(collapsedGroups);

                enableVersionOs = new CheckBox(SR.get(SR.MS_SHOW_HARDWARE), config.enableVersionOs);
                addControl(enableVersionOs);

                queryExit = new CheckBox(SR.get(SR.MS_CONFIRM_EXIT), config.queryExit);
                addControl(queryExit);
            }

            fullscr = new CheckBox(SR.get(SR.MS_FULLSCREEN), config.fullscreen);
            addControl(fullscr);

            if (phoneManufacturer == Config.SONYE) {
                oldSE = new CheckBox(SR.get(SR.MS_KEYS_FOR_OLD_SE), config.oldSE);
                addControl(oldSE);
            }
            if (config.allowMinimize) {
                popupFromMinimized = new CheckBox(SR.get(SR.MS_ENABLE_POPUP), config.popupFromMinimized);
                addControl(popupFromMinimized);
            }

            executeByNum = new CheckBox(SR.get(SR.MS_EXECUTE_MENU_BY_NUMKEY), config.executeByNum);
            addControl(executeByNum);

            addControl(new SpacerItem(10));
            addControl(new SimpleString(SR.get(SR.MS_TIME_SETTINGS), true));

            fieldGmt = new NumberInput(display, SR.get(SR.MS_GMT_OFFSET), Integer.toString(config.gmtOffset), -12, 12);
            addControl(fieldGmt);

            Vector langs[] = new StringLoader().stringLoader("/lang/res.txt", 3);
            if (langs[0].size() > 1) {
               addControl(new SpacerItem(10));

               langFiles = new DropChoiceBox(display, "*" + SR.get(SR.MS_LANGUAGE));
               String tempLang = config.lang;
               // not detected
               if (tempLang == null) {
                   String locale = System.getProperty("microedition.locale");
                   if (locale != null) {
                       tempLang = locale.substring(0, 2).toLowerCase();
                   }
               }
               for (int i = 0; i < langs[0].size(); ++i) {
                   String label = (String)langs[2].elementAt(i);
                   String langCode = (String)langs[0].elementAt(i);
                   langFiles.append(label);
                   if (tempLang.equals(langCode))
                       langFiles.setSelectedIndex(i);
               }
               addControl(langFiles);
            }
//#ifdef AUTOSTATUS
        } else if (type.equals(SR.get(SR.MS_AUTOSTATUS))) {
            autoAwayType = new DropChoiceBox(display, SR.get(SR.MS_AWAY_TYPE));
            autoAwayType.append(SR.get(SR.MS_AWAY_OFF));
            autoAwayType.append(SR.get(SR.MS_AWAY_LOCK));
            autoAwayType.append(SR.get(SR.MS_MESSAGE_LOCK));
            autoAwayType.append(SR.get(SR.MS_IDLE));
            autoAwayType.setSelectedIndex(config.autoAwayType);
            addControl(autoAwayType);

            fieldAwayDelay = new NumberInput(display, "*" + SR.get(SR.MS_AWAY_PERIOD), Integer.toString(config.autoAwayDelay), 1, 60);
            addControl(fieldAwayDelay);

            awayStatus = new CheckBox(SR.get(SR.MS_AUTOSTATUS_MESSAGE), config.setAutoStatusMessage);
            addControl(awayStatus);
//#endif
//#ifdef CLASSIC_CHAT
//#         } else if (type.equals(SR.get(SR.MS_clchatStr))) {
//#             addControl(new SimpleString(SR.get(SR.MS_CLCHAT_ON), true));
//# 
//#             usePhoneTheme = new CheckBox(SR.get(SR.MS_CLCHAT_BGNG_PHONE), config.usePhoneTheme);
//#             addControl(usePhoneTheme);
//# 
//#             classicChatHeight = new NumberInput(display,SR.get(SR.MS_CLCHAT_HEIGHT), Integer.toString(config.classicChatHeight), 80, 320);
//#             addControl(classicChatHeight);
//# 
//#             lineCount = new NumberInput(display, SR.get(SR.MS_CLCHAT_MSGLIMIT), Integer.toString(config.lineCount), 1, 1000);
//#             itemsList.addElement(lineCount);
//#             itemsList.addElement(new SpacerItem(10));
//#endif
        }
        setCommandListener(this);
        attachDisplay(display);
        this.parentView = pView;

    }

    public void cmdOk() {
        Config config = Config.getInstance();
        if (type.equals(SR.get(SR.MS_contactStr))) {
            config.autoSubscribe = subscr.getSelectedIndex();
            config.notInListDropLevel = nil.getSelectedIndex();

            if (config.userAppLevel == 1) {
                config.simpleContacts = simpleContacts.getValue();
                config.selfContact = selfContact.getValue();
            }

            config.showOfflineContacts = showOfflineContacts.getValue();
            config.showTransports = showTransports.getValue();

            if (config.userAppLevel == 1) {
                config.showResources = showResources.getValue();
//#ifdef CLIENTS_ICONS
                config.showClientIcon = showClientIcon.getValue();
//#endif
                config.iconsLeft = iconsLeft.getValue();
                config.ignore = ignore.getValue();
            }

            config.rosterStatus = rosterStatus.getValue();
            config.useBoldFont = useBoldFont.getValue();
            config.autoFocus = autoFocus.getValue();
        } else if (type.equals(SR.get(SR.MS_msgStr))) {
            config.msgEditType = msgEditType.getSelectedIndex();
//#ifdef RUNNING_MESSAGE
            if(config.runningMessage != runningMessage.getValue()) {
                BombusQD.sd.roster.createMessageEdit(true);
            }
            config.runningMessage = runningMessage.getValue();
//#endif
            config.textWrap = textWrap.getSelectedIndex();

            if(config.userAppLevel == 1) {
                config.messageLimit = Integer.parseInt(messageLimit.getValue());
            }

            config.msglistLimit = Integer.parseInt(msglistLimit.getValue());
            config.createMessageByFive = createMessageByFive.getValue();

            if(config.userAppLevel == 1) {
                config.storeConfPresence = storeConfPresence.getValue();
                config.showCollapsedPresences = showCollapsedPresences.getValue();
            }

//#ifdef DETRANSLIT
//#             config.autoDeTranslit = autoDetranslit.getValue();
//#endif
            if(config.userAppLevel == 1) {
                config.timePresence = timePresence.getValue();
                config.showNickNames = showNickNames.getValue();
                config.savePos = savePos.getValue();
            }

            config.useTabs = useTabs.getValue();
            config.capsState = capsState.getValue();

//#ifdef SMILES
            config.smiles=smiles.getValue();
            if(Config.hasAniSmiles) {
                config.animatedSmiles = animatedSmiles.getValue();
            }
            MessageParser parser = MessageParser.getInstance();
            boolean aniSmiles = parser.animated;
            if(aniSmiles != config.animatedSmiles) {
                // for SE
                parser.restart(config.animatedSmiles);
                System.out.println("restart parser");
            }
//#endif

            if(config.userAppLevel == 1) {
                config.useLowMemory_iconmsgcollapsed = useLowMemory_iconmsgcollapsed.getValue();
//#ifdef CLIPBOARD
                config.useClipBoard = useClipBoard.getValue();
//#endif
            }
            Config.swapSendAndSuspend = swapSendAndSuspend.getValue();
       } else if (type.equals(SR.get(SR.MS_netStr))) {
            if (config.userAppLevel == 1) {
                config.autoLoadTransports = autoLoadTransports.getValue();

//#ifdef PEP
                config.sndrcvmood = sndrcvmood.getValue();
                config.rcvtune = rcvtune.getValue();
                config.rcvactivity = rcvactivity.getValue();
//#endif
            }

            config.eventComposing = eventComposing.getValue();
            config.eventDelivery = eventDelivery.getValue();
            config.networkAnnotation = networkAnnotation.getValue();

            config.reconnectCount = Integer.parseInt(reconnectCount.getValue());
            config.reconnectTime = Integer.parseInt(reconnectTime.getValue());
            config.nokiaReconnectHack = nokiaReconnectHack.getValue();

            if(config.userAppLevel == 1) {
//#ifdef FILE_TRANSFER
                config.fileTransfer = fileTransfer.getValue();
//#endif
//#ifdef ADHOC
            config.adhoc = adhoc.getValue();
//#endif
            }
            EntityCaps.initCaps();
         } else if(type.equals(SR.get(SR.MS_grStr))) {
            config.panelsState = panels.getSelectedIndex();

            config.gradientBarLigth = gradientBarLigth.getValue();
            config.gradientBarLight1=gradientBarLight1.getValue() * 10;
            config.gradientBarLight2=gradientBarLight2.getValue() * 10;

            config.graphicsMenuPosition = graphicsMenuPosition.getSelectedIndex();
//#ifdef BACK_IMAGE
            config.bgnd_image = bgnd_image.getSelectedIndex();
//#endif

            config.scrollWidth = Integer.parseInt(scrollWidth.getValue());
			config.minItemHeight = Integer.parseInt(minItemHeight.getValue());

            if(config.userAppLevel == 1) {
//#ifdef MEMORY_MONITOR
//#              config.memMonitor = VirtualList.memMonitor = memMon.getValue();
//#endif
                config.shadowBar = shadowBar.getValue();
            }

            config.showTimeTraffic = VirtualList.showTimeTraffic = drawMenuCommand.getValue();
//#ifdef POPUPS
            config.popUps = popUps.getValue();
//#endif
            config.showBalloons = showBaloons.getValue();
            config.gradient_cursor = gradient_cursor.getValue();

            VirtualList.changeOrient(config.panelsState);
//#ifdef BACK_IMAGE
            VirtualList.createImage(false);
//#endif
        } else if (type.equals(SR.get(SR.MS_appStr))) {
            config.autoLogin = autoLogin.getValue();
            config.autoJoinConferences = autoJoinConferences.getValue();
            Config.cleanConfContacts = cleanConfContacts.getValue();

            if (config.userAppLevel == 1) {
                config.collapsedGroups = collapsedGroups.getValue();
                config.enableVersionOs = enableVersionOs.getValue();
                config.queryExit = queryExit.getValue();
            }

            VirtualList.fullscreen = config.fullscreen = fullscr.getValue();

            if (phoneManufacturer == Config.SONYE) {
                config.oldSE = oldSE.getValue();
            }
            if (config.allowMinimize) {
                config.popupFromMinimized = popupFromMinimized.getValue();
            }

            config.executeByNum = executeByNum.getValue();
            config.gmtOffset = Integer.parseInt(fieldGmt.getValue());

            Vector langs[] = new StringLoader().stringLoader("/lang/res.txt", 3);
            if (langs[0].size() > 1) {
                String lang = (String)langs[0].elementAt(langFiles.getSelectedIndex());
                if (!config.lang.equals(lang)) {
                    config.lang= lang;
                    SR.changeLocale();
                    Commands.initCommands();
//#ifdef COLOR_TUNE
                    ColorTheme.initNames();
//#endif
                    BombusQD.sd.roster.initCommands();
                    BombusQD.sd.roster.showRoster();
                    return;
                }
            }
 //#ifdef AUTOSTATUS
         } else if (type.equals(SR.get(SR.MS_AUTOSTATUS))) {
            config.autoAwayType = autoAwayType.getSelectedIndex();
            config.autoAwayDelay = Integer.parseInt(fieldAwayDelay.getValue());
            config.setAutoStatusMessage = awayStatus.getValue();
//#endif
//#ifdef CLASSIC_CHAT
//#         } else if (type.equals(SR.get(SR.MS_clchatStr))) {
//#             config.usePhoneTheme = usePhoneTheme.getValue();
//#             config.classicChatHeight = Integer.parseInt(classicChatHeight.getValue());
//#             config.lineCount = Integer.parseInt(lineCount.getValue());
//#endif
        }
        destroyView();
    }

    public void destroyView(){
        display.setCurrent(parentView);
    }
}
