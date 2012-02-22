/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info;

import java.util.Date;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import locale.SR;
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
        try {
            RecordStore.deleteRecordStore(storeList[cursor].toString());
            itemsList.setElementAt(new MultiLine(storeList[cursor], "deleted"), cursor);
            redraw();
        } catch (RecordStoreException e) {
        }
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_DELETE);
    }   
    
}
