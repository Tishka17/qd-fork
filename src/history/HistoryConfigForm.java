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
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
//#ifdef FILE_IO
import ui.controls.form.NumberInput;
import ui.controls.form.PathSelector;
import ui.controls.form.SpacerItem;
//#endif

/**
 *
 * @author aqent
 */

public class HistoryConfigForm extends DefForm {
    private DropChoiceBox historyType;
    private NumberInput loadLastMsgCount;
//#ifdef FILE_IO
    private PathSelector historyFolder;
//#endif

    public HistoryConfigForm() {
        super(SR.get(SR.MS_HISTORY));
        
        Config config = Config.getInstance();

        historyType = new DropChoiceBox(SR.get(SR.MS_HISTORY_TYPE));
        historyType.append(SR.get(SR.MS_HISTORY_RMS));
//#ifdef FILE_IO
        historyType.append(SR.get(SR.MS_HISTORY_FS));
//#endif
        historyType.setSelectedIndex(config.historyTypeIndex);
        addControl(historyType);

//#ifdef FILE_IO
        historyFolder = new PathSelector(SR.get(SR.MS_HISTORY_FOLDER), config.historyPath, PathSelector.TYPE_DIR);
        addControl(historyFolder);
//#endif
        
        addControl(new SpacerItem(5));
        
        loadLastMsgCount = new NumberInput("Show last messages from history", config.loadLastMsgCount, 0, 50);
        addControl(loadLastMsgCount);
    }

    public void cmdOk() {
        Config config = Config.getInstance();
//#ifdef FILE_IO
        config.historyPath = historyFolder.getValue();
//#endif
        config.historyTypeIndex = historyType.getValue();
        config.loadLastMsgCount = loadLastMsgCount.getIntValue();

        destroyView();
    }
}
//#endif

