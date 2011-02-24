/*
 * MessageEdit.java
 *
 * Created on 20.02.2005, 21:20
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

package client;
//#ifndef WMUC
import conference.AppendNick;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;
import io.TranslateSelect;
//#ifdef STATS
//# import stats.Stats;
//#endif
//#ifdef ARCHIVE
import archive.ArchiveList;
//#endif
import java.util.Vector;

/**
 *
 * @author Eugene Stahov,aqent
 */
public final class MessageEdit 
        implements CommandListener//, Runnable
{
    private Display display;
    private Displayable parentView;

    private String body;
    private String subj;
    public Contact to;
    private boolean emptyChat;
    private boolean multiMessage=false;
    private boolean composing=true;
    private Vector active_contacts;
    
//#ifdef DETRANSLIT
//#     private boolean sendInTranslit=false;
//#     private boolean sendInDeTranslit=false;
//#     DeTranslit dt;
//#endif
    

    
    Command cmdSend;
//#ifdef SMILES
    Command cmdSmile;
//#endif
    Command cmdInsNick;
    Command cmdInsMe;
//#ifdef DETRANSLIT
//#     Command cmdSendInTranslit;
//#     Command cmdSendInDeTranslit;
//#endif
    Command cmdLastMessage;
    Command cmdSubj;
    Command cmdSuspend;
    Command cmdCancel;
    Command cmdSendEvil;
    Command cmdTranslate;
//#ifdef ARCHIVE
    Command cmdPaste;
//#endif    
//#ifdef CLIPBOARD
//#     Command cmdPasteText;
//#endif  
//#if TEMPLATES
//#     Command cmdTemplate;
//#endif   
    
    
    public void initCommands(){
          cmdSend=new Command(SR.get(SR.MS_SEND), Command.OK, 1);
//#ifdef SMILES
          cmdSmile=new Command(SR.get(SR.MS_ADD_SMILE), Command.SCREEN,2);
//#endif
          cmdInsNick=new Command(SR.get(SR.MS_NICKNAMES),Command.SCREEN,3);
          cmdInsMe=new Command(SR.get(SR.MS_SLASHME), Command.SCREEN, 4); // /me
//#ifdef DETRANSLIT
//#           cmdSendInTranslit=new Command(SR.get(SR.MS_TRANSLIT), Command.SCREEN, 5);
//#           cmdSendInDeTranslit=new Command(SR.get(SR.MS_DETRANSLIT), Command.SCREEN, 5);
//#endif
          cmdLastMessage=new Command(SR.get(SR.MS_PREVIOUS), Command.SCREEN, 9);
          cmdSubj=new Command(SR.get(SR.MS_SET_SUBJECT), Command.SCREEN, 10);
          cmdSuspend=new Command(SR.get(SR.MS_SUSPEND), Command.BACK,90);
          cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.SCREEN,99);
          cmdSendEvil=new Command(SR.get(SR.MS_SEND_EVIL_MSG), Command.SCREEN,229);
          cmdTranslate=new Command(SR.get(SR.MS_TRANSLATE), Command.SCREEN ,337);
//#ifdef ARCHIVE
          cmdPaste=new Command(SR.get(SR.MS_ARCHIVE), Command.SCREEN, 6);
//#endif    
//#ifdef CLIPBOARD
//#           cmdPasteText=new Command(SR.get(SR.MS_PASTE), Command.SCREEN, 8);
//#endif  
//#if TEMPLATES
//#       cmdTemplate=new Command(SR.get(SR.MS_TEMPLATE), Command.SCREEN, 7);
//#endif 
          //System.out.println("initCommands");
    }

    
    public void replaceText(Contact to, String bodyNew, Displayable pView){
        this.parentView=pView;
        this.to = to;
         switch(midlet.BombusQD.cf.msgEditType){
            case 0: t.setString(bodyNew); break;
            case 1: textField.setString(bodyNew); break;
         }
         setText(bodyNew,to,pView, false);
    }
    
    public void setText(Displayable pView, Vector contacts, boolean multiMessage){
       this.multiMessage = multiMessage;
       
       active_contacts = null;
       active_contacts = new Vector(0);
       
       this.active_contacts = contacts;
       setText("", null, pView, false );
    }
    
    public void setText(String body, Contact to, Displayable pView, boolean emptyChat){
       if(display == null) this.display = midlet.BombusQD.getInstance().display;
       this.body = body; 
       this.parentView=pView;
       this.to = to;
       this.emptyChat = emptyChat;
       boolean phoneSONYE = (midlet.BombusQD.cf.phoneManufacturer == Config.SONYE);
       System.gc();
       
       switch(midlet.BombusQD.cf.msgEditType){
            case 0: 
                if(!phoneSONYE) t.setTitle( null == to ? "Multi-Message" : to.toString() );
                //t.setConstraints(2);//just magic for clearing attributes
                //t.setConstraints(0);
                t.setString("");
                if (body!=null) t.insert(body,0);
                if (midlet.BombusQD.cf.capsState) t.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
//#ifdef CLIPBOARD
//#                 if (midlet.BombusQD.cf.useClipBoard && !midlet.BombusQD.clipboard.isEmpty())  t.addCommand(cmdPasteText);
//#endif   
                if(null != to){
                  if (to.origin>=4) t.addCommand(cmdInsNick);//Contact.ORIGIN_GROUPCHAT==4
                  else t.removeCommand(cmdInsNick);
                  if (to.origin==4) t.addCommand(cmdSubj);
                  else t.removeCommand(cmdSubj);
                  if (to.lastSendedMessage!=null) t.addCommand(cmdLastMessage);
                } else {
                    if(t.equals(cmdInsNick)) t.removeCommand(cmdInsNick);
                    if(t.equals(cmdSubj)) t.removeCommand(cmdSubj);
                }
                display.setCurrent(t);
                break;
            case 1:
                if(!phoneSONYE) form.setTitle(null == to ? "Multi-Message" : to.toString());
                //textField.setConstraints(2);//just magic for clearing attributes
                //textField.setConstraints(0);
                textField.setString("");
                if (body!=null) textField.insert(body,0);
//#ifdef CLIPBOARD
//#                 if (midlet.BombusQD.cf.useClipBoard && !midlet.BombusQD.clipboard.isEmpty())  form.addCommand(cmdPasteText);
//#endif  
                if(null != to){
                  if (to.origin>=4) form.addCommand(cmdInsNick);
                  else form.removeCommand(cmdInsNick);
                  if (to.origin==4) form.addCommand(cmdSubj);
                  else form.removeCommand(cmdSubj);
                  if (to.lastSendedMessage!=null) form.addCommand(cmdLastMessage);
                } else {
                    if(form.equals(cmdInsNick)) form.removeCommand(cmdInsNick);
                    if(form.equals(cmdSubj)) form.removeCommand(cmdSubj);
                }
                display.setCurrent(form);
                break;
       }
       if(!multiMessage) {
         composing=true;
         send(null,null);
       }
    }

    
 //************OLD MsgEdit************   
    public TextBox t;
    public MessageEdit(Display display) {
       this.display = display;
       if (midlet.BombusQD.cf.runningMessage)
       {
          t=new TextBox("", body, 4096 , TextField.ANY);
          ticker = new Ticker("BombusQD");
          t.setTicker(ticker);
       }
       else {
        int maxSize=4096;
        t=new TextBox("", null, maxSize, TextField.ANY);
        try {
            maxSize=t.setMaxSize(4096);
            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
        } catch (Exception e) {}
       }

//#ifdef DETRANSLIT
//#         dt=DeTranslit.getInstance();
//#endif
        
        if(cmdSend == null) initCommands();
        
        t.addCommand(cmdSend);
        t.addCommand(cmdInsMe);
//#ifdef SMILES
        t.addCommand(cmdSmile);
        t.addCommand(cmdSendEvil);
//#endif
        t.addCommand(cmdTranslate);

//#ifdef DETRANSLIT
//#         t.addCommand(cmdSendInTranslit);
//#         t.addCommand(cmdSendInDeTranslit);
//#endif
        t.addCommand(cmdSuspend);
        t.addCommand(cmdCancel);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
        t.addCommand(cmdPaste);
//#endif
//#ifdef TEMPLATES
//#         t.addCommand(cmdTemplate);
//#endif
        t.setCommandListener(this);
//#ifdef RUNNING_MESSAGE
//#         /*
//#        if(midlet.BombusQD.cf.useLowMemory_msgedit==false){ 
//#          if (thread==null) (thread=new Thread(this)).start() ;
//#        }
//#          */
//#         
//#else
    new Thread(this).start() ;
//#endif
    }    
//************OLD MsgEdit************   
    
    
    
    private boolean evil=false;
    public Form form;
    public Ticker ticker = null; 
    public TextField textField = null;//default msgEdit
    
    public MessageEdit(Display display,boolean altMsgEdit) {
       this.display = display;
       form = new Form("");
//#ifdef DETRANSLIT
//#         dt=DeTranslit.getInstance();
//#endif

         //***************default MessageEdit Window based on TextField***************
            
          int maxSize = 4096;
          textField = new TextField("Message Edit", null, maxSize, TextField.ANY);
          try {
            maxSize = textField.setMaxSize(4096);
            if (body!=null) {
                if (body.length()>maxSize)
                    body = body.substring(0, maxSize-1);
                textField.setString(body);
            }
          } catch (Exception e) {}
          
         if (Config.getInstance().capsState) textField.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
         form.append(textField);    

        
       if (midlet.BombusQD.cf.runningMessage) {
         ticker = new Ticker("BombusQD");
         form.setTicker(ticker);
       } 
        
       if (midlet.BombusQD.cf.capsState)
           textField.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
       
       if(cmdSend == null) initCommands();
       
       form.addCommand(cmdSend);
       form.addCommand(cmdInsMe);
//#ifdef SMILES
       form.addCommand(cmdSmile);
       form.addCommand(cmdSendEvil);
//#endif 
       form.addCommand(cmdTranslate);

//#ifdef DETRANSLIT
//#        form.addCommand(cmdSendInTranslit);
//#        form.addCommand(cmdSendInDeTranslit);
//#endif
       form.addCommand(cmdSuspend);
       form.addCommand(cmdCancel);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
       form.addCommand(cmdPaste);
//#endif
       //#ifdef TEMPLATES
//#        form.addCommand(cmdTemplate);
       //#endif
       form.setCommandListener(this);
    }
    
    public void commandAction(Command c, Displayable d){
        if(to == null && !multiMessage) return;
        if(midlet.BombusQD.cf.msgEditType>0){
          body=textField.getString();
        }else{
          body=t.getString();
        }
        //System.out.println("commandAction.");
        if (body.length()==0) body=null;
        
        int caretPos=midlet.BombusQD.cf.msgEditType>0?textField.getCaretPosition():t.getCaretPosition();


//#ifdef ARCHIVE
	if (c==cmdPaste) { 
            //System.out.println(to);
                //composing=false;
                if(null != to) to.msgSuspended=body; 
                if(midlet.BombusQD.cf.msgEditType>0){
                  new ArchiveList(display , textField.getCaretPosition(), 1, textField, null,  to); return;                    
                }else{
                  new ArchiveList(display , t.getCaretPosition(),1,null,t,to); return;
                }
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { 
//#             if(midlet.BombusQD.cf.msgEditType>0){
//#               textField.insert(midlet.BombusQD.clipboard.getClipBoard(), textField.getCaretPosition() ); 
//#             }else{
//#               t.insert(midlet.BombusQD.clipboard.getClipBoard(), t.getCaretPosition() );                 
//#             }
//#             return;
//#         }
//#endif        
        if (c==cmdInsMe) {
            if(midlet.BombusQD.cf.msgEditType>0){
              textField.insert("/me ", 0);
            }else{
              t.insert("/me ", 0);                
            }
            return;
        }
//#if TEMPLATES
//#         if (c==cmdTemplate) {
//#             try {
//#                 if(null != to) to.msgSuspended=body; 
//#                 if(midlet.BombusQD.cf.msgEditType>0){
//#                   new ArchiveList(display , textField.getCaretPosition(), 0, textField, null, to); return;                    
//#                 }else{
//#                   new ArchiveList(display , t.getCaretPosition(), 0, null, t, to); return;
//#                 }
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif        
        if (c==cmdLastMessage) {
            if(null == to) return;
            if(null == to.lastSendedMessage) return;
            if(midlet.BombusQD.cf.msgEditType>0){
              textField.insert(to.lastSendedMessage,textField.getCaretPosition());
            }else{
              t.insert(to.lastSendedMessage,t.getCaretPosition());
            }
            return;
        }
//#ifdef SMILES
        if (c==cmdSmile) { 
            if(midlet.BombusQD.cf.msgEditType>0){
              new SmilePicker(display, display.getCurrent(), textField.getCaretPosition(), textField, null); 
            }else{
              new SmilePicker(display, display.getCurrent(), t.getCaretPosition(), null, t); 
            }
            return; 
        }
//#endif
//#ifndef WMUC
        if (c==cmdInsNick) {
            if(midlet.BombusQD.cf.msgEditType>0){
              new AppendNick(display, display.getCurrent(), to, textField.getCaretPosition(), textField, null);  
            }else{
              new AppendNick(display, display.getCurrent(), to, t.getCaretPosition(), null , t);  
            }
            return;
        }
//#endif
        

        if (c==cmdCancel) {
            composing=false;
            if(!multiMessage) send(null,null);
            body=null;
            if(multiMessage) multiMessage = false;
            if(null != to && to.msgSuspended!=null) to.msgSuspended=null;
            destroyView();
            return;
        }
        if (c==cmdSuspend) {
                composing=false;
                if(!multiMessage) send(null,null);
                if(multiMessage) multiMessage = false;
                if(null != to) to.msgSuspended=body; 
                body=null;
                destroyView(); 
                return;
        }
        if(c==cmdTranslate){
          new TranslateSelect(display,parentView,to,body,"none",false,-1);
          body=null;
          return;
        }
        if (c==cmdSend){
            if(body==null){
                composing=false;
                if(!multiMessage) send(null,null);
                if(multiMessage) multiMessage = false;
                if(null != to && to.msgSuspended!=null) to.msgSuspended=null;
                destroyView();
                return;
            }else{
              if(null != to) to.msgSuspended=null; 
            }
        }
//#ifdef DETRANSLIT
//#         if (c==cmdSendInTranslit) {
//#             sendInTranslit=true;
//#         }
//#  
//#         if (c==cmdSendInDeTranslit) {
//#             sendInDeTranslit=true;
//#         }
//#endif
        if (c==cmdSubj) {
            if (body==null) return;
            subj=body;
            body=null; //"/me "+SR.get(SR.MS_HAS_SET_TOPIC_TO+": "+subj;
        }
        
        if(c==cmdSendEvil){
           evil=true; 
        }        
        
//#ifdef RUNNING_MESSAGE
//#        if(null == to || multiMessage) {
//# 
//#             composing=false;
//#             if(active_contacts != null) {
//#               int size=active_contacts.size();
//#                for(int i=0; i<size; ++i) {    
//#                  to = (Contact)active_contacts.elementAt(i);
//#                  send();
//#                }
//#             }
//#             if(sendInTranslit) this.sendInTranslit = false;
//#             if(sendInDeTranslit) this.sendInDeTranslit = false;
//#             multiMessage = false;
//#        } 
//#        else {
//#          if(to.msgSuspended==null) {
//#             composing=false;
//#             //send(null,null);//check it on Sony Ericsson W595
//#             send();
//#          }
//#          if(sendInTranslit) this.sendInTranslit = false;
//#          if(sendInDeTranslit) this.sendInDeTranslit = false;
//#        }
//#        if(evil) this.evil = false;
//#endif
    }

    private void destroyView() {
       if(emptyChat) {
           if(null != to) {
              if(to.getChatInfo().getMessageCount()==0) {
                 midlet.BombusQD.sd.roster.showRoster();
              } else display.setCurrent(parentView);
           }
       } else display.setCurrent(parentView);
    }
    
    private void send(){
       send(body,subj);
       if(emptyChat) {
           if(null != to) {
               display.setCurrent(to.getMessageList());
               return;
           }
       }
       destroyView();
    }
    
    private void send(String body,String subj) {
        String comp = null;
        String id = String.valueOf((int) System.currentTimeMillis());
        Msg msg = new Msg(Constants.MESSAGE_TYPE_OUT, midlet.BombusQD.sd.account.toString() , subj, body);

        if (body!=null) body=body.trim();
//#ifdef DETRANSLIT
//#         if (sendInTranslit) {
//#             if (body!=null) body=dt.translit(body);
//#             if (subj!=null ) subj=dt.translit(subj);
//#         }
//#         if (sendInDeTranslit || midlet.BombusQD.cf.autoDeTranslit) {
//#             if (body!=null) body=dt.deTranslit(body);
//#             if (subj!=null ) subj=dt.deTranslit(subj);
//#         }
//#endif
        if (body!=null || subj!=null ) {
            msg.subject = subj;
            msg.body = body;
            
            if(evil) msg.body="(!)"+msg.body;                 
            msg.id=id;

            if (to.origin!=Constants.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp="active";
            }
        } else if (to.acceptComposing) comp=(composing)? "composing":"paused";
        if (!midlet.BombusQD.cf.eventComposing) comp=null;
        try { //??
            if (body!=null || subj!=null || comp!=null) {
               if (body!=null) to.lastSendedMessage=body; //System.out.println("sendMessage=>" + body + " > " + subj + " >>> " + to);
                 if(evil)
                     midlet.BombusQD.sd.roster.sendMessage(to, id, body, subj, comp,true);
                 else midlet.BombusQD.sd.roster.sendMessage(to, id, body, subj, comp,false);
               msg = null;
               if(subj!=null) this.subj = null;
               id = to.msgSuspended = null;
            }
        } catch (Exception e) {
          msg.body = "::MessageEdit Exception->Error send message(" + e.getMessage() + ")";
          to.addMessage(msg);
        }
    }    
}

