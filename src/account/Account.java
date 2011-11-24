/*
 * Account.java
 *
 * Created on 19.03.2005, 21:52
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
package account;

import client.Config;
import info.Version;
import com.alsutton.jabber.JabberStream;
import font.FontCache;
import javax.microedition.lcdui.Font;
import images.MenuIcons;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ui.IconTextElement;
import io.DnsSrvResolver;
import io.NvStorage;
import client.StaticData;
import com.alsutton.jabber.datablocks.Presence;

public class Account extends IconTextElement {
    private String username = "";
    private String password = "";
    private String server = "";
    private String email = "";
    private String hostAddr = "";
    private int port = 5222;
    private boolean active;
    private boolean useSSL;
    private boolean compression = true;
    private boolean plainAuth;
    private boolean mucOnly = false;
    private String nick = "";
    private String resource = "";
    private boolean enableProxy;
    private String proxyHost = "";
    private int proxyPort;
    private int keepAlivePeriod = 120;
    private int keepAliveType = 1;
    private boolean dnsResolver = false;

    public Account() {
        super(MenuIcons.getInstance());
    }

    public static void loadAccount(boolean launch, int accountIndex, int status) {
        StaticData sd = StaticData.getInstance();
        Account a = sd.account = Account.createFromStorage(accountIndex);
        if (a != null && sd.roster != null) {
            if (sd.roster.isLoggedIn()) {
                sd.roster.logoff(null);
            }
            sd.roster.resetRoster();
            if (launch) {
                int loginstatus = Config.getInstance().loginstatus;
                if (loginstatus >= Presence.PRESENCE_OFFLINE) {
                    sd.roster.sendPresence(Presence.PRESENCE_INVISIBLE, null);
                } else {
                    if (status == -1) {
                        sd.roster.sendPresence(loginstatus, null);
                    } else {
                        sd.roster.sendPresence(status, null);
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (nick.length() != 0) {
            buf.append(nick);
        } else {
            buf.append(username).append('@').append(server);
        }
        if (resource.indexOf(Version.NAME) == -1) {
            buf.append('/').append(resource);
        }
        return buf.toString();
    }

    public String getJid() {
        return username + '@' + server + '/' + resource;
    }

    public String getBareJid() {
        return username + '@' + server;
    }

    public static Account createFromStorage(int index) {
        DataInputStream is = NvStorage.ReadFileRecord("accnt_db", 0);
        if (is == null) {
            return null;
        }
        try {
            Account a;

            do {
                if (is.available() == 0) {
                    a = null;
                    break;
                }
                a = createFromDataInputStream(is);
                index--;
            } while (index > - 1);
            is.close();

            return a;
        } catch (Exception e) {
            return null;
        }
    }

    public void setIconElement() {
        if (server.indexOf("ya.ru") > -1) {
            imageindex = MenuIcons.ICON_YANDEX_ACCOUNT;
        } else if (server.indexOf("gmail.com") > -1) {
            imageindex = MenuIcons.ICON_GTALK_ACCOUNT;
        } else if (server.indexOf("livejournal.com") > -1) {
            imageindex = MenuIcons.ICON_LJ_ACCOUNT;
        } else if (server.indexOf("qip.ru") > -1) {
            imageindex = MenuIcons.ICON_QIP_ACCOUNT;
        } else {
            imageindex = MenuIcons.ICON_OTHER_ACCOUNT;
        }
    }

    public static Account createFromDataInputStream(DataInputStream inputStream) {
        Account a = new Account();
        try {
            inputStream.readByte(); // skip
            a.username = inputStream.readUTF();
            a.password = inputStream.readUTF();
            a.server = inputStream.readUTF();
            a.setIconElement();
            a.email = inputStream.readUTF();
            a.hostAddr = inputStream.readUTF();
            a.port = inputStream.readInt();

            a.nick = inputStream.readUTF();
            a.resource = inputStream.readUTF();

            a.useSSL = inputStream.readBoolean();
            a.plainAuth = inputStream.readBoolean();

            a.mucOnly = inputStream.readBoolean();

            a.setEnableProxy(inputStream.readBoolean());
            a.setProxyHostAddr(inputStream.readUTF());
            a.setProxyPort(inputStream.readInt());

            a.compression = inputStream.readBoolean();

            a.keepAliveType = inputStream.readInt() % 4;
            a.keepAlivePeriod = inputStream.readInt();

            a.dnsResolver = inputStream.readBoolean(); //firstrun
        } catch (IOException e) {
        }
        return (a.username == null) ? null : a;
    }

    public void saveToDataOutputStream(DataOutputStream outputStream) {
        if (hostAddr == null) {
            hostAddr = "";
        }
        if (proxyHost == null) {
            proxyHost = "";
        }

        try {
            outputStream.writeByte(7);
            outputStream.writeUTF(username);
            outputStream.writeUTF(password);
            outputStream.writeUTF(server);
            outputStream.writeUTF(email);

            outputStream.writeUTF(hostAddr);
            outputStream.writeInt(port);

            outputStream.writeUTF(nick);
            outputStream.writeUTF(resource);

            outputStream.writeBoolean(useSSL);
            outputStream.writeBoolean(plainAuth);

            outputStream.writeBoolean(mucOnly);

            outputStream.writeBoolean(enableProxy);
            outputStream.writeUTF(proxyHost);
            outputStream.writeInt(proxyPort);

            outputStream.writeBoolean(compression);

            outputStream.writeInt(keepAliveType);
            outputStream.writeInt(keepAlivePeriod);

            outputStream.writeBoolean(dnsResolver);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public Font getFont() {
        return FontCache.getFont(active, Config.rosterFont);
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public String getHostAddr() {
        return hostAddr;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setHostAddr(String hostAddr) {
        this.hostAddr = hostAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean ssl) {
        this.useSSL = ssl;
    }

    public boolean getPlainAuth() {
        return plainAuth;
    }

    public void setPlainAuth(boolean plain) {
        this.plainAuth = plain;
    }

    public boolean getDnsResolver() {
        return dnsResolver;
    }

    public void setDnsResolver(boolean dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getNickName() {
        return (nick == null || nick.length() == 0) ? username : nick;
    }

    public String getNick() {
        return (nick == null || nick.length() == 0) ? null : nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean isMucOnly() {
        return mucOnly;
    }

    public void setMucOnly(boolean mucOnly) {
        this.mucOnly = mucOnly;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JabberStream openJabberStream() throws java.io.IOException {
        String proxy=null;
        String host=this.server;
        int tempPort=port;
        
        if (hostAddr!=null && hostAddr.length()>0) {
                host=hostAddr;
        } else {
            DnsSrvResolver dns=new DnsSrvResolver();
            int type = DnsSrvResolver.XMPP_TCP;
//#if HTTPCONNECT || HTTPBIND || HTTPPOLL                    
//#             if (enableProxy) {
//#ifdef HTTPBIND
//#                 type = DnsSrvResolver.XMPP_HTTPBIND;
//#endif            
//#ifdef HTTPPOLL
//#                 type = DnsSrvResolver.XMPP_HTTPPOLL;
//#endif            
//#             }
//#endif            
            if (dns.getSrv(server, type)) {
                host=dns.getHost();
                tempPort=dns.getPort();
//#if HTTPBIND || HTTPPOLL
//#                 proxyHostAddr = host;
//#endif                
            }
        }
        StringBuffer url = new StringBuffer(host);

//#if HTTPPOLL || HTTPCONNECT || HTTPBIND
//#         if (!isEnableProxy()) {
//# 	    url.insert(0, "socket://");
//#         } else {
//#if HTTPPOLL || HTTPBIND
//#              proxy = proxyHostAddr;
//#elif HTTPCONNECT
//#             proxy="socket://" + getProxyHostAddr() + ':' + getProxyPort();
//#endif
//#     }
//#else
//#if !(Android)        
            url.insert(0, "socket://");
//#endif            
//#endif
        return new JabberStream(getServer(), url.toString(), tempPort, proxy);
    }

    public boolean isEnableProxy() {
        return enableProxy;
    }

    public void setEnableProxy(boolean enableProxy) {
        this.enableProxy = enableProxy;
    }

    public String getProxyHostAddr() {
        return proxyHost;
    }

    public void setProxyHostAddr(String proxyHostAddr) {
        this.proxyHost = proxyHostAddr;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean useCompression() {
        return compression;
    }

    public void setUseCompression(boolean value) {
        this.compression = value;
    }

    public boolean isGmail() {
        return server.startsWith("gmail.com");
    }

    public String getTipString() {
        return getJid();
    }

    public boolean useGoogleToken() {
        if (useSSL) {
            return false;
        }
        return isGmail();
    }

    public void setActive(boolean b) {
        active = b;
    }

    public int getKeepAliveType() {
        return keepAliveType;
    }

    public int getKeepAlivePeriod() {
        return keepAlivePeriod;
    }

    public void setKeepAlivePeriod(int i) {
        keepAlivePeriod = i;
    }

    public void setKeepAliveType(int i) {
        keepAliveType = i;
    }
}
