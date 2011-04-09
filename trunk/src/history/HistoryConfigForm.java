/*
 * HistoryConfig.java
 *
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
//#ifdef HISTORY

package history;

import client.Config;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#ifdef DETRANSLIT
//# import ui.controls.form.CheckBox;
//#endif
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.PathSelector;

/**
 *
 * @author aqent
 */

public class HistoryConfigForm extends DefForm {   
    private DropChoiceBox historyType;
    private PathSelector historyFolder;
//#ifdef DETRANSLIT
//#     private CheckBox translit;
//#endif

    public HistoryConfigForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_HISTORY_OPTIONS));

        historyType = new DropChoiceBox(display, SR.get(SR.MS_HISTORY_TYPE));
        historyType.append(SR.get(SR.MS_HISTORY_RMS));
//#ifdef FILE_IO
        historyType.append(SR.get(SR.MS_HISTORY_FS));
//#endif
        historyType.setSelectedIndex(Config.historyTypeIndex);
        addControl(historyType);

//#ifdef FILE_IO
//#ifdef DETRANSLIT
//#         translit = new CheckBox(SR.get(SR.MS_1251_TRANSLITERATE_FILENAMES), Config.transliterateFilenames);
//#         addControl(translit);
//#endif
        historyFolder = new PathSelector(SR.get(SR.MS_HISTORY_FOLDER), Config.historyPath, PathSelector.TYPE_DIR);
        addControl(historyFolder);
//#endif

        attachDisplay(display);
        this.parentView = pView;
    }

    public void cmdOk() {
        Config.historyPath = historyFolder.getValue();
        Config.historyTypeIndex = historyType.getValue();
//#ifdef FILE_IO
//#ifdef DETRANSLIT
//#         Config.transliterateFilenames = translit.getValue();
//#endif
//#endif

        destroyView();
    }
}
//#endif

