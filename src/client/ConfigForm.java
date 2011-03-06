/*
 * ConfigForm.java
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
import ui.controls.form.PluginBox;
import ui.controls.form.DefForm;
import locale.SR;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import menu.MenuListener;
//#ifdef HISTORY
//# import history.HistoryConfig;
//#endif

/**
 *
 * @author aqent
 */

public class ConfigForm extends DefForm implements MenuListener {
    public ConfigForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_OPTIONS));
        this.display = display;

        Config config = Config.getInstance();
        addControl(new PluginBox(SR.get(SR.MS_contactStr), config.module_contacts, 0));
        addControl(new PluginBox(SR.get(SR.MS_msgStr), config.module_messages, 0));
        addControl(new PluginBox(SR.get(SR.MS_notifyStr), config.module_notify, 0));
        addControl(new PluginBox(SR.get(SR.MS_netStr), config.module_network, 0));
        addControl(new PluginBox(SR.get(SR.MS_appStr), config.module_app, 0));
        addControl(new PluginBox(SR.get(SR.MS_grStr), config.module_graphics, 0));
        addControl(new PluginBox(SR.get(SR.MS_COLOR_TUNE), config.module_theme, 0));
        addControl(new PluginBox(SR.get(SR.MS_fontsStr), config.module_fonts, 0));

//#ifdef AUTOSTATUS
//#         addControl(new PluginBox(SR.get(SR.MS_AUTOSTATUS), config.module_autostatus, 1));
//#endif
//#ifdef USER_KEYS
//#         addControl(new PluginBox(SR.get(SR.MS_hotkeysStr), config.userKeys, 2));
//#endif
        addControl(new PluginBox(SR.get(SR.MS_avatarStr), config.module_avatars, 3));
//#ifdef HISTORY
//#         addControl(new PluginBox(SR.get(SR.MS_historyStr), config.module_history, 4));
//#endif

//#ifdef IMPORT_EXPORT
//#ifdef FILE_IO
//#         addControl(new PluginBox(SR.get(SR.MS_ieStr), config.module_ie, 5));
//#endif
//#endif
        if(config.userAppLevel == 1) {
//#ifdef AUTOTASK
//#             addControl(new PluginBox(SR.get(SR.MS_taskstr), config.module_tasks, 6));
//#endif
//#ifdef CLASSIC_CHAT
//#             addControl(new PluginBox(SR.get(SR.MS_clchatStr), config.module_classicchat, 7));
//#endif
        }

        attachDisplay(display);
        this.parentView = pView;
    }

    public String touchRightCommand() {
        return SR.get(SR.MS_BACK);
    }

    public String touchLeftCommand() {
        Config config = Config.getInstance();

        String text = getFocusedObject().toString();
        if (text.equals(SR.get(SR.MS_hotkeysStr))) {
            if (!config.userKeys) {
                return null;
            }
//#ifdef AUTOSTATUS
//#         } else if (text.equals(SR.get(SR.MS_astatusStr))) {
//#             if (!config.module_autostatus) {
//#                 return null;
//#             }
//#endif
//#ifdef CLASSIC_CHAT
//#         } else if (text.equals(SR.get(SR.MS_clchatStr))) {
//#             if (!config.module_classicchat) {
//#                 return null;
//#             }
//#endif
//#ifdef HISTORY
//#         } else if (text.equals(SR.get(SR.MS_historyStr))) {
//#             if (!config.module_history) {
//#                 return null;
//#             }
//#endif
//#ifdef IMPORT_EXPORT
//#ifdef FILE_IO
//#         } else if(text.equals(SR.get(SR.MS_ieStr))) {
//#             if (!config.module_ie) {
//#                 return null;
//#             }
//#endif
//#endif
//#ifdef AUTOTASK
//#         } else if(text.equals(SR.get(SR.MS_taskstr))) {
//#             if (!config.module_tasks) {
//#                 return null;
//#             }
//#endif
        } else if(text.equals(SR.get(SR.MS_avatarStr))) {
            if (!config.module_avatars) {
                return null;
            }
        }
        return SR.get(SR.MS_config);
    }

    public void eventLongOk() {
	    touchLeftPressed();
    }

    public void cmdOk() {
        if(touchLeftCommand() == null) {
              return;
        }
        String type = getFocusedObject().toString();
        if (type.equals(SR.get(SR.MS_COLOR_TUNE))) {
            display.setCurrent(new colors.ColorConfigForm(display, this));
//#ifdef USER_KEYS
//#       } else if (type.equals(SR.get(SR.MS_hotkeysStr))) {
//#             display.setCurrent(new ui.keys.UserKeysList(display));
//#endif
//#ifdef HISTORY
//#         } else if (type.equals(SR.get(SR.MS_historyStr))) {
//#             display.setCurrent(new HistoryConfig(display, this));
//#endif
        } else if (type.equals(SR.get(SR.MS_fontsStr))) {
            display.setCurrent(new font.ConfigFonts(display, this));
//#ifdef IMPORT_EXPORT
//#ifdef FILE_IO
//#         } else if(type.equals(SR.get(SR.MS_ieStr))) {
//#             new impexp.IEMenu(display, this);
//#endif
//#endif
        } else if (type.equals(SR.get(SR.MS_notifyStr))) {
            display.setCurrent(new alert.AlertCustomizeForm(display, this));
//#ifdef AUTOTASK
//#         } else if (type.equals(SR.get(SR.MS_taskstr))) {
//#             new autotask.AutoTaskForm(display, this);
//#endif
        } else if (type.equals(SR.get(SR.MS_avatarStr))) {
           display.setCurrent(new ConfigAvatar(display,this));
        } else {
            new ModuleConfigForm(display, this, type);
        }
    }

    public void destroyView(){
        Config.getInstance().saveToStorage();
        display.setCurrent(parentView);
    }
}
