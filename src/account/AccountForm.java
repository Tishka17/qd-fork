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

import java.util.Random;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.NumberInput;
import ui.controls.form.TextInput;
import ui.controls.form.PasswordInput;
import ui.MainBar;
import ui.controls.form.SpacerItem;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif

public class AccountForm extends DefForm {
    private TextInput fulljid;
    private TextInput passbox;
    private TextInput ipbox;
    private NumberInput portbox;
    private TextInput nickbox=null;
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
    private NumberInput keepAlive;
    private DropChoiceBox keepAliveType;
//#if HTTPPOLL || HTTPCONNECT
//#     private TextInput proxyHost;
//#     private TextInput proxyPort;
//#endif
    private Account account;
    private boolean newaccount;
    private boolean showExtended;

    private LinkString insertpass;
    private int type_profile = -1;
    boolean register = false;
    boolean createSimpleAddForm = false;
    private String serverReg = "";

    public static final byte PROFILE_JABBER = 1;
    public static final byte PROFILE_YANDEX = 2;
    public static final byte PROFILE_GTALK_SSL = 3;
    public static final byte PROFILE_LIVEJOURNAL = 4;
    public static final byte PROFILE_QIP = 5;
    public static final byte PROFILE_GTALK_HTTPS = 6;
    public static final byte PROFILE_VKONTAKTE = 7;
    public static final byte PROFILE_ODNOKLASSNIKI = 8;

    private String uid;

    public AccountForm(Account account, int type_profile) {
        this(account, type_profile, false, null);
    }

    public AccountForm(String regServer) {
        this(null, 1, true, regServer);
    }

    public AccountForm(Account account, int type_profile, boolean register, String serverReg) {
        super(null);
        this.type_profile = type_profile;
        this.register = register;
        this.serverReg = serverReg;

        newaccount = (account == null);
        if (newaccount) {
            account = new Account();
        }
        this.account = account;

        if (register) {
            setMainBarItem(new MainBar(SR.get(SR.MS_REGISTER)));
        } else {
            if (newaccount) {
                setMainBarItem(new MainBar(SR.get(SR.MS_NEW_ACCOUNT)));
            } else {
                setMainBarItem(new MainBar(this.account.toString()));
            }
        }

        String server = register ? serverReg : "";
        String password = account.getPassword();
        int port_box = 5222;

        if (!register) {
            switch (type_profile) {
                case -1:
                    server = account.getServer();
                    port_box = account.getPort();
                    break;
                case PROFILE_YANDEX:
                    server = "ya.ru";
                    break;
                case PROFILE_GTALK_HTTPS:
                case PROFILE_GTALK_SSL:
                    server = "gmail.com";
                    port_box = 5223;
                    break;
                case PROFILE_LIVEJOURNAL:
                    server = "livejournal.com";
                    break;
                case PROFILE_QIP:
                    server = "qip.ru";
                    break;
                case PROFILE_VKONTAKTE:
                    server = "vk.com";
                    break;
                case PROFILE_ODNOKLASSNIKI:
                    server = "odnoklassniki.ru";
                    break;
                default:
                    break;
            }
        } else {
            password = generate();
        }

        StringBuffer uidBuffer = new StringBuffer(0);
        if (register == false) {
            String res = account.getResource();
            String uname = account.getUserName();
            if (uname.length() > 0) {
                uidBuffer.append(account.getUserName());
            }

            uidBuffer.append('@');
            uidBuffer.append(server);
            if (res.length() > 0 && res.indexOf(info.Version.NAME) == -1) {
                uidBuffer.append('/').append(account.getResource());
            }
        } else {
            uidBuffer.append('@').append(server);
        }
        uid=uidBuffer.toString();
        uidBuffer=null;
        fulljid = new TextInput(SR.get(SR.MS_USER_PROFILE) + "(JID)", uid, TextField.ANY);
        
        addControl(fulljid);

        if (register) {
            passbox = new TextInput(SR.get(SR.MS_PASSWORD), password, TextField.ANY);
        } else {
            passbox = new PasswordInput(SR.get(SR.MS_PASSWORD), password);
        }        
        addControl(passbox);
       // 

        createSimpleAddForm = (null == serverReg && newaccount);//true if add,false if edit
        if(createSimpleAddForm==false && register == false){
            nickbox = new TextInput(SR.get(SR.MS_NICKNAME), account.getNick(), TextField.ANY);
            addControl(nickbox);

        }

        if (register) {
            addControl(new LinkString(SR.get(SR.MS_GENERATE) + " " + SR.get(SR.MS_PASSWORD))   {
                public void doAction() {
                    passbox.setValue(generate());
                }
            });
            
            addControl(new SpacerItem(5));
        }

        portbox = new NumberInput(SR.get(SR.MS_PORT), port_box, 0, 65535);
        if (!createSimpleAddForm) {
            addControl(portbox);
        }

        emailbox = new TextInput("E-mail", account.getEmail(), TextField.EMAILADDR);
        if (midlet.BombusQD.cf.userAppLevel == 1 && !createSimpleAddForm) {
            addControl(emailbox);
        }

//#ifdef CLIPBOARD
        if (!ClipBoard.isEmpty() && ClipBoard.getClipBoard().startsWith("!")) {
            insertpass = new LinkString(SR.get(SR.MS_INSERT_NEW_PASSWORD))   {
                public void doAction() {
                    passbox.setValue(ClipBoard.getClipBoard().substring(1));
                    itemsList.removeElement(insertpass);
                    ClipBoard.setClipBoard("");
                }
            };
            if (!createSimpleAddForm) {
                addControl(new SpacerItem(3));
                addControl(insertpass);
            }
        }
//#endif

        if (!register) {
            showExtended();
        }
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
                String replace = uid;
                if (replace.indexOf(value) == -1) {
                    fulljid.setValue(value.concat(replace));
                }
            }
        }
    }

    private static final int PASSWORD_LEN = 9;

    private String generate() {
        StringBuffer sb = new StringBuffer(0);
        Random rand = new Random();

        char[] chars = { '%',
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
            'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z',
            'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R',
            'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F',
            'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B',
            'N', 'M', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'};

        for (int i = 0; i < PASSWORD_LEN; ++i) {
            int index = Math.abs(rand.nextInt()%chars.length);
            sb.append(chars[index]);
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
            case PROFILE_JABBER://
                ip_box = "";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = !register;//true;
                break;
            case PROFILE_YANDEX:
                ip_box = "xmpp.yandex.ru";
                sslbox_ = false;
                plainPwdbox_ = true;
                compressionBox_ = true;
                break;
            case PROFILE_GTALK_HTTPS:
                ip_box = "talk.google.com";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = false;
                break;
            case PROFILE_GTALK_SSL:
                ip_box = "talk.google.com";
                sslbox_ = true;
                plainPwdbox_ = true;
                compressionBox_ = false;
                break;
            case PROFILE_LIVEJOURNAL:
                ip_box = "xmpp.services.livejournal.com";
                sslbox_ = false;
                plainPwdbox_ = true;
                compressionBox_ = false;
                break;
            case PROFILE_QIP:
                ip_box = "webim.qip.ru";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = true;
                break;
            case PROFILE_VKONTAKTE:
                ip_box = "vkmessenger.com";
                sslbox_ = false;
                plainPwdbox_ = false;
                compressionBox_ = true;
                break;
            case PROFILE_ODNOKLASSNIKI:
                ip_box = "xmpp.odnoklassniki.ru";
                sslbox_ = false;
                plainPwdbox_ = true;
                compressionBox_ = true;
                break;
        }
        ipbox = new TextInput(SR.get(SR.MS_HOST_IP), ip_box, TextField.ANY);
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
            addControl(sslbox);
            addControl(plainPwdbox);
            addControl(compressionBox);
            addControl(confOnlybox);
//#if HTTPCONNECT
//#        addControl(proxybox);
//#elif HTTPPOLL
//#        addControl(pollingbox);
//#endif
        }

        keepAliveType = new DropChoiceBox(SR.get(SR.MS_KEEPALIVE));
        keepAliveType.append("by socket");
        keepAliveType.append("1 byte");
        keepAliveType.append("<iq/>");
        keepAliveType.append("ping");
        keepAliveType.setSelectedIndex(account.getKeepAliveType());
        keepAlive = new NumberInput(SR.get(SR.MS_KEEPALIVE_PERIOD), account.getKeepAlivePeriod(), 10, 2048);
        if (!createSimpleAddForm) {
            addControl(keepAliveType);
            addControl(keepAlive);
//#if HTTPCONNECT
//# 	proxyHost = new TextInput(SR.get(SR.MS_PROXY_HOST), account.getProxyHostAddr(), null, TextField.URL);
//#
//# 	proxyPort = new TextInput(SR.get(SR.MS_PROXY_PORT), Integer.toString(account.getProxyPort()));
//#elif HTTPPOLL
//# 	proxyHost = new TextInput(SR.get(SR.MS_PROXY_HOST), account.getProxyHostAddr(), null, TextField.URL);
//#endif
            addControl(ipbox);



//#if HTTPCONNECT
//# 	addControl(proxyHost);
//# 	addControl(proxyPort);
//#elif HTTPPOLL
//# 	addControl(proxyHost);
//#endif
        }
    }

    public void cmdOk() {
        String value = fulljid.getValue().trim();
        String pass = passbox.getValue();
        

        int indexPr = value.indexOf('@') + 1;
        int indexRes = value.indexOf('/') + 1;
        int indexRes_ = value.indexOf('\"') + 1;

        String nick = null == nickbox ? null :nickbox.getValue() ;

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
        account.setPort(portbox.getIntValue());
        if (midlet.BombusQD.cf.userAppLevel == 1) {
            account.setEmail(emailbox.getValue().trim());
        }
        account.setPassword(pass);
        account.setNick(nick);
        account.setResource(resource);

        if (showExtended) {
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

            account.setKeepAlivePeriod(keepAlive.getIntValue());
            account.setKeepAliveType(keepAliveType.getValue());
        }

        if (newaccount) {
            ((AccountSelect)getParentView()).addAccount(account);
            newaccount = false;
            newaccount = false;
        }
        ((AccountSelect)getParentView()).rmsUpdate();
        if (!register) {
            destroyView();
            account = null;
        } else {
            new AccountRegister(account, (AccountSelect)getParentView());
        }
    }
}
