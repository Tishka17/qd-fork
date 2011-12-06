/*
 * InfoWindow.java
 *
 * Created on 25.05.2008, 19:29
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

package info;

import client.Config;
import conference.ConferenceForm;
import javax.microedition.io.ConnectionNotFoundException;
import locale.SR;
import midlet.BombusQD;
//#ifdef STATS
import stats.StatsWindow;
//#endif
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
/**
 *
 * @author ad
 */

public class InfoWindow extends DefForm {

    public InfoWindow() {
        super(SR.get(SR.MS_ABOUT));
        addControl(new MultiLine(Version.NAME, Version.getVersionNumber()
                + "\n" + Config.platformName
                + "\nMobile Jabber client"));
        addControl(new SpacerItem(6));

        addControl(new LinkString("QD ".concat(SR.get(SR.MS_QD_NEWS))) {
            public void doAction() {
                GetFileServer form = new GetFileServer();
                form.setParentView(BombusQD.sd.roster);
                form.show();
            }
        });

        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            addControl(new LinkString(SR.get(SR.MS_SUPPORT)) {
                public void doAction() {
                    new ConferenceForm("BombusQD@", "qd@conference.jabber.ru", null, false).show();
                }
            });
        }

//#ifdef STATS
        addControl(new LinkString(SR.get(SR.MS_STATS)) {
            public void doAction() {
                StatsWindow form = new StatsWindow();
                form.setParentView(BombusQD.sd.roster);
                form.show();
            }
        });
//#endif
        
        addControl(new SpacerItem(6));

        //TODO: real names
        addControl(new MultiLine("Copyright (c) 2005-2011","Distributed under GPL v2 License"));
        addControl(new MultiLine("BombusQD",   
                "Alexej Kotov(aqent),\n" +
                "Andrey Tikhonov (Tishka17)\n" + 
                "Alexey Esprit (-Esprit-)"));
        addControl(new LinkString("http://bombusqd.hdd1.ru") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://bombusqd.hdd1.ru/m/");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });
        addControl(new MultiLine("BombusMod", 
                "Daniel Apatin (ad)"));
        addControl(new LinkString("http://bombusmod.net.ru") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://bombusmod.net.ru");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });
        addControl(new MultiLine("Bombus",  
                  "Eugene Stahov (evgs)"));
        addControl(new LinkString("http://bombus-im.org") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://bombus-im.org");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });
        addControl(new SpacerItem(6));
        addControl(new SimpleString("Special thanks", true));
        addControl(new MultiLine("Patches:", "Vladimir Krukov (aspro), vinz@, radiance, Grebnev Yaroslav (modi), Марс, Ivan Kudryakov (Ivansuper)"));
        addControl(new MultiLine("Graphics:", "Xa, Makasi, fin, -dp-"));
        addControl(new MultiLine("Site managment:", "Makasi, BiLLy"));
        addControl(new MultiLine("Android Market managment:", "Kirill Ashikhmin (BOOMik)"));
        addControl(new MultiLine("Actions icons:", "Rederick Asher"));
        addControl(new MultiLine("Themes:", "Lesli, Я2R and others"));
        addControl(new MultiLine("Localization:", "balor (Українська), Samer top (العربية), czater (Polski)"));
        addControl(new MultiLine("Testing: ", "zaetz, balor, demon(Dmitry Krylov), magnit, Sniffy, NNn, DsXack, Gho$t, Artem, Tiesto and many others"));
        addControl(new MultiLine("Smiles:", "Copyright (c) Aiwan. Kolobok smiles"));
        addControl(new LinkString("http://www.kolobok.us") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://www.kolobok.us");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });


        addControl(new SpacerItem(6));

        System.gc();
        long freemem = Runtime.getRuntime().freeMemory() >> 10;
        long totalmem = Runtime.getRuntime().totalMemory() >> 10;

        StringBuffer memInfo = new StringBuffer();

        memInfo.append(SR.get(SR.MS_FREE)).append(freemem).append('\n');
        memInfo.append(SR.get(SR.MS_TOTAL)).append(totalmem);

        addControl(new MultiLine(SR.get(SR.MS_MEMORY), memInfo.toString()));

        enableListWrapping(false);
    }

    public void cmdOk() {
        destroyView();
    }

}
