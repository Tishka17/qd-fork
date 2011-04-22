/*
 * Configs.java
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
 */

//#if IMPORT_EXPORT && FILE_IO
package impexp;

import alert.AlertCustomize;
import client.Config;
import light.CustomLight;
import midlet.BombusQD;
import ui.VirtualList;
import xmpp.EntityCaps;

public class Configs {
    private static final String CONFIGS_FILE = "config.txt";

    public Configs(String path, int action) {
        switch (action) {
            case ImportExportForm.CONFIG_EXPORT:
                exportData(path);
                break;
            case ImportExportForm.CONFIG_IMPORT:
                importData(path);
                break;
        }
    }

    private void exportData(String path) {
        Config config = Config.getInstance();

        StringBuffer data = new StringBuffer();
        data.append("<config>\n");

        writeInt(data, "gmtOffset", config.gmtOffset);
        writeInt(data, "rosterFont", config.rosterFont);
        writeInt(data, "msgFont", config.msgFont);
        writeInt(data, "notInListDropLevel", config.notInListDropLevel);
        writeInt(data, "textWrap", config.textWrap);
//#ifdef AUTOSTATUS
        writeInt(data, "autoAwayDelay", config.autoAwayDelay);
        writeInt(data, "autoAwayType", config.autoAwayType);
//#endif
        writeInt(data, "panelsState", config.panelsState);
        writeInt(data, "messageLimit", config.messageLimit);
        writeInt(data, "msglistLimit", config.msglistLimit);
        writeInt(data, "autoSubscribe", config.autoSubscribe);
        writeInt(data, "barFont", config.barFont);
        writeInt(data, "baloonFont", config.baloonFont);
        writeInt(data, "reconnectCount", config.reconnectCount);
        writeInt(data, "reconnectTime", config.reconnectTime);
//#ifdef AVATARS
        writeInt(data, "maxAvatarHeight", config.maxAvatarHeight);
//#endif
//#ifdef BACK_IMAGE
        writeInt(data, "backImgType", config.backImgType);
//#endif
        writeInt(data, "scrollWidth", config.scrollWidth);
//#ifdef CLASSIC_CHAT
//#         writeInt(data, "classicChatHeight", config.classicChatHeight);
//#         writeInt(data, "lineCount", config.lineCount);
//#endif
        writeInt(data, "argb_bgnd", config.argb_bgnd);
        writeInt(data, "gmenu_bgnd", config.gmenu_bgnd);
        writeInt(data, "popup_bgnd", config.popup_bgnd);
        writeInt(data, "cursor_bgnd", config.cursor_bgnd);
//#ifdef AVATARS
        writeInt(data, "maxAvatarWidth", config.maxAvatarWidth);
//#endif
        writeInt(data, "msgEditType", config.msgEditType);
        writeInt(data, "graphicsMenuPosition", config.graphicsMenuPosition);
        writeInt(data, "gradientBarLight1", config.gradientBarLight1);
        writeInt(data, "gradientBarLight2", config.gradientBarLight2);
        writeInt(data, "userAppLevel", config.userAppLevel);
        writeInt(data, "menuFont", config.menuFont);
        writeInt(data, "contactXOffset", config.contactXOffset);
        writeInt(data, "minItemHeight", config.minItemHeight);
        writeInt(data, "defaultAlertProfile", config.defaultAlertProfile);
//#ifdef LIGHT_CONTROL
        writeInt(data, "lightKeyPressTime", Config.lightKeyPressTime);
        writeInt(data, "lightMessageTime", Config.lightMessageTime);
        writeInt(data, "lightPresenceTime", Config.lightPresenceTime);
        writeInt(data, "lightConnectTime", Config.lightConnectTime);
        writeInt(data, "lightErrorTime", Config.lightErrorTime);
        writeInt(data, "lightBlinkTime", Config.lightBlinkTime);

        writeInt(data, "lightIdle", Config.lightIdle);
        writeInt(data, "lightKeyPress", Config.lightKeyPress);
        writeInt(data, "lightMessage", Config.lightMessage);
        writeInt(data, "lightPresence", Config.lightPresence);
        writeInt(data, "lightConnect", Config.lightConnect);
        writeInt(data, "lightError", Config.lightError);
        writeInt(data, "lightBlink", Config.lightBlink);
//#endif

        writeUTF(data, "lang", config.lang);
//#ifdef AVATARS
        writeUTF(data, "msgAvatarPath", config.msgAvatarPath);
//#endif
//#if BACK_IMAGE && FILE_IO
        writeUTF(data, "backImgPath", config.backImgPath);
//#endif

        writeBoolean(data, "oldSE", config.oldSE);
        writeBoolean(data, "showTimeTraffic", config.showTimeTraffic);
        writeBoolean(data, "hideMessageIcon", config.hideMessageIcon);
        writeBoolean(data, "iconsLeft", config.iconsLeft);
        writeBoolean(data, "usePhoneTheme", config.usePhoneTheme);
        writeBoolean(data, "gradient_cursor", config.gradient_cursor);
//#ifdef AVATARS
        writeBoolean(data, "auto_queryPhoto", config.auto_queryPhoto);
        writeBoolean(data, "autoSaveVcard", config.autoSaveVcard);
        writeBoolean(data, "showAvatarRect", config.showAvatarRect);
        writeBoolean(data, "autoload_FSPhoto", config.autoload_FSPhoto);
//#endif
        writeBoolean(data, "nokiaReconnectHack", config.nokiaReconnectHack);
        writeBoolean(data, "showTimeInMsgs", config.showTimeInMsgs);
        writeBoolean(data, "autoScroll", config.autoScroll);
        writeBoolean(data, "useItalic", config.useItalic);
        writeBoolean(data, "userKeys", config.userKeys);
        writeBoolean(data, "module_autostatus", config.module_autostatus);
        writeBoolean(data, "module_classicchat", config.module_classicchat);
        writeBoolean(data, "module_history", config.module_history);
        writeBoolean(data, "module_ie", config.module_ie);
        writeBoolean(data, "module_tasks", config.module_tasks);
//#ifdef AVATARS
        writeBoolean(data, "module_avatars", config.module_avatars);
//#endif
        writeBoolean(data, "animatedSmiles", config.animatedSmiles);
        writeBoolean(data, "runningMessage", config.runningMessage);
        writeBoolean(data, "debug", config.debug);
        writeBoolean(data, "showCollapsedPresences", config.showCollapsedPresences);
        writeBoolean(data, "networkAnnotation", config.networkAnnotation);
        writeBoolean(data, "showOfflineContacts", config.showOfflineContacts);
        writeBoolean(data, "fullscreen", config.fullscreen);
//#ifdef SMILES
        writeBoolean(data, "smiles", config.smiles);
//#endif
        writeBoolean(data, "showTransports", config.showTransports);
        writeBoolean(data, "selfContact", config.selfContact);
        writeBoolean(data, "collapsedGroups", config.collapsedGroups);
        writeBoolean(data, "ignore", config.ignore);
        writeBoolean(data, "eventComposing", config.eventComposing);
        writeBoolean(data, "autoLogin", config.autoLogin);
        writeBoolean(data, "autoJoinConferences", config.autoJoinConferences);
        writeBoolean(data, "popupFromMinimized", config.popupFromMinimized);
        writeBoolean(data, "notifyBlink", config.notifyBlink);
//#ifdef MEMORY_MONITOR
//#         writeBoolean(data, "memMonitor", config.memMonitor);
//#endif
        writeBoolean(data, "autoFocus", config.autoFocus);
        writeBoolean(data, "storeConfPresence", config.storeConfPresence);
        writeBoolean(data, "capsState", config.capsState);
        writeBoolean(data, "fileTransfer", config.fileTransfer);
        writeBoolean(data, "lightState", config.lightState);
//#ifdef AUTOSTATUS
        writeBoolean(data, "setAutoStatusMessage", config.setAutoStatusMessage);
//#endif
        writeBoolean(data, "cleanConfContacts", config.cleanConfContacts);
//#ifdef POPUPS
        writeBoolean(data, "popUps", config.popUps);
//#endif
        writeBoolean(data, "showResources", config.showResources);
        writeBoolean(data, "enableVersionOs", config.enableVersionOs);
        writeBoolean(data, "eventDelivery", config.eventDelivery);
        writeBoolean(data, "transliterateFilenames", config.transliterateFilenames);
        writeBoolean(data, "rosterStatus", config.rosterStatus);
        writeBoolean(data, "queryExit", config.queryExit);
        writeBoolean(data, "notifyPicture", config.notifyPicture);
        writeBoolean(data, "showBalloons", config.showBalloons);
        writeBoolean(data, "useTabs", config.useTabs);
        writeBoolean(data, "useBoldFont", config.useBoldFont);
        writeBoolean(data, "IQNotify", config.IQNotify);
//#ifdef PEP
        writeBoolean(data, "sndrcvmood", config.sndrcvmood);
        writeBoolean(data, "rcvtune", config.rcvtune);
        writeBoolean(data, "rcvactivity", config.rcvactivity);
//#endif
//#ifdef CLIPBOARD
        writeBoolean(data, "useClipBoard", config.useClipBoard);
//#endif
        writeBoolean(data, "autoDeTranslit", config.autoDeTranslit);
//#ifdef CLIENTS_ICONS
        writeBoolean(data, "showClientIcon", config.showClientIcon);
//#endif
        writeBoolean(data, "executeByNum", config.executeByNum);
        writeBoolean(data, "showNickNames", config.showNickNames);
        writeBoolean(data, "adhoc", config.adhoc);
        writeBoolean(data, "createMessageByFive", config.createMessageByFive);
        writeBoolean(data, "gradientBarLigth", config.gradientBarLigth);
        writeBoolean(data, "shadowBar", config.shadowBar);
        writeBoolean(data, "simpleContacts", config.simpleContacts);
        writeBoolean(data, "swapSendAndSuspend", config.swapSendAndSuspend);
//#ifdef LIGHT_CONTROL
        writeBoolean(data, "lightControl", config.lightControl);
//#endif

//#ifdef HISTORY
        writeInt(data, "historyTypeIndex", Config.historyTypeIndex);
        writeUTF(data, "historyPath", Config.historyPath);
//#endif

        AlertCustomize ac = AlertCustomize.getInstance();
        writeInt(data, "soundsMsgIndex", ac.soundsMsgIndex);
        writeInt(data, "soundOnlineIndex", ac.soundOnlineIndex);
        writeInt(data, "soundOfflineIndex", ac.soundOfflineIndex);
        writeInt(data, "soundForYouIndex", ac.soundForYouIndex);
        writeInt(data, "soundComposingIndex", ac.soundComposingIndex);
        writeInt(data, "soundConferenceIndex", ac.soundConferenceIndex);
        writeInt(data, "soundStartUpIndex", ac.soundStartUpIndex);
        writeInt(data, "soundOutgoingIndex", ac.soundOutgoingIndex);
        writeInt(data, "soundVIPIndex", ac.soundVIPIndex);
        writeInt(data, "soundAttentionIndex", ac.soundAttentionIndex);
        writeInt(data, "soundVol", ac.soundVol);
        writeInt(data, "vibraLen", ac.vibraLen);
        writeInt(data, "vibraRepeatCount", ac.vibraRepeatCount);
        writeInt(data, "vibraRepeatPause", ac.vibraRepeatPause);

        writeBoolean(data, "vibrateOnlyHighlited", ac.vibrateOnlyHighlited);

        data.append("</config>\n");

        IEUtils.writeFile(path + CONFIGS_FILE, data.toString());
    }

    private void importData(String path) {
        String data = IEUtils.readFile(path);

        if (data != null) {
            Config config = Config.getInstance();

            config.gmtOffset = readInt(data, "gmtOffset", config.gmtOffset);
            config.rosterFont = readInt(data, "rosterFont", config.rosterFont);
            config.msgFont = readInt(data, "msgFont", config.msgFont);
            config.notInListDropLevel = readInt(data, "notInListDropLevel", config.notInListDropLevel);
            config.textWrap = readInt(data, "textWrap", config.textWrap);
//#ifdef AUTOSTATUS
            config.autoAwayDelay = readInt(data, "autoAwayDelay", config.autoAwayDelay);
            config.autoAwayType = readInt(data, "autoAwayType", config.autoAwayType);
//#endif
            config.panelsState = readInt(data, "panelsState", config.panelsState);
            config.messageLimit = readInt(data, "messageLimit", config.messageLimit);
            config.msglistLimit = readInt(data, "msglistLimit", config.msglistLimit);
            config.autoSubscribe = readInt(data, "autoSubscribe", config.autoSubscribe);
            config.barFont = readInt(data, "barFont", config.barFont);
            config.baloonFont = readInt(data, "baloonFont", config.baloonFont);
            config.reconnectCount = readInt(data, "reconnectCount", config.reconnectCount);
            config.reconnectTime = readInt(data, "reconnectTime", config.reconnectTime);
//#ifdef AVATARS
            config.maxAvatarHeight = readInt(data,  "maxAvatarHeight", config.maxAvatarHeight);
//#endif
//#ifdef BACK_IMAGE
            config.backImgType = readInt(data, "backImgType", config.backImgType);
//#endif
            config.scrollWidth = readInt(data, "scrollWidth", config.scrollWidth);
//#ifdef CLASSIC_CHAT
//#             config.classicChatHeight = readInt(data, "classicChatHeight", config.classicChatHeight);
//#             config.lineCount = readInt(data, "lineCount", config.lineCount);
//#endif
            config.argb_bgnd = readInt(data, "argb_bgnd", config.argb_bgnd);
            config.gmenu_bgnd = readInt(data, "gmenu_bgnd", config.gmenu_bgnd);
            config.popup_bgnd = readInt(data, "popup_bgnd", config.popup_bgnd);
            config.cursor_bgnd = readInt(data, "cursor_bgnd", config.cursor_bgnd);
//#ifdef AVATARS
            config.maxAvatarWidth = readInt(data, "maxAvatarWidth", config.maxAvatarWidth);
//#endif
            config.msgEditType = readInt(data, "msgEditType", config.msgEditType);
            config.graphicsMenuPosition = readInt(data, "graphicsMenuPosition", config.graphicsMenuPosition);
            config.gradientBarLight1 = readInt(data, "gradientBarLight1", config.gradientBarLight1);
            config.gradientBarLight2 = readInt(data, "gradientBarLight2", config.gradientBarLight2);
            config.userAppLevel = readInt(data, "userAppLevel", config.userAppLevel);
            config.menuFont = readInt(data, "menuFont", config.menuFont);
            config.contactXOffset = readInt(data, "contactXOffset", config.contactXOffset);
            config.minItemHeight = readInt(data, "minItemHeight", config.minItemHeight);
            config.defaultAlertProfile = readInt(data, "defaultAlertProfile", config.defaultAlertProfile);
//#ifdef LIGHT_CONTROL
            Config.lightKeyPressTime =readInt(data, "lightKeyPressTime", Config.lightKeyPressTime);
            Config.lightMessageTime = readInt(data, "lightMessageTime", Config.lightMessageTime);
            Config.lightPresenceTime = readInt(data, "lightPresenceTime", Config.lightPresenceTime);
            Config.lightConnectTime = readInt(data, "lightConnectTime", Config.lightConnectTime);
            Config.lightErrorTime = readInt(data, "lightErrorTime", Config.lightErrorTime);
            Config.lightBlinkTime = readInt(data, "lightBlinkTime", Config.lightBlinkTime);

            Config.lightIdle = readInt(data, "lightIdle", Config.lightIdle);
            Config.lightKeyPress = readInt(data, "lightKeyPress", Config.lightKeyPress);
            Config.lightMessage = readInt(data, "lightMessage", Config.lightMessage);
            Config.lightPresence = readInt(data, "lightPresence", Config.lightPresence);
            Config.lightConnect = readInt(data, "lightConnect", Config.lightConnect);
            Config.lightError = readInt(data, "lightError", Config.lightError);
            Config.lightBlink = readInt(data, "lightBlink", Config.lightBlink);
//#endif
//#ifdef AVATARS
            config.msgAvatarPath = readUTF(data, "msgAvatarPath", config.msgAvatarPath);
//#endif
            config.lang = readUTF(data, "lang", config.lang);
//#if BACK_IMAGE && FILE_IO
            config.backImgPath = readUTF(data, "backImgPath", config.backImgPath);
//#endif
            config.oldSE = readBoolean(data, "oldSE", config.oldSE);
            config.showTimeTraffic = readBoolean(data, "showTimeTraffic", config.showTimeTraffic);
            config.hideMessageIcon = readBoolean(data, "hideMessageIcon", config.hideMessageIcon);
            config.iconsLeft = readBoolean(data, "iconsLeft", config.iconsLeft);
            config.usePhoneTheme = readBoolean(data, "usePhoneTheme", config.usePhoneTheme);
            config.gradient_cursor = readBoolean(data, "gradient_cursor", config.gradient_cursor);
//#ifdef AVATARS
            config.auto_queryPhoto = readBoolean(data, "auto_queryPhoto", config.auto_queryPhoto);
            config.autoSaveVcard = readBoolean(data, "autoSaveVcard", config.autoSaveVcard);
            config.showAvatarRect = readBoolean(data, "showAvatarRect", config.showAvatarRect);
            config.autoload_FSPhoto = readBoolean(data, "autoload_FSPhoto", config.autoload_FSPhoto);
//#endif
            config.nokiaReconnectHack = readBoolean(data, "nokiaReconnectHack", config.nokiaReconnectHack);
            config.showTimeInMsgs = readBoolean(data, "showTimeInMsgs", config.showTimeInMsgs);
            config.autoScroll = readBoolean(data, "autoScroll", config.autoScroll);
            config.useItalic = readBoolean(data, "useItalic", config.useItalic);
            config.userKeys = readBoolean(data, "userKeys", config.userKeys);
            config.module_autostatus = readBoolean(data, "module_autostatus", config.module_autostatus);
            config.module_classicchat = readBoolean(data, "module_classicchat", config.module_classicchat);
            config.module_history = readBoolean(data, "module_history", config.module_history);
            config.module_ie = readBoolean(data, "module_ie", config.module_ie);
            config.module_tasks = readBoolean(data, "module_tasks", config.module_tasks);
//#ifdef AVATARS
            config.module_avatars = readBoolean(data, "module_avatars", config.module_avatars);
//#endif
            config.animatedSmiles = readBoolean(data, "animatedSmiles", config.animatedSmiles);
            config.runningMessage = readBoolean(data, "runningMessage", config.runningMessage);
            config.debug = readBoolean(data, "debug", config.debug);
            config.showCollapsedPresences = readBoolean(data, "showCollapsedPresences", config.showCollapsedPresences);
            config.networkAnnotation = readBoolean(data, "networkAnnotation", config.networkAnnotation);
            config.showOfflineContacts = readBoolean(data, "showOfflineContacts", config.showOfflineContacts);
            config.fullscreen = readBoolean(data, "fullscreen", config.fullscreen);
//#ifdef SMILES
            config.smiles = readBoolean(data, "smiles", config.smiles);
//#endif
            config.showTransports = readBoolean(data, "showTransports", config.showTransports);
            config.selfContact = readBoolean(data, "selfContact", config.selfContact);
            config.collapsedGroups = readBoolean(data, "collapsedGroups", config.collapsedGroups);
            config.ignore = readBoolean(data, "ignore", config.ignore);
            config.eventComposing = readBoolean(data, "eventComposing", config.eventComposing);
            config.autoLogin = readBoolean(data, "autoLogin", config.autoLogin);
            config.autoJoinConferences = readBoolean(data, "autoJoinConferences", config.autoJoinConferences);
            config.popupFromMinimized = readBoolean(data, "popupFromMinimized", config.popupFromMinimized);
            config.notifyBlink = readBoolean(data, "notifyBlink", config.notifyBlink);
//#ifdef MEMORY_MONITOR
//#             config.memMonitor = readBoolean(data, "memMonitor", config.memMonitor);
//#endif
            config.autoFocus = readBoolean(data, "autoFocus", config.autoFocus);
            config.storeConfPresence = readBoolean(data, "storeConfPresence", config.storeConfPresence);
            config.capsState = readBoolean(data, "capsState", config.capsState);
            config.fileTransfer = readBoolean(data, "fileTransfer", config.fileTransfer);
            config.lightState = readBoolean(data, "lightState", config.lightState);
//#ifdef AUTOSTATUS
            config.setAutoStatusMessage = readBoolean(data, "setAutoStatusMessage", config.setAutoStatusMessage);
//#endif
            config.cleanConfContacts = readBoolean(data, "cleanConfContacts", config.cleanConfContacts);
//#ifdef POPUPS
            config.popUps = readBoolean(data, "popUps", config.popUps);
//#endif
            config.showResources = readBoolean(data, "showResources", config.showResources);
            config.enableVersionOs = readBoolean(data, "enableVersionOs", config.enableVersionOs);
            config.eventDelivery = readBoolean(data, "eventDelivery", config.eventDelivery);
            config.transliterateFilenames = readBoolean(data, "transliterateFilenames", config.transliterateFilenames);
            config.rosterStatus = readBoolean(data, "rosterStatus", config.rosterStatus);
            config.queryExit = readBoolean(data, "queryExit", config.queryExit);
            config.notifyPicture = readBoolean(data, "notifyPicture", config.notifyPicture);
            config.showBalloons = readBoolean(data, "showBalloons", config.showBalloons);
            config.useTabs = readBoolean(data, "useTabs", config.useTabs);
            config.useBoldFont = readBoolean(data, "useBoldFont", config.useBoldFont);
            config.IQNotify = readBoolean(data, "IQNotify", config.IQNotify);
//#ifdef CLIPBOARD
            config.useClipBoard = readBoolean(data, "useClipBoard", config.useClipBoard);
//#endif
//#ifdef PEP
            config.sndrcvmood = readBoolean(data, "sndrcvmood", config.sndrcvmood);
            config.rcvtune = readBoolean(data, "rcvtune", config.rcvtune);
            config.rcvactivity = readBoolean(data, "rcvactivity", config.rcvactivity);
//#endif
            config.autoDeTranslit = readBoolean(data, "autoDeTranslit", config.autoDeTranslit);
//#ifdef CLIENTS_ICONS
            config.showClientIcon = readBoolean(data, "showClientIcon", config.showClientIcon);
//#endif
            config.executeByNum = readBoolean(data, "executeByNum", config.executeByNum);
            config.showNickNames = readBoolean(data, "showNickNames", config.showNickNames);
            config.adhoc = readBoolean(data, "adhoc", config.adhoc);
            config.createMessageByFive = readBoolean(data, "createMessageByFive", config.createMessageByFive);
            config.gradientBarLigth = readBoolean(data, "gradientBarLigth", config.gradientBarLigth);
            config.shadowBar = readBoolean(data, "shadowBar", config.shadowBar);
            config.simpleContacts = readBoolean(data, "simpleContacts", config.simpleContacts);
            config.swapSendAndSuspend = readBoolean(data, "swapSendAndSuspend", config.swapSendAndSuspend);
//#ifdef LIGHT_CONTROL
            config.lightControl = readBoolean(data, "lightControl", config.lightControl);
//#endif

            AlertCustomize ac = AlertCustomize.getInstance();
            ac.soundsMsgIndex = readInt(data, "soundsMsgIndex", ac.soundsMsgIndex);
            ac.soundOnlineIndex = readInt(data, "soundOnlineIndex", ac.soundOnlineIndex);
            ac.soundOfflineIndex = readInt(data, "soundOfflineIndex", ac.soundOfflineIndex);
            ac.soundForYouIndex = readInt(data, "soundForYouIndex", ac.soundForYouIndex);
            ac.soundComposingIndex = readInt(data, "soundComposingIndex", ac.soundComposingIndex);
            ac.soundConferenceIndex = readInt(data, "soundConferenceIndex", ac.soundConferenceIndex);
            ac.soundStartUpIndex = readInt(data, "soundStartUpIndex", ac.soundStartUpIndex);
            ac.soundOutgoingIndex = readInt(data, "soundOutgoingIndex", ac.soundOutgoingIndex);
            ac.soundVIPIndex = readInt(data, "soundVIPIndex", ac.soundVIPIndex);
            ac.soundAttentionIndex = readInt(data, "soundAttentionIndex", ac.soundAttentionIndex);
            ac.soundVol = readInt(data, "soundVol", ac.soundVol);
            ac.vibraLen = readInt(data, "vibraLen", ac.vibraLen);
            ac.vibraRepeatCount = readInt(data, "vibraRepeatCount", ac.vibraRepeatCount);
            ac.vibraRepeatPause = readInt(data, "vibraRepeatPause", ac.vibraRepeatPause);

            ac.vibrateOnlyHighlited = readBoolean(data, "vibrateOnlyHighlited", ac.vibrateOnlyHighlited);

//#ifdef HISTORY
            Config.historyTypeIndex = readInt(data, "historyTypeIndex", Config.historyTypeIndex);
            Config.historyPath = readUTF(data, "historyPath", Config.historyPath);
//#endif
            EntityCaps.initCaps();
            BombusQD.sd.roster.updateBarsFont();
            VirtualList.changeOrient(Config.panelsState);
//#ifdef BACK_IMAGE
            VirtualList.createImage(false);
//#endif

//#ifdef LIGHT_CONTROL
            CustomLight.switchOn(Config.lightControl);
//#endif

            ac.saveToStorage();
            config.saveToStorage();
        }
    }

    private void writeInt(StringBuffer buf, String tag, int value) {
        buf.append(IEUtils.createBlock(tag, String.valueOf(value)));
    }

    private int readInt(String raw, String tag, int def) {
        String value = IEUtils.findBlock(raw, tag);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return def;
    }

    private void writeUTF(StringBuffer buf, String tag, String value) {
        buf.append(IEUtils.createBlock(tag, value));
    }

    private String readUTF(String raw, String tag, String def) {
        String value = IEUtils.findBlock(raw, tag);
        if (value != null) {
            return value;
        }
        return def;
    }

    private void writeBoolean(StringBuffer buf, String tag, boolean value) {
        buf.append(IEUtils.createBlock(tag, value ? "true" : "false"));
    }

    private boolean readBoolean(String raw, String tag, boolean def) {
        String value = IEUtils.findBlock(raw, tag);
        if (value != null) {
            return value.equals("true");
        }
        return def;
    }


}
//#endif
