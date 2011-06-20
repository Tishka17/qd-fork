/*
 * PrivacyModifyList.java
 *
 * Created on 11.09.2005, 15:51
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
//#ifdef PRIVACY
package privacy;

import client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import images.RosterIcons;
import java.util.Vector;
import locale.SR;
import menu.Command;
import menu.MenuListener;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author EvgS,aqent
 */

public class PrivacyModifyList extends VirtualList  implements  MenuListener, JabberBlockListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif

    private PrivacyList plist;

    private Command cmdAdd;
    private Command cmdDel;
    private Command cmdEdit;
    private Command cmdUp;
    private Command cmdDwn;
    private Command cmdSave;

    JabberStream stream=StaticData.getInstance().roster.theStream;

    public PrivacyModifyList(PrivacyList privacyList) {
        super();

        cmdAdd=new Command (SR.get(SR.MS_ADD_RULE), 0x47);
        cmdDel=new Command (SR.get(SR.MS_DELETE_RULE), 0x41);
        cmdEdit=new Command (SR.get(SR.MS_EDIT_RULE), 0x40);
        cmdUp=new Command (SR.get(SR.MS_MOVE_UP), 0x45);
        cmdDwn=new Command (SR.get(SR.MS_MOVE_DOWN), 0x46);
        cmdSave=new Command (SR.get(SR.MS_SAVE_LIST), 0x44);

        setMainBarItem(new MainBar(2, null, SR.get(SR.MS_PRIVACY_LISTS), false));

        plist=privacyList;
        getList();
    }

    public void commandState() {
        menuCommands.removeAllElements();

        addCommand(cmdEdit);
        addCommand(cmdAdd);
        addCommand(cmdDel);
        addCommand(cmdUp);
        addCommand(cmdDwn);
        addCommand(cmdSave);
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.PRIVACY_MODIFY_LIST;
        return GMenu.PRIVACY_MODIFY_LIST;
    }

    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }

    private void getList(){
        processIcon(true);
        stream.addBlockListener(this);
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", plist.getName());
        PrivacyList.privacyListRq(false, list, "getlistitems");
    }

    protected int getItemCount() { return plist.rules.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) plist.rules.elementAt(index); }

    public void commandAction(Command c) {
        if (c==cmdAdd) {
            new PrivacyForm(new PrivacyItem(), plist).show();
        }
        if (c==cmdEdit) {
            eventOk();
        }
        if (c==cmdDel) {
            Object del=getFocusedObject();
            if (del!=null) plist.rules.removeElement(del);
        }
        if (c==cmdSave) {
            plist.generateList();
            PrivacyList.privacyListRq(false, null, "setplists");

            destroyView();
        }

        if (c==cmdUp) { move(-1); keyUp(); }
        if (c==cmdDwn) { move(+1); keyDwn(); }
        redraw();
    }

    public void move(int offset){
        try {
            int index=cursor;
            PrivacyItem p1=(PrivacyItem)plist.rules.elementAt(index);
            PrivacyItem p2=(PrivacyItem)plist.rules.elementAt(index+offset);

            plist.rules.setElementAt(p1, index+offset);
            plist.rules.setElementAt(p2, index);

            int tmpOrder=p1.order;
            p1.order=p2.order;
            p2.order=tmpOrder;

        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void eventOk(){
        PrivacyItem pitem=(PrivacyItem) getFocusedObject();
        if (pitem!=null) {
            new PrivacyForm(pitem, null).show();
        }
    }
    
    public void destroy() {}

    public int blockArrived(JabberDataBlock data){
        if (data.getTypeAttribute().equals("result")){
            if (data.getAttribute("id").equals("getlistitems")) {
                data=data.findNamespace("query", "jabber:iq:privacy");
                try {
                    data=data.getChildBlock("list");
                    plist.rules=null;
                    plist.rules=new Vector(0);
                    int size = data.getChildBlocks().size();
                    for(int i=0;i<size;i++){
                        JabberDataBlock item = (JabberDataBlock)data.getChildBlocks().elementAt(i);
                        plist.addRule(new PrivacyItem(item));
                    }
                } catch (Exception e) {}

                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            } //id, result
        } else processIcon(false);
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void destroyView() {
        stream.cancelBlockListener(this);
        super.destroyView();
    }
}
//#endif
