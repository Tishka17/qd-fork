/*
 * StatusList.java
 *
 * Created on 3.12.2005, 17:33
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

import com.alsutton.jabber.datablocks.Presence;
//#ifdef PEP
import images.ActivityIcons;
import images.MoodIcons;
import ui.controls.form.SimpleString;
//#endif
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import locale.SR;
import io.NvStorage;
import java.io.IOException;

/**
 *
 * @author EvgS
 */

public class StatusList {
    public static final int STATUS_COUNT = 7;

    private static StatusList instance;

    public static StatusList getInstance() {
	if (instance==null) instance=new StatusList();
	return instance;
    }
    
    public void reinit() {
       instance = null; 
    }

    public Vector statusList;

    private StatusList() {
        statusList = new Vector(0);
        try {
	    DataInputStream inputStream=NvStorage.ReadFileRecord("status", 0);
	    
	    createFromStream(Presence.PRESENCE_ONLINE, Presence.PRS_ONLINE, inputStream);
	    createFromStream(Presence.PRESENCE_CHAT, Presence.PRS_CHAT, inputStream);
	    createFromStream(Presence.PRESENCE_AWAY, Presence.PRS_AWAY, inputStream);
 	    createFromStream(Presence.PRESENCE_XA, Presence.PRS_XA, inputStream);
 	    createFromStream(Presence.PRESENCE_DND, Presence.PRS_DND, inputStream);
 	    createFromStream(Presence.PRESENCE_INVISIBLE, Presence.PRS_INVISIBLE, inputStream);
	    createFromStream(Presence.PRESENCE_OFFLINE, Presence.PRS_OFFLINE, inputStream);
	    
	    inputStream.close();
            inputStream=null;
        } catch (Exception e) { 
            //e.printStackTrace(); 
        }
//#ifdef PEP
        boolean hasMoodIcons = MoodIcons.getInstance().isLoaded();
        boolean hasActIcons = ActivityIcons.getInstance().isLoaded();
        if (hasActIcons || hasMoodIcons) {
            statusList.addElement(new SimpleString(SR.get(SR.MS_PEP),true));
            if (hasMoodIcons) {
                statusList.addElement(new ExtendedStatus(0x13, "pep", SR.get(SR.MS_USERMOOD), true));
            }
            if (hasActIcons) {
                statusList.addElement(new ExtendedStatus(0x24, "pep", SR.get(SR.MS_USERACTIVITY), false));
            }
        }
//#endif
    }
    
    private void createFromStream(int index, String name, DataInputStream stream) {
	ExtendedStatus status=new ExtendedStatus(index, name, SR.getPresence(name));
        if (stream != null) {
            try {
                int priority=stream.readInt();
                status.setPriority((priority>128)?128:priority);
                status.setMessage(stream.readUTF());
                status.setAutoRespond(stream.readBoolean());
                status.setAutoRespondMessage(stream.readUTF());
            } catch (IOException e) { 
                //e.printStackTrace();
            }
        }
	statusList.addElement(status);
    }
    
    public void saveStatusToStorage(){
        try {
            DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
            
            for (int i=0;i<STATUS_COUNT;i++) {
                ExtendedStatus e=(ExtendedStatus)statusList.elementAt(i);
                outputStream.writeInt(e.getPriority());
                outputStream.writeUTF(e.getMessage());
                outputStream.writeBoolean(e.getAutoRespond());
                outputStream.writeUTF(e.getAutoRespondMessage());
            }
            NvStorage.writeFileRecord(outputStream, "status", 0, true);            
        } catch (Exception e) { 
            //e.printStackTrace(); 
        }
    }
    
    public ExtendedStatus getStatus(final int index) {
        int size = statusList.size();
        ExtendedStatus status;
        for (int i = 0; i < size; ++i) {
            status = (ExtendedStatus)statusList.elementAt(i);
            if (index == status.getImageIndex()) {
                return status;
            }
        }                
        return null;
    }
}
