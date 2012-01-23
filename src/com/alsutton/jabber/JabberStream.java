/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.alsutton.jabber;
import client.StaticData;
//#ifdef XML_CONSOLE
//# import console.xml.XMLList;
//#endif
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
//#if Android
//# import java.net.Socket;
//# import javax.net.ssl.SSLSocketFactory;
//# import javax.net.ssl.HttpsURLConnection;
//# import javax.net.ssl.SSLContext;
//# import javax.net.ssl.SSLSocket;
//#else
import javax.microedition.io.*;
//#endif
import xml.*;
import locale.SR;
import xmpp.XmppError;
import xmpp.XmppParser;
import xmpp.extensions.IqPing;

public class JabberStream extends XmppParser implements Runnable {

    private Utf8IOStream iostream;
    private boolean isConnected = true;
    /**
     * The dispatcher thread.
     */

    private JabberDataBlockDispatcher dispatcher;

    private String server; // for ping

    public boolean pingSent;

    public boolean loggedIn;

    private boolean xmppV1;

    private String sessionId;
//#if Android
//#     private Socket connection;
//#else
    private StreamConnection connection;
//#endif

    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */

    public JabberStream( String server, String host,  int port, String proxy, boolean  ssl) throws IOException {
        this.server=server;

        if (proxy==null) {
//#if Android
//#         if (ssl) {
//#            connection = SSLSocketFactory.getDefault().createSocket(host, port);
//#         } else 
//#            connection = new Socket(host, port);
//#else
            System.out.println((ssl?"ssl://":"socket://")+host + ":" + port);
            connection = (StreamConnection) Connector.open((ssl?"ssl://":"socket://")+host + ":" + port);
//#endif  
        } else {
//#if HTTPCONNECT
//#             connection = io.HttpProxyConnection.open(hostAddr, proxy);
//#elif HTTPPOLL
//#             connection = new io.HttpPollingConnection(hostAddr, proxy);
//#else
            throw new IllegalArgumentException ("no proxy supported");
//#endif
        }
        if (connection == null) {
            System.out.println("Null connection");
            throw new IOException("null connection");
        }
        iostream=new Utf8IOStream(connection, host, port);
        dispatcher = new JabberDataBlockDispatcher(this);

        new Thread(this).start();
    }

    public void initiateStream() throws IOException {
        StringBuffer header=new StringBuffer("<stream:stream to='" ).append( server ).append( "' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'");
        if (SR.get(SR.MS_XMLLANG)!=null) {
            header.append(" xml:lang='").append(SR.get(SR.MS_XMLLANG)).append('\'');
        }
        header.append( '>' );
        send(header.toString());
        header=null;
    }

    public boolean tagStart(String name, Vector attributes) {
        if (name.equals( "stream:stream" ) ) {
            sessionId = XMLParser.extractAttribute("id", attributes);
            String version=XMLParser.extractAttribute("version", attributes);
            xmppV1 = ("1.0".equals(version));

            dispatcher.broadcastBeginConversation();
            return false;
        }

        return super.tagStart(name, attributes);
    }

    public boolean isXmppV1() { return xmppV1; }

    public String getSessionId() { return sessionId; }

    public void tagEnd(String name) throws XMLException {
        if (currentBlock == null) {
            if (name.equals( "stream:stream" ) ) {
                //close();
                dispatcher.halt();
                iostream.close();
                if(!midlet.BombusQD.cf.nokiaReconnectHack) {
                    iostream=null;
                }
                throw new XMLException("Normal stream shutdown");
            }
            return;
        }

        if (currentBlock.getParent() == null) {
            if (currentBlock.getTagName().equals("stream:error")) {
                XmppError xe = XmppError.decodeStreamError(currentBlock);
                //close();
                dispatcher.halt();
                iostream.close();
                if(!midlet.BombusQD.cf.nokiaReconnectHack) {
                    iostream=null;
                }
                throw new XMLException("Stream error: "+xe.toString());
            }
        }
        super.tagEnd(name);
    }

    protected void dispatchXmppStanza(JabberDataBlock currentBlock) {
        ++StaticData.incPacketCount;
        dispatcher.broadcastJabberDataBlock( currentBlock );
    }

    public void startKeepAliveTask(){
        int keepAliveType=midlet.BombusQD.sd.account.getKeepAliveType();
        if (keepAliveType==0) return;
        int keepAlivePeriod=midlet.BombusQD.sd.account.getKeepAlivePeriod();

        if (keepAlive!=null) { keepAlive.destroyTask(); keepAlive=null; }

        keepAlive=new TimerTaskKeepAlive(keepAlivePeriod, keepAliveType);
    }

    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void coldreconnect(){
//#ifdef DEBUG
//#         System.out.println("cException in parser:");
//#endif
        dispatcher.broadcastTerminatedConnection(null);
    }

    public void updateTraffic(boolean in, int value) {
       if(null == iostream) return;
       iostream.updateTraffic(in,value);
    }


    public void run() {
        XMLParser parser = new XMLParser( this );
        byte cbuf[]=new byte[1024];

        try {
            int length=-1;
            while (isConnected && iostream!=null) {
                length=iostream.read(cbuf);
                if (0 == length) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {};
                    continue;
                }
                parser.parse(cbuf, length);
            }
        } catch( Exception e ) {
//#ifdef DEBUG
//#             System.out.println("rException in parser:");
//#endif
//#ifdef DEBUG_CONSOLE
//#             midlet.BombusQD.debug.add(" rException in parser" ,10);
//#endif
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
            dispatcher.broadcastTerminatedConnection(e);
        };
     }

    /**
     * Method to close the connection to the server and tell the listener
     * that the connection has been terminated.
     */

    public void close() {
        if (keepAlive!=null) keepAlive.destroyTask();

        try {

	    dispatcher.setJabberListener( null );
            //TODO: see FS#528
            try {  Thread.sleep(500); } catch (Exception e) {}
             send( "</stream:stream>" );
            int time=10;
            while (dispatcher.isActive()) {
                try {  Thread.sleep(500); } catch (Exception e) {}
                if ((--time)<0) break;
            }
             //connection.close();
        } catch( IOException e ) {
            // Ignore an IO Exceptions because they mean that the stream is
            // unavailable, which is irrelevant.
        } finally {
            if (dispatcher!=null) {
                dispatcher.halt();
                dispatcher.restart();
            }
	    if (iostream!=null) iostream.close();
             if(!midlet.BombusQD.cf.nokiaReconnectHack) {
               iostream=null;
             }
        }
    }

    /**
     * Method of sending data to the server.
     *
     * @param The data to send to the server.
     */
    public void sendKeepAlive(int type) throws IOException {
        switch (type){
            case 3:
                if (pingSent) {
                    dispatcher.broadcastTerminatedConnection(new Exception("Ping Timeout"));
                    throw new IOException("Ping Timeout");
                } else {
                    //System.out.println("Ping myself");
                    ping();
                }
                break;
             case 2:
                send("<iq/>");
                 break;
             case 1:
                 send(" ");
         }
     }

    private StringBuffer sendData;
    public void send( String data ) throws IOException {
        sendData = new StringBuffer(0);
        sendData.append(data);
        sendBuf(sendData);
    }

    public void sendBuf( StringBuffer data ) throws IOException {
        if (null == data) {
            return;
        }
        if (null != iostream) iostream.send(data);
//#ifdef XML_CONSOLE
//#         if (console.xml.XMLList.enabled) {
//#             addLog(data.toString(), 1);
//#         }
//#endif
        ++StaticData.outPacketCount;
        data = new StringBuffer(0); //Tishka17
    }


    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */

    private StringBuffer buf = new StringBuffer(0);

    public void send( JabberDataBlock block )  {
        try{
            //System.out.println("SEND: "+block.toString());
            buf = new StringBuffer(0);
            block.constructXML(buf);
            sendBuf(buf);
            block.destroy();
        } catch (Exception e) { }
    }

//#ifdef XML_CONSOLE
//#     public void addLog (String data, int type) {
//#         XMLList.getInstance().add(data, type);
//#    }
//#endif

    /**
     * Set the listener to this stream.
     */
    public void addBlockListener(JabberBlockListener listener) {
        if (null != dispatcher) dispatcher.addBlockListener(listener);
    }

    public void cancelBlockListener(JabberBlockListener listener) {
        if (null != dispatcher) dispatcher.cancelBlockListener(listener);
    }

    public void resetBlockListners() {
        if (null != dispatcher) dispatcher.resetBlockListners();
    }

    public void cancelBlockListenerByClass(Class removeClass) {
        if (null != dispatcher) dispatcher.cancelBlockListenerByClass(removeClass);
    }

    public void setJabberListener( JabberListener listener ) {
        if (null != dispatcher) dispatcher.setJabberListener( listener );
    }

    private void ping() {
        pingSent=true;
        send(IqPing.query(StaticData.getInstance().account.getServer(), "ping"));
    }

//#if ZLIB
    public void setZlibCompression() {
        iostream.setStreamCompression();
    }

    public String getPocketsStats() {
        if(null == iostream) return "";
        return iostream.getPocketsStats();
    }
//#endif

    public String getStreamStats() {
        if(null == iostream) return "";
        return iostream.getStreamStats();
    }
    public String getStreamStatsBar() {
        if(null == iostream) return "";
        return iostream.getStreamStatsBar();
    }

//#if TLS || Android
//#    public void setTls() throws IOException {
//#        iostream.setTls();
//#    }
//#endif  

    public String getConnectionData() {
        if(null == iostream) return "";
        return iostream.getConnectionData();
    }

    public long getBytes() {
        if(null == iostream) return 0;
        return iostream.getBytes();
    }

    private TimerTaskKeepAlive keepAlive;

     private class TimerTaskKeepAlive extends TimerTask{
        private Timer t;

        private int type;
        public TimerTaskKeepAlive(int periodSeconds, int type){
            t=new Timer();
            this.type=type;
            long periodRun=periodSeconds*1000; // milliseconds
            t.schedule(this, periodRun, periodRun);
        }

        public void run() {
            try {
                 if (loggedIn)
                     sendKeepAlive(type);
            } catch (Exception e) {
               if (isConnected) dispatcher.broadcastTerminatedConnection(e);
            }
        }

        public void destroyTask(){
            if (t!=null){
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
}



