/*
 * ContactEdit.java
 *
 * Created on 26.05.2008, 10:04
 *
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

package client;
//#ifndef WMUC
import conference.MucContact;
//#endif
import vcard.*;
import javax.microedition.lcdui.*;
import java.util.*;
import locale.SR;
import ui.controls.form.LinkString;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;

/**
 *
 * @author Evg_S 
 */
public final class ContactEdit
        extends DefForm implements JabberBlockListener  {
    private Display display;

    private LinkString vCardReq;
    private TextInput tJid;
    private TextInput tNick;
    private TextInput tGroup;
    private DropChoiceBox tGrpList;
    private DropChoiceBox tTranspList;
    private CheckBox tAskSubscrCheckBox;

    int ngroups;

    int newGroupPos=0;

    boolean newContact=true;
    Config cf;
    
    private boolean newGroup;

    StaticData sd=StaticData.getInstance();
    
    public static ContactEdit instance;
    
    public static ContactEdit getInstance() {
        if (instance==null) instance=new ContactEdit();
        return instance;
    }    
    public ContactEdit() { } ;   

    public int blockArrived( JabberDataBlock data ) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String type=data.getTypeAttribute();
        String from=data.getAttribute("from");
        String id=(String) data.getAttribute("id");        
                if (id!=null) {
                     if (id.startsWith("getvc")) {
                        if (type.equals("get") || type.equals("set") || type.equals("error") ) return JabberBlockListener.BLOCK_REJECTED;
                        VCard vcard=new VCard(data);
                        String jid=id.substring(5);
                        Contact c=StaticData.getInstance().roster.getContact(jid, false); // drop unwanted vcards
                        if (c!=null) {
                            c.vcard=vcard;
                            if (display.getCurrent() instanceof ui.VirtualList) {
                                if (c.getGroupType()==Groups.TYPE_SELF)
                                    new VCardEdit(display, this, vcard);
                                else
                                    new VCardView(display, this, c);
                            }
                        } else {
                            new VCardView(display, this, c);
                        }
                       StaticData.getInstance().roster.theStream.cancelBlockListener(this); 
                       return BLOCK_REJECTED;        
                    }
                }
        return BLOCK_REJECTED;        
    }    
    
    public ContactEdit(Display display, Displayable pView, Contact c) {
        super(display, pView, SR.get(SR.MS_ADD_CONTACT));
        this.display=display;
        cf=Config.getInstance();
        
        tJid=new TextInput(display, SR.get(SR.MS_USER_JID), cf.add_contact_name, null, TextField.ANY);
        
        tNick=new TextInput(display, SR.get(SR.MS_NAME), null, null, TextField.ANY);
        
        tGroup=new TextInput(display, SR.get(SR.MS_NEWGROUP), (c==null)?"":c.group.name, null, TextField.ANY);

        tTranspList=new DropChoiceBox(display, SR.get(SR.MS_TRANSPORT));
        // Transport droplist
        tTranspList.append(sd.account.getServer());
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=ct.jid;
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getBareJid());
        }
        tTranspList.append(SR.get(SR.MS_OTHER));
        tTranspList.setSelectedIndex(tTranspList.size()-1);
        
        tAskSubscrCheckBox=new CheckBox(SR.get(SR.MS_ASK_SUBSCRIPTION), false);

        try {
            String jid;
//#ifndef WMUC
            if (c instanceof MucContact) {
                jid=Jid.toBareJid( ((MucContact)c).realJid );
            } else {
//#endif
                jid=c.bareJid;
//#ifndef WMUC
            }
//#endif
            // edit contact
            tJid.setValue(jid);
            tNick.setValue(c.getNick());
//#ifndef WMUC
            if (c instanceof MucContact) {
                c=null;
                throw new Exception();
            } 
//#endif
            if (c.getGroupType()!=Groups.TYPE_NOT_IN_LIST  && c.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // edit contact
                getMainBarItem().setElementAt(jid, 0);
                newContact=false;
            } else c=null; // adding not-in-list
        } catch (Exception e) {
            c=null;
        } // if MucContact does not contains realJid
        
        int sel=-1;
        ngroups=0;
        String grpName="";
        if (c!=null) grpName=c.group.name;
        
        Vector groups=sd.roster.contactList.groups.getRosterGroupNames();
        if (groups!=null) {
            tGrpList=new DropChoiceBox(display, SR.get(SR.MS_GROUP));
            ngroups=groups.size();
            for (int i=0;i<ngroups; i++) {
                String gn=(String)groups.elementAt(i);
                tGrpList.append(gn);
                
                if (gn.equals(grpName)) sel=i;
            }
        }
        if (sel<0) sel=0;
        
        if (c==null){
            itemsList.addElement(tJid);

            itemsList.addElement(tTranspList);
        }
        itemsList.addElement(tNick);

        tGrpList.append(SR.get(SR.MS_NEWGROUP));
        tGrpList.setSelectedIndex(sel);
        itemsList.addElement(tGrpList);
        
        newGroupPos=itemsList.indexOf(tGrpList)+1;


        if (newContact) {
            itemsList.addElement(new SimpleString(SR.get(SR.MS_SUBSCRIPTION), true));
            itemsList.addElement(tAskSubscrCheckBox);
            
            vCardReq=new LinkString(SR.get(SR.MS_VCARD)){ public void doAction() { requestVCard(); }};
            itemsList.addElement(vCardReq);
        }
        
        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
        this.parentView=pView;
    }
    
    private void requestVCard() {
        String jid=tJid.getValue();
        if (jid.length()>0)
            StaticData.getInstance().roster.theStream.addBlockListener(this);
            cf.add_contact_name = jid;
            VCard.request(jid, jid);
    }

    public void cmdOk() {
        String jid=tJid.getValue().trim().toLowerCase();
        if (jid!=null) {
            String name=tNick.getValue();
            String group=group(tGrpList.getSelectedIndex());

            if (tGrpList.getSelectedIndex()==tGrpList.size()-1)
                group=tGroup.getValue();

            boolean ask=tAskSubscrCheckBox.getValue();
            
            if (group.equals(SR.get(SR.MS_GENERAL))) group=null;

            int at=jid.indexOf('@');
            if (at<0 && tTranspList.getSelectedIndex()!=tTranspList.size()-1) {
                StringBuffer jidBuf=new StringBuffer(jid);
                at=jid.length();
                jidBuf.setLength(at);
                jidBuf.append('@')
                      .append((String) tTranspList.items.elementAt(tTranspList.getSelectedIndex()));
                jid=jidBuf.toString();
            }
            if (!new Jid(jid).getBareJid().equals(StaticData.getInstance().roster.selfContact().bareJid))
                sd.roster.storeContact(jid, name, group, ask);
            destroyView();
        }
    }
    
    protected void beginPaint(){
        if (tGrpList!=null) {
            if (tGrpList.toString()==SR.get(SR.MS_NEWGROUP)) {
                if (!newGroup) {
                    itemsList.insertElementAt(tGroup, newGroupPos);
                    newGroup=true;
                }
            } else {
                if (newGroup) {
                    itemsList.removeElement(tGroup);
                    newGroup=false;
                }
            }
        }
    }
    
    private String group(int index) {
        if (index==0) return SR.get(SR.MS_GENERAL);
        if (index==tGrpList.size()-1) return "";
        return (String) tGrpList.items.elementAt(index);
    }
 
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView/*roster*/);
    }

}
