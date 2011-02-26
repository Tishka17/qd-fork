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

package history;
import client.Constants;
import client.Contact;
import client.Msg;
import client.CommandForm;
import java.io.*;
import ui.controls.form.CheckBox;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.rms.RecordStore;
//#if FILE_IO
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
import util.Strconv;
//#endif
import util.DeTranslit;
import util.StringUtils;
import client.ContactMessageList;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
/**
 *
 * @author aqent
 */
public class HistoryStorage {
    
    /** Creates a new instance of HistoryStorage */
    public HistoryStorage() { }
    private final static Contact c = null;
    private final static String store = "store_";//link this with selfjid
    private static CommandForm cmd = null;
    private static ContactMessageList messageList = null;

    public static void addText(Contact c, Msg message, ContactMessageList msgList)
    {
        if (midlet.BombusQD.cf.module_history==false) return;
        messageList = msgList;
        switch(HistoryConfig.getInstance().historyTypeIndex) {
            case 0:
                //System.out.println("add to->RMS");
                addRMSrecord(c, message, messageList.getRecordStore() );
                break;
            case 1:
                //System.out.println("add to->FS");
                addFSMessage(message, c.bareJid);
                if(true) return;
                break;
            case 2:
                //System.out.println("add to->SERVER");
                if(true) return;
                break;
        }
   }
    
   public static void loadData(Contact c, RecordStore recordStore) {
       //#ifdef CONSOLE
//#         midlet.BombusQD.debug.add("loadRMSData " + c + "/" + recordStore,10);
        //#endif
        if (midlet.BombusQD.cf.module_history==false) return;
        switch(HistoryConfig.getInstance().historyTypeIndex) {
            case 0:
                startTimer(recordStore, c, 50);
                //System.out.println("get->RMS");
                break;
            case 1:
                //System.out.println("get->FS");
                if(true) return;
                break;
            case 2:
                //System.out.println("get->SERVER");
                if(true) return;
                break;
        }
   }
    
    
//#if FILE_IO
    private static FileIO file;
    private static OutputStream os;
    private static byte[] bodyMessage;
    private static StringBuffer buf;
    
    private static String createBody(Msg m) {
        //String fromName=midlet.BombusQD.sd.account.getUserName();
        //if (m.messageType!=Constants.MESSAGE_TYPE_OUT) fromName=m.from;

        buf = new StringBuffer(0);
        switch(m.messageType){
             case Constants.MESSAGE_TYPE_IN:
                    buf.append('<')
                          .append('-');
                    break;
             case Constants.MESSAGE_TYPE_OUT:
                    buf.append('-')
                          .append('>');
                    break;
             case Constants.MESSAGE_TYPE_ERROR:
                    buf.append('!');
                    break;
             case Constants.MESSAGE_TYPE_SUBJ:
                    if (m.subject!=null) {
                       buf.append('*')
                             .append(m.subject)
                             .append('\r')
                             .append('\n');
                    }
                    break;
        }
        buf.append(' ')
           .append('[')
           .append(m.getDayTime())
           .append(']')
           .append(' ');
            
        buf.append(m.body)
           .append('\r')
           .append('\n');
        /*
         <- [date gmt] message
         -> [date gmt] message
         *SUBJECT
         */
        return (HistoryConfig.getInstance().cp1251) ? Strconv.convUnicodeToCp1251(buf.toString()) : buf.toString();
    }
    
    private static void addFSMessage(Msg m, String filename) {
       bodyMessage = createBody(m).getBytes();
       filename = (HistoryConfig.getInstance().transliterateFilenames) ? DeTranslit.getInstance().translit(filename) : filename;
       
       buf = new StringBuffer(0);
       buf.append(HistoryConfig.getInstance().historyPath)
                   .append(StringUtils.replaceBadChars(filename))
                   .append(".txt");
       filename = buf.toString();
       
       file = FileIO.createConnection(filename);

        try {
            os = file.openOutputStream(0);
            if(bodyMessage.length > 0) os.write(bodyMessage);
            os.close();
            os.flush();
            file.close();
            file = null;
            os = null;
        } catch (IOException ex) {
            try {
                file.close();
                file = null;
            } catch (IOException ex2) { }
        }
        filename = null;
        bodyMessage = null;
        bodyMessage = new byte[0];
    }
    
//#endif
    
    
    private static ByteArrayOutputStream baos = null;
    private static DataOutputStream das = null;
    private static byte[] buffer = null;
    private static byte[] textData = null;
    
    private final static int SAVE_RMS_STORE = 0;
    private final static int CLEAR_RMS_STORE = 1;
    private final static int CLOSE_RMS_STORE = 2;
    private final static int READ_ALL_DATA = 3;
    
    synchronized private static void addRMSrecord(Contact c, Msg message, RecordStore recordStore) {
        buffer = textData = null;
        int len;
        try {
              if(null == recordStore) {
                            String rName = getRSName(c.bareJid);
                            if(rName.length()>30) rName = rName.substring(0,30);
                            recordStore = RecordStore.openRecordStore(rName, true);
                            messageList.getRmsData(SAVE_RMS_STORE, recordStore);//save
                            rName = null;
              }
              baos = new ByteArrayOutputStream();
              das = new DataOutputStream(baos);
              das.writeUTF(message.getDayTime());
              das.writeUTF(message.body);
                        
            textData = baos.toByteArray();
            len = textData.length;

            buffer = new byte[len+1];
            System.arraycopy(textData, 0, buffer, 1, len);
            recordStore.addRecord(buffer, 0, buffer.length);

         } catch (Exception ex) {
                 ex.printStackTrace();
         } finally {
                  if (recordStore != null) {
                    messageList.getRmsData(CLOSE_RMS_STORE, recordStore);
                    recordStore = null;
                  }
                  try{
                     textData = buffer = null;
                     buffer = new byte[0];
                     textData = new byte[0];
                     if (dis != null)  { das.close(); das = null; }
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
    
    public static RecordStore clearRecordStore(RecordStore recordStore) {
        try {
            int size = recordStore.getNumRecords();
            if (size > 0) {
              for (int i = 1; i <= size; ++i) {
                 recordStore.deleteRecord(i);
              }
            } 
            recordStore.closeRecordStore();
            return null;
         } catch (Exception e) { 
             e.printStackTrace(); return null;
         }
    }

    private static int getRecordCount(RecordStore recordStore) {	
       try {
         return recordStore.getNumRecords();
       } catch (Exception e) {  return -1; }
    }
    
    public static String getRSName(String bareJid) {
       return store + bareJid;
    }
    

    
    private static Timer timer;
    private static LoadMessages load;
    private static void startTimer(RecordStore rs, Contact c, int repeatTime) {
       cmd = null;
       cmd = new CommandForm(midlet.BombusQD.getInstance().display, midlet.BombusQD.sd.roster, 6 , "" , null, null);
       if (timer != null) {
           timer.cancel();
           timer = null;
       }
       if ( timer == null) {
           timer = new Timer();
           load = new LoadMessages();
           load.set(rs, c);
           timer.schedule( load, 0, repeatTime );
       }
    }
   
    private static void stopTimer() {
      if ( timer != null ) {
          timer.cancel();
          timer = null;
          load = null;
      }
    }
  
    private static ByteArrayInputStream bais = null;
    private static DataInputStream dis = null;
    private static StringBuffer sb;
    private static class LoadMessages extends TimerTask {
      int posRecord;
      RecordStore recordStore;
      Contact c;
      long timeS,timeE;
      
      public void set(RecordStore rs, Contact c){
        posRecord = 0;  
        this.recordStore = rs;
        this.c = c;
      }
      
      public void run () {
         if(posRecord == 0) {
           cmd.setParentView(c);
           timeS = System.currentTimeMillis();
         }
         int size = 0;
         int i;
         try {
              byte[] msgData = null;
              size = recordStore.getNumRecords();
              try {
                   for (i=0; i < 5; ++i) {
                    posRecord++;
                    msgData = recordStore.getRecord(posRecord);
                          bais = new ByteArrayInputStream(msgData, 1, msgData.length - 1);
                          dis = new DataInputStream(bais);

                          MultiLine item = new MultiLine(dis.readUTF(), dis.readUTF(), cmd.superWidth);
                          item.selectable = true;

                          cmd.addControl(item);
                   }
                   msgData = null;
              } catch (Exception e) {
                posRecord = size;
              }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(posRecord == size) {
                   timeE = System.currentTimeMillis();
                   stopTimer();
                   if (size == 0) {
                       SimpleString item = new SimpleString("Empty history!");
                       item.setSelectable(true);
                       
                       cmd.addControl(item);
                   }

                  if (recordStore != null) {
                    messageList.getRmsData(CLOSE_RMS_STORE, recordStore);
                    recordStore = null;
                  }
                    try{
                         if (dis != null) { das.close(); das = null; }
                         if (baos != null) { baos.close(); baos = null; }
                         if (bais != null) { bais.close(); bais = null; }
                    } catch (Exception e) { }
                }
            }
      }
    }
}
