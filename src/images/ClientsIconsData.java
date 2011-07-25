/*
 *
 * Created on 29.08.2008, 0:20
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */

//#ifdef CLIENTS_ICONS
package images;

import client.Contact;
import java.util.Vector;
import util.StringLoader;

/**
 *
 * @author ad,aqent
 */

public class ClientsIconsData {
    private static String[] names;
    private static String[] nodes;

    public static void initClients() {
        Vector clients[] = new StringLoader().stringLoader("/images/clients.txt", 2);
        if (clients != null) {
            int size = clients[0].size();
            names = new String[size];
            nodes = new String[size];

            for (int i = 0; i < size; ++i) {
                 String clientNode = (String)clients[0].elementAt(i);
                 String clientName = (String)clients[1].elementAt(i);

                names[i] = clientName;
                nodes[i] = clientNode;
            }
        } else {
            names = new String[0];
            nodes = new String[0];
        }
    }

    // from bm2
    private static int getClientIDByCaps(String caps) {
        if (nodes.length == 0) {
            return -1;
        }
        String lcaps = caps.toLowerCase();
        for (int i = 0; i < nodes.length; i++) {
            String client = nodes[i].toLowerCase();
            if (client.indexOf(',') > -1) {
                boolean parse = true;
                int pos = 0;
                while (parse) {
                    if (pos > -1) {
                        int endpos = client.indexOf(',', pos);
                        String eqStr = (endpos < 0) ? client.substring(pos) : client.substring(pos, endpos);
                        if (lcaps.indexOf(eqStr) > -1) {
                            return i;
                        }

                        pos = client.indexOf(",", pos + 1);
                        if (pos < 0) {
                            parse = false;
                        } else {
                            pos = pos + 1;
                        }
                    } else {
                        parse = false;
                    }
                }
            } else {
                if (lcaps.indexOf(client) > -1) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String getClientNameByID(int id) {
        return names[id];
    }

    public static void processData(Contact c, String data) {
        c.client = getClientIDByCaps(data);
        if (c.client > -1) {
            c.clientName = getClientNameByID(c.client);
        } else {
            c.clientName = "";
        }
    }
}
//#endif
