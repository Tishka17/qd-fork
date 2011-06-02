//#ifdef JUICK.COM
/*
 * JuickModule.java
 *
 * Created on 24 �������� 2009 �., 22:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import client.Msg;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import client.Contact;

/**
 *
 * @author aqent
 */
public class JuickModule{

    /** Creates a new instance of JuickListener */

    public static JuickModule listener;

    public final static String BOTNAME = "juick@juick.com/Juick";
    public final static String NS_MESSAGES = "http://juick.com/query#messages";
    public final static String NS_MESSAGE = "http://juick.com/message";
    private final static String TAG_RECOMMENDATION = "Recommended by ";

    private static StringBuffer buf = new StringBuffer(0);
    private static StringBuffer message_id = new StringBuffer(0);
    private static Vector childBlocks = new Vector(0);
    private static Vector childTags = new Vector(0);

    public JuickModule() {
    }

    public static JuickModule jm(){
        if (listener==null) { listener=new JuickModule(); }
        return listener;
    }


    private void storeMessage(Msg msg){
//#ifdef DEBUG_CONSOLE
//#       midlet.BombusQD.debug.add("STORE wait::",10);
//#endif
       Contact c = midlet.BombusQD.sd.roster.getContact(BOTNAME,false);
       midlet.BombusQD.sd.roster.messageStore( c ,msg);
//#ifdef DEBUG_CONSOLE
//#       midlet.BombusQD.debug.add("STORE::"+msg.body,10);
//#endif
    }


    private boolean separateMsgs(Msg message){
       String body = message.getBody().concat("\n");
       StringBuffer bufMsg = new StringBuffer(0);
       StringBuffer id = new StringBuffer(0);
        int len = body.length();
        int i = 0;
        int count = 0;
        boolean lastPopularMessages = body.startsWith("Last popular messages:");
        boolean lastMessages = body.startsWith("Last messages:");

        if( lastMessages || lastPopularMessages) {
          message = new Msg(Msg.JUICK, BOTNAME, null,"<nick>[Last" + (lastMessages?" ":" popular ") + "messages]</nick>" );
          storeMessage(message);
          boolean parse = false;
          for (i = 0; i < len; i++) {
              char c = body.charAt(i);
              if(c=='\n'){
                 c = body.charAt(i+1);

                if (c == '@'){  //@NICK: tagslist \n
                    count++;
                    bufMsg = new StringBuffer(0);
                    bufMsg.append(count);
                    bufMsg.append(").");
                    while(c!='\n'){ i++;  c = body.charAt(i); bufMsg.append(c); } //include tags
                    parse = true;
                }
                else if (c=='#'){ //get num post and send to msg.id
                   bufMsg.append('\n');
                   int j = i;
                   while(parse) { j++; c = body.charAt(j);
                     if(body.charAt(j)==' '){
                       bufMsg.append(' ');
                       c = body.charAt(j+1);
                       if(c=='('){
                         j=j-1;
                         while(c!=')') { j++;  c = body.charAt(j);  bufMsg.append(c); } //include replies
                       }
                       message = new Msg(Msg.JUICK, BOTNAME, null, bufMsg.toString() );
                                message.setId(id.append(' ').toString());
                       storeMessage(message);
                         bufMsg = new StringBuffer(0);
                         id = new StringBuffer(0);
                         parse = false;
                     } else {
                       bufMsg.append(c); id.append(c);
                     }
                   }
                }
              } else bufMsg.append(c);
           }
         bufMsg = new StringBuffer(0);
         id = null;
         body = null;
         return true;
      }
      return false;
      //speed 10 messages - 14 msec
    }


    private String parseRecomendation(String body) {
       if(body.startsWith(TAG_RECOMMENDATION)) {
           int len = TAG_RECOMMENDATION.length();
           int pos = body.indexOf(":");
           return body.substring(len,pos);
       }
       return "";
    }

    public Msg getMsg(Msg m,JabberDataBlock data) {
        if( data instanceof Iq ) {
//#ifdef DEBUG_CONSOLE
//#             midlet.BombusQD.debug.add("IQ::"+data.toString(),10);
//#endif
            String id= data.getAttribute("id");
            String type = data.getTypeAttribute();

            if (id!=null) {
                if (type.equals( "result" )) {
                    JabberDataBlock query = data.findNamespace("query",NS_MESSAGES);

                    if(query!=null) { //use <nick> for highlite tags
                      if(id.startsWith("cmts_")) {
                      //send query comments
                          String body = id.substring(5,id.length());
                          String replies = "";
                          if(query!=null){
                              JabberDataBlock ns = query.findNamespace("juick",NS_MESSAGE);
                              if(ns!=null) replies = ns.getAttribute("replies");
                          }
//#ifdef DEBUG_CONSOLE
//#                           midlet.BombusQD.debug.add("IQ replies::"+replies,10);
//#endif
                          if(replies!=null){
                             JabberDataBlock comments = new Iq(BOTNAME, Iq.TYPE_GET,"qd_comments");
                             JabberDataBlock comments_query = comments.addChildNs("query",NS_MESSAGES);
                             comments_query.setAttribute("mid",body);
                             comments_query.setAttribute("rid","*");
                             if(body.length()>0) midlet.BombusQD.sd.roster.theStream.send(comments);
                             comments=null;
                             body=null;
                          }
                      }

                      JabberDataBlock child = null;
                      JabberDataBlock tag = null;

                      childBlocks = new Vector(0);
                      childTags = new Vector(0);

                      String uname = "";
                      String mid = "";
                      String rid = "";
                      String replies = "";
                      String body = "";

                      childBlocks = query.getChildBlocks();
                      int size=childBlocks.size();
                        for(int i=0;i<size;i++){
                           child = (JabberDataBlock)childBlocks.elementAt(i);
                           childTags = child.getChildBlocks();

                             uname = child.getAttribute("uname");
                             replies = child.getAttribute("replies");
                             mid = child.getAttribute("mid");
                             rid = child.getAttribute("rid");

                           if (uname!=null) buf.append("<nick>@").append(uname).append("</nick>:");
                           if (replies!=null)
                               buf.append('\n')
                                  .append("<nick>")
                                  .append("Replies ")
                                  .append('(')
                                  .append(replies)
                                  .append(')')
                                  .append("</nick>");

                           int tagSize = childTags.size();
                           if(tagSize>0){
                            for(int k=0;k<tagSize;k++){
                              //specially for "#+" type
                              tag = (JabberDataBlock)childTags.elementAt(k);
                               if (tag.getTagName().equals("tag"))
                                    buf.append('\n')
                                      .append("<nick>*")
                                      .append(tag.getText())
                                      .append("</nick>");
                               if (tag.getTagName().equals("body")) body = tag.getText();
                             }
                             buf.append('\n').append(body);
                           }

                          if (mid!=null){
                              buf.append('\n');
                              message_id.append('#').append(mid);
                                 if(rid!=null) message_id.append('/').append("<nick>").append(rid).append("</nick>");
                              message_id.append(' ');
                              buf.append(message_id.toString());
                          }

                          m = new Msg(Msg.JUICK, BOTNAME, null, buf.toString() );
                            m.setId(message_id.toString());
                          storeMessage(m);
                          buf = new StringBuffer(0);
                          message_id = new StringBuffer(0);
                          childTags = new Vector(0);
                           /*
                            *<nick>NICK</nick>: textbody
                           */
                        }
                    }else {
                       //m = new Msg(Msg.MESSAGE_TYPE_JUICK, BOTNAME , null , "none messages." );
                       //storeMessage(m);
                    }
                }
                if ( type.equals( "error" ) ) {
                   m = new Msg(Msg.JUICK, BOTNAME, null, "<nick>error:</nick> "+data.toString());
                   storeMessage(m);
                }
               return null;
            }

        } else if( data instanceof Message ) {//��������� �� ��������
//#ifdef DEBUG_CONSOLE
//#                  midlet.BombusQD.debug.add("MESSAGE::"+data.toString(),10);
//#endif
                 Message message = (Message) data;

                 //JabberDataBlock juickUnameNs = data.findNamespace("nick", "http://jabber.org/protocol/nick");
                 //if (juickUnameNs!=null) juickUnameNs.getText();

                 JabberDataBlock juickNs = data.findNamespace("juick",NS_MESSAGE);
                 m.setType(Msg.JUICK);
                 m.setId(null);
                 m.setFrom(BOTNAME);

                 if(juickNs!=null){
                       Vector childBlocks = new Vector(0);
                       StringBuffer sb = new StringBuffer(0);
                       String rid = juickNs.getAttribute("rid");
                       String mid = juickNs.getAttribute("mid");
                       String replyto = juickNs.getAttribute("replyto");
                       String bodyAnsw = "";
                       boolean photo = (juickNs.getAttribute("photo")==null)?false:true;
                       JabberDataBlock child = null;

                       String body = message.getBody().trim();
                       if(body.length() > 0) {
                            String recomendation = parseRecomendation(body);
                            if(recomendation.length() > 0) {
                                sb.append(recomendation)
                                  .append(' ')
                                  .append("recommends you:")
                                  .append('\n');
                                recomendation = null;
                            }
                       }

                       sb.append("<nick>@")
                           .append(juickNs.getAttribute("uname"))
                           .append(":</nick>");

                       childBlocks = juickNs.getChildBlocks();
                       int size=childBlocks.size();
                       for(int i=0;i<size;i++){
                          child = (JabberDataBlock)childBlocks.elementAt(i);
                          if (child.getTagName().equals("tag"))
                              sb.append('\n')
                              .append("<nick>*")
                              .append(child.getText())
                              .append("</nick>");

                          if (child.getTagName().equals("body")) bodyAnsw = child.getText();
                       }
                       sb.append('\n').append(bodyAnsw);
                       sb.append('\n');
                       sb.append('#').append(mid==null?"<nick>PM</nick>":mid);
                       if(rid!=null)
                            sb.append('/')
                                 .append("<nick>")
                                 .append(rid)
                                 .append("</nick>");

                       if (replyto!=null)
                             sb.append(" (replyto /").append(replyto).append(")");
                       if(photo)
                            sb.append("+photo");

                       if(message.getUrl()!=null) sb.append('\n').append(message.getOOB());
                       m.setBody(sb.toString());

                        /*
                         *  @NICK
                         *  *enabled tags []
                         *  MESSAGE_TEXT
                         *  #NUMBER_POST/comment
                         *  url:OOB_LINK
                         */

                       sb = new StringBuffer(0);//Clear for next msg.id
                       if(mid==null)
                             sb.append("PM @").append(juickNs.getAttribute("uname"));
                       else
                             sb.append('#').append(mid);
                       if(rid!=null) sb.append('/').append(rid);

                       sb.append(' ');

                       m.setId(sb.toString()); // #id/num || #id
///////////////
                        //if(mid!=null) m.from = "[j]"+mid;
                        //created [j] temp contact
                        //juickNs!=null,�.�. ��������� � juick namespace
///////////////
                       sb = new StringBuffer(0);
                       childBlocks = new Vector(0);
                       childBlocks=null;
                       sb=null;
                       body=bodyAnsw=null;
                       mid=null;
                       rid=null;
                       return m;
             } else { //Simple message
                juickNs = null;
                return separateMsgs(m)?null:m;
             }
        }
     return null;
    }

}
//#endif