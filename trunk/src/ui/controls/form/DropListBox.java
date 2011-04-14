/*
 * DropListBox.java
 *
 * Created on 22 ��� 2008 �., 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ui.controls.form;

import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;

/**
 *
 * @author ad
 */

public class DropListBox extends DefForm {
    private Vector listItems;
    private DropChoiceBox cb;

    public DropListBox(Vector listItems, DropChoiceBox cb) {
        super(cb.getCaption());

        this.listItems = listItems;
        this.cb = cb;

        moveCursorTo(cb.getSelectedIndex());
    }

    public void eventOk() {
        if (listItems.size() > 0) {
            cb.setSelectedIndex(cursor);
        }

        destroyView();
    }

    public VirtualElement getItemRef(int index) {
        return new ListItem((String)listItems.elementAt(index));
    }

    public int getItemCount() {
        return listItems.size();
    }
}
