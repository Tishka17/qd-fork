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

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberListener;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.SplashScreen;
import xmpp.XmppError;

public class AccountRegister implements JabberListener, Runnable {
    private static final long DELAY = 1000L;

    private final Displayable accountselect;
    private final Account raccount;
    private JabberStream theStream;
    private final SplashScreen splash;

    public AccountRegister(Account account, Displayable accountselect) {
        this.accountselect = accountselect;
        raccount = account;

        splash = new SplashScreen();
        splash.setProgress(SR.get(SR.MS_STARTUP), 5);
        splash.show();

        new Thread(this).start();
    }

    public void run() {
        try {
            splash.setProgress(SR.get(SR.MS_CONNECT_TO_) + raccount.getServer(), 30);
            Thread.sleep(500L);
            theStream = raccount.openJabberStream();
            theStream.setJabberListener(this);
            theStream.initiateStream();
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
            splash.setFailed();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException ie) {}
            splash.destroyView();
        }
    }

    public void connectionTerminated(Exception e) {
//#ifdef DEBUG
//#         if (e != null) {
//#             e.printStackTrace();
//#         }
//#endif
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
            String type = data.getTypeAttribute();
            String mainbar;
            if ("result".equals(type)) {
                mainbar = SR.get(SR.MS_DONE);
                splash.setParentView(accountselect);
            } else {
                mainbar = SR.get(SR.MS_ERROR_) + XmppError.findInStanza(data).toString();
            }
            theStream.close();

            splash.setProgress(mainbar, 100);
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException ie) {}
            splash.destroyView();
        }
        return JabberBlockListener.BLOCK_PROCESSED;
    }
}
