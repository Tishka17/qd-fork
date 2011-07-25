/*
 * ServiceDiscovery.java
 *
 * Created on 4.06.2005, 21:12
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

import client.Config;
import client.Contact;
import client.ContactEdit;
import client.StaticData;
//#ifndef WMUC
import conference.ConferenceForm;
//#endif
import images.RosterIcons;
import images.MenuIcons;
import menu.MenuListener;
import menu.Command;
import locale.SR;
import colors.ColorTheme;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import conference.bookmark.Bookmarks;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import midlet.BombusQD;
import ui.GMenu;
import ui.GMenuConfig;
import ui.IconTextElement;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.AlertBox;
import vcard.VCard;
import vcard.VCardEdit;
import xmpp.XmppError;

/**
 *
 * @author Evg_S,aqent,tishka17
 */

public class ServiceDiscovery extends VirtualList implements MenuListener, JabberBlockListener {
    private static final String FAV_SERVERS_DB = "favourite-servers"; 
    
     public final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
     public final static String NS_INFO="http://jabber.org/protocol/disco#info";
     public final static String NS_REGS="jabber:iq:register";
     public final static String NS_SRCH="jabber:iq:search";
     public final static String NS_GATE="jabber:iq:gateway";
     public final static String NS_MUC="http://jabber.org/protocol/muc";
     public final static String NS_COMMANDS="http://jabber.org/protocol/commands";

    private Command cmdOk;
    private Command cmdRfsh;
    private Command cmdFeatures;
    private Command cmdSrv;
    private Command cmdShowStatistics;

    private Vector items;
    private Vector stackItems=new Vector(0);

    private Vector features;
    private Vector cmds;
    
    private Vector favServers;

    private String service;
    private String node;

    private int discoIcon;

    private JabberStream stream;

    public ServiceDiscovery(String service, String node, boolean search) {
        super();

        cmdOk=new Command(SR.get(SR.MS_BROWSE), 0x43);
        cmdRfsh=new Command(SR.get(SR.MS_REFRESH), 0x10);
        cmdFeatures=new Command(SR.get(SR.MS_FEATURES), 0x56);
        cmdSrv=new Command(SR.get(SR.MS_ADD_SERVER), 0x55);
        cmdShowStatistics=new Command(SR.get(SR.MS_STATICSTICS), 0x57);

        setMainBarItem(new MainBar(3, null, null, false));
        getMainBarItem().addRAlign();
        getMainBarItem().addElement(null);

        stream=midlet.BombusQD.sd.roster.theStream;
        stream.cancelBlockListenerByClass(this.getClass());
        stream.addBlockListener(this);

        items=new Vector(0);
        features=new Vector(0);
        favServers = new Vector(0);

        this.node=node;

        if (service!=null && search) {
            this.service=service;
            requestQuery(NS_SRCH, "discosrch");
        } else if (service!=null) {
            this.service=service;
            requestQuery(NS_INFO, "disco");
        } else {
            this.service=null;
            
            String myServer = BombusQD.sd.account.getServer();

            items.addElement(new DiscoCommand(MenuIcons.ICON_VCARD, SR.get(SR.MS_VCARD), 4));
            items.addElement(new DiscoCommand(MenuIcons.ICON_CONFERENCE, SR.get(SR.MS_CONFERENCE), 4));
            
            items.addElement(new DiscoCommand(0x00, SR.get(SR.MS_MY_SERVERS)));
            items.addElement(new DiscoCommand(MenuIcons.ICON_ADD_SERVER, SR.get(SR.MS_ADD_SERVER), 16));
            items.addElement(new DiscoCommand(MenuIcons.ICON_REMOVE, SR.get(SR.MS_CLEAR), 16));
            try {
                DataInputStream is = NvStorage.ReadFileRecord(FAV_SERVERS_DB, 0);
                if (is == null) {
                    throw new IOException();
                }
                
                try {
                    while (true) {
                        String server = is.readUTF();
                        if (myServer.equals(server)) {
                            continue;
                        }
                        items.addElement(new DiscoContact(null, server, 0, 16));
                        favServers.addElement(server);
                    }
                } catch (EOFException e) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {}
            items.addElement(new DiscoContact(null, myServer, 0, 16));

            items.addElement(new DiscoCommand(MenuIcons.ICON_ADD_CONTACT, SR.get(SR.MS_ADD_CONTACT), 4));
            items.addElement(new DiscoCommand(MenuIcons.ICON_USER_SEARCH, SR.get(SR.MS_USERS_SEARCH), 4));
//#ifdef PRIVACY
            items.addElement(new DiscoCommand(MenuIcons.ICON_PRIVACY, SR.get(SR.MS_PRIVACY_LISTS), 4));
//#endif
//#if FILE_IO && FILE_TRANSFER
            if (midlet.BombusQD.cf.fileTransfer) {
                  if(io.file.transfer.TransferDispatcher.getInstance().getTaskList().size()>0) {
                      items.addElement(new DiscoCommand(MenuIcons.ICON_FT, SR.get(SR.MS_FILE_TRANSFERS), 4));
                  }
            }
//#endif
            if (midlet.BombusQD.sd.account.isGmail()) {
                items.addElement(new DiscoCommand(MenuIcons.ICON_GMAIL, SR.get(SR.MS_CHECK_GOOGLE_MAIL), 5));
            }
            items.addElement(new DiscoCommand(0x50, SR.get(SR.MS_BREAK_CONECTION), 5));

            discoIcon=0;
            mainbarUpdate();
            moveCursorHome();
            redraw();
        }

        isServiceDiscoWindow = true;
    }

    private String discoId(String id) {
        return id+service.hashCode();
    }

    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}

    public void showNotify() {
        discoIcon = 0;
        mainbarUpdate();
    }
    
    private void mainbarUpdate(){
        getMainBarItem().setElementAt(new Integer(discoIcon), 0);
        if(null == service && null == node) {
          getMainBarItem().setElementAt( SR.get(SR.MS_SERVICE) , 2);
        } else {
          getMainBarItem().setElementAt((service==null)?SR.get(SR.MS_RECENT):service, 2);
        }
        getMainBarItem().setElementAt(midlet.BombusQD.sd.roster.getEventIcon(), 4);

        int size = items.size();
        if (size > 0) {
            getMainBarItem().setElementAt(" (" + size + ") ", 1);
        }
    }

    private void requestQuery(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX;
        mainbarUpdate();
        redraw();

        JabberDataBlock req=new Iq(service, Iq.TYPE_GET, discoId(id));
        JabberDataBlock qry=req.addChildNs("query", namespace);
        qry.setAttribute("node", node);

        //stream.addBlockListener(this);
        //System.out.println(">> "+req.toString());
        stream.send(req);
    }

    private void requestCommand(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX;
        mainbarUpdate();
        redraw();

        JabberDataBlock req=new Iq(service, Iq.TYPE_SET, id);
        JabberDataBlock qry=req.addChildNs("command", namespace);
        qry.setAttribute("node", node);
        qry.setAttribute("action", "execute");

        //stream.addBlockListener(this);
        //System.out.println(req.toString());
        stream.send(req);
    }
    
    public void destroy() {}

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return JabberBlockListener.BLOCK_REJECTED;
        String id=data.getAttribute("id");
        if(null == id) return JabberBlockListener.BLOCK_REJECTED;

        if (!id.startsWith("disco")) return JabberBlockListener.BLOCK_REJECTED;

        String typeAttr;
        typeAttr = data.getTypeAttribute();
        if(null != typeAttr) {
          if (typeAttr.equals("error")) {
            discoIcon=RosterIcons.ICON_ERROR_INDEX;
            mainbarUpdate();
            //redraw();

            XmppError xe=XmppError.findInStanza(data);

            AlertBox box = new AlertBox(data.getAttribute("from"), xe.toString(), AlertBox.BUTTONS_OK);
            box.show();

            return JabberBlockListener.BLOCK_PROCESSED;
          }
        }
        JabberDataBlock command1=data.getChildBlock("query");
        JabberDataBlock command2=data.getChildBlock("command");
        if (command1==null && command2!=null) {
            String node = command2.getAttribute("node");
            if(node != null){
               if (node.startsWith("http://jabber.org/protocol/rc#")) id="discocmd"; //hack
            }
        }
        JabberDataBlock query=data.getChildBlock((id.equals("discocmd"))?"command":"query");
        Vector childs=query.getChildBlocks();


        if (id.equals(discoId("disco2"))) {
            Vector items=new Vector(0);
            if (null != childs) {
              JabberDataBlock item;
              int size = childs.size();
              String name = "";
              String jid = "";
              String node = "";
              Object serv = null;

              for(int y = 0; y < size; ++y) {
                item = (JabberDataBlock)childs.elementAt(y);
                if (item.getTagName().equals("item")){
                    name = item.getAttribute("name");
                    jid = item.getAttribute("jid");
                    node = item.getAttribute("node");
                    if (node==null) {
                        //int resourcePos=jid.indexOf('/');
                        //if (resourcePos>-1)
                        //    jid=jid.substring(0, resourcePos);
                        serv = new DiscoContact( name , jid  , 0 , 4);
                    } else {
                        serv=new Node(name, jid, node);
                    }
                    if(null != serv) items.addElement(serv);
                }
              }
              if(null != name) name = null;
              if(null != jid) jid = null;
              if(null != node) node = null;
            }
            childs = null;
            childs = new Vector(0);
            showResults(items);

        }  else if (id.equals(discoId("disco"))) {
            Vector cmds=new Vector(0);
            boolean showPartialResults=false;
            boolean loadItems=true;
            if (childs!=null) {
                JabberDataBlock identity=query.getChildBlock("identity");
                if (identity!=null) {
                    String category=identity.getAttribute("category");
                    typeAttr = identity.getTypeAttribute();
                    if(null == typeAttr) typeAttr = "-";
                    if (category.equals("automation") && typeAttr.equals("command-node"))  {
                        //cmds.addElement(new DiscoCommand(RosterIcons.ICON_AD_HOC, strCmds));
                        requestCommand(NS_COMMANDS, "discocmd");
                    }
                    if (category.equals("conference")) {
                        cmds.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, SR.get(SR.MS_JOIN_CONFERENCE)));
                        if (service.indexOf('@')<=0) {
                            loadItems=false;
                            showPartialResults=true;
                            cmds.addElement(new DiscoCommand(RosterIcons.ICON_ROOMLIST, SR.get(SR.MS_LOAD_ROOMLIST)));
                        }
                    }
                 }
               for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                    JabberDataBlock i=(JabberDataBlock)e.nextElement();
                    if (i.getTagName().equals("feature")) {
                        String var=i.getAttribute("var");
                        features.addElement(var);
                        //if (var.equals(NS_MUC)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, strJoin)); }
                        if (var.equals(NS_SRCH)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_SEARCH_INDEX, SR.get(SR.MS_SEARCH))); }
                        if (var.equals(NS_REGS)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_REGISTER_INDEX, SR.get(SR.MS_REGISTER))); }
                        if (var.equals(NS_GATE)) { showPartialResults=true; }
                        //if (var.equals(NODE_CMDS)) { cmds.addElement(new DiscoCommand(AD_HOC_INDEX,strCmds)); }
                    }
                }
             }
            /*if (data.getAttribute("from").equals(service)) */ { //FIXME!!!
                this.cmds=cmds;
            //System.out.println("cmds: "+items.toString());
                if (loadItems) requestQuery(NS_ITEMS, "disco2");
                if (showPartialResults) showResults(new Vector(0));
            }
        } else if (id.startsWith ("discoreg")) {
            discoIcon=0;
            new DiscoForm(data, stream, "discoResult", "query").show();
        } else if (id.startsWith("discocmd")) {
            discoIcon=0;
            new DiscoForm(data, stream, "discocmd", "command").show();
        } else if (id.startsWith("discosrch")) {
            discoIcon=0;
            new DiscoForm(data, stream, "discoRSearch", "query").show();
        } else if (id.startsWith("discoR")) {
            String text=SR.get(SR.MS_DONE);
            if(null == typeAttr) {
                typeAttr = "-";
            }
            if (typeAttr.equals("error")) {
                text=XmppError.findInStanza(data).toString();
            }
            if (text.equals(SR.get(SR.MS_DONE)) && id.endsWith("Search") ) {
                new SearchResult(data).show();
            } else {
                new AlertBox(typeAttr, text, AlertBox.BUTTONS_OK).show();
            }
        }
        redraw();
        return JabberBlockListener.BLOCK_PROCESSED;
    }

    public void eventOk(){
        super.eventOk();
        Object o = getFocusedObject();
        if (o != null) {
          if (o instanceof DiscoCommand) {
                switch (((IconTextElement) o).getImageIndex()) {
                    case MenuIcons.ICON_VCARD:
                        Contact self = midlet.BombusQD.sd.roster.selfContact();
                        if (self.vcard != null) {
                            new VCardEdit(self.vcard).show();
                            return;
                        }
                        VCard.request(self.bareJid, self.getJid());
                        break;
                    case MenuIcons.ICON_CONFERENCE: {
                        new Bookmarks(null).show();
                        break;
                    }
                    case MenuIcons.ICON_ADD_CONTACT: {
                        new ContactEdit(null).show();
                        break;
                    }
                    case MenuIcons.ICON_USER_SEARCH: {
                        new DiscoSearchForm(null, -1).show();
                        break;
                    }
//#ifdef PRIVACY
                    case MenuIcons.ICON_PRIVACY: {
                        new privacy.PrivacySelect().show();
                        break;
                    }
//#endif
//#if FILE_IO && FILE_TRANSFER
                    case MenuIcons.ICON_FT:
                        new io.file.transfer.TransferManager().show();
                        break;
//#endif
                    case MenuIcons.ICON_GMAIL:
                        midlet.BombusQD.sd.roster.theStream.send(xmpp.extensions.IqGmail.query());
                        break;
                    case 0x50:
                        midlet.BombusQD.sd.roster.show();
                        midlet.BombusQD.sd.roster.errorLog(SR.get(SR.MS_SIMULATED_BREAK));
                        midlet.BombusQD.sd.roster.doReconnect();
                        return;
                    case MenuIcons.ICON_ADD_SERVER:
                        new ServerBox(service, this).show();
                        break;
                    case MenuIcons.ICON_REMOVE:
                        try {
                            RecordStore.deleteRecordStore(FAV_SERVERS_DB);
                        } catch (RecordStoreException e) {
                        }
                        midlet.BombusQD.sd.roster.show();
                        break;
//#ifndef WMUC
                    case RosterIcons.ICON_GCJOIN_INDEX: {
                        int rp = service.indexOf('@');
                        String room = null;
                        if (rp > 0) {
                            room = service.substring(0, rp);
                        }
                        new ConferenceForm(room, service, null, false).show();
                        break;
                    }
//#endif
                    case RosterIcons.ICON_SEARCH_INDEX:
                        requestQuery(NS_SRCH, "discosrch");
                        break;
                    case RosterIcons.ICON_REGISTER_INDEX:
                        requestQuery(NS_REGS, "discoreg");
                        break;
                    case RosterIcons.ICON_ROOMLIST:
                        requestQuery(NS_ITEMS, "disco2");
                        break;
                    case RosterIcons.ICON_AD_HOC:
                        requestCommand(NS_COMMANDS, "discocmd");
                        break;
                }
            } else if (o instanceof IconTextElement) {
                String element = ((IconTextElement) o).getTipString();
                if (null == element) {
                    element = service;
                }
                if (o instanceof Node) {
                    browse(element, ((Node) o).getNode());
                    return;
                }
                browse(element, null);
                element = null;
            }
        }
    }

    private void showResults(final Vector items) {
        try {
            sort(items);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Object obj;
        for (Enumeration e=cmds.elements(); e.hasMoreElements();) {
            obj = e.nextElement();
            if (!items.contains(obj)) items.insertElementAt(obj,0);
        }
        this.items=items;
        moveCursorHome();
        discoIcon=0;
        mainbarUpdate();
    }

    public void browse(String service, String node){
            State st=new State();
            st.cursor=cursor;
            st.items=items;
            st.service=this.service;
            st.node=this.node;
            st.features=features;
            stackItems.addElement(st);

            items=new Vector(0);
            features=new Vector(0);

            this.service=service;
            this.node=node;
            requestQuery(NS_INFO,"disco");
    }
    
    private void commandState() {
        menuCommands.removeAllElements();

        if (getItemCount() > 0) {
            addCommand(cmdOk);
        }
        addCommand(cmdRfsh);
        if (service != null) {
            addCommand(cmdSrv);   
        }
        if (!features.isEmpty()) {
            addCommand(cmdFeatures);
        }
        Object o = getFocusedObject();
        if (o instanceof DiscoContact) {
            addCommand(cmdShowStatistics);
        }        
    }

    public void commandAction(Command c){
	if (c==cmdOk) {
            eventOk();
        } else if (c==cmdRfsh) { 
            if (service!=null) {
                requestQuery(NS_INFO, "disco");
            } 
        } else if (c == cmdSrv) {
            new ServerBox(service, this).show();
        } else if (c == cmdFeatures) {
            new DiscoFeatures(service, features).show();
        } else if (c==cmdShowStatistics) {
            JabberDataBlock req = new Iq(getFocusedObject().toString(), Iq.TYPE_GET, "getst");
            req.addChildNs("query", "http://jabber.org/protocol/stats");
            StaticData.getInstance().roster.theStream.send(req);
        }
    }

    public void destroyView()	{
        boolean stackItemsEmpty = false;
        try {
        if(stackItems != null) stackItemsEmpty = stackItems.isEmpty();
        if (stackItems==null || stackItemsEmpty) {
            if (stream!=null) {
                stream.cancelBlockListener(this);
            }
            isServiceDiscoWindow = false;
            super.destroyView();
        } else {
            if (stackItems!=null) {
              if(stackItems.size()>0) {
                State st=(State)stackItems.lastElement();
                service=st.service;
                items=st.items;
                features=st.features;
                stackItems.removeElement(st);
                moveCursorTo(st.cursor);
              }
            }
            discoIcon=0;
            mainbarUpdate();
            redraw();
        }
        } catch (Exception ex) {
//#ifdef DEBUG_CONSOLE
//#             midlet.BombusQD.debug.add("ServiceDiscovery exception(" + ex.getMessage() + ")",10);
//#endif
        }
    }

    private static String bareJid = "";
    private String getTransport(){
        try {
            int beginIndex = bareJid.indexOf('@')+1;
            int endIndex = bareJid.indexOf('.',beginIndex);
            return bareJid.substring(beginIndex, endIndex);
        } catch (Exception e) {
            return "-";
        }
    }

    private int getTransportStatus(String jid) {
        int resourcePos = jid.indexOf('/');
        if (resourcePos<0) resourcePos = jid.length();
        bareJid = jid.substring(0,resourcePos).toLowerCase(); //Strconv.toLowerCase( s.substring(0,resourcePos) );???
        return RosterIcons.getInstance().getTransportIndex(getTransport());
    }
    
    public void addServer(String server) {
        if (favServers.indexOf(server) == -1) {
            favServers.addElement(server);
            
            DataOutputStream ostream = NvStorage.CreateDataOutputStream();
            int size = favServers.size();
            for (int i = 0; i < size; ++i) {
                try {
                    ostream.writeUTF((String)favServers.elementAt(i));
                } catch (IOException e) {
//#ifdef DEBUG
//#                     e.printStackTrace();
//#endif
                }
            }
            NvStorage.writeFileRecord(ostream, FAV_SERVERS_DB, 0, true);
        }
    }

    public int showGraphicsMenu() {
        commandState();
        
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.SERVICE_DISCOVERY;
        redraw();
        return GMenu.SERVICE_DISCOVERY;
    }

    public String touchLeftCommand() {
        return (Config.getInstance().oldSE) ? SR.get(SR.MS_BACK) : SR.get(SR.MS_MENU);
    }

    public String touchRightCommand() {
        return (Config.getInstance().oldSE) ? SR.get(SR.MS_MENU) : SR.get(SR.MS_BACK);
    }

    private static class State {
        public String service;
        public String node;
        public Vector items;
        public Vector features;
        public int cursor;
    }
    
    private static class DiscoCommand extends IconTextElement {
        private int offs = 4;
        private boolean selectable = true;

        private DiscoCommand(int icon, String name, int offs) {
            super(name, MenuIcons.getInstance(), icon);
            this.offs=offs;
        }

        private DiscoCommand(int icon, String name) {
            super(name, RosterIcons.getInstance(), icon);
        }

        public int getOffset() {
            return offs;
        }

        public int getColor() {
            return ColorTheme.getColor(ColorTheme.DISCO_CMD);
        }


        public boolean isSelectable() {
            return selectable;
        }
        
        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }
    }
    
    private class DiscoContact extends IconTextElement {
        private String nickname;
        private String discoJid;
        private int offs = 4;
        private int status;

        public DiscoContact(String nick, String sJid, int status, int offs) {
            super(RosterIcons.getInstance());
            this.nickname = (nick == null) ? null : nick.trim();
            this.discoJid = sJid;
            this.offs = offs;
            if (null != sJid) {
                this.status = getTransportStatus(sJid);
            }
            if (-1 == status) {
                this.status = -1;
            }
        }

        public int getOffset() {
            return offs;
        }

        public boolean getFontIndex() {
            return true;
        } //change font

        public int getImageIndex() {
            return status;
        }

        ;
      public String toString() {
            return (nickname == null) ? discoJid : nickname;
        }

        public String getTipString() {
            return discoJid;
        }
    }
}
//#endif
