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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusQD;
import ui.IconTextElement;
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
        new AlertBox(SR.get(SR.MS_INFO), authMsg, display, parentView, false) {
            public void yes() {
            }

            public void no() {
            }

        };
    }

    int auth = 0;

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

    public InfoWindow(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_ABOUT));

        IconTextElement item;

        item = new MultiLine(Version.getName(), Version.getVersionNumber()
                + "\n" + Config.platformName
                + "\nMobile Jabber client", super.superWidth);

        ((MultiLine)item).setSelectable(true);
        addControl(item);

        final InfoWindow window = this;
        item = new LinkString("QD ".concat(SR.get(SR.MS_QD_NEWS))) {
            public void doAction() {
                new GetFileServer(midlet.BombusQD.getInstance().display, window);
            }

        };
        addControl(item);

        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            addControl(
                    new LinkString(SR.get(SR.MS_SUPPORT)) {

                        public void doAction() {
                            new ConferenceForm(
                                    midlet.BombusQD.getInstance().display,
                                    midlet.BombusQD.sd.roster,
                                    "BombusQD@",
                                    "qd@conference.jabber.ru", null, false);
                        }

                    });
        }

//#ifdef STATS
//#         addControl(new LinkString(SR.get(SR.MS_STATS)) {
//#             public void doAction() {
//#                 new stats.StatsWindow(midlet.BombusQD.getInstance().display);
//#             }
//# 
//#         });
//#endif

        /*
        addControl(new LinkString("Create NullPointer") {
        public void doAction() {
        //try{
        String text = null;
        int pos = text.indexOf("text");
        //} catch (Exception e) { e.printStackTrace(); }
        }}
        );
        addControl(new LinkString("Create ArrayIndexOutOfBounds") {
        public void doAction() {
        //try{
        int[] arr = new int[2];
        for(int i = 0; i <= 3; ++i) arr[i] = i;
        //} catch (Exception e) { e.printStackTrace(); }
        }}
        );
        addControl(new LinkString("Create OutOfMemory") {
        public void doAction() {
        //try{
        Vector array = new Vector();
        for (int i = 0; i < 30; ++i) {
        for (int k = 0; k < 20; ++k) {
        for (int m = 0; m < 10; ++m) {
        Object add = array.toString();
        array.addElement(add);
        }
        }
        }
        //} catch (Exception e) { e.printStackTrace(); }
        }}
        );
         */

        if (midlet.BombusQD.cf.isTouchPhone) {
            item = new MultiLine("Easter Egg", "Press link under this text", super.superWidth);
            ((MultiLine)item).setSelectable(true);
            addControl(item);

            addControl(new LinkString("Yes,give me egg") {

                public void doAction() {
                    showMsg();
                }

            });
            addControl(new SpacerItem(5));
        } else {
            item = new MultiLine("Easter Egg:", "Press 5-1-2 keys to lock/unlock new options", super.superWidth);
            ((MultiLine)item).setSelectable(true);
            addControl(item);
        }

        item = new MultiLine("Copyright (c) 2005-2010",
                "Eugene Stahov (evgs,Bombus);\nDaniel Apatin (ad,BombusMod);\nAlexej Kotov(aqent,BombusQD);\n"
                + "Andrey Tikhonov(Tishka17,BombusQD)\n"
                + "Distributed under GPL v2 License", super.superWidth);
        ((MultiLine)item).setSelectable(true);
        addControl(item);

        item = new MultiLine("Thanks to:", "Testing: zaetz,balor,demon(Dmitry Krylov),magnit,Sniffy,NNn,DsXack and many others\n"
                + "Patches: Vladimir Krukov (aspro),vinz@\n"
                + "Graphics: Xa,Makasim\n"
                + "Actions icons: Rederick Asher\n"
                + "Site managment: BiLLy\n"
                + "Jimm Dev's for back.png ;)\n"
                + "Windows Fonts: magdelphi(mobilab.ru)"
                + "\nMathFP library"
                + "\nSmiles Author: Copyright (c) Aiwan. Kolobok smiles", super.superWidth);
        ((MultiLine)item).setSelectable(true);
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

        addControl(new SpacerItem(10));

        System.gc();
        long freemem = Runtime.getRuntime().freeMemory() >> 10;
        long totalmem = Runtime.getRuntime().totalMemory() >> 10;

        StringBuffer memInfo = new StringBuffer();

        memInfo.append(SR.get(SR.MS_FREE)).append(freemem).append("\n");
        memInfo.append(SR.get(SR.MS_TOTAL)).append(totalmem);

        item = new MultiLine(SR.get(SR.MS_MEMORY), memInfo.toString(), super.superWidth);
        ((MultiLine)item).setSelectable(true);
        addControl(item);

        enableListWrapping(false);

        attachDisplay(display);
        this.parentView = pView;
    }
}
