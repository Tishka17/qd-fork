/*
 * AccountForm.java
 *
 * Created on 20.05.2008, 13:05
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */
package account;

import client.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.TextInput;
import java.util.Random;
import ui.controls.form.SpacerItem;

public class AccountForm extends DefForm {
    private final AccountSelect accountSelect;
    private TextInput fulljid;
    private TextInput passbox;
    private TextInput ipbox;
    private NumberInput portbox;
    private TextInput nickbox;
    private CheckBox sslbox;
    private CheckBox plainPwdbox;
    private CheckBox compressionBox;
    private CheckBox confOnlybox;
    private TextInput emailbox;
//#if HTTPCONNECT
//#       private CheckBox proxybox;
//#elif HTTPPOLL        
//#       private CheckBox pollingbox;
//#endif
    private CheckBox registerbox;
    private NumberInput keepAlive;
    private DropChoiceBox keepAliveType;
//#if HTTPPOLL || HTTPCONNECT  
//#     private TextInput proxyHost;
//#     private TextInput proxyPort;
//#endif
    Account account;
    boolean newaccount;
    boolean showExtended;
    LinkString link_genPass;
    LinkString link_genServer;
    LinkString insertpass;
    private int type_profile = -1;
    boolean register = false;
    boolean createSimpleAddForm = false;
    String serverReg = "";

    private static final byte JABBER_PROFILE = 1;
    private static final byte YARU_PROFILE = 2;
    private static final byte GTALK_SSL_PROFILE = 3;
    private static final byte LJ_PROFILE = 4;
    private static final byte QIP_PROFILE = 5;
    private static final byte GTALK_HTTPS_PROFILE = 6;
    private static final byte VK_PROFILE = 7;

    private StringBuffer uid;

    public AccountForm(Display display, Displayable pView, AccountSelect accountSelect, Account account, int type_profile,
            boolean register, String serverReg) {
        super(display, pView, null);
        this.type_profile = type_profile;
        this.register = register;
        this.serverReg = serverReg;

        this.accountSelect = accountSelect;
        this.display = display;

        newaccount = (account == null);
        if (newaccount) {
            account = new Account();
        }
        this.account = account;

        if (register) {
            getMainBarItem().setElementAt(SR.get(SR.MS_REGISTER), 0);
        } else {
            if (newaccount) {
                getMainBarItem().setElementAt(SR.get(SR.MS_NEW_ACCOUNT), 0);
            } else {
                getMainBarItem().setElementAt(account.toString(), 0);
            }            
        }

        String server = register ? serverReg : "";
        String password = account.getPassword();
        int port_box = 5222;
        boolean generatePaswForm = register;
        if (!register) {
            switch (type_profile) {
                case -1:
                    server = account.getServer();
                    port_box = account.getPort();
                    break;
                case JABBER_PROFILE:
                    server = "";
                    break;
                case YARU_PROFILE:
                    server = "ya.ru";
                    break;
                case GTALK_HTTPS_PROFILE:
                case GTALK_SSL_PROFILE:
                    server = "gmail.com";
                    port_box = 5223;
                    break;
                case LJ_PROFILE:
                    server = "livejournal.com";
                    break;
                case QIP_PROFILE:
                    server = "qip.ru";
                    break;
                case VK_PROFILE:
                    server = "vk.com";
                    break;
            }
        } else {
            password = generate(1);
        }

        uid = new StringBuffer(0);
        if (register == false) {
            String res = account.getResource();
            String uname = account.getUserName();
            if (uname.length() > 0) {
                uid.append(account.getUserName());
            }
            /*else {
            uid.append('.');
            }*/
            uid.append('@');
            uid.append(server);
            if (res.length() > 0) {
                if (res.indexOf(info.Version.NAME) == -1) {
                    uid.append('/').append(account.getResource());
                }
            }
        } else {
            uid//.append(generate(2))
                    .append('@').append(server);
        }
        fulljid = new TextInput(display, SR.get(SR.MS_USER_PROFILE) + "(JID)", uid.toString(), null, TextField.ANY);
        nickbox = new TextInput(display, SR.get(SR.MS_NICKNAME), account.getNick(), null, TextField.ANY);
        itemsList.addElement(nickbox);
        itemsList.addElement(fulljid);


        passbox = new TextInput(display, SR.get(SR.MS_PASSWORD), password, null, TextField.ANY);
        itemsList.addElement(passbox);

        createSimpleAddForm = (null == serverReg && newaccount);//true if add,false if edit
        if (generatePaswForm) {
            link_genPass = new LinkString(SR.get(SR.MS_GENERATE) + " " + SR.get(SR.MS_PASSWORD))   {
                public void doAction() {
                    passbox.setValue(generate(1));
                }
            };
            itemsList.addElement(link_genPass);
            itemsList.addElement(new SpacerItem(5));
        }

        portbox = new NumberInput(display, SR.get(SR.MS_PORT), Integer.toString(port_box), 0, 65535);
        if (!createSimpleAddForm) {
            itemsList.addElement(portbox);
        }

        emailbox = new TextInput(display, "E-mail:", account.getEmail(), null, TextField.EMAILADDR);
        if (midlet.BombusQD.cf.userAppLevel == 1) {
            if (!createSimpleAddForm) {
                itemsList.addElement(emailbox);
            }
        }

        if (midlet.BombusQD.clipboard.getClipBoard() != null) {
            if (midlet.BombusQD.clipboard.getClipBoard().startsWith("!")) {
                insertpass = new LinkString(SR.get(SR.MS_INSERT_NEW_PASSWORD))   {
                    public void doAction() {
                        passbox.setValue(midlet.BombusQD.clipboard.getClipBoard().substring(1));
                        itemsList.removeElement(insertpass);
                        midlet.BombusQD.clipboard.setClipBoard("");
                    }
                };
                if (!createSimpleAddForm) {
                    itemsList.addElement(new SpacerItem(3));
                    itemsList.addElement(insertpass);
                }
            }
        }

        registerbox = new CheckBox(SR.get(SR.MS_REGISTER_ACCOUNT), register);

        /*if (newaccount && register) {
        itemsList.addElement(registerbox);
        }*/

        if (!register) {
            showExtended();
        }

        attachDisplay(display);
        this.parentView = pView;
    }

    protected void beginPaint() {
        if (null == serverReg) {
            return;
        }
        if (!register || 0 == serverReg.length()) {
            return;
        }
        if (null != nickbox) {
            String value = nickbox.getValue();
            if (0 != value.length()) {
                String replace = uid.toString();
                if (replace.indexOf(value) == -1) {
                    fulljid.setValue(value.concat(replace));
                }
            }
        }
    }

    private String generate(int type) {
        StringBuffer sb = new StringBuffer(0);
        if (type == 0) {
            Random rand = new Random();
            int i = 0;
            String[] servers = {
                "jabber.ru", "jabbim.com", "jabbus.org", "xmpp.ru", "jtalk.ru",
                "mytlt.ru", "gajim.org", "jabber.org.by"};
            i = Math.abs(rand.nextInt()) % 8;
            sb.append(servers[i]);
        } else if (type == 1) {
            Random rand = new Random();
            int i = 0;
            char[] chars = {
                'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
                'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z',
                'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R',
                'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F',
                'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B',
                'N', 'M', '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9'}; //62
            char[] pass = {'*', '*', '*', '*', '*', '*', '*', '*'};
            for (int k = 0; k < pass.length; k++) {
                i = Math.abs(rand.nextInt()) % 62;
                pass[k] = chars[i];
            }
            sb.append(pass);
        } else if (type == 2) { //generate nickname
            Random rand = new Random();
            sb.append("nick");
            int i = 0;
            for (int k = 0; k < 8; k++) {
                i = Math.abs(rand.nextInt()) % 10;
                sb.append(i);
            }
        }
        return sb.toString();
    }

    private void showExtended() {
        showExtended = true;

        boolean sslbox_ = false;
        boolean plainPwdbox_ = false;
        boolean compressionBox_ = false;
        String ip_box = "";
        switch (type_profile) {
            case -1:
                ip_box = account.getHostAddr();
                sslbox_ = account.getUseSSL();
                plainPwdbox_ = account.getPlainAuth();
                compressionBox_ = account.useCompression();
                break;
            case JABBER_PROFILE://
                ip_box = "";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = !register;//true;
                break;
            case YARU_PROFILE:
                ip_box = "xmpp.yandex.ru";
                sslbox_ = false;
                plainPwdbox_ = true;
                compressionBox_ = true;
                break;
            case GTALK_HTTPS_PROFILE:
                ip_box = "talk.google.com";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = false;
                break;
            case GTALK_SSL_PROFILE:
                ip_box = "talk.google.com";
                sslbox_ = true;
                plainPwdbox_ = true;
                compressionBox_ = false;
                break;
            case LJ_PROFILE:
                ip_box = "xmpp.services.livejournal.com";
                sslbox_ = false;
                plainPwdbox_ = true;
                compressionBox_ = false;
                break;
            case QIP_PROFILE:
                ip_box = "webim.qip.ru";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = true;
                break;
            case VK_PROFILE:
                ip_box = "vkmessenger.com";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = true;
                break;
        }
        ipbox = new TextInput(display, SR.get(SR.MS_HOST_IP), ip_box, null, TextField.ANY);
        sslbox = new CheckBox(SR.get(SR.MS_SSL), sslbox_);
        plainPwdbox = new CheckBox(SR.get(SR.MS_PLAIN_PWD), plainPwdbox_);
        compressionBox = new CheckBox(SR.get(SR.MS_COMPRESSION), compressionBox_);
        confOnlybox = new CheckBox(SR.get(SR.MS_CONFERENCES_ONLY), account.isMucOnly());
//#if HTTPCONNECT
//#        proxybox = new CheckBox("proxybox", SR.get(SR.MS_PROXY_ENABLE), account.isEnableProxy());
//#elif HTTPPOLL        
//#        pollingbox = new CheckBox("pollingbox", "HTTP Polling", false);
//#endif

        if (!createSimpleAddForm) {
            itemsList.addElement(sslbox);
            itemsList.addElement(plainPwdbox);
            itemsList.addElement(compressionBox);
            itemsList.addElement(confOnlybox);
//#if HTTPCONNECT
//#        itemsList.addElement(proxybox);
//#elif HTTPPOLL        
//#        itemsList.addElement(pollingbox);
//#endif
        }

        keepAliveType = new DropChoiceBox(display, SR.get(SR.MS_KEEPALIVE));
        keepAliveType.append("by socket");
        keepAliveType.append("1 byte");
        keepAliveType.append("<iq/>");
        keepAliveType.append("ping");
        keepAliveType.setSelectedIndex(account.getKeepAliveType());
        keepAlive = new NumberInput(display, SR.get(SR.MS_KEEPALIVE_PERIOD), Integer.toString(account.getKeepAlivePeriod()), 10, 2048);
        if (!createSimpleAddForm) {
            itemsList.addElement(keepAliveType);
            itemsList.addElement(keepAlive);
//#if HTTPCONNECT
//# 	proxyHost = new TextInput(display, SR.get(SR.MS_PROXY_HOST), account.getProxyHostAddr(), null, TextField.URL);
//# 
//# 	proxyPort = new TextInput(display, SR.get(SR.MS_PROXY_PORT), Integer.toString(account.getProxyPort()));
//#elif HTTPPOLL        
//# 	proxyHost = new TextInput(display, SR.get(SR.MS_PROXY_HOST), account.getProxyHostAddr(), null, TextField.URL);
//#endif
            itemsList.addElement(ipbox);



//#if HTTPCONNECT
//# 	itemsList.addElement(proxyHost);
//# 	itemsList.addElement(proxyPort);
//#elif HTTPPOLL        
//# 	itemsList.addElement(proxyHost);
//#endif
        }
    }

    public void cmdOk() {
        //midlet.BombusQD.debug.add("::saved",10);
        String value = fulljid.getValue().trim();
        String pass = passbox.getValue();
        String nick = nickbox.getValue();

        int indexPr = value.indexOf('@') + 1;
        int indexRes = value.indexOf('/') + 1;
        int indexRes_ = value.indexOf('\"') + 1;
        if (indexPr < 1 || pass.length() == 0) {
            return;
        }
        if (indexPr == 1 && (nick == null || nick.length() == 0)) {
            return;
        }

        String user = indexPr > 1 ? value.substring(0, indexPr - 1) : nick;
        String server = "server";
        String resource = "BombusQD";

        if (indexRes > 0) {
            server = value.substring(indexPr, indexRes - 1);
            resource = value.substring(indexRes);
        } else if (indexRes_ > 0) {
            server = value.substring(indexPr, indexRes_ - 1);
            resource = value.substring(indexRes_);
        } else {
            server = value.substring(indexPr);
        }

        account.setUserName(user);
        account.setServer(server);
        account.setPort(Integer.parseInt(portbox.getValue()));
        if (midlet.BombusQD.cf.userAppLevel == 1) {
            account.setEmail(emailbox.getValue().trim());
        }
        account.setPassword(pass);
        account.setNick(nick);
        account.setResource(resource);


        boolean registerNew = false;

        if (newaccount) {
            registerNew = registerbox.getValue();
        }

        if (showExtended) {
            registerNew = registerbox.getValue();
            account.setHostAddr(ipbox.getValue());
            account.setUseSSL(sslbox.getValue());
            account.setPlainAuth(plainPwdbox.getValue());
            account.setUseCompression(compressionBox.getValue());
            account.setMucOnly(confOnlybox.getValue());

//#if HTTPPOLL || HTTPCONNECT            
//#         account.setEnableProxy(proxybox.getValue());
//#         account.setProxyHostAddr(proxyHost.getValue());
//#         account.setProxyPort(proxyPort.getValue());
//#endif

            account.setKeepAlivePeriod(Integer.parseInt(keepAlive.getValue()));
            account.setKeepAliveType(keepAliveType.getValue());
        }

        if (newaccount) {
            accountSelect.accountList.addElement(account);
        }
        accountSelect.rmsUpdate();
        accountSelect.commandState();

        if (registerNew) {
            new AccountRegister(account, display, parentView);
        } else {
            destroyView();
        }
        account = null;
    }

    public void destroyView() {
        display.setCurrent(accountSelect);
    }

//#ifdef MENU_LISTENER    
    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM4:
                pageLeft();
                break;
            case KEY_NUM6:
                pageRight();
                break;
        }
    }
//#endif     

    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold == keyCode) {
            return;
        }
        kHold = keyCode;

        if (keyCode == KEY_NUM6) {
            Config cf = Config.getInstance();
            cf.fullscreen = !cf.fullscreen;
            cf.saveToStorage();
            VirtualList.fullscreen = cf.fullscreen;
            StaticData.getInstance().roster.setFullScreenMode(cf.fullscreen);
        }
    }
}
