/*
 * HistoryStorage.java
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

//#ifdef HISTORY
//# package history;
//# 
//# import client.Constants;
//# import client.Contact;
//# import client.Msg;
//# import client.CommandForm;
//# import java.io.*;
//# import java.util.Timer;
//# import java.util.TimerTask;
//# import javax.microedition.rms.RecordStore;
//#if FILE_IO
//# import io.file.FileIO;
//# import java.io.IOException;
//# import java.io.OutputStream;
//# import util.Strconv;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
//# import util.StringUtils;
//# import client.ContactMessageList;
//# import javax.microedition.rms.RecordStoreException;
//# import ui.controls.form.MultiLine;
//# import ui.controls.form.SimpleString;
//# /**
//#  *
//#  * @author aqent
//#  */
//# public class HistoryStorage {
//#     public static final String STORAGE_PREFIX = "hist_";
//# 
//#     public HistoryStorage() { }
//#     private final static Contact c = null;
//# 
//#     private static CommandForm cmd = null;
//#     private static ContactMessageList messageList = null;
//# 
//#     public static void addText(Contact c, Msg message, ContactMessageList msgList)
//#     {
//#         if (midlet.BombusQD.cf.module_history==false) return;
//#         messageList = msgList;
//# 
//#         switch(HistoryConfig.historyTypeIndex) {
//#             case HistoryConfig.TYPE_RMS:
//#                 addRMSrecord(c, message, messageList.getRecordStore() );
//#                 break;
//#ifdef FILE_IO
//#             case HistoryConfig.TYPE_FS:
//#                 addFSMessage(message, c.bareJid);
//#                 break;
//#endif
//#         }
//#    }
//# 
//#if FILE_IO
//#     private static FileIO file;
//#     private static OutputStream os;
//#     private static byte[] bodyMessage;
//#     private static StringBuffer buf;
//#
//#     private static String createBody(Msg m) {
//#         //String fromName=midlet.BombusQD.sd.account.getUserName();
//#         //if (m.messageType!=Constants.MESSAGE_TYPE_OUT) fromName=m.from;
//#
//#         buf = new StringBuffer(0);
//#         switch(m.messageType){
//#              case Constants.MESSAGE_TYPE_IN:
//#                     buf.append('<')
//#                           .append('-');
//#                     break;
//#              case Constants.MESSAGE_TYPE_OUT:
//#                     buf.append('-')
//#                           .append('>');
//#                     break;
//#              case Constants.MESSAGE_TYPE_ERROR:
//#                     buf.append('!');
//#                     break;
//#              case Constants.MESSAGE_TYPE_SUBJ:
//#                     if (m.subject!=null) {
//#                        buf.append('*')
//#                              .append(m.subject)
//#                              .append('\r')
//#                              .append('\n');
//#                     }
//#                     break;
//#         }
//#         buf.append(' ')
//#            .append('[')
//#            .append(m.getDayTime())
//#            .append(']')
//#            .append(' ');
//#
//#         buf.append(m.body)
//#            .append('\r')
//#            .append('\n');
//#         /*
//#          <- [date gmt] message
//#          -> [date gmt] message
//#          *SUBJECT
//#          */
//#         return (HistoryConfig.getInstance().cp1251) ? Strconv.convUnicodeToCp1251(buf.toString()) : buf.toString();
//#     }
//#
//#     private static void addFSMessage(Msg m, String filename) {
//#        bodyMessage = createBody(m).getBytes();
//#ifdef DETRANSLIT
//#        filename = (HistoryConfig.getInstance().transliterateFilenames) ? DeTranslit.getInstance().translit(filename) : filename;
//#endif
//#
//#        buf = new StringBuffer(0);
//#        buf.append(HistoryConfig.getInstance().historyPath)
//#                    .append(StringUtils.replaceBadChars(filename))
//#                    .append(".txt");
//#        filename = buf.toString();
//#
//#        file = FileIO.createConnection(filename);
//#
//#         try {
//#             os = file.openOutputStream(0);
//#             if(bodyMessage.length > 0) os.write(bodyMessage);
//#             os.close();
//#             os.flush();
//#             file.close();
//#             file = null;
//#             os = null;
//#         } catch (IOException ex) {
//#             try {
//#                 file.close();
//#                 file = null;
//#             } catch (IOException ex2) { }
//#         }
//#         filename = null;
//#         bodyMessage = null;
//#         bodyMessage = new byte[0];
//#     }
//#
//#endif
//# 
//# 
//#     private static ByteArrayOutputStream baos = null;
//#     private static DataOutputStream das = null;
//#     private static byte[] buffer = null;
//#     private static byte[] textData = null;
//# 
//#     private final static int SAVE_RMS_STORE = 0;
//#     private final static int CLEAR_RMS_STORE = 1;
//#     private final static int CLOSE_RMS_STORE = 2;
//#     private final static int READ_ALL_DATA = 3;
//# 
//#     synchronized private static void addRMSrecord(Contact c, Msg message, RecordStore recordStore) {
//#         buffer = textData = null;
//#         int len;
//#         try {
//#               if(null == recordStore) {
//#                 String rName = getRSName(c.bareJid);
//#                 recordStore = RecordStore.openRecordStore(rName, true);
//#                 //messageList.getRmsData(SAVE_RMS_STORE, recordStore);//save
//#                 //rName = null;
//#               }
//#               baos = new ByteArrayOutputStream();
//#               das = new DataOutputStream(baos);
//# 
//#               das.writeByte(message.messageType);
//#               das.writeUTF(message.from);
//#               das.writeUTF(message.getDayTime());
//#               das.writeUTF(message.body);
//# 
//#             textData = baos.toByteArray();
//#             //len = textData.length;
//# 
//#             //buffer = new byte[len+1];
//#             //System.arraycopy(textData, 0, buffer, 1, len);
//#             recordStore.addRecord(textData, 0, textData.length);
//# 
//#          } catch (Exception ex) {
//#                  ex.printStackTrace();
//#          } finally {
//#                 try {
//#                     recordStore.closeRecordStore();
//#                     recordStore = null;
//#                 } catch (RecordStoreException e ) {
//# 
//#                 }
//#               try{
//#                  textData = buffer = null;
//#                  buffer = new byte[0];
//#                  textData = new byte[0];
//#                  if (das != null)  { das.close(); das = null; }
//#                  if (baos != null) { baos.close(); baos = null; }
//#               } catch (Exception e) { }
//#         }
//#     }
//# 
//# 
//#     public static RecordStore closeStore(RecordStore recordStore) {
//#        try {
//#            recordStore.closeRecordStore();
//#        } catch (Exception e) {}
//#        return null;
//#     }
//# 
//#     public static String getRSName(String bareJid) {
//#         String str = STORAGE_PREFIX + bareJid;
//#         if (str.length() > 30) {
//#             str = str.substring(0, 30);
//#         }
//#         return str;
//#     }
//# 
//# }
//#endif
