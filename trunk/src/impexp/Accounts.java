/*
 * Accounts.java
 *
 * Created on 14.06.2008., 17:12
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

//#if IMPORT_EXPORT && FILE_IO
package impexp;

import account.Account;
import io.NvStorage;
import java.io.DataOutputStream;

/**
 *
 * @author ad
 */

public class Accounts {
    private static final String ACCOUNTS_FILE = "accounts.txt";

    private final static String USERNAME = "userName";
    private final static String SERVER = "server";
    private final static String PASSWORD = "password";
    private final static String HOST = "hostAddr";
    private final static String PORT = "port";
    private final static String NICK = "nick";
    private final static String RESOURCE = "resource";
    private final static String USE_SSL = "useSSL";
    private final static String PLAIN_AUTH = "plainAuth";
    private final static String MUC_ONLY = "mucOnly";
    private final static String COMPRESSION = "compression";
    private final static String KEEPALIVE = "keepAliveType";
    private final static String KEEPALIVEPERIOD = "keepAlivePeriod";
 
    public Accounts(String path, int action) {
        switch (action) {
            case ImportExportForm.ACCOUNT_EXPORT:
                exportData(path);
                break;
            case ImportExportForm.ACCOUNT_IMPORT:
                importData(path);
                break;
        }
    }

    private void exportData(String path) {
        StringBuffer body = new StringBuffer();

        Account a;
        int index = 0;
        do {
            a = Account.createFromStorage(index);
            if (a != null) {

                body.append("<a>");
                body.append(IEUtils.createBlock(USERNAME, a.getUserName()));
                body.append(IEUtils.createBlock(SERVER, a.getServer()));
                body.append(IEUtils.createBlock(PASSWORD, a.getPassword()));
                body.append(IEUtils.createBlock(HOST, a.getHostAddr()));
                body.append(IEUtils.createBlock(PORT, Integer.toString(a.getPort())));
                body.append(IEUtils.createBlock(NICK, a.getNick()));
                body.append(IEUtils.createBlock(RESOURCE, a.getResource()));
                body.append(IEUtils.createBlock(USE_SSL, (a.getUseSSL() ? "1" : "0")));
                body.append(IEUtils.createBlock(PLAIN_AUTH, (a.getPlainAuth() ? "1" : "0")));
                body.append(IEUtils.createBlock(MUC_ONLY, (a.isMucOnly() ? "1" : "0")));
                body.append(IEUtils.createBlock(COMPRESSION, (a.useCompression() ? "1" : "0")));
                body.append(IEUtils.createBlock(KEEPALIVE, Integer.toString(a.getKeepAliveType())));
                body.append(IEUtils.createBlock(KEEPALIVEPERIOD, Integer.toString(a.getKeepAlivePeriod())));
                body.append("</a>\n");

                ++index;
            }
        } while (a != null);

        IEUtils.writeFile(path + ACCOUNTS_FILE, body.toString());
    }

    private void importData(String path) {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
        String raw = IEUtils.readFile(path);

        if (raw != null) {
            try {
                int pos = 0;
                int start_pos = 0;
                int end_pos = 0;

                boolean parse = true;

                while (parse) {
                    start_pos = raw.indexOf("<a>", pos);
                    end_pos = raw.indexOf("</a>", pos);

                    if (start_pos > -1 && end_pos > -1) {
                        pos = end_pos + 4;
                        String tempstr = raw.substring(start_pos + 3, end_pos);

                        Account account = new Account();
                        account.setUserName(IEUtils.findBlock(tempstr, USERNAME));
                        account.setServer(IEUtils.findBlock(tempstr, SERVER));
                        account.setPassword(IEUtils.findBlock(tempstr, PASSWORD));
                        account.setNick(IEUtils.findBlock(tempstr, NICK));

                        account.setPort(Integer.parseInt(IEUtils.findBlock(tempstr, PORT)));
                        account.setHostAddr(IEUtils.findBlock(tempstr, HOST));
                        account.setResource(IEUtils.findBlock(tempstr, RESOURCE));
                        account.setPassword(IEUtils.findBlock(tempstr, PASSWORD));
                        account.setUseSSL((IEUtils.findBlock(tempstr, USE_SSL).equals("1")) ? true : false);
                        account.setPlainAuth((IEUtils.findBlock(tempstr, PLAIN_AUTH).equals("1")) ? true : false);
                        account.setUseCompression((IEUtils.findBlock(tempstr, COMPRESSION).equals("1")) ? true : false);
                        account.setMucOnly((IEUtils.findBlock(tempstr, MUC_ONLY).equals("1")) ? true : false);
                        account.setKeepAlivePeriod(Integer.parseInt(IEUtils.findBlock(tempstr, KEEPALIVEPERIOD)));
                        account.setKeepAliveType(Integer.parseInt(IEUtils.findBlock(tempstr, KEEPALIVE)));

                        account.saveToDataOutputStream(outputStream);
                        account.setIconElement();
                    } else {
                        parse = false;
                    }
                }

                NvStorage.writeFileRecord(outputStream, "accnt_db", 0, true);
            } catch (Exception e) {
            }
        }
    }
}
//#endif
