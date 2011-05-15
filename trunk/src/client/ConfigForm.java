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

import alert.AlertCustomizeForm;
//#ifdef AUTOTASK
import autotask.AutoTaskForm;
//#endif
import colors.ColorConfigForm;
import font.FontConfigForm;
//#if FILE_IO && IMPORT_EXPORT 
import impexp.ImportExportForm;
//#endif
import ui.controls.form.PluginBox;
import ui.controls.form.DefForm;
import locale.SR;
//#ifdef USER_KEYS
import ui.keys.UserKeysList;
//#endif
//#ifdef HISTORY
import history.HistoryConfigForm;
//#endif

/**
 *
 * @author aqent
 */

public class ConfigForm extends DefForm {
    public ConfigForm() {
        super(SR.get(SR.MS_OPTIONS));

        addPluginBox(SR.get(SR.MS_CONTACTS), PluginBox.CONTACTS);
        addPluginBox(SR.get(SR.MS_CHATS), PluginBox.CHATS);
        addPluginBox(SR.get(SR.MS_notifyStr), PluginBox.NOTIFY);
        addPluginBox(SR.get(SR.MS_netStr), PluginBox.NETWORK);
        addPluginBox(SR.get(SR.MS_APPLICATION), PluginBox.APPLICATION);
        addPluginBox(SR.get(SR.MS_APPEARANCE), PluginBox.APPEARANCE);
        addPluginBox(SR.get(SR.MS_THEMES), PluginBox.COLOR_TUNE);
        addPluginBox(SR.get(SR.MS_FONTS), PluginBox.FONTS);
//#if IMPORT_EXPORT && FILE_IO
        addPluginBox(SR.get(SR.MS_IMPORT_EXPORT), PluginBox.IMPORT_EXPORT);
//#endif
//#ifdef AUTOSTATUS
        addPluginBox(SR.get(SR.MS_AUTOSTATUS), PluginBox.AUTOSTATUS);
//#endif
//#ifdef USER_KEYS
        addPluginBox(SR.get(SR.MS_hotkeysStr), PluginBox.USERKEYS);
//#endif
//#ifdef AVATARS
        addPluginBox(SR.get(SR.MS_AVATARS), PluginBox.AVATARS);
//#endif
//#ifdef HISTORY
        addPluginBox(SR.get(SR.MS_HISTORY), PluginBox.HISTORY);
//#endif
        if(Config.getInstance().userAppLevel == 1) {
//#ifdef AUTOTASK
            addPluginBox(SR.get(SR.MS_taskstr), PluginBox.TASKS);
//#endif
//#ifdef CLASSIC_CHAT
//#             addPluginBox(SR.get(SR.MS_CLASSIC_CHAT), PluginBox.CLASSIC_CHAT);
//#endif
//#ifdef DEBUG_CONSOLE
//#             addPluginBox(SR.get(SR.MS_DEBUG_MENU), PluginBox.DEBUG);
//#endif
        }
    }
    
    private void addPluginBox(String label, int type) {
        addControl(new PluginBox(label, type));
    }

    public String touchRightCommand() {
        return SR.get(SR.MS_BACK);
    }

    public String touchLeftCommand() {
        PluginBox box = (PluginBox)getFocusedObject();
        if (!box.isEnabled()) {
            return null;
        }
        return SR.get(SR.MS_config);
    }

    public void eventLongOk() {
	touchLeftPressed();
    }

    public void cmdOk() {
        PluginBox box = (PluginBox)getFocusedObject();
        if (!box.isEnabled()) {
            return;
        }        
        switch (box.getType()) {
            case PluginBox.COLOR_TUNE:
                new ColorConfigForm().show();
                return;
//#ifdef USER_KEYS
            case PluginBox.USERKEYS:
                new UserKeysList().show();
                return;
//#endif
//#ifdef HISTORY
            case PluginBox.HISTORY:
                new HistoryConfigForm().show();
                return;
//#endif
//#ifdef AVATARS
            case PluginBox.AVATARS:
                new AvatarConfigForm().show();
                return;
//#endif
//#if IMPORT_EXPORT && FILE_IO
            case PluginBox.IMPORT_EXPORT:
                new ImportExportForm().show();
                return;
//#endif
//#ifdef AUTOTASK
            case PluginBox.TASKS:
                new AutoTaskForm().show();
                return;
//#endif
//#ifdef DEBUG_CONSOLE
//#             case PluginBox.DEBUG:
//#                 return;
//#endif
            case PluginBox.FONTS:
                new FontConfigForm().show();
                return;
            case PluginBox.NOTIFY:
                new AlertCustomizeForm().show();
                return;
            default:
                new ModuleConfigForm(box.toString(), box.getType()).show();
                return;
        }
    }

    public void destroyView(){
        Config.getInstance().saveToStorage();
        super.destroyView();
    }
}
