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
package history;

import client.Config;
import client.Constants;
import client.Contact;
import client.Msg;
import java.io.*;
import javax.microedition.rms.RecordStore;
//#if FILE_IO
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import util.StringUtils;
import client.ContactMessageList;
import javax.microedition.rms.RecordStoreException;
/**
 *
 * @author aqent
 */
public class HistoryStorage {
    public static final String STORAGE_PREFIX = "hist_";

    public HistoryStorage() { }

    private static ContactMessageList messageList = null;

    public static void addText(Contact c, Msg message, ContactMessageList msgList)
    {
        if (midlet.BombusQD.cf.module_history==false) return;
        messageList = msgList;

        switch(Config.historyTypeIndex) {
            case HistoryConfigForm.TYPE_RMS:
                addRMSrecord(c, message, messageList.getRecordStore() );
                break;
//#ifdef FILE_IO
            case HistoryConfigForm.TYPE_FS:
                addFSMessage(message, c.bareJid);
                break;
//#endif
        }
   }

//#if FILE_IO
    private static FileIO file;
    private static OutputStream os;

    private static String createBody(Msg m) {
        StringBuffer buf = new StringBuffer(0);
        switch (m.messageType) {
            case Constants.MESSAGE_TYPE_OUT:
                buf.append("->");
                break;
            case Constants.MESSAGE_TYPE_ERROR:
                buf.append("!");
                break;
            case Constants.MESSAGE_TYPE_SUBJ:
                if (m.subject != null) {
                    buf.append('*').append(m.subject).append("\r\n");
                    break;
                }
            // incoming
            default:
                buf.append("<-");
                break;
        }
        buf.append(" [").append(m.getDayTime()).append("] ");
        buf.append(m.body).append("\r\n");
        return buf.toString();
    }

    private static void addFSMessage(Msg m, String filename) {
        byte[] bodyMessage;
        try {
            bodyMessage = createBody(m).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            bodyMessage = createBody(m).getBytes();
        }
//#ifdef DETRANSLIT
//#        filename = (Config.transliterateFilenames) ? DeTranslit.getInstance().translit(filename) : filename;
//#endif

        StringBuffer buf = new StringBuffer(0);
        buf.append(Config.historyPath).append(StringUtils.replaceBadChars(filename)).append(".txt");

        file = FileIO.createConnection(buf.toString());

        try {
            os = file.openOutputStream(0);
            if (bodyMessage.length > 0) {
                os.write(bodyMessage);
            }
        } catch (IOException e) {
        } finally {
            try {
                os.close();
                file.close();
            } catch (IOException io) {
            }
        }
    }
//#endif

    private static ByteArrayOutputStream baos = null;
    private static DataOutputStream das = null;

    synchronized private static void addRMSrecord(Contact c, Msg message, RecordStore recordStore) {
        try {
              if(null == recordStore) {
                String rName = getRSName(c.bareJid);
                recordStore = RecordStore.openRecordStore(rName, true);
              }
              baos = new ByteArrayOutputStream();
              das = new DataOutputStream(baos);

              das.writeByte(message.messageType);
              das.writeUTF(message.from);
              das.writeUTF(message.getDayTime());
              das.writeUTF(message.body);

            byte[] textData = baos.toByteArray();

            recordStore.addRecord(textData, 0, textData.length);

         } catch (Exception ex) {
                 //ex.printStackTrace();
         } finally {
                try {
                    recordStore.closeRecordStore();
                    recordStore = null;
                } catch (RecordStoreException e ) {

                }
              try{
                 if (das != null)  { das.close(); das = null; }
                 if (baos != null) { baos.close(); baos = null; }
              } catch (Exception e) { }
        }
    }


    public static RecordStore closeStore(RecordStore recordStore) {
       try {
           recordStore.closeRecordStore();
       } catch (Exception e) {}
       return null;
    }

    public static String getRSName(String bareJid) {
        String str = STORAGE_PREFIX + bareJid;
        if (str.length() > 30) {
            str = str.substring(0, 30);
        }
        return str;
    }
}
//#endif
