/*
 * TranslateText.java
 *
 *
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

package io;
import client.Contact;
import client.Msg;
import java.io.*; 
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Displayable;
import locale.SR;

public class TranslateText implements Runnable{
    
    
    public static final String ARABIC = "ar";
    public static final String BULGARIAN = "bg";
    public static final String CATALAN = "ca";
    public static final String CHINESE = "zh";
    public static final String CHINESE_SIMPLIFIED = "zh-CN";
    public static final String CHINESE_TRADITIONAL = "zh-TW";
    public static final String CROATIAN = "hr";
    public static final String CZECH = "cs";
    public static final String DANISH = "da";
    public static final String DUTCH = "nl";
    public static final String ENGLISH = "en";
    public static final String FILIPINO = "tl";
    public static final String FINNISH = "fi";
    public static final String FRENCH = "fr";
    public static final String GERMAN = "de";
    public static final String GREEK = "el";
    public static final String HEBREW = "iw";
    public static final String HINDI = "hi";
    public static final String INDONESIAN = "id";
    public static final String ITALIAN = "it";
    public static final String JAPANESE = "ja";
    public static final String KOREAN = "ko";
    public static final String LATVIAN = "lv";
    public static final String LITHUANIAN = "lt";
    public static final String NORWEGIAN = "no";
    public static final String POLISH = "pl";
    public static final String PORTUGESE = "pt";
    public static final String ROMANIAN = "ro";
    public static final String RUSSIAN = "ru";
    public static final String SERBIAN = "sr";
    public static final String SLOVAK = "sk";
    public static final String SLOVENIAN = "sl";
    public static final String SPANISH = "es";
    public static final String SWEDISH = "sv";
    public static final String UKRANIAN = "uk";
    public static final String VIETNAMESE = "vi";  
  
   public void Translate(){};

   private String body="";
   private String from="";
   private String to="";
   
   private Contact c;
   
   Thread mostRecent=null; 
   
   private boolean trCMsgList;   
   private int cursor;
   private String fromMucNick;   
           
   public void runTranslate(Contact c,
           String body,String from,String to,String fromMucNick,boolean trCMsgList,int cursor) {
      mostRecent = new Thread(this);
      mostRecent.start();
      this.body=body;
      this.to=to;
      this.from=from;
      this.c=c;
      this.trCMsgList=trCMsgList;
      this.cursor=cursor;
      this.fromMucNick=fromMucNick;
    } 

   public String translated_text = "";
  

   public static String URLencode(String s) throws IOException {
    ByteArrayOutputStream outstr = new ByteArrayOutputStream();
    DataOutputStream dataout = new DataOutputStream(outstr);
    StringBuffer ret = new StringBuffer();
    dataout.writeUTF(s);
    ByteArrayInputStream bais = new ByteArrayInputStream(outstr.toByteArray());
    //bais.read();
    //bais.read();
    int c = bais.read();
    while (c >= 0) {
      if ((c >= 'a' && c <= 'z') 
      || (c >= 'A' && c <= 'Z')
      || (c >= '0' && c <= '9') 
      || c == '.'|| c == '-'|| c == '*' || c == '_')
        ret.append((char)c);
      else if (c == ' ')
        ret.append('+');
      else {
        if (c < 128) {
          appendHex(c, ret);
        } else if (c < 224) {
          appendHex(c, ret);
          appendHex(bais.read(), ret);
        } else if (c < 240) {
          appendHex(c,ret);
          appendHex(bais.read(), ret);
          appendHex(bais.read(), ret);
        }
      }
      c = bais.read();
    } 
    bais.close();bais=null;
    outstr.close();outstr=null;
    dataout.close();dataout=null;
    return ret.toString();
  }
  
   private static void appendHex(int arg0, StringBuffer buff){
    buff.append('%');
    if (arg0 < 16)
      buff.append('0');
      buff.append(Integer.toHexString(arg0));
   }
   
    public void run(){
       boolean end = false;
       Thread thisThread = Thread.currentThread();       
       while( mostRecent == thisThread ){ 
	  try{
            HttpConnection httpConn = null;
            InputStream is = null;
            OutputStream os = null;
            if(from.startsWith("au")){
                from = "";
            }
            String url = "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0"+"&q="+
                    URLencode(body)+ "&langpair=" + from + "%7C" + to;
            httpConn = (HttpConnection)Connector.open(url);
            int respCode = httpConn.getResponseCode();
            if (respCode == httpConn.HTTP_OK) {
              StringBuffer sb = new StringBuffer();
              os = httpConn.openOutputStream();
              is = httpConn.openDataInputStream();
              int chr;
                InputStreamReader reader = new InputStreamReader(is,"UTF-8");
                String string;
                while ((chr = reader.read()) != -1)
                {
                    sb.append((char)(chr));
                }             
                String str = sb.toString();
                /*��������� ���������� ������*/
                
               //System.out.println(str);
                //fixit
                if(str.indexOf("invalid translation language pair")>-1){
                  midlet.BombusQD.sd.roster.setMsgEditText(c, SR.get(SR.MS_TRANSLATE)+": [" + from + "-" + to + "]"+
                         "\nERROR: invalid translation language pair");             
                }else{
                
                int i = str.indexOf("\"translatedText\":\"");
                String msgt="";
                 if(from.length()>0){
                    msgt = str.substring(i,str.indexOf("\"}"));                      
                    // {"responseData": {"translatedText":"hello","detectedSourceLanguage":"uk"}, "responseDetails": null, "responseStatus": 200}
                 }else{
                    msgt = str.substring(i,str.indexOf("\","));
                 }
                         
                
                translated_text=msgt.substring(18,msgt.length());             
                if(trCMsgList){
                  Msg tr_mess=(Msg)c.getChatInfo().msgs.elementAt(cursor); 
                    int size=c.getChatInfo().msgs.size();        
                     for(int k=0;k<size;k++){    
                         Msg msg=(Msg) c.getChatInfo().msgs.elementAt(k);
                            if (tr_mess==msg) {
                                 c.getChatInfo().msgs.removeElement(msg);
                                  StringBuffer b=new StringBuffer();
                                  b.append("<==Lang "+from+": " + msg.from +"> "+msg.body+"\n");
//#if NICK_COLORS
                                  b.append("<nick>");
//#endif
                                 b.append(SR.get(SR.MS_TRANSLATE)+" [");
                                  if(from.length()>0){
                                      b.append(from);
                                  }else{
                                      b.append("auto");
                                  }
                                      b.append("->" + to + "]:");
//#if NICK_COLORS
                                  b.append("</nick>");
//#endif                                 
                                  b.append("\n");                                  
                                 if(fromMucNick.indexOf("none")>-1){
                                  }else{
                                    b.append("==>"+fromMucNick+">");
                                 }                                  
                                 b.append(" "+translated_text);
                                 Msg tr=new Msg(tr_mess.messageType,tr_mess.from,tr_mess.subject,b.toString());
                                 c.getChatInfo().msgs.insertElementAt(tr,cursor);
                                 c.getMessageList().show();
                                 //new ContactMessageList(c);//  
                             }
                    }                    
                }else {
                    midlet.BombusQD.sd.roster.setMsgEditText(c, translated_text);
                    //midlet.BombusQD.sd.roster.createMessageEdit(c, translated_text, pView);
                } 
             }
             // is.close();
             // os.close();
              is=null;
              os=null;
            }
            else {
              //"invalid translation language";
            }
	  }catch(Exception e){}
        stopAll();     
       }
   }
    
    public void stopAll(){
	mostRecent = null;
        System.gc();
    }    
  
}