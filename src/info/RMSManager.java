/*
 * *
 * Created on 22.02.2012
 *
 * Copyright (c) 2012, Andrey Tikhonov (Tishka17), http://itishka.org
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
package info;

import java.util.Date;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import locale.SR;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;

/**
 *
 * @author tishka17
 */
public class RMSManager extends DefForm {
    String storeList[];
    public RMSManager() {
        super("RMS Manager");
        storeList=RecordStore.listRecordStores();
        for (int i=0;i<storeList.length;i++) {
            int size=0, count=0;
            Date lastModified = new Date(0);
            try {
                RecordStore r = RecordStore.openRecordStore(storeList[i], false);
                size = r.getSize();
                count = r.getNumRecords();
                lastModified = new Date (r.getLastModified());
                r.closeRecordStore();
            } catch ( RecordStoreException e) {
            }
            MultiLine ml = new MultiLine(storeList[i], "size: "+size+"\nrecords: "+count+"\nmodified: "+lastModified.toString());
            addControl(ml);
        }
    }
       
    public void cmdOk() { 
        AlertBox box = new AlertBox(SR.get(SR.MS_DELETE_ASK), storeList[cursor].toString(), AlertBox.BUTTONS_YESNO) {
            public void yes() {
                deleteRMS(cursor);
            }
        };
        box.show();
    }
       
    public void deleteRMS(int n) {
        try {
            RecordStore.deleteRecordStore(storeList[n].toString());
            itemsList.setElementAt(new MultiLine(storeList[n], "deleted"), n);
            redraw();
        } catch (RecordStoreException e) {
        }
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_DELETE);
    }   
    
}
