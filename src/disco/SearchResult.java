/*
 * SearchResult.java
 *
 * Created on 10.07.2005, 21:40
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
//#ifdef SERVICE_DISCOVERY
package disco;

import client.Contact;
import client.ContactEdit;
import client.Groups;
import client.Msg;
import client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.controls.form.DefForm;

/**
 *
 * @author EvgS,aqent
 */
public class SearchResult extends DefForm {
    StaticData sd=StaticData.getInstance();

    boolean xData;

    public SearchResult(JabberDataBlock result) {
        super("");

        String service=result.getAttribute("from");

        setMainBarItem(new MainBar(2, null, service, false));

        JabberDataBlock query=result.getChildBlock("query");
        if (query==null){
            return;
        }

        JabberDataBlock x=query.getChildBlock("x");
        if (x!=null) { query=x; xData=true; }

        sd.roster.cleanupSearch();

        StringBuffer vcard;
        Contact serv;
        Msg m;
        Vector queryElements = query.getChildBlocks();
        Vector childBlocks = new Vector(0);

        int size = queryElements.size();
        for(int i = 0; i<size; ++i){
            JabberDataBlock child = (JabberDataBlock) queryElements.elementAt(i);

            if (child.getTagName().equals("item")) {
                vcard = new StringBuffer(0);
                String jid="";

	        byte status=Presence.PRESENCE_OFFLINE;
                if (!xData) { jid=child.getAttribute("jid"); }

                childBlocks = child.getChildBlocks();
                int chSize = childBlocks.size();
                for(int k = 0; k<chSize; ++k){
                    JabberDataBlock field=(JabberDataBlock) childBlocks.elementAt(k);
                    String name;
                    String value;
                    if (xData) {
                        name=field.getAttribute("var");
                        value=field.getChildBlockText("value");
                    } else {
                        name=field.getTagName();
                        value=field.getText();
                    }
                    if (name.equals("jid")) jid=value;
                    if (value.length()>0)
                    {
                        //vcard.append(new StringItem(name,value+"\n"));
                        vcard.append(name)
                             .append((char)0xa0)
                             .append(value)
                             .append('\n');
                    }
		    // status returned by jit
		    if (name.equals("status")) if (!value.equals("offline")) status=Presence.PRESENCE_ONLINE;
                }
                serv = new Contact(null, jid, status,"search");
                serv.setGroup(sd.roster.contactList.groups.getGroup(Groups.TYPE_SEARCH_RESULT));
                m=new Msg(Msg.PRESENCE, jid, "Short info", vcard.toString());
                m.read();
                m.expand();
                serv.addMessage(m);

                addControl(serv);
                sd.roster.addContact(serv);
            }
        }
        vcard = new StringBuffer(0);
        vcard = null;

        queryElements = new Vector(0);
        childBlocks = new Vector(0);
        sd.roster.reEnumRoster();
     }

    public String touchLeftCommand() {
        return SR.get(SR.MS_ADD);
    }

    public void touchLeftPressed() {
        showContactEditForm();
    }

    private void showContactEditForm() {
        ContactEdit form = new ContactEdit((Contact)getFocusedObject());
        form.setParentView(sd.roster);
        form.show();
    }

    public void eventOk(){
        try {
            Contact c=(Contact)getFocusedObject();
            if (c==null) return;
            c.getMessageList().show();
        } catch (Exception e) {}
    }
}
//#endif
