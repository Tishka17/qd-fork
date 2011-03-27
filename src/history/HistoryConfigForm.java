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
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.PathSelector;
import ui.controls.form.CheckBox;

/**
 *
 * @author aqent
 */

public class HistoryConfigForm extends DefForm {
    private static final String HISTORY_DB_NAME = "history_storage";

    public static final int TYPE_RMS = 0;
    public static final int TYPE_FS = 1;
    
    private DropChoiceBox historyType;
    private PathSelector historyFolder;
//#ifdef DETRANSLIT
//#     private CheckBox translit;
//#endif

    public static int historyTypeIndex = 0;
//#ifdef FILE_IO
    public static String historyPath = "";
//#ifdef DETRANSLIT
//#     public static boolean transliterateFilenames = false;
//#endif
//#endif

    public HistoryConfigForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_HISTORY_OPTIONS));

        historyType = new DropChoiceBox(display, SR.get(SR.MS_HISTORY_TYPE));
        historyType.append(SR.get(SR.MS_HISTORY_RMS));
//#ifdef FILE_IO
        historyType.append(SR.get(SR.MS_HISTORY_FS));
//#endif
        historyType.setSelectedIndex(historyTypeIndex);
        addControl(historyType);

//#ifdef FILE_IO
//#ifdef DETRANSLIT
//#         translit = new CheckBox(SR.get(SR.MS_1251_TRANSLITERATE_FILENAMES), transliterateFilenames);
//#         addControl(translit);
//#endif
        historyFolder = new PathSelector(SR.get(SR.MS_HISTORY_FOLDER), historyPath, PathSelector.TYPE_DIR);
        addControl(historyFolder);
//#endif

        attachDisplay(display);
        this.parentView = pView;
    }

    public void cmdOk() {
        destroyView();
    }

    public void destroyView() {
        saveSettings();
        super.destroyView();
    }

    public static void loadSettings() {
        DataInputStream stream = NvStorage.ReadFileRecord(HISTORY_DB_NAME, 0);
        try {
//#ifdef FILE_IO
            historyPath = stream.readUTF();
//#endif
            historyTypeIndex = stream.readInt();
//#ifdef FILE_IO
//#ifdef DETRANSLIT
//#             transliterateFilenames=stream.readBoolean();
//#endif
//#endif
            stream.close();
        } catch (Exception e) {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private void saveSettings() {
        try {
            DataOutputStream stream = NvStorage.CreateDataOutputStream();
            historyTypeIndex = historyType.getSelectedIndex();
//#ifdef FILE_IO
            if (itemsList.contains(historyFolder)) {
                historyPath = historyFolder.getValue();
            }

//#ifdef DETRANSLIT
//#             transliterateFilenames = translit.getValue();
//#endif
            stream.writeUTF(historyPath);
//#endif
            stream.writeInt(historyTypeIndex);
//#ifdef FILE_IO
//#ifdef DETRANSLIT
//#             stream.writeBoolean(transliterateFilenames);
//#endif
//#endif
            NvStorage.writeFileRecord(stream, HISTORY_DB_NAME, 0, true);
        } catch (IOException e) {
        }
    }
}
//#endif

