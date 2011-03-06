/*
 * StringUtils.java
 *
 * Created on 28 ��� 2008 �., 13:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import conference.ConferenceGroup;
import conference.MucContact;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import ui.Time;
import xmpp.XmppError;
import font.*;
import com.ssttr.crypto.SHA1;
import java.util.Random;
//#ifdef CLASSIC_CHAT
//# import client.ClassicChat;
//#endif
import client.Msg;
import client.Config;
import client.StaticData;
import client.Constants;
/**
 *
 * @author ad
 */
public class StringUtils {

    /** Creates a new instance of StringUtils */
    public StringUtils() { }


    public static String quoteString(Msg msg) {
        StringBuffer out=new StringBuffer(0);
        String subject = msg.subject;
        if (subject!=null)
            if (subject.length()>0)
                out.append(subject).append('\n');
        out.append(msg.body);
        int i=0;
        int len = out.length();
        while (i<len) {
            if (out.charAt(i)<0x03) out.deleteCharAt(i);
            else i++;
        }
        return out.toString();
    }

    private final static String[] badChars= { "?", "\\", "/", "*", ".", "\"", ":", "%", "@", "|", "<", ">", "COM", "LPT", "NULL", "PRINT"};

    public static String replaceNickTags(String body){
         StringBuffer sb = new StringBuffer(0);
         if (null==body) return null;
         body = body+" ";
         boolean parse = true;
         int f1,f2,len=0;
         while(parse){
          len = body.length();
          f1 = body.indexOf("<nick>");
          f2 = body.indexOf("</nick>");
          if(f1>-1 || f2>-1){
            for(int i=0;i<len;i++){
              if(i==f1) i+=6;
              if(i==f2) i+=7;
              sb.append(body.charAt(i));
            }
            body = sb.toString();
            sb = new StringBuffer(0);
          } else parse = false;
         }
         len = body.length();
         sb = null;
         return body.substring(0,len-1);
    }

    public static Msg replaceNickTags(Msg msg){
         StringBuffer sb = new StringBuffer(0);
         String body = msg.body+" ";
         boolean parse = true;
         int f1,f2,len=0;
         while(parse){
          len = body.length();
          f1 = body.indexOf("<nick>");
          f2 = body.indexOf("</nick>");
          if(f1>-1 || f2>-1){
            for(int i=0;i<len;i++){
              if(i==f1) i+=6;
              if(i==f2) i+=7;
              sb.append(body.charAt(i));
            }
            body = sb.toString(); sb = new StringBuffer(0);
          } else parse = false;
         }
         sb = null;
         len = body.length();
         msg.body = body.substring(0,len-1);
         return msg;
    }


    private static StringBuffer buffer = new StringBuffer(0);
    public static String stringReplace(String aSearch, String aFind, String aReplace) {
    	int pos = aSearch.indexOf(aFind);
    	if (pos != -1) {
            buffer = new StringBuffer(0);
            int lastPos = 0;
             while (pos != -1) {
                    buffer.append(aSearch.substring(lastPos, pos)).append(aReplace);
                    lastPos = pos + aFind.length();
                    pos = aSearch.indexOf(aFind, lastPos);
            }
            buffer.append( aSearch.substring(lastPos));
            aSearch = buffer.toString();
    	}
    	return aSearch;
    }

    public static String calcHash() {
        SHA1 sha1=new SHA1();
        sha1.init();
        sha1.update(generate());
        sha1.finish();
        return sha1.getDigestBase64();
    }


   public static String generate() {
    StringBuffer sb = new StringBuffer();
      Random rand = new Random();
      int i=0;
      char[] chars = {
          'q','w','e','r','t','y','u','i','o','p',
          'a','s','d','f','g','h','j','k','l','z',
          'x','c','v','b','n','m','Q','W','E','R',
          'T','Y','U','I','O','P','A','S','D','F',
          'G','H','J','K','L','Z','X','C','V','B',
          'N','M','0','1','2','3','4','5','6','7',
          '8','9'}; //62
      char[] pass = {'*','*','*','*','*','*','*','*'};
      for (int k = 0; k<pass.length; k++) {
         i = Math.abs(rand.nextInt()) % 62;
         pass[k]=chars[i];
      }
      sb.append(pass);
   return sb.toString();
  }


  //static FontClass MFont = FontClass.getInstance();
  //static Font f = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_SMALL);

  public static Font getFont() {
        return FontCache.getFont(false, FontCache.msg);
  }

//#ifdef CLASSIC_CHAT
//#   public static void addClassicChatMsg(String message, int availWidth,ClassicChat scrMsg) {
//#         Vector lines=new Vector(0);
//#         char[] valueChars = message.concat("   ").toCharArray();
//#         int startPos = 0;
//#         availWidth-=24;
//#         int currentLineWidth = 0;
//# 
//#         for (int i = 0; i < valueChars.length; i++) {
//#             char c = valueChars[i];
//#             currentLineWidth += //MFont.isCheck()?MFont.getCharWidth(c):
//#                     getFont().charWidth(c);
//#             if (c == '\n') {
//#                 scrMsg.storeMessage( new String( valueChars, startPos, i - startPos ));
//#                 //lastSpacePos = -1;
//#                 startPos = i+1;
//#                 currentLineWidth = 0;
//#                 i = startPos;
//#             } else if (currentLineWidth >= availWidth && i > 0) {
//#                     i--;
//#                     scrMsg.storeMessage( new String( valueChars, startPos, i - startPos ));
//#                     startPos =  i;
//#                     currentLineWidth = 0;
//#             }
//#         }
//#         scrMsg.storeMessage( new String( valueChars, startPos, valueChars.length - startPos ));
//#     }
//#endif

    private static StringBuffer suffix;
    private static String ratio="";

    public static String getSizeString(long number) { //multiple calls
        suffix = new StringBuffer(0);
        try {
            if ( number > 1024000 ) {
                ratio=Long.toString(number/100000);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))

                      .append(" mb");
            } else if ( number > 1024 ) {
                ratio=Long.toString(number/100);

                int dotpos=ratio.length()-1;

                suffix.append( (dotpos==0)? "0":ratio.substring(0, dotpos))
                      .append('.')
                      .append(ratio.substring(dotpos))

                      .append(" kb");
            } else {
                suffix.append(number)
                      .append(" b");
            }
            suffix.append(midlet.BombusQD.sd.roster.theStream.getStreamStatsBar());
        } catch (Exception e) { }

        return suffix.toString();
    }

    public static String replaceBadChars (String src) {
        int len = badChars.length;
        for (int i=0; i<len;i++) src=stringReplace(src,badChars[i],"_");
        return src;
    }

    public static String urlPrep(String src){
        String mask=" #$%&/:;<=>?@[\\]^'{|}";
        StringBuffer out=new StringBuffer();

        for (int i=0; i<src.length(); i++) {
            char s=src.charAt(i);

            if (mask.indexOf(s)<0) {  out.append(s); continue;  }

            out.append('%').append(hexByteToString((byte)s));
        }

        return out.toString();
    }


    public static Vector parseBoxString(String value,int availWidth,Font font) {
        Vector lines = new Vector(0);
        char[] valueChars = value.toCharArray();
        int startPos = 0;
        int lastSpacePos = -1;
        int lastSpacePosLength = 0;
        int currentLineWidth = 0;
        int len = valueChars.length;
        String object = null;
        for (int i = 0; i < len; ++i) {
            char c = valueChars[i];
            currentLineWidth += font.charWidth(c);
            if (c == '%' || c == '\n') {
                object = new String( valueChars, startPos, i - startPos );
                lines.addElement( object );
                lastSpacePos = -1;
                startPos = i+1;
                currentLineWidth = 0;
                i = startPos;
            } else if (currentLineWidth >= availWidth && i > 0) {
                if ( lastSpacePos == -1 ) {
                    i--;
                    object = new String( valueChars, startPos, i - startPos );
                    lines.addElement( object );
                    startPos =  i;
                    currentLineWidth = 0;
                } else {
                    currentLineWidth -= lastSpacePosLength;
                    object = new String( valueChars, startPos, lastSpacePos - startPos );
                    lines.addElement( object );
                    startPos =  lastSpacePos + 1;
                    lastSpacePos = -1;
                }
            }
        }
        object = new String( valueChars, startPos, valueChars.length - startPos );
        lines.addElement( object );
        valueChars = null;
        object = null;
        return lines;
    }


    public static Vector parseMessage(String value, int availWidth, Font font) {
        StringBuffer out=new StringBuffer(value);
        int vi = 0;
        int size = out.length();
        while (vi<size) {
            if (out.charAt(vi)<0x03) out.deleteCharAt(vi);
            else vi++;
        }
        value=out.toString();
        Vector lines=new Vector(0);
        char[] valueChars = value.toCharArray();
        int startPos = 0;
        int lastSpacePos = -1;
        int lastSpacePosLength = 0;
        int currentLineWidth = 0;
        int len = valueChars.length;
        char c;
        for (int i = 0; i < len; i++) {
            c = valueChars[i];
            currentLineWidth += font.charWidth(c);
            if (c == '\r') {
            } else if (c == '\n') {
                lines.addElement( new String( valueChars, startPos, i - startPos ) );
                lastSpacePos = -1;
                startPos = i+1;
                currentLineWidth = 0;
            } else if (currentLineWidth >= availWidth && i > 0) {
                if ( lastSpacePos == -1 ) {
                    i--;
                    lines.addElement( new String( valueChars, startPos, i - startPos ) );
                    startPos =  i;
                    currentLineWidth = 0;
                } else {
                    currentLineWidth -= lastSpacePosLength;
                    lines.addElement( new String( valueChars, startPos, lastSpacePos - startPos ) );
                    startPos =  lastSpacePos + 1;
                    lastSpacePos = -1;
                }
            } else if (c == ' ' || c == '\t') {
                lastSpacePos = i;
                lastSpacePosLength = currentLineWidth;
            }
        }
        // last string
        lines.addElement( new String( valueChars, startPos, valueChars.length - startPos ) );
        valueChars=null;
        out=null;
        return lines;
    }

    public static String toExtendedString(String src){
        src=stringReplace(src,"%dt",Time.dispLocalTime());
        src=stringReplace(src,"%t",Time.localTime());
                Random rand = new Random();
                String[] qd_offline_status = {
                       "BombusQD.It's very simple",
                       "I use BombusQD.And you?",
                       "I'am happy user of BombusQD.Check it on http://bombusmod-qd.wen.ru/",
                       "BombusQD.Check it on http://bombusmod-qd.wen.ru/",
                       "Bombus,QD Bombus",
                       "I'am happy user of BombusQD.You get it here http://bombusmod-qd.wen.ru/",
                       "Are you ready for BombusQD?"
                };
                int i = Math.abs(rand.nextInt()) % 7;
        src=stringReplace(src,"%qd",qd_offline_status[i]);
        return src;
    }

    public static String hexByteToString(byte b){
        StringBuffer out=new StringBuffer();
        char c = (char) ((b >> 4) & 0xf);
        if (c > 9)   c = (char) ((c - 10) + 'a');
        else  c = (char) (c + '0');
        out.append(c);
        c = (char) (b & 0xf);
        if (c > 9)
            c = (char)((c-10) + 'a');
        else
            c = (char)(c + '0');
        out.append(c);

        return out.toString();
    }

    public static String processError(Presence presence, byte presenceType, ConferenceGroup group, MucContact muc) {
        XmppError xe=XmppError.findInStanza(presence);
        int errCode=xe.getCondition();

        //ConferenceGroup grp=(ConferenceGroup)group;//?
        if (presenceType>=Constants.PRESENCE_OFFLINE)
            midlet.BombusQD.sd.roster.testMeOffline(muc, group, true);
        if (errCode!=XmppError.CONFLICT || presenceType>=Constants.PRESENCE_OFFLINE)
            muc.setStatus(presenceType);

        String errText=xe.getText();
        if (errText!=null) return xe.toString(); // if error description is provided by server

        // legacy codes
        switch (errCode) {
            case XmppError.NOT_AUTHORIZED:        return "Password required";
            case XmppError.FORBIDDEN:             return "You are banned in this room";
            case XmppError.ITEM_NOT_FOUND:        return "Room does not exists";
            case XmppError.NOT_ALLOWED:           return "You can't create room on this server";
            case XmppError.NOT_ACCEPTABLE:        return "Reserved roomnick must be used";
            case XmppError.REGISTRATION_REQUIRED: return "This room is members-only";
            case XmppError.CONFLICT:              return "Nickname is already in use by another occupant";
            case XmppError.SERVICE_UNAVAILABLE:   return "Maximum number of users has been reached in this room";
            default: return xe.getName();
        }
    }
}
