/*
 * DefForm.java
 *
 * Created on 21.05.2008, 9:40
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

package ui.controls.form;

import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import ui.GMenu;


/**
 *
 * @author ad,aqent
 */

public class DefForm extends VirtualList {
    public Vector itemsList = new Vector(0);

    public DefForm(String caption) {
        super();
        setMainBarItem(new MainBar(caption)); 
    }

    public void show() {
        enableListWrapping(true);
        super.show();
    }

    protected int getItemCount() {
        return itemsList.size();
    }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }

    public void touchLeftPressed() {
        cmdOk();
    }

    public void touchRightPressed() {
        cmdCancel();
    }

    public void destroyView() {
        itemsList = null;
        itemsList = new Vector(0);

        super.destroyView();
    }

    public final void addControl(Object obj) {
        if (obj instanceof String) {
                SimpleString string = new SimpleString((String)obj);
                string.setSelectable(true);
                addControl(string);
        } else itemsList.addElement(obj);
    }

    public Object getControl(int index) {
        return itemsList.elementAt(index);
    }

    public final void insertControl(Object obj, int pos) {
        itemsList.insertElementAt(obj, pos);
    }

    public final void removeControl(Object obj) {
        itemsList.removeElement(obj);
    }

    public void cmdCancel() {
        destroyView();
    }

    public void cmdOk() {}

    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM4:
                pageLeft();
                break;
            case KEY_NUM6:
                pageRight();
                break;
        }
    }

    public int showGraphicsMenu() {
        //System.out.println("1");
        return GMenu.DEF_FORM;
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_OK);
    }
}
