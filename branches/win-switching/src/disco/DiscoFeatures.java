/*
 * DiscoFeatures.java
 *
 * Created on 6.07.2006, 23:30
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
 *
 */
//#ifdef SERVICE_DISCOVERY 
package disco;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import locale.SR;
import midlet.BombusQD;

/**
 *
 * @author Eugene Stahov
 */

public class DiscoFeatures implements CommandListener {
    
    Command cmdBack;
    Command cmdOk;
    
    List list;

    private Display display;
    private Displayable parentView;
    
    /** Creates a new instance of DiscoFeatures */
    public DiscoFeatures(String entity, Vector features) {
        if (features.isEmpty()) return;
        
        cmdBack=new Command(SR.get(SR.MS_BACK), Command.BACK, 99);
        cmdOk=new Command(SR.get(SR.MS_OK), Command.OK, 1);        
        
        list=new List(entity, List.IMPLICIT);
        for (Enumeration i=features.elements(); i.hasMoreElements(); ) {
            String feature=(String) (i.nextElement());
            list.append(feature, null);
        }
        
        list.addCommand(cmdBack);
        list.addCommand(cmdOk);
        list.setCommandListener(this);

        parentView=BombusQD.getCurrentView();
        BombusQD.setCurrentView(list);
    }

    public void commandAction(Command command, Displayable displayable) {
        BombusQD.setCurrentView(parentView);
    }
}
//#endif