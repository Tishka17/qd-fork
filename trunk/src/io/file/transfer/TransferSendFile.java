/*
 * TransferSendFile.java
 *
 * Created on 26.05.2008, 9:15
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
package io.file.transfer;

import images.ActionsIcons;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.IconTextElement;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.PathSelector;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;

public class TransferSendFile extends DefForm {
    private String to;

    private PathSelector selectFile;
    private TextInput description;
    private DropChoiceBox method;

    public TransferSendFile(String recipientJid) {
        super(SR.get(SR.MS_SEND_FILE));
        this.to=recipientJid;

        addControl(new SimpleString(recipientJid, true));

        selectFile = new PathSelector(SR.get(SR.MS_SELECT_FILE), null, PathSelector.TYPE_FILE);
        addControl(selectFile);

        description = new TextInput(SR.get(SR.MS_DESCRIPTION), null, TextField.ANY);
        addControl(description);
        
        method = new DropChoiceBox(SR.get(SR.MS_SEND_METHOD));
        method.append(new IconTextElement("IBB (default)", ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_FILE));
        method.append(new IconTextElement("Jimm Aspro", ActionsIcons.getInstance(), ActionsIcons.ICON_SEND_FILE));
        method.setSelectedIndex(TransferTask.METHOD_IBB);
        addControl(method);

        moveCursorTo(1);
    }

    public void cmdOk() {
        if (selectFile.getValue()==null || selectFile.getValue().length()==0) {
            return;
        }

        try {
            TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), selectFile.getValue(), description.getValue(), false, null, method.index);
            TransferDispatcher.getInstance().sendFile(task);

            TransferManager manager = new TransferManager();
            manager.setParentView(getParentView());
            manager.show();
            return;
        } catch (Exception e) {}
        destroyView();
    }
}
//#endif
//#endif
