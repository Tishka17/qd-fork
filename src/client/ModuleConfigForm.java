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

import java.util.Vector;
import locale.SR;
import message.MessageParser;
import midlet.BombusQD;
import midlet.Commands;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.NumberInput;
//#ifdef FILE_IO
import ui.controls.form.PathSelector;
//#endif
import ui.controls.form.PluginBox;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import ui.controls.form.LinkString;
import util.StringLoader;
import ui.IconTextElement;
import xmpp.EntityCaps;
import ui.controls.form.ColorThemeSelector;
import colors.ColorTheme;
//#ifdef COLOR_TUNE
import colors.ColorList;
//#endif

import images.ActionsIcons;
import images.MenuIcons;
import images.RosterIcons;

public class ModuleConfigForm extends DefForm {
    private int type;

    // for contacts options
    private CheckBox simpleContacts;
    private CheckBox selfContact;
    private CheckBox showOfflineContacts;
    private CheckBox showTransports;
//#ifdef CLIENTS_ICONS
    private CheckBox showClientIcon;
    private CheckBox iconsLeft;
//#endif
//#ifdef JUICK.COM
    private CheckBox juickImages;
//#endif
    private CheckBox ignore;
    private CheckBox autoFocus;
    private CheckBox showResources;
    private CheckBox useBoldFont;
    private CheckBox rosterStatus;
    private DropChoiceBox subscr;
    private DropChoiceBox nil;
    private NumberInput contactXOffset;

    // for chat config
//#ifdef RUNNING_MESSAGE
    private CheckBox runningMessage;
//#endif
    private CheckBox createMessageByFive;
    private CheckBox storeConfPresence;
    private CheckBox showCollapsedPresences;
    private CheckBox showNickNames;
    private CheckBox showTimeInMsgs;
    private CheckBox autoScroll;
    private CheckBox capsState;
    private CheckBox useTabs;
    private CheckBox hideMessageIcon;
    private CheckBox swapSendAndSuspend;
    private CheckBox showMsgSep;
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
    private CheckBox showTimeAndTraffic;
    private TrackItem gradientBarLight1;
    private TrackItem gradientBarLight2;
    private NumberInput scrollWidth;
    private NumberInput minItemHeight;
//#ifdef BACK_IMAGE
    private DropChoiceBox backImgType;
//#ifdef FILE_IO
    private PathSelector backImgPath;
//#endif
//#endif
    private DropChoiceBox graphicsMenuPosition;
    private DropChoiceBox panels;

    // for network config
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
//#if SERVICE_DISCOVERY && ADHOC
    private CheckBox adhoc;
//#endif
    private NumberInput reconnectCount;
    private NumberInput reconnectTime;

    private Vector langs[];
    
    private ColorThemeSelector skinFiles;

    public ModuleConfigForm(String caption, int type) {
        super(caption);

        this.type = type;

        Config config = Config.getInstance();

        if(type == PluginBox.CONTACTS) {
            subscr = new DropChoiceBox(SR.get(SR.MS_AUTH_NEW));
            subscr.append(new IconTextElement(SR.get(SR.MS_SUBSCR_AUTO), ActionsIcons.getInstance(), ActionsIcons.ICON_SUBSCR));
            subscr.append(new IconTextElement(SR.get(SR.MS_SUBSCR_ASK), ActionsIcons.getInstance(), ActionsIcons.ICON_RENAME));
            subscr.append(new IconTextElement(SR.get(SR.MS_SUBSCR_DROP), ActionsIcons.getInstance(), ActionsIcons.ICON_DELETE));
            subscr.append(new IconTextElement(SR.get(SR.MS_SUBSCR_REJECT), ActionsIcons.getInstance(), ActionsIcons.ICON_DELETE));
            subscr.setSelectedIndex(config.autoSubscribe);
            addControl(subscr);

            nil = new DropChoiceBox(SR.get(SR.MS_NOT_IN_LIST));
            nil.append(new IconTextElement(SR.get(SR.MS_NIL_DROP_MP), ActionsIcons.getInstance(), ActionsIcons.ICON_DELETE));
            nil.append(new IconTextElement(SR.get(SR.MS_NIL_DROP_P), RosterIcons.getInstance(), RosterIcons.ICON_MESSAGE_INDEX));
            nil.append(new IconTextElement(SR.get(SR.MS_NIL_ALLOW_ALL), MenuIcons.getInstance(), MenuIcons.ICON_STATUS));
            nil.setSelectedIndex(config.notInListDropLevel);
            addControl(nil);

            contactXOffset = new NumberInput(SR.get(SR.MS_CONTACT_XOFFSET), Config.contactXOffset, 0, 100);
            addControl(contactXOffset);

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
                iconsLeft = new CheckBox(SR.get(SR.MS_CLIENT_ICONS_LEFT), config.iconsLeft);
                addControl(iconsLeft);
//#endif

                ignore = new CheckBox(SR.get(SR.MS_IGNORE_LIST), config.ignore);
                addControl(ignore);
            }

            rosterStatus = new CheckBox(SR.get(SR.MS_SHOW_STATUSES), config.rosterStatus);
            addControl(rosterStatus);

            useBoldFont = new CheckBox(SR.get(SR.MS_BOLD_FONT), config.useBoldFont);
            addControl(useBoldFont);

            autoFocus = new CheckBox(SR.get(SR.MS_AUTOFOCUS), config.autoFocus);
            addControl(autoFocus);
        } else if (type == PluginBox.CHATS) {
            msgEditType = new DropChoiceBox(SR.get(SR.MS_MSG_EDIT_TYPE));
            msgEditType.append(new IconTextElement(SR.get(SR.MS_MES_EDIT_OLD), ActionsIcons.getInstance(), ActionsIcons.ICON_RENAME));
            msgEditType.append(new IconTextElement(SR.get(SR.MS_MES_EDIT_ALT), ActionsIcons.getInstance(), ActionsIcons.ICON_RENAME));
            msgEditType.setSelectedIndex(config.msgEditType);
            addControl(msgEditType);

            addControl(new SpacerItem(3));

//#ifdef RUNNING_MESSAGE
            runningMessage = new CheckBox(SR.get(SR.MS_RUNNING_MESSAGE), config.runningMessage);
            addControl(runningMessage);
//#endif

            addControl(new SpacerItem(3));

            textWrap = new DropChoiceBox(SR.get(SR.MS_TEXTWRAP));
            textWrap.append(new IconTextElement(SR.get(SR.MS_TEXTWRAP_CHARACTER), ActionsIcons.getInstance(), ActionsIcons.ICON_CONSOLE));
            textWrap.append(new IconTextElement(SR.get(SR.MS_TEXTWRAP_WORD), ActionsIcons.getInstance(), ActionsIcons.ICON_CONSOLE));
	    textWrap.setSelectedIndex(config.textWrap);
            addControl(textWrap);

            addControl(new SpacerItem(3));

            if(config.userAppLevel == 1) {
                messageLimit = new NumberInput(SR.get(SR.MS_MESSAGE_COLLAPSE_LIMIT), config.messageLimit, 200, 1000);
                addControl(messageLimit);
            }

            msglistLimit = new NumberInput(SR.get(SR.MS_MESSAGE_COUNT_LIMIT),config.msglistLimit, 5, 100000);
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
                autoScroll = new CheckBox(SR.get(SR.MS_AUTOSCROLL), Config.autoScroll);
                addControl(autoScroll);

                showTimeInMsgs = new CheckBox(SR.get(SR.MS_SHOW_TIME_IN_MSGS), Config.showTimeInMsgs);
                addControl(showTimeInMsgs);

                showNickNames = new CheckBox(SR.get(SR.MS_SHOW_NACKNAMES), Config.showNickNames);
                addControl(showNickNames);
            }

            useTabs = new CheckBox(SR.get(SR.MS_EMULATE_TABS), config.useTabs);
            addControl(useTabs);

            capsState = new CheckBox(SR.get(SR.MS_CAPS_STATE), config.capsState);
            addControl(capsState);

            showMsgSep = new CheckBox( "Show messages separator", Config.showMsgSep);
            addControl(showMsgSep);

//#ifdef SMILES
            smiles = new CheckBox(SR.get(SR.MS_SMILES), config.smiles);
            addControl(smiles);
            if(Config.hasAniSmiles) {
                animatedSmiles = new CheckBox(SR.get(SR.MS_ANI_SMILES), Config.animatedSmiles);
                addControl(animatedSmiles);
            }
//#endif

            if(config.userAppLevel == 1) {
                hideMessageIcon = new CheckBox(SR.get(SR.MS_ICON_COLP), Config.hideMessageIcon);
                addControl(hideMessageIcon);
//#ifdef CLIPBOARD
                useClipBoard = new CheckBox(SR.get(SR.MS_CLIPBOARD), Config.useClipBoard);
                addControl(useClipBoard);
//#endif
            }

            swapSendAndSuspend = new CheckBox(SR.get(SR.MS_SWAP_SEND_SUSPEND), Config.swapSendAndSuspend);
            addControl(swapSendAndSuspend);
        } else if (type == PluginBox.NETWORK) {
            if (config.userAppLevel == 1) {
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
//#ifdef JUICK.COM
                juickImages = new CheckBox(SR.get(SR.MS_JUICK_IMAGES), config.juickImages);
                addControl(juickImages);
//#endif
                
            networkAnnotation = new CheckBox(SR.get(SR.MS_CONTACT_ANNOTATIONS), config.networkAnnotation);
            addControl(networkAnnotation);

            addControl(new SpacerItem(10));
            addControl(new SimpleString(SR.get(SR.MS_RECONNECT), true));

            reconnectCount = new NumberInput(SR.get(SR.MS_RECONNECT_COUNT_RETRY), config.reconnectCount, 0, 100);
            addControl(reconnectCount);

            reconnectTime = new NumberInput(SR.get(SR.MS_RECONNECT_WAIT), config.reconnectTime, 1, 60 );
            addControl(reconnectTime);

            nokiaReconnectHack = new CheckBox(SR.get(SR.MS_NOKIA_RECONNECT_HACK), config.nokiaReconnectHack);
            addControl(nokiaReconnectHack);

            if (config.userAppLevel == 1) {
//#if FILE_TRANSFER
                fileTransfer = new CheckBox(SR.get(SR.MS_FILE_TRANSFERS), config.fileTransfer);
                addControl(fileTransfer);
//#endif
//#if SERVICE_DISCOVERY && ADHOC
            adhoc = new CheckBox(SR.get(SR.MS_ADHOC), config.adhoc);
            addControl(adhoc);
//#endif
            }
            // first control is not selectable
            moveCursorTo(1);
        } else if (type == PluginBox.APPEARANCE) {
            
            Vector[] files = new StringLoader().stringLoader("/themes/res.txt", 2);
            if (files != null) {
                int size = files[0].size();

                if (size > 0) {
                    skinFiles = new ColorThemeSelector(SR.get(SR.MS_THEMES));

                    for (int i = 0; i < size; ++i) {
                        String themeName = (String)files[1].elementAt(i);
                        String themePath = (String)files[0].elementAt(i);

                        skinFiles.append(themeName, themePath);
                    }

                    addControl(skinFiles);
                }
            }
//#ifdef COLOR_TUNE
            addControl(new LinkString(SR.get(SR.MS_EDIT_COLORS)) {
                public void doAction() {
                    new ColorList().show();
                }

            });
            addControl(new LinkString(SR.get(SR.MS_INVERT)) {
                public void doAction() {
                    ColorTheme.invertSkin();
                }

            });
            addControl(new LinkString(SR.get(SR.MS_CLEAR)) {
                public void doAction() {
                    ColorTheme.initColors();
                    ColorTheme.saveToStorage();
                }
            });          
            addControl(new SpacerItem(5));
//#endif            
            panels = new DropChoiceBox(SR.get(SR.MS_PANELS));
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

            graphicsMenuPosition = new DropChoiceBox(SR.get(SR.MS_GRAPHICSMENU_POS));
            graphicsMenuPosition.append(new IconTextElement(SR.get(SR.MS_GRMENU_CENTER), RosterIcons.getInstance(), RosterIcons.ICON_ARROW_RIGHTLEFT));
            graphicsMenuPosition.append(new IconTextElement(SR.get(SR.MS_GRMENU_LEFT), RosterIcons.getInstance(), RosterIcons.ICON_ARROW_LEFT));
            graphicsMenuPosition.append(new IconTextElement(SR.get(SR.MS_GRMENU_RIGHT), RosterIcons.getInstance(), RosterIcons.ICON_ARROW_RIGHT));
            graphicsMenuPosition.setSelectedIndex(config.graphicsMenuPosition);
            addControl(graphicsMenuPosition);

//#ifdef BACK_IMAGE
            addControl(new SpacerItem(3));
            backImgType = new DropChoiceBox(SR.get(SR.MS_TYPE_BACKGROUND));
            backImgType.append(new IconTextElement(SR.get(SR.MS_BGND_NONE), ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_COLORS));
            backImgType.append(new IconTextElement(SR.get(SR.MS_BGND_IMAGE), ActionsIcons.getInstance(), ActionsIcons.ICON_VCARD));
            backImgType.append(new IconTextElement(SR.get(SR.MS_BGND_GRADIENT), ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_COLORS));
            backImgType.append(new IconTextElement(SR.get(SR.MS_MY_BGND_IMAGE), ActionsIcons.getInstance(), ActionsIcons.ICON_VCARD));
//#ifdef FILE_IO
            backImgType.append(new IconTextElement(SR.get(SR.MS_BGND_FROM_FS), ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_FILE));
            backImgType.setSelectedIndex(Config.backImgType);
//#endif
            addControl(backImgType);

//#ifdef FILE_IO
            backImgPath = new PathSelector(SR.get(SR.MS_BACK_IMG_PATH), Config.backImgPath, PathSelector.TYPE_FILE);
            addControl(backImgPath);
//#endif
//#endif

            addControl(new SpacerItem(3));

            scrollWidth = new NumberInput(SR.get(SR.MS_SCROLL_WIDTH), config.scrollWidth, 3, 25);
            addControl(scrollWidth);

            minItemHeight = new NumberInput(SR.get(SR.MS_MIN_ITEM_HEIGHT), config.minItemHeight, 0, 100);
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

            showTimeAndTraffic = new CheckBox(SR.get(SR.MS_SHOW_TIME_TRAFFIC), Config.showTimeTraffic);
            addControl(showTimeAndTraffic);

//#ifdef POPUPS
            popUps = new CheckBox(SR.get(SR.MS_POPUPS), config.popUps);
            addControl(popUps);
//#endif

            showBaloons = new CheckBox(SR.get(SR.MS_SHOW_BALLONS), config.showBalloons);
            addControl(showBaloons);

            gradient_cursor = new CheckBox(SR.get(SR.MS_GRADIENT_CURSOR), config.gradient_cursor);
            addControl(gradient_cursor);
        } else if (type == PluginBox.APPLICATION) {
            addControl(new SimpleString(SR.get(SR.MS_STARTUP_ACTIONS), true));

            autoLogin = new CheckBox(SR.get(SR.MS_AUTOLOGIN), config.autoLogin);
            addControl(autoLogin);

            autoJoinConferences = new CheckBox(SR.get(SR.MS_AUTO_CONFERENCES), config.autoJoinConferences);
            addControl(autoJoinConferences);

            cleanConfContacts = new CheckBox(SR.get(SR.MS_AUTOCLEAN_MUC), Config.cleanConfContacts);
            addControl(cleanConfContacts);

            if (config.userAppLevel == 1) {
                collapsedGroups = new CheckBox(SR.get(SR.MS_COLLAPSED_GROUPS), config.collapsedGroups);
                addControl(collapsedGroups);

                enableVersionOs = new CheckBox(SR.get(SR.MS_SHOW_HARDWARE), config.enableVersionOs);
                addControl(enableVersionOs);

                queryExit = new CheckBox(SR.get(SR.MS_CONFIRM_EXIT), config.queryExit);
                addControl(queryExit);
            }

            fullscr = new CheckBox(SR.get(SR.MS_FULLSCREEN), Config.fullscreen);
            addControl(fullscr);

            if (phoneManufacturer == Config.SONYE) {
                oldSE = new CheckBox(SR.get(SR.MS_KEYS_FOR_OLD_SE), config.oldSE);
                addControl(oldSE);
            }
            if (config.allowMinimize) {
                popupFromMinimized = new CheckBox(SR.get(SR.MS_ENABLE_POPUP), config.popupFromMinimized);
                addControl(popupFromMinimized);
            }

            executeByNum = new CheckBox(SR.get(SR.MS_EXECUTE_MENU_BY_NUMKEY), Config.executeByNum);
            addControl(executeByNum);

            addControl(new SpacerItem(10));
            addControl(new SimpleString(SR.get(SR.MS_TIME_SETTINGS), true));

            fieldGmt = new NumberInput(SR.get(SR.MS_GMT_OFFSET), config.gmtOffset, -12, 12);
            addControl(fieldGmt);

            langs=new StringLoader().stringLoader("/lang/res.txt", 3);
            if (langs[0].size() > 1) {
               addControl(new SpacerItem(10));

               langFiles = new DropChoiceBox(SR.get(SR.MS_LANGUAGE));
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
                   langFiles.append(new IconTextElement(label, ActionsIcons.getInstance(), ActionsIcons.ICON_INFO));
                   if (tempLang.equals(langCode))
                       langFiles.setSelectedIndex(i);
               }
               addControl(langFiles);
            }
            // first control is not selectable
            moveCursorTo(1);
//#ifdef AUTOSTATUS
        } else if (type == PluginBox.AUTOSTATUS) {
            autoAwayType = new DropChoiceBox(SR.get(SR.MS_AWAY_TYPE));
            autoAwayType.append(new IconTextElement(SR.get(SR.MS_AWAY_LOCK), RosterIcons.getInstance(), RosterIcons.ICON_KEYBLOCK_INDEX));
            autoAwayType.append(new IconTextElement(SR.get(SR.MS_MESSAGE_LOCK), RosterIcons.getInstance(), RosterIcons.ICON_MESSAGE_INDEX));
            autoAwayType.append(new IconTextElement(SR.get(SR.MS_IDLE), ActionsIcons.getInstance(), ActionsIcons.ICON_IDLE));
            autoAwayType.setSelectedIndex(config.autoAwayType);
            addControl(autoAwayType);

            fieldAwayDelay = new NumberInput(SR.get(SR.MS_AWAY_PERIOD), config.autoAwayDelay, 1, 60);
            addControl(fieldAwayDelay);

            awayStatus = new CheckBox(SR.get(SR.MS_AUTOSTATUS_MESSAGE), config.setAutoStatusMessage);
            addControl(awayStatus);
//#endif
//#ifdef CLASSIC_CHAT
//#         } else if (type == PluginBox.CLASSIC_CHAT) {
//#             usePhoneTheme = new CheckBox(SR.get(SR.MS_CLCHAT_BGNG_PHONE), config.usePhoneTheme);
//#             addControl(usePhoneTheme);
//# 
//#             classicChatHeight = new NumberInput(SR.get(SR.MS_CLCHAT_HEIGHT), config.classicChatHeight, 80, 320);
//#             addControl(classicChatHeight);
//# 
//#             lineCount = new NumberInput(SR.get(SR.MS_CLCHAT_MSGLIMIT), config.lineCount, 1, 1000);
//#             addControl(lineCount);
//#endif
        }
    }

    public void cmdOk() {
        Config config = Config.getInstance();
        if (type == PluginBox.CONTACTS) {
            config.autoSubscribe = subscr.getSelectedIndex();
            config.notInListDropLevel = nil.getSelectedIndex();

            Config.contactXOffset = contactXOffset.getIntValue();

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
                config.iconsLeft = iconsLeft.getValue();
//#endif

                config.ignore = ignore.getValue();
            }

            config.rosterStatus = rosterStatus.getValue();
            config.useBoldFont = useBoldFont.getValue();
            config.autoFocus = autoFocus.getValue();
        } else if (type == PluginBox.CHATS) {
            boolean createMsgEdit = false;

            if (config.msgEditType != msgEditType.getSelectedIndex()) {
                createMsgEdit = true;
            }
            config.msgEditType = msgEditType.getSelectedIndex();
//#ifdef RUNNING_MESSAGE
            if(config.runningMessage != runningMessage.getValue()) {
                createMsgEdit = true;
            }
            config.runningMessage = runningMessage.getValue();
//#endif
            config.textWrap = textWrap.getSelectedIndex();

            if(config.userAppLevel == 1) {
                config.messageLimit = messageLimit.getIntValue();
            }

            config.msglistLimit = msglistLimit.getIntValue();
            config.createMessageByFive = createMessageByFive.getValue();

            if(config.userAppLevel == 1) {
                config.storeConfPresence = storeConfPresence.getValue();
                config.showCollapsedPresences = showCollapsedPresences.getValue();
            }

//#ifdef DETRANSLIT
//#             config.autoDeTranslit = autoDetranslit.getValue();
//#endif
            if(config.userAppLevel == 1) {
                Config.showTimeInMsgs = showTimeInMsgs.getValue();
                Config.showNickNames = showNickNames.getValue();
                Config.autoScroll = autoScroll.getValue();
            }

            config.useTabs = useTabs.getValue();
            config.capsState = capsState.getValue();
            Config.showMsgSep = showMsgSep.getValue();

//#ifdef SMILES
            config.smiles=smiles.getValue();
            if(Config.hasAniSmiles) {
                Config.animatedSmiles = animatedSmiles.getValue();
            }
            MessageParser parser = MessageParser.getInstance();
            boolean aniSmiles = parser.animated;
            if(aniSmiles != Config.animatedSmiles) {
                parser.restart();
            }
//#endif

            if(config.userAppLevel == 1) {
                Config.hideMessageIcon = hideMessageIcon.getValue();
//#ifdef CLIPBOARD
                Config.useClipBoard = useClipBoard.getValue();
//#endif
            }
            if (Config.swapSendAndSuspend != swapSendAndSuspend.getValue()) {
                createMsgEdit = true;
            }
            Config.swapSendAndSuspend = swapSendAndSuspend.getValue();

            if (createMsgEdit) {
                BombusQD.sd.roster.createMessageEdit();
            }
       } else if (type == PluginBox.NETWORK) {
            if (config.userAppLevel == 1) {
//#ifdef PEP
                config.sndrcvmood = sndrcvmood.getValue();
                config.rcvtune = rcvtune.getValue();
                config.rcvactivity = rcvactivity.getValue();
//#endif
            }

            config.eventComposing = eventComposing.getValue();
            config.eventDelivery = eventDelivery.getValue();
//#ifdef JUICK.COM            
            config.juickImages = juickImages.getValue();
//#endif
            config.networkAnnotation = networkAnnotation.getValue();

            config.reconnectCount = reconnectCount.getIntValue();
            config.reconnectTime = reconnectTime.getIntValue();
            config.nokiaReconnectHack = nokiaReconnectHack.getValue();

            if(config.userAppLevel == 1) {
//#if FILE_IO && FILE_TRANSFER
                config.fileTransfer = fileTransfer.getValue();
//#endif
//#if SERVICE_DISCOVERY && ADHOC
            config.adhoc = adhoc.getValue();
//#endif
            }
            EntityCaps.initCaps();
         } else if(type == PluginBox.APPEARANCE) {
            ColorTheme.saveToStorage();
            Config.panelsState = panels.getSelectedIndex();
            VirtualList.updatePanelsState();

            config.gradientBarLigth = gradientBarLigth.getValue();
            config.gradientBarLight1=gradientBarLight1.getValue() * 10;
            config.gradientBarLight2=gradientBarLight2.getValue() * 10;

            config.graphicsMenuPosition = graphicsMenuPosition.getSelectedIndex();
//#ifdef BACK_IMAGE
            Config.backImgType = backImgType.getSelectedIndex();
//#ifdef FILE_IO
            Config.backImgPath = backImgPath.getValue();
//#endif
//#endif

            config.scrollWidth = scrollWidth.getIntValue();
            config.minItemHeight = minItemHeight.getIntValue();

            if(config.userAppLevel == 1) {
//#ifdef MEMORY_MONITOR
//#              config.memMonitor = VirtualList.memMonitor = memMon.getValue();
//#endif
                config.shadowBar = shadowBar.getValue();
            }

            Config.showMsgSep = showMsgSep.getValue();
            Config.showTimeTraffic = showTimeAndTraffic.getValue();
//#ifdef POPUPS
            config.popUps = popUps.getValue();
//#endif
            config.showBalloons = showBaloons.getValue();
            config.gradient_cursor = gradient_cursor.getValue();

//#ifdef BACK_IMAGE
            VirtualList.createImage(false);
//#endif
        } else if (type == PluginBox.APPLICATION) {
            config.autoLogin = autoLogin.getValue();
            config.autoJoinConferences = autoJoinConferences.getValue();
            Config.cleanConfContacts = cleanConfContacts.getValue();

            if (config.userAppLevel == 1) {
                config.collapsedGroups = collapsedGroups.getValue();
                config.enableVersionOs = enableVersionOs.getValue();
                config.queryExit = queryExit.getValue();
            }

            Config.fullscreen = fullscr.getValue();
            BombusQD.sd.canvas.setFullScreenMode(Config.fullscreen);

            if (phoneManufacturer == Config.SONYE) {
                config.oldSE = oldSE.getValue();
            }
            if (config.allowMinimize) {
                config.popupFromMinimized = popupFromMinimized.getValue();
            }

            Config.executeByNum = executeByNum.getValue();
            config.gmtOffset = fieldGmt.getIntValue();

            Vector langs[] = new StringLoader().stringLoader("/lang/res.txt", 3);
            if (langs[0].size() > 1) {
                String lang = (String)langs[0].elementAt(langFiles.getSelectedIndex());
                if (!config.lang.equals(lang)) {
                    config.lang = lang;
                    config.saveUTF();

                    SR.changeLocale();
                    Commands.initCommands();
                    BombusQD.sd.roster.initCommands();
                    BombusQD.sd.roster.show();
                    return;
                }
            }
//#ifdef AUTOSTATUS
        } else if (type == PluginBox.AUTOSTATUS) {
            config.autoAwayType = autoAwayType.getSelectedIndex();
            config.autoAwayDelay = Integer.parseInt(fieldAwayDelay.getValue());
            config.setAutoStatusMessage = awayStatus.getValue();

            if (autoAwayType.getSelectedIndex() != Config.AWAY_LOCK) {
                if (AutoStatus.getInstance().active()) {
                    AutoStatus.getInstance().reset();
                }
            }
//#endif
//#ifdef CLASSIC_CHAT
//#         } else if (type == PluginBox.CLASSIC_CHAT) {
//#             config.usePhoneTheme = usePhoneTheme.getValue();
//#             config.classicChatHeight = classicChatHeight.getIntValue();
//#             config.lineCount = lineCount.getIntValue();
//#endif
        }
        destroyView();
    }
}
