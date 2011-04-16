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
//#ifndef WMUC
import conference.ConferenceForm;
//#endif
import images.RosterIcons;
import images.MenuIcons;
import java.util.*;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import javax.microedition.lcdui.Displayable;
import locale.SR;
import colors.ColorTheme;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import client.*;
import conference.bookmark.Bookmarks;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.EOFException;
import midlet.BombusQD;
import ui.controls.AlertBox;
import xmpp.XmppError;
import vcard.*;

/**
 *
 * @author Evg_S,aqent,tishka17
 */

public class ServiceDiscovery
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        JabberBlockListener
{
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

    private String service;
    private String node;

    private int discoIcon;

    private JabberStream stream;

    private ServiceDiscovery serviceDisco;

    public ServiceDiscovery(String service, String node, boolean search) {
        super();

        cmdOk=new Command(SR.get(SR.MS_BROWSE), Command.SCREEN, 1);
        cmdRfsh=new Command(SR.get(SR.MS_REFRESH), Command.SCREEN, 2);
        cmdFeatures=new Command(SR.get(SR.MS_FEATURES), Command.SCREEN, 3);
        cmdSrv=new Command(SR.get(SR.MS_ADD_SERVER), Command.SCREEN, 10);
        cmdShowStatistics=new Command(SR.get(SR.MS_STATICSTICS), Command.SCREEN, 4);

        setMainBarItem(new MainBar(3, null, null, false));
        getMainBarItem().addRAlign();
        getMainBarItem().addElement(null);

        stream=midlet.BombusQD.sd.roster.theStream;
        stream.cancelBlockListenerByClass(this.getClass());
        stream.addBlockListener(this);

//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#else
//#         addCommand(cmdBack);
//#endif
        setCommandListener(this);
        addCommand(cmdSrv); cmdSrv.setImg(0x55);
        addCommand(cmdRfsh); cmdRfsh.setImg(0x10);
        addCommand(cmdFeatures); cmdFeatures.setImg(0x56);
        addCommand(cmdShowStatistics); cmdShowStatistics.setImg(0x57);

        //addCommand(cmdAdd);
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif

        items=new Vector(0);
        features=new Vector(0);

        this.node=node;

        if (service!=null && search) {
            this.service=service;
            requestQuery(NS_SRCH, "discosrch");
        } else if (service!=null) {
            this.service=service;
            requestQuery(NS_INFO, "disco");
        } else {
            this.service=null;

            Object add;

            add = new DiscoCommand(MenuIcons.ICON_VCARD , SR.get(SR.MS_VCARD), true, 5);  items.addElement(add);//0
            add = new DiscoCommand(MenuIcons.ICON_CONFERENCE , SR.get(SR.MS_CONFERENCE) , true, 5);  items.addElement(add);//1
            add = new DiscoCommand(MenuIcons.ICON_DISCO_SERVICE , SR.get(SR.MS_IM_NETWORKS) , true, 5);  items.addElement(add);//2
            add = new DiscoCommand(MenuIcons.ICON_ADD_CONTACT , SR.get(SR.MS_ADD_CONTACT) , true, 5);  items.addElement(add);//3
            add = new DiscoCommand(MenuIcons.ICON_USER_SEARCH , SR.get(SR.MS_USERS_SEARCH) , true, 5);  items.addElement(add);//4
//#ifdef PRIVACY
            add = new DiscoCommand(MenuIcons.ICON_PRIVACY , SR.get(SR.MS_PRIVACY_LISTS), true, 5);  items.addElement(add);
//#endif
//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
            if (midlet.BombusQD.cf.fileTransfer) {
                  if(io.file.transfer.TransferDispatcher.getInstance().getTaskList().size()>0) {
                      add = new DiscoCommand(MenuIcons.ICON_FT , SR.get(SR.MS_FILE_TRANSFERS), true, 5);
                      items.addElement(add);
                  }
            }
//#endif
//#endif
            if (midlet.BombusQD.sd.account.isGmail()) {
                add = new DiscoCommand(MenuIcons.ICON_GMAIL , SR.get(SR.MS_CHECK_GOOGLE_MAIL), true, 5);  items.addElement(add);
            }
            add = new DiscoCommand(MenuIcons.ICON_RECONNECT , SR.get(SR.MS_BREAK_CONECTION) , true, 5);  items.addElement(add);


            String myServer=midlet.BombusQD.sd.account.getServer();
            int insertPos = 3;
            try {
                DataInputStream is=NvStorage.ReadFileRecord("disco", 0);
                try {
                    while (true) {
                        String recent=is.readUTF();
                        if (myServer.equals(recent)) continue; //only one instance for our service
                        add = new DiscoContact(null, recent, 0, 16); items.insertElementAt(add,insertPos);
                        insertPos++;
                    }
                } catch (EOFException e) {
                    is.close();
                    is = null;
                }
            } catch (Exception e) {}

            if(insertPos > 2) {//3 for hide(!)
               add = new DiscoContact(null, myServer, 0, 16);  items.insertElementAt(add,insertPos);
               add = new DiscoCommand(0x00, SR.get(SR.MS_MY_SERVERS)); items.insertElementAt(add,3);
               add = new DiscoCommand(MenuIcons.ICON_ADD_SERVER, SR.get(SR.MS_ADD_SERVER), true, 16); items.insertElementAt(add,4);
               add = new DiscoCommand(MenuIcons.ICON_REMOVE_ICON, SR.get(SR.MS_CLEAR), true, 16); items.insertElementAt(add,5);
            }

            //sort(items);
            discoIcon=0;
            mainbarUpdate();
            moveCursorHome();
            redraw();
        }
        serviceDisco = this;
        isServiceDiscoWindow = true;
    }

    private String discoId(String id) {
        return id+service.hashCode();
    }

    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}

    protected void beginPaint(){
        getMainBarItem().setElementAt(midlet.BombusQD.sd.roster.getEventIcon(), 4);
    }

    private void mainbarUpdate(){
        getMainBarItem().setElementAt(new Integer(discoIcon), 0);
        if(null == service && null == node) {
          getMainBarItem().setElementAt( SR.get(SR.MS_SERVICE) , 2);
        } else {
          getMainBarItem().setElementAt((service==null)?SR.get(SR.MS_RECENT):service, 2);
        }
        getMainBarItem().setElementAt(midlet.BombusQD.sd.roster.getEventIcon(), 4);

	int size=0;
	try { size=items.size(); } catch (Exception e) {}
	String count=null;

	removeCommand(cmdOk);

	if (size>0) {
//#ifdef MENU_LISTENER
	    menuCommands.insertElementAt(cmdOk, 0);
            cmdOk.setImg(0x43);
//#else
//#             addCommand(cmdOk);
//#endif
	    count=" ("+size+") ";
	}
        getMainBarItem().setElementAt(count,1);
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

            AlertBox box = new AlertBox(data.getAttribute("from"), xe.toString(), false) {
                public void yes() { };
                public void no() { };
            };
            box.show();

            return JabberBlockListener.BLOCK_PROCESSED;
          }
        }
        JabberDataBlock command1=data.getChildBlock("query");
        JabberDataBlock command2=data.getChildBlock("command");
        if (command1==null) {
            if (command2!=null) {
                command1=command2;
            }
            String node = command1.getAttribute("node");
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
            boolean client=false;
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
            DiscoForm form = new DiscoForm(data, stream, "discoResult", "query");
            form.setParentView(BombusQD.sd.roster);
            form.show();
        } else if (id.startsWith("discocmd")) {
            discoIcon=0;
            DiscoForm form = new DiscoForm(data, stream, "discocmd", "command");
            //form.setParentView(BombusQD.sd.roster);
            form.show();
        } else if (id.startsWith("discosrch")) {
            discoIcon=0;
            DiscoForm form = new DiscoForm(data, stream, "discoRSearch", "query");
            form.setParentView(BombusQD.sd.roster);
            form.show();
        } else if (id.startsWith("discoR")) {
            String text=SR.get(SR.MS_DONE);
            if(null == typeAttr) typeAttr = "-";
            if (typeAttr.equals("error")) text=XmppError.findInStanza(data).toString();
            if (text.equals(SR.get(SR.MS_DONE)) && id.endsWith("Search") ) {
                new SearchResult(data).show();
            } else {
                AlertBox box = new AlertBox(typeAttr, text, false) {
                    public void yes() { }
                    public void no() { }
                };
                box.show();
            }
        }
        redraw();
        return JabberBlockListener.BLOCK_PROCESSED;
    }


    public void eventOk(){
        super.eventOk();
        Object o= getFocusedObject();
        if (o!=null) {
          if (o instanceof DiscoContact) {
              if (((DiscoContact)o).imNetwork > 0) return;
          }
          if (o instanceof DiscoCommand) {
              return;
          }
          if (o instanceof IconTextElement) {
            String element = ((IconTextElement)o).getTipString();
            if(null == element) element=service;
            if (o instanceof Node) {
            //if(null == element) {
                browse( element, ((Node) o).getNode() );
               return;
            }
            browse( element, null );
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
//#ifndef GRAPHICS_MENU
//#         removeCommand(cmdBack);
//#         addCommand(cmdBack);
//#endif
            this.service=service;
            this.node=node;
            requestQuery(NS_INFO,"disco");
    }

    public void commandAction(Command c, Displayable d){
	if (c==cmdOk) eventOk();
        if (c==cmdRfsh) { if (service!=null) requestQuery(NS_INFO, "disco"); }
        if (c == cmdSrv) {
            new ServerBox(service, serviceDisco).show();
        }
        if (c == cmdFeatures) {
            new DiscoFeatures(service, features).show();
        }
        if (c==cmdShowStatistics) {
            Object o=getFocusedObject();
            JabberDataBlock req=new Iq(o.toString(), Iq.TYPE_GET,"getst");
            //JabberDataBlock query =
            req.addChildNs("query","http://jabber.org/protocol/stats");
            StaticData.getInstance().roster.theStream.send(req);
            req=null;
            //query=null;
        }
    }
    private void exitDiscovery(boolean cancel){
        boolean stackItemsEmpty = false;
        try {
        if(stackItems != null) stackItemsEmpty = stackItems.isEmpty();
        if (cancel || stackItems==null || stackItemsEmpty) {
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


    public void destroyView()	{
           exitDiscovery(false);
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

    class DiscoContact extends IconTextElement {
      private String nickname;
      private String discoJid;
      private int offs = 4;
      private int status;
      private int imNetwork = 0;

      public DiscoContact(String nick, String sJid, int status, int offs, int imNetwork) {
        super(RosterIcons.getInstance());
        this.nickname = (nick==null) ? null : nick.trim();
        this.discoJid = sJid.substring( 0, sJid.indexOf(".") );
        this.offs = offs;
        this.imNetwork = imNetwork;
        this.status = getTransportStatus(sJid);
      }

      public DiscoContact(String nick, String sJid, int status, int offs) {
        super(RosterIcons.getInstance());
        this.nickname = (nick==null) ? null : nick.trim();
        this.discoJid = sJid;
        this.offs = offs;
        if(null != sJid) this.status = getTransportStatus(sJid);
        if(-1 == status) this.status = -1;
      }

      public void onSelect(VirtualList view) { //todo: %)
//#ifdef POPUPS
         if(imNetwork>0) {
            switch(imNetwork) {
                case 1://ICQ
                    setWobble(0, "none", "ICQ");
                    break;
                case 2://MRIM
                    setWobble(0, "none", "MRIM");
                    break;
                case 3://VKontakte
                    setWobble(0, "none", "VKontakte");
                    break;
                case 4://AIM
                    setWobble(0, "none", "AIM");
                    break;
                case 5://J2J
                    setWobble(0, "none", "J2J");
                    break;
            }
            return;
         }
//#endif
         super.onSelect(view);
      }

      public int getOffset() { return offs; }
      public boolean getFontIndex() { return true; } //change font
      public int getImageIndex() { return status; };
      public String toString() { return (nickname==null)?discoJid:nickname; }
      public String getTipString() { return discoJid; }
    }


    private class DiscoCommand extends IconTextElement {
        String name;
        int index;
        int icon;
        boolean userCommands;
        boolean lock;
        int offs = 4;

        public DiscoCommand(int icon, String name, boolean value, int offs) {
            super(MenuIcons.getInstance());
            this.icon=icon;
            this.name=name;
            this.userCommands=value;
            this.offs=offs;
        }

        public DiscoCommand(int icon, String name) {
            super(RosterIcons.getInstance());
            this.lock = name.startsWith("My ");
            this.icon=icon;
            this.name=name;
        }

        public int getOffset() { return offs; }
        public int getColor(){ return ColorTheme.getColor(ColorTheme.DISCO_CMD); }
        public int getImageIndex() { return icon; }
        public String toString(){ return name; }
        public void onSelect(VirtualList view) {
            if(lock) return;
            if(userCommands) {
                switch (icon) {
                    case MenuIcons.ICON_VCARD:
                        Contact self=midlet.BombusQD.sd.roster.selfContact();
                        if (self.vcard!=null) {
                            showForm(new VCardEdit(self.vcard));
                          return;
                        }
                        VCard.request(self.bareJid, self.getJid());
                        break;
                    case MenuIcons.ICON_CONFERENCE: {
                        showForm(new Bookmarks(null));
                        break;
                    }
                    case MenuIcons.ICON_ADD_CONTACT: {
                        showForm(new ContactEdit(null));
                        break;
                    }
                    case MenuIcons.ICON_USER_SEARCH: {
                        showForm(new DiscoSearchForm(null , -1));
                        break;
                    }
//#ifdef PRIVACY
                    case MenuIcons.ICON_PRIVACY: {
                        showForm(new privacy.PrivacySelect());
                        break;
                    }
//#endif
//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
                    case MenuIcons.ICON_FT:
                        showForm(new io.file.transfer.TransferManager());
                        break;
//#endif
//#endif
                    case MenuIcons.ICON_GMAIL:
                        midlet.BombusQD.sd.roster.theStream.send(xmpp.extensions.IqGmail.query());
                        break;
                    case MenuIcons.ICON_RECONNECT:
                        midlet.BombusQD.sd.roster.show();
                        midlet.BombusQD.sd.roster.errorLog(SR.get(SR.MS_SIMULATED_BREAK));
                        midlet.BombusQD.sd.roster.doReconnect();
                        return;
                    case MenuIcons.ICON_DISCO_SERVICE:
                        showIMmenu();
                        break;
                    case MenuIcons.ICON_ADD_SERVER: //add server
                        new ServerBox(service, serviceDisco).show();
                        break;
                    case MenuIcons.ICON_REMOVE_ICON: //remove server
                        try {
                            javax.microedition.rms.RecordStore recordStore = javax.microedition.rms.RecordStore.openRecordStore("disco", false);
                            int size = recordStore.getNumRecords();
                            if (size > 0) {
                              for (int i = 1; i <= size; ++i) recordStore.deleteRecord(i);
                            }
                            recordStore.closeRecordStore();
                            recordStore = null;
                        } catch (Exception e) {
//#ifdef DEBUG
//#                           System.out.println("disco rms exception");
//#endif
                        }
                        midlet.BombusQD.sd.roster.show();
                        break;
                }
                return;
            }
            switch (icon) {
//#ifndef WMUC
                case RosterIcons.ICON_GCJOIN_INDEX: {
                    int rp=service.indexOf('@');
                    String room=null;
                    if (rp>0) {
                        room=service.substring(0,rp);
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
                default:
            }
        }
    }

    private void showForm(VirtualList list) {
        list.setParentView(getParentView());
        list.show();
    }

    public void showIMmenu() {
        Object add = items.elementAt(3);
        if(add instanceof DiscoContact) {
            if( ((DiscoContact)add).getTipString().startsWith("ICQ") ) { //hardcode
                byte del = 0;
                while(del!=5) {
                   items.removeElementAt(3);
                   del++;
                }
                return;
            }
        }
        add = new DiscoContact(null, "ICQ.", 0, 25, 1);        items.insertElementAt(add, 3);
        add = new DiscoContact(null, "MRIM.", 0, 25, 2);       items.insertElementAt(add, 4);
        add = new DiscoContact(null, "VKontakte.", 0, 25, 3);  items.insertElementAt(add, 5);
        add = new DiscoContact(null, "AIM.", 0, 25, 4);        items.insertElementAt(add, 6);
        add = new DiscoContact(null, "J2J.", 0, 25, 5);        items.insertElementAt(add, 7);
    }


//#ifdef MENU_LISTENER
/*
//#ifdef GRAPHICS_MENU
    public void touchRightPressed(){ if (Config.getInstance().oldSE) showGraphicsMenu(); else destroyView(); }
    public void touchLeftPressed(){ if (Config.getInstance().oldSE) destroyView(); else showGraphicsMenu(); }
//#else
//#     public void touchRightPressed(){ if (cf.oldSE) showMenu(); else destroyView(); }
//#     public void touchLeftPressed(){ if (cf.oldSE) keyGreen(); else showMenu(); }
//#endif
 */

//#endif


//#ifdef MENU_LISTENER


//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        menuItem = new GMenu(this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.SERVICE_DISCOVERY;
        redraw();
        return GMenu.SERVICE_DISCOVERY;
    }
//#else
//#     public void showMenu() {
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_DISCO), null, menuCommands);
//#     }
//#endif

    public String touchLeftCommand(){ return (Config.getInstance().oldSE)?SR.get(SR.MS_BACK):SR.get(SR.MS_MENU); }
    public String touchRightCommand(){ return (Config.getInstance().oldSE)?SR.get(SR.MS_MENU):SR.get(SR.MS_BACK); }

//#endif

}
class State{
    public String service;
    public String node;
    public Vector items;
    public Vector features;
    public int cursor;
}
//#endif
