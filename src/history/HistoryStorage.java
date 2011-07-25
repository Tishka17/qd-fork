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
import client.Contact;
import client.Msg;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
//#if FILE_IO
import io.file.FileIO;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreNotOpenException;
import util.StringUtils;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif

/**
 *
 * @author aqent
 */

public class HistoryStorage {
    public static final String STORAGE_PREFIX = "hist_";

    private HistoryStorage() {}

    public static void addText(Contact c, Msg message) {
        if (!Config.module_history) {
            return;
        }

        switch(Config.historyTypeIndex) {
            case Config.HISTORY_RMS:
                addRMSrecord(c, message);
                break;
//#ifdef FILE_IO
            case Config.HISTORY_FS:
                addFSMessage(message, c.bareJid);
                break;
//#endif
        }
   }

//#if FILE_IO
    public static byte[] msg2byte(Msg m) {
        StringBuffer buf = new StringBuffer(0);
        switch (m.getType()) {
            case Msg.OUTGOING:
                buf.append("->");
                break;
            case Msg.ERROR:
                buf.append('!');
                break;
            case Msg.SUBJECT:
                if (m.getSubject() != null) {
                    buf.append('*').append(m.getSubject()).append("\r\n");
                }
                break;
            default:
                buf.append("<-");
                break;
        }
        buf.append(" [").append(m.getDayTime()).append("] ");
        buf.append(StringUtils.replaceNickTags(m.getBody())).append("\r\n");

        byte[] arr;
        try {
            arr = buf.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            arr = buf.toString().getBytes();
        }
        return arr;
    }

    private static void addFSMessage(Msg m, String filename) {
//#ifdef DETRANSLIT
//#        filename = (Config.getInstance().autoDeTranslit) ? DeTranslit.translit(filename) : filename;
//#endif

        StringBuffer buf = new StringBuffer(0);
        buf.append(Config.historyPath).append(StringUtils.replaceBadChars(filename)).append(".txt");

        FileIO file = FileIO.createConnection(buf.toString());
        if (file == null) {
            return;
        }
        OutputStream os = null;

        try {
            os = file.openOutputStream(0);

            byte arr[] = msg2byte(m);
            if (arr.length > 0) {
                os.write(arr);
            }
        } catch (IOException e) {
//#ifdef DEBUG
//#              e.printStackTrace();
//#endif
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                file.close();
            } catch (IOException io) {
//#ifdef DEBUG
//#                 io.printStackTrace();
//#endif
            }
        }
    }
//#endif

    synchronized private static void addRMSrecord(Contact c, Msg message) {
        RecordStore recordStore = null;
        ByteArrayOutputStream baos = null;
        DataOutputStream das = null;
        try {
              String rName = getRSName(c.bareJid);
              recordStore = RecordStore.openRecordStore(rName, true);
              baos = new ByteArrayOutputStream();
              das = new DataOutputStream(baos);

              das.writeByte(message.getType());
              das.writeUTF(message.getFrom());
              das.writeLong(message.dateGmt);
              das.writeUTF(message.getBody());

            byte[] textData = baos.toByteArray();

            recordStore.addRecord(textData, 0, textData.length);

         } catch (Exception ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
         } finally {
                try {
                    if (recordStore != null) {
                        recordStore.closeRecordStore();
                        recordStore = null;
                    }
                } catch (RecordStoreException e ) {
//#ifdef DEBUG
//#                     e.printStackTrace();
//#endif
                }
              try{
                  if (das != null) {
                      das.close();
                      das = null;
                  }
                  if (baos != null) {
                      baos.close();
                      baos = null;
                  }
              } catch (Exception e) {
//#ifdef DEBUG
//#                     e.printStackTrace();
//#endif
              }
        }
    }
    
    public static Vector getLastMessages(Contact contact, int count) {
        RecordStore store = null;        
        Vector vector = null;
        
        try {
            store = RecordStore.openRecordStore(getRSName(contact.bareJid), true);
            vector = new Vector(count);
            int size = store.getNumRecords();

            for (int i = size - count + 1; i <= size; ++i) {
                Msg msg = readMessage(store, i);
                if (msg != null) {
                    msg.setType(Msg.HISTORY);
                    vector.addElement(msg);
                }
            }
        } catch (RecordStoreException e) {
        } finally {
            if (store != null) {
                try {
                    store.closeRecordStore();
                } catch (RecordStoreException ex) {}
            }
        }
        return vector;
    }

    private static final int MAX_RECORDNAME_LEN = 32;

    public static String getRSName(String bareJid) {
        String str = STORAGE_PREFIX + bareJid;
        if (str.length() > MAX_RECORDNAME_LEN) {
            str = str.substring(0, MAX_RECORDNAME_LEN);
        }
        return str;
    }
    
    public static Msg readMessage(RecordStore store, int id) 
            throws RecordStoreNotOpenException, RecordStoreException {
        byte buf[];

        try {
            buf = store.getRecord(id);
        } catch (InvalidRecordIDException e) {
//#ifdef DEBUG
//#                         System.out.println(id + " record doesn't exist, skipping...");
//#endif
            return null;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        DataInputStream dis = new DataInputStream(bais);
        try {
            byte msgtype = dis.readByte();
            String from = dis.readUTF();
            long date = dis.readLong();
            String text = dis.readUTF();
            
            Msg msg = new Msg(msgtype, from, null, text);
            msg.setDayTime(date);
            return msg;
        } catch (IOException e) {}
        
        return null;
    }
}
//#endif
