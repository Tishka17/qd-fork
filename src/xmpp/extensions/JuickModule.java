//#ifdef JUICK.COM
/*
 * JuickModule.java
 *
 * Created on 24 �������� 2009 �., 22:10
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
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

package xmpp.extensions;

import client.Msg;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import client.Contact;
import ui.controls.form.ImageItem;
import com.alsutton.jabber.JabberBlockListener;
import util.Time;

/**
 *
 * @author aqent
 */
public class JuickModule implements JabberBlockListener {

    /** Creates a new instance of JuickListener */

    public static JuickModule listener;

    public final static String BOTNAME = "juick@juick.com/Juick";
    public final static String NS_MESSAGES = "http://juick.com/query#messages";
    public final static String NS_MESSAGE = "http://juick.com/message";
    public final static String NS_GEOLOC = "http://jabber.org/protocol/geoloc";
    public final static String NS_SUBSCRIPTION = "http://juick.com/subscriptions";
    private final static String TAG_RECOMMENDATION = "Recommended by ";

    public JuickModule() {}
    public void destroy() {}
    
    public static JabberDataBlock LastMessages() {
        JabberDataBlock requestLastMessages=new Iq(BOTNAME, Iq.TYPE_GET,"lastmsgs");
        requestLastMessages.addChildNs("query",NS_MESSAGES);
        midlet.BombusQD.sd.roster.theStream.send(requestLastMessages);
        return requestLastMessages;
    }
    public static JabberDataBlock ShowMyFeed() {
        JabberDataBlock requestMyFeed=new Iq(BOTNAME, Iq.TYPE_GET, "myfeed");
        requestMyFeed.addChildNs("query", NS_MESSAGES).setAttribute("filter","myfeed");
        midlet.BombusQD.sd.roster.theStream.send(requestMyFeed);
        return requestMyFeed;
    }
    /*
    <iq to='juick@juick.com' id='id123' type='set'>
        <subscriptions xmlns='http://juick.com/subscriptions#messages' action='subscribe' mid='123456'/>
    </iq>
     */
    public static String getMid(String id) {
        if (id!=null && id.startsWith("#")) {
            int i = id.indexOf('/');
            if (i==-1) { return id.substring(1);}
            else {return id.substring(1,i);}
        }
        return null;
    }
    public static JabberDataBlock Subscribe(Msg m) {
        String mid = getMid(m.getId());
        if (mid==null) {
            return null; 
        }
        JabberDataBlock request=new Iq(BOTNAME, Iq.TYPE_SET, "jsubs"+mid);
        JabberDataBlock sub = request.addChildNs("subscriptions", NS_SUBSCRIPTION+"#messages");
        sub.setAttribute("action", "subscribe");
        sub.setAttribute("mid", mid);
        midlet.BombusQD.sd.roster.theStream.send(request);
        return request;  
    }
    public static JabberDataBlock Unsubscribe(Msg m) {
        String mid = getMid(m.getId());
        if (mid==null) {
            return null; 
        }
        JabberDataBlock request=new Iq(BOTNAME, Iq.TYPE_SET, "junsubs"+mid);
        JabberDataBlock sub = request.addChildNs("subscriptions", NS_SUBSCRIPTION+"#messages");
        sub.setAttribute("action", "unsubscribe");
        sub.setAttribute("mid", mid);
        midlet.BombusQD.sd.roster.theStream.send(request);
        return request;  
    }
    
    public static JuickModule instance(){
        if (listener==null) { listener=new JuickModule(); }
        return listener;
    }

    private static class CheckException extends NumberFormatException{};
    public static JabberDataBlock constuctRequest(String body) {
        String chars = "0123456789";
        int charLen = chars.length();
        int bodyLen = body.length();
        int post = -1, comment = -1;
        int mode = 0;//0 -запрос поста, 1 - запрос поста с комментами, 2 - ответ, 3 - просто новый пост
        JabberDataBlock request = null;
        //вытаскиваем номер поста и коммента и получаем тип запроса
        try {
            if(body.charAt(0)!='#') throw new CheckException();
            int i = 1, j = 0, start = 0;
            char c;
            //проверка, что введено
            for (i=1; i<bodyLen; i++) {
                c = body.charAt(i);
                for(j=0;j<charLen;j++) {
                    if(c==chars.charAt(j)) break;
                }
                if (j==charLen) {
                    if (c=='/') {//разделитель коммента и поста
                        if (post!=-1 || i+1==bodyLen) throw new CheckException();
                        post = Integer.parseInt(body.substring(1, i));
                        start = i+1;
                    } else if (c=='+') {//запрос числа комментов
                        //был номер коммента или есть ещё символы
                        if (comment!=-1 || (i+1)!=bodyLen) throw new CheckException();
                        post = Integer.parseInt(body.substring(1, i));
                        mode = 1;
                    } else if (c==' ') {//номер поста перед текстом
                        if (post==-1) post = Integer.parseInt(body.substring(1, i));
                        else comment = Integer.parseInt(body.substring(start, i));
                        mode = 2;
                    } else throw new CheckException();//хз что такое
                }
            }
            //сообщение не содержит больше символов
            if (post==-1) {
                if (i>1) post = Integer.parseInt(body.substring(1, i));
            }
            else if (mode==0) comment = Integer.parseInt(body.substring(start, i));
        } catch (NumberFormatException e){ //если распарсить не удалось - делаем просто сообщение
            mode = 3;
        }
        switch (mode){
            case 0: {//запрос поста/коммента
                request = new Iq("juick@juick.com/Juick", Iq.TYPE_GET, "lastmsgs"+post );
                JabberDataBlock query = request.addChildNs("query", NS_MESSAGES);
                if (post!=-1) query.setAttribute("mid", ""+post);
                if (comment!=-1) query.setAttribute("rid", ""+comment);
                break;
            }
            case 1: {//запрос поста с комментами
                request = new Iq("juick@juick.com/Juick", Iq.TYPE_GET, "cmts_"+post);
                JabberDataBlock query = request.addChildNs("query", NS_MESSAGES);
                query.setAttribute("mid", ""+post);
                break;
            }
            default: {//новый пост или ответ
                 request = new Message("juick@juick.com", body, null, false);
                 break;
            }

        }
        return request;
    }

    private void storeMessage(Msg msg){
//#ifdef DEBUG_CONSOLE
//#       midlet.BombusQD.debug.add("STORE wait::",10);
//#endif
        Contact c = midlet.BombusQD.sd.roster.getContact(BOTNAME,false);
        midlet.BombusQD.sd.roster.messageStore(c ,msg);
//#ifdef DEBUG_CONSOLE
//#       midlet.BombusQD.debug.add("STORE::"+msg.getBody(),10);
//#endif
    }


    private Msg parseJuickMessage(JabberDataBlock child, JabberDataBlock stanza) {
        JabberDataBlock tag;
        Msg m;
        Vector childTags = child.getChildBlocks();

        final String uname = child.getAttribute("uname");
        final String replies = child.getAttribute("replies");
        final String mid = child.getAttribute("mid");
        final String rid = child.getAttribute("rid");
        final String replyto = child.getAttribute("replyto");
        ImageItem attachment = null;
        StringBuffer buf = new StringBuffer();
        String body = null;
        StringBuffer id = new StringBuffer();

        if (stanza != null && stanza instanceof Message) {
            String msgbody = ((Message)stanza).getBody();
            if (msgbody!=null && msgbody.startsWith(TAG_RECOMMENDATION)) {
                int x = msgbody.indexOf(':');
                buf.append(msgbody.substring(0, x)).append('\n');
            } else {//TODO: make "Jubo recommends"
            }
        }
        if (uname!=null && !midlet.BombusQD.cf.showNickNames) {
            buf.append("<nick>@").append(uname).append("</nick>:\n");
        }
        if (replies!=null) {
            buf.append("<nick>Replies (").append(replies).append(")</nick>\n");
        }
        int tagSize = childTags.size();
        for(int k=0;k<tagSize;k++){
            tag = (JabberDataBlock)childTags.elementAt(k);
            if (tag.getTagName().equals("tag"))
                buf.append("<nick>*")
                    .append(tag.getText())
                    .append("</nick>\n");
            if (tag.getTagName().equals("body")) body = tag.getText();
        }
        if (body!=null) {
            buf.append(body);
        }
        //Вложения - фото и видео. Для фото показываем миниатюру
        if (midlet.BombusQD.cf.juickImages && "jpg".equals(child.getAttribute("attach"))) {
            StringBuffer url = new StringBuffer("http://i.juick.com/ps/");
            buf.append("\nhttp://i.juick.com/photos-1024/");
            url.append(mid);
            buf.append(mid);
            if (rid!=null) {
                url.append('-').append(rid);
                buf.append('-').append(rid);
            }
            url.append(".jpg");
            buf.append(".jpg");
            attachment = new ImageItem(url.toString());
        } else if ("mp4".equals(child.getAttribute("attach"))) {
            buf.append("\nhttp://i.juick.com/video/");
            buf.append(mid);
            if (rid!=null) {
                buf.append('-').append(rid);
            }
            buf.append(".mp4");
        }
        //не работает при запросе поста через iq
        try {
            JabberDataBlock geo = stanza.findNamespace("geoloc", NS_GEOLOC);
            String tmp = geo.getChildBlock("description").getText();
            buf.append("\nLocation: <nick>").append(tmp).append("</nick>");
            tmp = geo.getChildBlock("uri").getText();
            buf.append(' ').append(tmp);
        } catch (NullPointerException e) {}
        
        if (mid!=null){
            buf.append('\n');
            buf.append("<nick>");
            id.append("#").append(mid);
            if(rid!=null) {
                id.append('/').append(rid);
            }
            buf.append(id.toString());
            buf.append("</nick> ");
            if (replyto!=null && !replyto.equals("0")) {
                buf.append("(/").append(replyto).append(')');
            }
        } else {
            buf.append("\n#<nick>PM</nick>");
            id.append("PM @").append(uname);
        }
        id.append(' ');

        m = new Msg(Msg.JUICK, (uname==null)?BOTNAME:uname, null, buf.toString());
        m.setId(id.toString());
        try {
            String datetime = child.getAttribute("ts");
            m.setDayTime(Time.dateJuickStringToLong(datetime));
        } catch (NullPointerException e) {}
        m.attachment = attachment;
        storeMessage(m);
        buf = new StringBuffer(0);
        childTags = new Vector(0);
        return m;
    }
    
    
   public int blockArrived( JabberDataBlock data ) { 
       if (data instanceof Message) {
           JabberDataBlock juickNs = data.findNamespace("juick",NS_MESSAGE);
           if (juickNs==null) {
               return JabberBlockListener.BLOCK_REJECTED;   
           }
           Msg m = parseJuickMessage(juickNs, data);
           if (m!=null) {
               return JabberBlockListener.BLOCK_PROCESSED;
           }
       } else if (data instanceof Iq) {
           JabberDataBlock query = data.findNamespace("query",NS_MESSAGES);           
           if (query==null) {
               return JabberBlockListener.BLOCK_REJECTED;   
           }
           Vector childBlocks = query.getChildBlocks();
           int size=childBlocks.size();
           Msg m=null;
           for(int i=0;i<size;i++){
               m = parseJuickMessage((JabberDataBlock)childBlocks.elementAt(i), null);
           }
           //запрашиваем комменты, если был запрос поста с +
           String id = data.getAttribute("id");
           if(id.startsWith("cmts_") && data.getAttribute("from").equals(BOTNAME) && m!=null) {
               id = id.substring(5);
               JabberDataBlock comments = new Iq(BOTNAME, Iq.TYPE_GET,"qd_comments_"+id);
               JabberDataBlock comments_query = comments.addChildNs("query",NS_MESSAGES);
               comments_query.setAttribute("mid",id);
               comments_query.setAttribute("rid","*");
               midlet.BombusQD.sd.roster.theStream.send(comments);
           }
           return JabberBlockListener.BLOCK_PROCESSED;
       }
       return JabberBlockListener.BLOCK_REJECTED;
   }
}
//#endif
