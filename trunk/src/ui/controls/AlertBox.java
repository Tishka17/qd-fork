/*
 * AlertBox.java
 *
 * Created on 17.05.2008, 14:35
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

package ui.controls;

import ui.controls.form.DefForm;
import locale.SR;
import ui.controls.form.MultiLine;
import ui.controls.form.ProgressItem;
import ui.controls.form.SpacerItem;
/**
 *
 * @author tishka17
 */

public class AlertBox extends DefForm implements Runnable {
    public static final int BUTTONS_OK=0x0001;
    public static final int BUTTONS_YESNO=0x0002;
    private int buttons;
    private int timeout = 0;
    private int maxtimeout = 0;
    private ProgressItem progress = null;

    public AlertBox(String mainbar, String text, final int buttons) {
	this(mainbar, text, buttons, 0);
    }
    public AlertBox(String mainbar, String text, final int buttons, final int timeout) {
	super(mainbar);
	this.buttons = buttons;
	MultiLine line  = new MultiLine(null, text, width);
        line.setSelectable(true);
        addControl(line);
	if (timeout>0) {
	    SpacerItem sp = new SpacerItem(5);
	    addControl(sp);
	    this.timeout = timeout;
	    maxtimeout = timeout;
	    progress = new ProgressItem();
	    addControl(progress);
	}
    }

    public void show() {
        if (timeout > 0) {
            new Thread(this).start();
        }
        super.show();
    }

    public String touchLeftCommand() {
	if (buttons==BUTTONS_OK)
	    return SR.get(SR.MS_OK);
	else
	    return SR.get(SR.MS_YES);
    }
    public String touchRightCommand() {
	if (buttons==BUTTONS_OK)
	    return "";
	else
	    return SR.get(SR.MS_NO);
    }
    
    public void cmdOk() {destroyView(); stop(); yes(); }
    public void yes(){};
    public void cmdCancel() {destroyView(); stop(); no();}
    public void no(){};
    public void run() {
        while (timeout>0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { break; }
            --timeout;
            progress.setProgress((100*(maxtimeout - timeout)) /maxtimeout);
            redraw();
        }
        if (timeout==0) cmdOk();
    }
    public void stop() {
	timeout = -1;
    }
}

