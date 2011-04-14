/*
 * AffiliationList.java
 *
 * Created on 30.10.2005, 12:34
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

package conference.affiliation;

import client.*;
//#ifndef MENU_LISTENER
//#     import javax.microedition.lcdui.CommandListener;
//#     import javax.microedition.lcdui.Command;
//#else
    import menu.MenuListener;
    import menu.Command;
    import ui.GMenu;
    import ui.GMenuConfig;
//#endif
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import midlet.Commands;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif

/**
 *
 * @author EvgS
 */

public final class AffiliationList extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
            MenuListener,
//#endif
            JabberBlockListener
{

    private Vector items;
    private String affiliation;
    private String namespace="http://jabber.org/protocol/muc#admin";
    private String room;

    private JabberStream stream=StaticData.getInstance().roster.theStream;

    private Command cmdModify;
    private Command cmdNew;

    public AffiliationList(String room, int affiliationIndex) {
        super();
        this.room=room;

	//fix for old muc
	switch (affiliationIndex) {
	    case AffiliationItem.AFFILIATION_OWNER:
	    case AffiliationItem.AFFILIATION_ADMIN:
		if (!Config.getInstance().muc119) namespace="http://jabber.org/protocol/muc#owner";
	}

        this.affiliation = AffiliationItem.getNameByIndex(affiliationIndex);

        setMainBarItem(new MainBar(2, null, " ", false));
        getMainBarItem().addElement(affiliation);

        items=null;
        items=new Vector(0);

        cmdModify = new Command (SR.get(SR.MS_MODIFY), Command.SCREEN, 1);
        cmdModify.setImg(0x03);

        cmdNew    = new Command (SR.get(SR.MS_NEW_JID), Command.SCREEN, 2);
        cmdNew.setImg(0x02);

        setCommandListener(this);
        getList();
    }

    public void commandState() {
//#ifdef MENU_LISTENER
            menuCommands.removeAllElements();
            cmdfirstList.removeAllElements();
            cmdsecondList.removeAllElements();
            cmdThirdList.removeAllElements();
//#endif
        addCommand(cmdNew);
        addCommand(cmdModify);

//#ifdef CLIPBOARD
        if (getItemCount() != 0) {
            if (Config.useClipBoard) {
                addCommand(Commands.cmdCopy);
            }
        }
 //#endif
    }

    public final void getList() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        item.setAttribute("affiliation", affiliation);
        listRq(false, item, affiliation);
    }

    public void commandAction(Command c, Displayable d){
        if (c == cmdNew) {
            new AffiliationEditForm(room, null, affiliation, "").show();
        }
        if (c == cmdModify) {
            eventOk();
        }
//#ifdef CLIPBOARD
        if (c == Commands.cmdCopy) {
            AffiliationItem item = (AffiliationItem)getFocusedObject();
            if (item.getJid() != null) {
                ClipBoard.setClipBoard(item.getJid());
            }
        }
//#endif
    }

    public void destroyView(){
	stream.cancelBlockListener(this);
        super.destroyView();
    }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)items.elementAt(index);
    }

    protected int getItemCount() {
        return items.size();
    }

    public void eventOk(){
        try {
            AffiliationItem item=(AffiliationItem)getFocusedObject();
            new AffiliationEditForm(room, item.getJid(),
                                        AffiliationItem.getNameByIndex(item.getIndex()),
                                        (item.getReason() == null) ? "" : item.getReason()
                    ).show();
        } catch (Exception e) { }
    }



    private void processIcon(boolean processing){
        String count=(items==null)? null: String.valueOf(items.size());
        getMainBarItem().setElementAt((processing)?
            (Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):
            (Object)count, 0);
        redraw();
    }

    public int blockArrived(JabberDataBlock data) {
        try {
            if (data.getAttribute("id").equals(affiliation)) {
                JabberDataBlock query = data.findNamespace("query", namespace);
                if (query != null) {
                    Vector children = query.getChildBlocks();
                    Vector tempItems = new Vector(0);

                    if (children != null) {
                        for (int i = 0; i < children.size(); i++) {
                            JabberDataBlock block = (JabberDataBlock)children.elementAt(i);
                            String jid = block.getAttribute("jid");
                            if (jid != null) {
                                tempItems.addElement(new AffiliationItem(block));
                            }
                        }
                        sort(tempItems);
                        items = tempItems;
                    }
                }
                redraw();

                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void listRq(boolean set, JabberDataBlock child, String id) {
        JabberDataBlock request=new Iq(room, (set)? Iq.TYPE_SET: Iq.TYPE_GET, id);
        JabberDataBlock query=request.addChildNs("query", namespace);
        query.addChild(child);

        processIcon(true);
        stream.addBlockListener(this);
        stream.send(request);
    }

//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
        GMenuConfig.getInstance().itemGrMenu = GMenu.AFFILIATIONS_EDIT;
        return GMenu.AFFILIATIONS_EDIT;
    }
//#endif
}
