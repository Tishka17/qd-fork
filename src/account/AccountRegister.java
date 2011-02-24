/*
 * AccountRegister.java
 *
 * Created on 24.04.2005, 2:36
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */
package account;

import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;
import xmpp.XmppError;

/**
 *
 * @author Evg_S,aqent
 */
public class AccountRegister
        implements
        JabberListener,
        CommandListener,
        Runnable {
    private Display display;
    private Displayable parentView;
    private Account raccount;
    private JabberStream theStream;
    private SplashScreen splash;
    private Command cmdOK;
    private Command cmdCancel;

    /** Creates a new instance of AccountRegister */
    public AccountRegister(Account account, Display display, Displayable pView) {
        this.display = display;
        this.parentView = pView;
        raccount = account;

        cmdOK = new Command(SR.get(SR.MS_OK), Command.OK, 1);
        cmdCancel = new Command(SR.get(SR.MS_BACK), Command.BACK, 2);

        splash = midlet.BombusQD.getInstance().s;
        splash.setProgress(SR.get(SR.MS_STARTUP), 5);
        display.setCurrent(splash);
        splash.addCommand(cmdCancel);
        splash.setCommandListener(this);
        new Thread(this).start();
    }

    public void run() {
        try {
            splash.setProgress(SR.get(SR.MS_CONNECT_TO_) + raccount.getServer(), 30);
            Thread.sleep(500);
            theStream = raccount.openJabberStream();
            theStream.setJabberListener(this);
            theStream.initiateStream();
        } catch (Exception e) {
            e.printStackTrace();
            splash.setFailed();
        }

    }

    public void rosterItemNotify() {
    }

    public void connectionTerminated(Exception e) {
        if (e != null) {
            e.printStackTrace();
        }
    }

    public void beginConversation() {
        splash.setProgress(SR.get(SR.MS_REGISTERING), 60);
        Iq iqreg = new Iq(null, Iq.TYPE_SET, "regac");
        JabberDataBlock qB = iqreg.addChildNs("query", "jabber:iq:register");
        qB.addChild("username", raccount.getUserName());
        qB.addChild("password", raccount.getPassword());
        qB.addChild("email", raccount.getEmail());
        theStream.send(iqreg);
        iqreg = null;
        qB = null;
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            int pgs = 100;
            String type = data.getTypeAttribute();
            String mainbar = SR.get(SR.MS_DONE);
            if (type.equals("result")) {
                splash.removeCommand(cmdCancel);
            } else {
                splash.removeCommand(cmdCancel);
                mainbar = SR.get(SR.MS_ERROR_) + XmppError.findInStanza(data).toString();
            }
            splash.setProgress(mainbar, pgs);
            splash.setExit(display, parentView);

            theStream.close();
        }
        return JabberBlockListener.BLOCK_PROCESSED;
    }

    public void commandAction(Command c, Displayable d) {
        splash.setCommandListener(null);
        splash.removeCommand(cmdCancel);
        try {
            theStream.close();
        } catch (Exception e) {
        }
        destroyView();
    }

    public void destroyView() {
        if (display != null) {
            display.setCurrent(parentView);
        }
    }
}
