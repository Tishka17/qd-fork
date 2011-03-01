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

    private static ClientsIconsData instance;
    private static String[] clientName = {
        "BombusMod",
        "Bombus",
        "Bombus-ng",
        "Psi",
        "Miranda",
        "Bombus+",
        "Google",
        "Tkabber",
        "Gajim",
        "QIP",
        "Pidgin",
        "Kopete",
        "Exodus",
        "Siemens Native Jabber Client",
        "BitlBee",
        "Coccinella",
        "mcabber",
        "iChatAgent",
        "Jabbim",
        "BombusMod (PL)",
        "Home",//?
        "Office",//?
        "JAJC",
        "Bombus-Avalon",
        "Sm@per",
        "mChat",
        "Gaim",
        "Ya.Online",
        "qutIM",
        "Vacuum",
        "BombusQD",
        "Ex",
        "Jimm",
        "Talkonaut",
        "Lampiro",
        "Nimbuzz",
        "movamessenger",
        "Psi+",
        "MailAgent",
        "Bayan",
        "Simak.ru",
        "JETI",
        "Trillian",
        "WokJab",
        "Adium"
        //"svn.xmpp.ru/repos/mrim",
        //"jit.mytlt.ru"
    };

    private static String[] sites = {
        "bombusmod.net.ru",
        "bombus-im.org",
        "bombus-im.org/ng",
        "psi-im.org",
        "miranda-im.org",
        "voffk.org.ru",
        "google.com/xmpp/client/caps",
        "tkabber.jabber.ru",
        "gajim.org",
        "qip.ru",
        "pidgin.im",
        "kopete.kde.org/jabber/caps",
        "exodus.jabberstudio.org",
        "Siemens Native Jabber Client",
        "bitlbee.org",
        "coccinella.sourceforge.net",
        "mcabber",
        "apple.com/ichat/caps",
        "dev.jabbim.cz/jabbim/caps",
        "bombus.pl",
        "Home",
        "Office",
        "jajc.jrudevels.org",
        "java.util.Random",
        "smaper",
        "mchat.mgslab.com",
        "gaim.sf.net",
        "online.yandex.ru",
        "qutim",
        "vacuum",
        "bombusmod-qd.wen.ru",
        "ex-im.name",
        "jimm.net.ru",
        "talkonaut.com",
        "Lampiro",
        "nimbuzz.com",
        "movamessenger",
        "psi-dev.googlecode.com",
        "agent.mail.ru",
        "barobin.com",
        "simak.ru",
        "jeti.sf.net",
        "trillian.cc",
        "wokjab.nedo.se",
        "adiumx.com"
        //"svn.xmpp.ru/repos/mrim",
        //"jit.mytlt.ru"
    };

    public static ClientsIconsData getInstance() {
	if (instance==null) instance=new ClientsIconsData();
	return instance;
    }

    private ClientsIconsData() { }

    private static byte getClientIDByCaps(String caps) {
        byte clientsSize = (byte)sites.length;
         //System.out.println("   client_www-> " + sites[i] );
         //System.out.println("   client_name-> " + clientName[i] );
        for (byte i=0; i<clientsSize; ++i) {
           if(-1 != caps.indexOf(sites[i])) return i;
        }
        return -1;
    }

    public static void processData(Contact c, String data) {
        //System.out.println("set-> " + c);
        c.client = getClientIDByCaps(data);
        c.clientName = (c.client>-1)?c.clientName=getClientNameByID(c.client):"";
        //System.out.println("set->OK. " + c + "->" + c.client + "/" + c.clientName );
    }

    private static String getClientNameByID(byte id) {
        return clientName[id];
    }

}
//#endif
