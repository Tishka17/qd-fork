/*
 * ServerBox.java
 *
 * Created on 2.06.2008, 22:43
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
//#ifdef SERVICE_DISCOVERY
package disco;

import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.TextInput;

/**
 *
 * @author ad
 */

public class ServerBox  extends DefForm {  
    private static final String SERVERADD_RECENT_ID = "server-add";

    private TextInput serverName;
    private ServiceDiscovery sd;

    public ServerBox(String service, ServiceDiscovery sd) {
        super(SR.get(SR.MS_DISCO));

        this.sd = sd;

        serverName = new TextInput(SR.get(SR.MS_SERVER), service, SERVERADD_RECENT_ID, TextField.ANY);
        addControl(serverName);
    }

    public void cmdOk() {
        destroyView();
        
        String server = serverName.getValue();
        if (server.length() > 0) {
            sd.addServer(server);
            sd.browse(server, null);
        }
    }
}
//#endif