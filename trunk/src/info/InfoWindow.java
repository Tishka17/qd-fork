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
import ui.controls.AlertBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;

/**
 *
 * @author ad
 */

public class InfoWindow extends DefForm {
    private int auth = 0;

    public InfoWindow() {
        super(SR.get(SR.MS_ABOUT));

        MultiLine item;

        item = new MultiLine(Version.NAME, Version.getVersionNumber()
                + "\n" + Config.platformName
                + "\nMobile Jabber client", getWidth());

        item.setSelectable(true);
        addControl(item);

        addControl(new SpacerItem(6));

        addControl(new LinkString("QD ".concat(SR.get(SR.MS_QD_NEWS))) {
            public void doAction() {
                GetFileServer form = new GetFileServer();
                form.setParentView(BombusQD.sd.roster);
                form.show();
            }
        });

        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            addControl( new LinkString(SR.get(SR.MS_SUPPORT)) {
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

//#ifdef TOUCH
        if (Config.isTouchPhone) {
            item = new MultiLine("Easter Egg", "Press link under this text", getWidth());
            item.setSelectable(true);
            addControl(item);

            addControl(new LinkString("Yes,give me egg") {

                public void doAction() {
                    showMsg();
                }

            });
        } else
//#endif
        {
            item = new MultiLine("Easter Egg:", "Press 5-1-2 keys to lock/unlock new options", getWidth());
            item.setSelectable(true);
            addControl(item);
        }

        addControl(new SpacerItem(6));

        item = new MultiLine("Copyright (c) 2005-2011",
                "Eugene Stahov (evgs,Bombus);\nDaniel Apatin (ad,BombusMod);\nAlexej Kotov(aqent,BombusQD);\n"
                + "Andrey Tikhonov(Tishka17,BombusQD)\n"
                + "Distributed under GPL v2 License", getWidth());
        item.setSelectable(true);
        addControl(item);

        item = new MultiLine("Thanks to:", "Testing: zaetz,balor,demon(Dmitry Krylov),magnit,Sniffy,NNn,DsXack and many others\n"
                + "Patches: Vladimir Krukov (aspro),vinz@\n"
                + "Graphics: Xa,Makasim\n"
                + "Actions icons: Rederick Asher\n"
                + "Site managment: BiLLy\n"
                + "Jimm Dev's for back.png ;)\n"
                + "Windows Fonts: magdelphi(mobilab.ru)"
                + "\nMathFP library"
                + "\nSmiles Author: Copyright (c) Aiwan. Kolobok smiles", getWidth());
        item.setSelectable(true);
        addControl(item);

        addControl(new LinkString("http://www.kolobok.us") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://www.kolobok.us");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });

        addControl(new LinkString("http://bombusmod-qd.wen.ru") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://bombusmod-qd.wen.ru");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });

        addControl(new LinkString("http://bombusmod.net.ru") {
            public void doAction() {
                try {
                    BombusQD.getInstance().platformRequest("http://bombusmod.net.ru");
                } catch (ConnectionNotFoundException ex) {
                }
            }
        });

        addControl(new SpacerItem(6));

        System.gc();
        long freemem = Runtime.getRuntime().freeMemory() >> 10;
        long totalmem = Runtime.getRuntime().totalMemory() >> 10;

        StringBuffer memInfo = new StringBuffer();

        memInfo.append(SR.get(SR.MS_FREE)).append(freemem).append("\n");
        memInfo.append(SR.get(SR.MS_TOTAL)).append(totalmem);

        item = new MultiLine(SR.get(SR.MS_MEMORY), memInfo.toString(), getWidth());
        item.setSelectable(true);
        addControl(item);

        enableListWrapping(false);
    }

    public void cmdOk() {
        destroyView();
    }

    private void showMsg() {
        String authMsg;
        if (midlet.BombusQD.cf.userAppLevel == 0) {
            midlet.BombusQD.cf.userAppLevel = 1;
            authMsg = "Advanced Mode now ON";
        } else {
            midlet.BombusQD.cf.userAppLevel = 0;
            authMsg = "Advanced Mode now OFF!";
        }
        midlet.BombusQD.cf.saveInt();
        AlertBox box = new AlertBox(SR.get(SR.MS_INFO), authMsg, AlertBox.BUTTONS_OK, 10);
        box.setParentView(getParentView());
        box.show();
    }

    public void keyPressed(int keyCode) {
        switch (auth) {
            /*1*/ case 0:
                if (keyCode == KEY_NUM5) {
                    auth++;
                }
                break;
            /*2*/ case 1:
                if (keyCode == KEY_NUM1) {
                    auth++;
                } else {
                    auth = 0;
                }
                break;
            /*3*/ case 2:
                if (keyCode == KEY_NUM2) {
                    auth++;
                } else {
                    auth = 0;
                }
                break;
        }
        if (auth == 3) {
            showMsg();
            auth = 0;
        }
        super.keyPressed(keyCode);
    }
}
