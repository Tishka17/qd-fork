/*
 * TransferTask.java
 *
 * Created on 28.10.2006, 17:00
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 */

//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
package io.file.transfer;

import client.Msg;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import images.RosterIcons;
import io.file.FileIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import colors.ColorTheme;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import midlet.BombusQD;
import ui.IconTextElement;
import util.Strconv;
import xmpp.XmppError;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class TransferTask
        extends IconTextElement
        implements Runnable
{
    public final static int COMPLETE=1;
    public final static int PROGRESS=3;
    public final static int ERROR=4;
    public final static int NONE=5;
    public final static int HANDSHAKE=6;
    public final static int IN_ASK=7;

    private int state=NONE;
    private boolean sending;
    boolean showEvent;
    boolean isBytes;
    byte[] bytes;

    public String jid;
    String id;
    String sid;
    public String fileName;
    public String description;
    String errMsg;

    public int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
    private InputStream is;

    private int method;

    long started;
    long finished;
    
    public static byte METHOD_IBB=0;
    public static byte METHOD_ASPRO=1;
    public static byte METHOD_AUTO=127;

    /** Creates TransferTask for incoming file */
    public TransferTask(String jid, String id, String sid, String name, String description, int size, int method) {
        super(RosterIcons.getInstance());
        state=IN_ASK;
        showEvent=true;
        this.jid=jid;
        this.id=id;
        this.sid=sid;
        this.fileName=name;
        this.description=description;
        this.fileSize=size;
        this.method=method;
    }

    /**
     * Sending constructor
     */
    public TransferTask(String jid, String sid, String fileName, String description, boolean isBytes, byte[] bytes, int method) {
        super(RosterIcons.getInstance());
        state=HANDSHAKE;
        sending=true;
        this.method=method;
        this.jid=jid;
        this.sid=sid;
        this.fileName=fileName.substring( fileName.lastIndexOf('/')+1 );
        this.description=description;

        this.isBytes=isBytes;
        this.bytes=bytes;

        if (!isBytes) {
            try {
                file=FileIO.createConnection(fileName);
                is=file.openInputStream();

                fileSize=(int)file.fileSize();
            } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
                state=ERROR;
                errMsg=SR.get(SR.MS_CANT_OPEN_FILE);
                showEvent=true;
            }
        } else {
            is=new ByteArrayInputStream(bytes);
            fileSize=bytes.length;

        }
    }

    public int getImageIndex() { return state; }

    public int getColor() { return (sending)? ColorTheme.getColor(ColorTheme.MESSAGE_OUT) : ColorTheme.getColor(ColorTheme.MESSAGE_IN); }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int xpgs=(g.getClipWidth()/3)*2;
        int pgsz=g.getClipWidth()-xpgs-4;
        int filled=(fileSize==0)? 0 : (pgsz*filePos)/fileSize;

        int oldColor=g.getColor();
        g.setColor(0xffffff);

        g.fillRect(xpgs, 3, pgsz, getVHeight()-6);
        g.setColor(0x668866);
        g.drawRect(xpgs, 3, pgsz, getVHeight()-6);
        g.fillRect(xpgs, 3, filled, getVHeight()-6);
        g.setColor(oldColor);

        super.drawItem(view, g, ofs, sel);
        showEvent=false;
    }

    public String toString() { return fileName; }

    void decline() {
        finished=System.currentTimeMillis();
        JabberDataBlock reject=new Iq(jid, Iq.TYPE_ERROR, id);
        reject.addChild(new XmppError(XmppError.NOT_ALLOWED, "declined by user"));
        TransferDispatcher.getInstance().send(reject, true);

        state=ERROR;
        errMsg=SR.get(SR.MS_REJECTED);
        showEvent=true;
    }

    void accept() {
        started=System.currentTimeMillis();
        try {
            file=FileIO.createConnection(filePath+fileName);
            os=file.openOutputStream();
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
            decline();
            return;
        }
        JabberDataBlock accept=new Iq(jid, Iq.TYPE_RESULT, id);

        JabberDataBlock si=accept.addChildNs("si", "http://jabber.org/protocol/si");

        JabberDataBlock feature=si.addChildNs("feature", "http://jabber.org/protocol/feature-neg");

        JabberDataBlock x=feature.addChildNs("x", "jabber:x:data");
        x.setTypeAttribute("submit");

        JabberDataBlock field=x.addChild("field", null);
        field.setAttribute("var","stream-method");
        field.addChild("value", "http://jabber.org/protocol/ibb");

        TransferDispatcher.getInstance().send(accept, true);
        state=HANDSHAKE;
    }

    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
            state=PROGRESS;
        } catch (IOException ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
            state=ERROR;
            errMsg="Write error";
            showEvent=true;
            //todo: terminate transfer
        }
    }

    int readFile(byte b[]) {
        try {
            int len=is.read(b);
            if (len<0) len=0;
            filePos+=len;
            state=PROGRESS;

            return len;
        } catch (IOException ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
            state=ERROR;
            errMsg="Read error";
            showEvent=true;
            //todo: terminate transfer
            return 0;
        }
    }

    public boolean isAcceptWaiting() { return state==IN_ASK; }

    void closeFile() {
        finished=System.currentTimeMillis();
        try {
            if (os!=null)
                os.close();
            if (is!=null)
                is.close();
            file.close();
            if (state!=ERROR) state=COMPLETE;
        } catch (Exception ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
            errMsg="File close error";
            state=ERROR;
        }
        file=null;
        is=null;
        os=null;
        showEvent=true;
    }

    void sendInit() {
        
        if (method==METHOD_ASPRO) {
            startTransfer();
            return;
        }
        
        started=System.currentTimeMillis();
        if (state==ERROR) return;

        JabberDataBlock iq=new Iq(jid, Iq.TYPE_SET, sid);

        JabberDataBlock si=iq.addChildNs("si", "http://jabber.org/protocol/si");
        si.setAttribute("id",sid);
        si.setAttribute("mime-type","text/plain");
        si.setAttribute("profile", "http://jabber.org/protocol/si/profile/file-transfer");

        JabberDataBlock file=si.addChildNs("file", "http://jabber.org/protocol/si/profile/file-transfer");
        file.setAttribute("name", fileName);
        file.setAttribute("size", String.valueOf(fileSize));

        file.addChild("desc", description);

        JabberDataBlock feature=si.addChildNs("feature", "http://jabber.org/protocol/feature-neg");

        JabberDataBlock x=feature.addChildNs("x", "jabber:x:data");
        x.setTypeAttribute("form");

        JabberDataBlock field=x.addChild("field", null);
        field.setTypeAttribute("list-single");
        field.setAttribute("var", "stream-method");

        field.addChild("option", null).addChild("value", "http://jabber.org/protocol/ibb");

        TransferDispatcher.getInstance().send(iq, true);
    }

    void initIBB() {
        JabberDataBlock iq=new Iq(jid, Iq.TYPE_SET, sid);
        JabberDataBlock open=iq.addChildNs("open", "http://jabber.org/protocol/ibb");
        open.setAttribute("sid", sid);
        open.setAttribute("block-size","2048");
        TransferDispatcher.getInstance().send(iq, false);
    }

    
    private void runAspro() {
        if (fileSize==0) {
            state = ERROR;
            errMsg = "Cannot send empty file";
            return;
        }
        StreamConnection sc;
        try {
            sc = (StreamConnection) Connector.open("socket://files.jimm.net.ru:2000");
        }
        catch (IOException e) {
//#if DEBUG
//#             e.printStackTrace();
//#endif
            state = ERROR;
            return;
        }
        state = PROGRESS;
        try {        
            DataInputStream dis = sc.openDataInputStream();
            DataOutputStream dos = sc.openDataOutputStream();    
            dos.writeByte(0);
            dos.writeByte(1);
            
            byte []str=fileName.getBytes("utf-8");
            int s=str.length;
            dos.writeByte(s>>8);
            dos.writeByte(s&255);
            dos.write(str);            
            str=description.getBytes("utf-8");
            s=str.length;
            dos.writeByte(s>>8);
            dos.writeByte(s&255);
            dos.write(str);
            s=str.length;
            dos.writeByte(0);
            dos.writeByte(8);
            dos.write("bombusqd".getBytes());
            dos.writeByte(((fileSize & 0xFF000000) >> 24) & 0xFF);
            dos.writeByte(((fileSize & 0x00FF0000) >> 16) & 0xFF);
            dos.writeByte(((fileSize & 0x0000FF00) >> 8)  & 0xFF);
            dos.writeByte(  fileSize & 0x000000FF);
            dos.flush(); 
            byte[] buffer = new byte[4*1024];
            int counter = fileSize;
            System.out.println("\nFileSize: "+fileSize);
            System.out.println("Socket: "+sc.toString()+" "+sc.getClass());
            while (counter > 0 && state==PROGRESS) {
                int read = readFile(buffer);
                dos.write(buffer, 0, read);
                counter -= read;
                if (counter != 0) dos.flush();
            }
            dos.flush();

            int length = dis.read();
            if (-1 == length) {
                throw new IOException("Cannot get url");
            }
            buffer[0]=0;
            buffer[1]=(byte)(length&255);//we think than length is less than 127
            dis.read(buffer, 0, length);
 
            // Send info about file
            StringBuffer messText = new StringBuffer();
            messText.append(SR.get(SR.MS_FILE))
                    .append(": ")
                    .append(fileName)
                    .append('\n')
                    .append(SR.get(SR.MS_FILE_SIZE))
                    .append(": ")
                    .append(String.valueOf(fileSize))
                    .append("\nURL: ");
            for (int i=0;i<length;i++) messText.append((char)buffer[i]);
            messText.append('\n').append(SR.get(SR.MS_DESCRIPTION))
                    .append(": ")
                    .append(description);
//#if DEBUG
//#             System.out.println(messText.toString());
//#endif        
            String body = messText.toString();
            JabberDataBlock mess=new Message(jid, body, null, false);
            TransferDispatcher.getInstance().send(mess, true);
            BombusQD.sd.roster.getContact(jid, true).getMessageList().addMessage(new Msg(Msg.SYSTEM, null, body));
            state = COMPLETE;
            
        } catch (Exception e) {
            state=ERROR;
            errMsg = e.toString();
//#if DEBUG
//#             e.printStackTrace();
//#endif
        }
        try {
            sc.close();
        } catch (IOException e){
        }
    }
    
    public void run() {
        if (method==METHOD_ASPRO) {
            runAspro();
            return;
        }
        
        byte buf[]=new byte[2048];
        int seq=0;
        try {
            while (true) {
                int sz=readFile(buf);
                if (sz==0) break;

                JabberDataBlock msg=new Message(jid);

                JabberDataBlock data=msg.addChildNs("data", "http://jabber.org/protocol/ibb");
                data.setAttribute("sid", sid);
                data.setAttribute("seq", String.valueOf(seq));   seq++;
                data.setText(Strconv.toBase64(buf, sz));

                JabberDataBlock amp=msg.addChildNs("amp", "http://jabber.org/protocol/amp");

                JabberDataBlock rule;

                rule=amp.addChild("rule", null);
                rule.setAttribute("condition", "deliver-at");
                rule.setAttribute("value", "stored");
                rule.setAttribute("action", "error");

                rule=amp.addChild("rule", null);
                rule.setAttribute("condition", "match-resource");
                rule.setAttribute("value", "exact");
                rule.setAttribute("action", "error");

                TransferDispatcher.getInstance().send(msg, false);
                TransferDispatcher.getInstance().repaintNotify();

                Thread.sleep( 1500L ); //shaping traffic
            }
        } catch (Exception e) { /*null pointer exception if terminated*/}
        closeFile();
        JabberDataBlock iq=new Iq(jid, Iq.TYPE_SET, "close");
        JabberDataBlock close=iq.addChildNs("close", "http://jabber.org/protocol/ibb");
        close.setAttribute("sid", sid);
        TransferDispatcher.getInstance().send(iq, false);
        TransferDispatcher.getInstance().eventNotify();
    }

    void startTransfer() {
        new Thread(this).start();
    }

    public boolean isStopped() {
        return (state==COMPLETE || state==ERROR);

    }

    boolean isStarted() {
        return (state!=NONE && state!=IN_ASK);
    }

    public void cancel() {
        if (isStopped()) return;
        state=ERROR;
        errMsg="Canceled";
        if (!isBytes)
            closeFile();
        else
            bytes=null;
    }
}
//#endif
//#endif
