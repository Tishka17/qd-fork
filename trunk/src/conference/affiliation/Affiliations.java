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
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

/**
 *
 * @author EvgS
 */
public class Affiliations
        extends VirtualList
		implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
            MenuListener,
//#endif
            JabberBlockListener
{

    private Vector items;
    private String id="admin";
    private String namespace="http://jabber.org/protocol/muc#admin";
    private String room;

    private JabberStream stream=StaticData.getInstance().roster.theStream;

    private Command cmdCancel;
    private Command cmdModify;
    private Command cmdNew;
//#ifdef CLIPBOARD
//#     private Command cmdCopy;
//#endif

    protected VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index); }
    protected int getItemCount() { return items.size(); }


    /** Creates a new instance of AffiliationList */
    public Affiliations(Display display, Displayable pView, String room, short affiliationIndex) {
        super ();
        this.room=room;

	//fix for old muc
	switch (affiliationIndex) {
	    case AffiliationItem.AFFILIATION_OWNER:
	    case AffiliationItem.AFFILIATION_ADMIN:
		if (!Config.getInstance().muc119) namespace="http://jabber.org/protocol/muc#owner";
	}

        this.id=AffiliationItem.getAffiliationName(affiliationIndex);

        setMainBarItem(new MainBar(2, null, " ", false));
        getMainBarItem().addElement(id);

        items=null;
        items=new Vector(0);

        initCommands();

        setCommandListener(this);
        attachDisplay(display);
        this.parentView=pView;
        getList();
    }

    public void initCommands() {
        cmdCancel = new Command (SR.get(SR.MS_BACK), Command.BACK, 99);
        cmdModify = new Command (SR.get(SR.MS_MODIFY), Command.SCREEN, 1);
        cmdNew    = new Command (SR.get(SR.MS_NEW_JID), Command.SCREEN, 2);
        //#ifdef CLIPBOARD
//#             cmdCopy   = new Command(SR.get(SR.MS_COPY), Command.SCREEN, 3);
        //#endif
    }

    public void commandState() {
        //#ifdef MENU_LISTENER
            menuCommands.removeAllElements();
            cmdfirstList.removeAllElements();
            cmdsecondList.removeAllElements();
            cmdThirdList.removeAllElements();
        //#endif
        //addCommand(cmdCancel);
        addCommand(cmdModify);
        cmdModify.setImg(0x03);
        addCommand(cmdNew);
        cmdNew.setImg(0x02);
        //#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addCommand(cmdCopy);
//#                 cmdCopy.setImg(0x23);
//#             }
        //#endif
    }

    public void getList() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        item.setAttribute("affiliation", id);
        listRq(false, item, id);
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdNew) new AffiliationModify(display, this, room, null, "none", "");
        if (c==cmdModify) eventOk();
//#ifdef CLIPBOARD
//#         if (c==cmdCopy) {
//#             try {
//#                 AffiliationItem item=(AffiliationItem)getFocusedObject();
//#                 if (item.jid!=null)
//#                     ClipBoard.setClipBoard(item.jid);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        if (c!=cmdCancel)
            return;

        destroyView();
    }

    public void destroyView(){
	super.destroyView();
	stream.cancelBlockListener(this);
    }

    public void eventOk(){
        try {
            AffiliationItem item=(AffiliationItem)getFocusedObject();
            new AffiliationModify(display, this, room, item.jid,
					AffiliationItem.getAffiliationName( (short)item.affiliation),
                                        (item.reason==null)? "":item.reason
                    );
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
            if (data.getAttribute("id").equals(id)) {
                JabberDataBlock query=data.findNamespace("query", namespace);
                Vector tempItems=new Vector(0);
                try {
                  int size=query.getChildBlocks().size();
                    for(int i=0;i<size;i++){
                        tempItems.addElement(new AffiliationItem((JabberDataBlock)query.getChildBlocks().elementAt(i)));
                    }
                } catch (Exception e) { /* no any items */}
                sort(tempItems);
                items=tempItems;
                tempItems=null;

                if (display!=null) redraw();

                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
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
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.AFFILIATIONS_EDIT;
//#         return GMenu.AFFILIATIONS_EDIT;
//#     }
//#endif
}
