/*
 * PluginBox.java
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

package ui.controls.form;

import client.Config;
import ui.IconTextElement;
import ui.VirtualList;
import images.RosterIcons;

/**
 *
 * @author aqent
 */

public final class PluginBox extends IconTextElement {
    public static final int STANDART = 0;
    public static final int AUTOSTATUS = 1;
    public static final int USERKEYS = 2;
    public static final int AVATARS = 3;
    public static final int HISTORY = 4;
    public static final int IMPORT_EXPORT = 5;
    public static final int TASKS = 6;
    public static final int CLASSIC_CHAT = 7;
    public static final int DEBUG = 8;
    public static final int FONTS = 10;
    public static final int NOTIFY = 11;
    public static final int CONTACTS = 12;
    public static final int CHATS = 13;
    public static final int NETWORK = 14;
    public static final int APPEARANCE = 15;
    public static final int APPLICATION = 16;

    private int type;

    public PluginBox(String text, int type) {
        super(RosterIcons.getInstance());

        this.text = text;
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public boolean isEnabled() {
        switch (type) {
//#ifdef AUTOSTATUS
            case AUTOSTATUS:
                return Config.module_autostatus;
//#endif
//#ifdef USER_KEYS
            case USERKEYS:
                return Config.userKeys;
//#endif
//#ifdef AVATARS
            case AVATARS:
                return Config.module_avatars;
//#endif
//#ifdef HISTORY
            case HISTORY:
                return Config.module_history;
//#endif
//#ifdef AUTOTASK 
            case TASKS:
                return Config.module_tasks;
//#endif
//#ifdef CLASSIC_CHAT
//#             case CLASSIC_CHAT:
//#                 return Config.module_classicchat;
//#endif
//#ifdef DEBUG_CONSOLE
//#             case DEBUG:
//#                 return Config.debug;
//#endif
        }
        return true;
    }

    public void onSelect(VirtualList view) {
        if (type != STANDART) {
            switch (type) {
//#ifdef AUTOSTATUS
                case AUTOSTATUS:
                    Config.module_autostatus = !Config.module_autostatus;
                    break;
//#endif
//#ifdef USER_KEYS
                case USERKEYS:
                    Config.userKeys = !Config.userKeys;
                    break;
//#endif
//#ifdef AVATARS
                case AVATARS:
                    Config.module_avatars = !Config.module_avatars;
                    break;
//#endif
//#ifdef HISTORY
                case HISTORY:
                    Config.module_history = !Config.module_history;
                    break;
//#endif
//#ifdef IMPORT_EXPORT
                case IMPORT_EXPORT:
                    Config.module_ie = !Config.module_ie;
                    break;
//#endif
//#ifdef AUTOTASK
                case TASKS:
                    Config.module_tasks = !Config.module_tasks;
                    break;
//#endif
//#ifdef CLASSIC_CHAT
//#                 case CLASSIC_CHAT:
//#                     Config.module_classicchat = !Config.module_classicchat;
//#                     break;
//#endif
//#ifdef DEBUG_CONSOLE
//#                 case DEBUG:
//#                     Config.debug = !Config.debug;
//#                     break;
//#endif
            }
        }
    }

    public int getImageIndex() {        
        return isEnabled() ? 
                RosterIcons.ICON_PLUGINBOX_CHECKED : RosterIcons.ICON_PLUGINBOX_UNCHECKED;
    }
}
