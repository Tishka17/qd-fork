/*
 * Utf8IOStream.java
 *
 * Created on 18.12.2005, 0:52
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

package io;

//#if ZLIB
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
//#endif
//#ifdef TLS
//# import bwmorg.bouncycastle.crypto.tls.TlsProtocolHandler;
//# import bwmorg.bouncycastle.crypto.tls.AlwaysValidVerifyer;
//#endif
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//#if Android
//# import java.net.Socket;
//#else
import javax.microedition.io.*;
//#endif
import util.Strconv;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream {

//#if Android
//#     private Socket connection;
//#else    
    private StreamConnection connection;
//#endif   
    private InputStream inpStream;
    private OutputStream outStream;

    private static int bytesRecv;
    private static int bytesSent;
    private boolean isZlib = false;

//#if TLS
//#     private TlsProtocolHandler tls;
//#     public boolean tlsExclusive = false;
 //#endif    

//#if (ZLIB)
     public void setStreamCompression(){
        inpStream = new ZInputStream(inpStream);
        outStream = new ZOutputStream(outStream, JZlib.Z_DEFAULT_COMPRESSION);
         ((ZOutputStream)outStream).setFlushMode(JZlib.Z_SYNC_FLUSH);
        this.isZlib = true;
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add("::ZLIB->" + this.isZlib,10);
//#endif
     }
//#endif
//#if Android
//# /** Creates a new instance of Utf8IOStream */
//#     public Utf8IOStream(Socket connection) throws IOException {
//#         this.connection = connection;
//#         try {
//#             connection.setKeepAlive(true);
//#             connection.setSoLinger(true, 300);            
//#         } catch (Exception e) {
//#             e.printStackTrace();
//#         }
//#         
//#         inpStream = connection.getInputStream();
//#         outStream = connection.getOutputStream();
//#         
//#         
//#     }    
//#else    
    /** Creates a new instance of Utf8IOStream */
    public Utf8IOStream(StreamConnection connection) throws IOException {
	this.connection=connection;
        try {
            SocketConnection sc=(SocketConnection)connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 1);
            sc.setSocketOption(SocketConnection.LINGER, 300);
        } catch (Exception e) {}

	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();

    }
//#endif
//#if (ZLIB)
    public long countPocketsSend=0;
//#endif

    public void send( StringBuffer data ) throws IOException {
//#if (ZLIB)
	countPocketsSend++;
//#endif
	synchronized (outStream) {
            byte[] bytes = Strconv.stringToByteArray(data.toString());
	    outStream.write(bytes);
	    outStream.flush();
            bytes=null;
            bytes=new byte[0];

            if(isZlib == false){
              addOutTraffic(avail);
              updateTraffic(false, 0);
            }
	}
//#if (XML_STREAM_DEBUG)
//#         System.out.println(">> "+data);
//#endif
    }

    int avail=0;
    int lenbuf=0;

    public int read(byte buf[]) throws IOException {
//#ifdef TLS
//#         if (tlsExclusive)
//#             return 0;
//#endif     
        avail=inpStream.read(buf, 0, lenbuf);
//#if (XML_STREAM_DEBUG)
//# 	System.out.println("<< "+new String(buf, 0, avail));
//#endif
        if(isZlib == false){
            addInTraffic(avail);
            updateTraffic(false, 0);
        }
        return avail;
    }


    public void updateTraffic(boolean in, int value) {
        if(isZlib) {
           if(in)
               addInTraffic(value);
           else
               addOutTraffic(value);
           midlet.BombusQD.sd.traffic = bytesSent + bytesRecv;
        } else {
          midlet.BombusQD.sd.traffic = getBytes();
        }
        midlet.BombusQD.sd.updateTrafficOut();
     }

//#if TLS
//#     public void setTls() throws IOException {
//#         tlsExclusive=true;
//#         tls=new TlsProtocolHandler(inpStream, outStream);
//#         tls.connect(new AlwaysValidVerifyer());
//#         inpStream=tls.getTlsInputStream();
//#         outStream=tls.getTlsOuputStream();
//#         tlsExclusive=false;
//#     }
//#endif 

     private static final int TCP_SERVICEINFO_IN_PROCENT = 80;

     private static int getBytes(int bytes){
         return bytes + (bytes * TCP_SERVICEINFO_IN_PROCENT)/100;
     }

     public static void addInTraffic(int bytes) { bytesRecv += getBytes(bytes); }
     public static void addOutTraffic(int bytes) { bytesSent += getBytes(bytes); }


    public void close() {
         bytesSent = 0;
         bytesRecv = 0;
 	try {
//#ifdef ZLIB
             boolean outZ = (outStream instanceof ZOutputStream);
//#ifdef DEBUG_CONSOLE
//#              midlet.BombusQD.debug.add("::CLOSE_OUT_ZLIB->"  + outZ,10);
//#endif
             if(outZ)
                 ((ZOutputStream)outStream).close();
             else
//#endif
		 outStream.close();
         } catch (Exception e) {} finally {  outStream = null; }
 	try {
//#ifdef ZLIB
             boolean inZ = (inpStream instanceof ZInputStream);
//#ifdef DEBUG_CONSOLE
//#              midlet.BombusQD.debug.add("::CLOSE_IN_ZLIB->" +  inZ,10);
//#endif
             if(inZ)
                 ((ZInputStream)inpStream).close();
             else
//#endif
		 inpStream.close();
         } catch (Exception e) {} finally {  inpStream = null; }
    }

//#if ZLIB
    private StringBuffer stats;

    public String getPocketsStats() {
        return Long.toString(countPocketsSend);
    }

    public String getStreamStatsBar() { //for info panel
        stats = new StringBuffer(0);
        try {
            if (isZlib) {
                ZInputStream z = (ZInputStream) inpStream;
		    ZOutputStream zo = (ZOutputStream) outStream;

                String ratio=Long.toString((10*(bytesSent+bytesRecv)/(zo.getTotalOut()+z.getTotalIn())));
                int dotpos=ratio.length()-1;

                stats.append(" (");
                if(0 == dotpos)
                    stats.append('0');
                else
                    stats.append(ratio.substring(0, dotpos));
                stats.append('.')
                 .append(ratio.substring(dotpos))
                 .append("x)");
                ratio=null;
            }else{
               return "";
            }
        } catch (Exception e) {
            return "";
        }
        return stats.toString();
    }

    public String getStreamStats() { //for stats window
        stats = new StringBuffer(0);
        try {
            if (isZlib) {
                ZInputStream z = (ZInputStream) inpStream;
                ZOutputStream zo = (ZOutputStream) outStream;
                stats.append("ZLib:\nin: "); appendZlibStats(stats, z.getTotalIn(), bytesRecv, true);
                stats.append("\nout: "); appendZlibStats(stats, zo.getTotalOut(), bytesSent, false);
            } else {
              stats.append("\nin: ")
                  .append(bytesRecv)
                  .append("\nout: ")
                  .append(bytesSent);
            }
        } catch (Exception e) {
            return "";
        }
        return stats.toString();
    }

    private void appendZlibStats(StringBuffer s, long packed, long unpacked, boolean read){
        s.append(packed).append(read?'>':'<').append(unpacked);
        String ratio = Long.toString((10*unpacked)/packed);
        int dotpos=ratio.length()-1;
        s.append(" (");
        if(0 == dotpos)
            s.append('0');
        else
            s.append(ratio.substring(0, dotpos));
        s.append('.')
         .append(ratio.substring(dotpos))
         .append("x)");
    }


    public String getConnectionData() {
        stats = new StringBuffer(0);
        try {
//#if Android
//#             stats.append(connection.getLocalAddress()).append(":").append(connection.getLocalPort());
//#             stats.append("->").append(connection.getInetAddress()).append(":").append(connection.getPort());
//#else
            stats.append(((SocketConnection)connection).getLocalAddress())
                 .append(':')
                 .append(((SocketConnection)connection).getLocalPort())
                 .append(" -> ")
                 .append(((SocketConnection)connection).getAddress())
                 .append(':')
                 .append(((SocketConnection)connection).getPort());
//#endif
        } catch (Exception ex) {
            stats.append("unknown");
        }
        return stats.toString();
    }

    public long getBytes() {
        long startBytes=bytesSent+bytesRecv;
        try {
            if (inpStream instanceof ZInputStream) {
                ZOutputStream zo = (ZOutputStream) outStream;
                ZInputStream z = (ZInputStream) inpStream;
                return zo.getTotalOut()+z.getTotalIn();
            }
            return startBytes;
        } catch (Exception e) { }
        return 0;
    }
//#else
//# 
//#      public String getStreamStats() {
//#          StringBuffer stats=new StringBuffer();
//#          try {
//#              long sent=bytesSent;
//#              long recv=bytesRecv;
//#              stats.append("\nStream: in=").append(recv).append(" out=").append(sent);
//#          } catch (Exception e) {
//#              stats=null;
//#              return "";
//#          }
//#          return stats.toString();
//#      }
//# 
//#      public long getBytes() {
//#          try {
//#              return bytesSent+bytesRecv;
//#          } catch (Exception e) { }
//#          return 0;
//#      }
//#      public String getStreamStatsBar() { return ""; }
//#      public String getConnectionData() { return ""; }
//#endif
}
