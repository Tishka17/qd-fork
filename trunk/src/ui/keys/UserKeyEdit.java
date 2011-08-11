/*
 * UserKeyEdit.java 
 *
 * Created on 14.09.2007, 11:01
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
//#ifdef USER_KEYS
package ui.keys;

import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.KeyScanner;

/**
 *
 * @author ad
 */

public class UserKeyEdit extends DefForm {
    private final UserKeysList keysList;

    private CheckBox multiKey;
    private DropChoiceBox keyDesc;
    private KeyScanner keyCode;
    private KeyScanner secCode;

    UserKey u;
    
    boolean newKey;

    public UserKeyEdit(UserKeysList keysList, UserKey u) {

        super((u==null)?SR.get(SR.MS_ADD_CUSTOM_KEY):(u.toString()));
        
	this.keysList = keysList;
        UserKeyExec.stopExecute( );
	
	newKey=(u==null);
	if (newKey) u=new UserKey();
	this.u=u;
     
        keyDesc=new DropChoiceBox(SR.get(SR.MS_KEYS_ACTION));
        for (int i=0;i<u.COMMANDS_DESC.length;i++) {
            keyDesc.append(u.COMMANDS_DESC[i]);
        }
        keyDesc.setSelectedIndex(u.commandId);

        multiKey= new CheckBox("is multikey", u.mKey);
        keyCode= new KeyScanner(SR.get(SR.MS_KEY), u.keyCode, u.keyLong);
        secCode= new KeyScanner("second key", u.secCode, u.secLong);

        itemsList.addElement(keyDesc);
        itemsList.addElement(multiKey);
        itemsList.addElement(keyCode);

        moveCursorTo(getNextSelectableRef(-1));
    }
    
    public void cmdOk() {
        u.mKey = multiKey.getValue();
        u.commandId = keyDesc.getSelectedIndex();
        u.keyCode = keyCode.getKeyCode();
        u.keyLong = keyCode.isLong;
        u.secCode = secCode.getKeyCode();
        u.secLong = secCode.isLong;

        if (newKey) {
            keysList.commandsList.addElement(u);
        }

        keysList.rmsUpdate();
        keysList.commandState();
        UserKeyExec.startExecute( );
        destroyView();
    }

    protected void beginPaint(){
        update();
    }

    private void update(){
        itemsList.removeElement(secCode);
        if( multiKey.getValue())
            itemsList.addElement(secCode);
    }
}
//#endif