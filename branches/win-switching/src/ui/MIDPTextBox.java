/*
 * MIDPTextBox.java
 * 
 * Created on 26.03.2005, 20:56
 *
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
 */

package ui;

import javax.microedition.lcdui.*;
import locale.SR;
import midlet.BombusQD;

/**
 *
 * @author  Eugene Stahov
 * @version
 */

// TODO make Ok and Back swapping, add copy/paste commands

public final class MIDPTextBox implements CommandListener {
    private Displayable parentView;
    
    private Command cmdCancel;
    private Command cmdOK;
    
    private TextBox t;
    
    private TextBoxNotify tbn;

    public interface TextBoxNotify {
        void OkNotify(String text);
    }
   
    public MIDPTextBox(String mainbar, String text, int constraints,int maxLen) {        
        cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);
        cmdOK=new Command(SR.get(SR.MS_OK), Command.OK, 1);
        
        t = new TextBox(mainbar, text, maxLen, constraints);
        
        t.addCommand(cmdOK);
        t.addCommand(cmdCancel);      
    }

    public void setCommandListener(TextBoxNotify notify) {
        this.tbn = notify;
    }

    public void show() {
        t.setCommandListener(this);

        parentView = BombusQD.getCurrentView();
        BombusQD.setCurrentView(t);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == cmdOK && tbn != null) {
            tbn.OkNotify(t.getString());
        }
        destroyView();
    }

    public void destroyView(){
        tbn = null;
        BombusQD.setCurrentView(parentView);
    }
}
