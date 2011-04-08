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

//#ifdef IMPORT_EXPORT
//#ifdef FILE_IO
package impexp;

import account.Account;
import io.NvStorage;
import io.file.FileIO;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ad
 */
public class Accounts {
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
                body.append(createBlock(USERNAME, a.getUserName()));
                body.append(createBlock(SERVER, a.getServer()));
                body.append(createBlock(PASSWORD, a.getPassword()));
                body.append(createBlock(HOST, a.getHostAddr()));
                body.append(createBlock(PORT, Integer.toString(a.getPort())));
                body.append(createBlock(NICK, a.getNick()));
                body.append(createBlock(RESOURCE, a.getResource()));
                body.append(createBlock(USE_SSL, (a.getUseSSL() ? "1" : "0")));
                body.append(createBlock(PLAIN_AUTH, (a.getPlainAuth() ? "1" : "0")));
                body.append(createBlock(MUC_ONLY, (a.isMucOnly() ? "1" : "0")));
                body.append(createBlock(COMPRESSION, (a.useCompression() ? "1" : "0")));
                body.append(createBlock(KEEPALIVE, Integer.toString(a.getKeepAliveType())));
                body.append(createBlock(KEEPALIVEPERIOD, Integer.toString(a.getKeepAlivePeriod())));
                body.append("</a>\r\n");

                index++;
            }
        } while (a != null);

        byte buf[];
        try {
            buf = body.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            buf = body.toString().getBytes();
        }

        FileIO fileIO = FileIO.createConnection(path + "accounts.txt");
        fileIO.fileWrite(buf);
    }

    private void importData(String path) {
        FileIO fileIO = FileIO.createConnection(path);
        byte[] buf = fileIO.fileRead();

        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();

        if (buf != null) {
            String raw;
            try {
                raw = new String(buf, "utf-8");
            } catch (UnsupportedEncodingException e) {
                raw = new String(buf);
            }

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
                        account.setUserName(findBlock(tempstr, USERNAME));
                        account.setServer(findBlock(tempstr, SERVER));
                        account.setPassword(findBlock(tempstr, PASSWORD));
                        account.setNick(findBlock(tempstr, NICK));

                        account.setPort(Integer.parseInt(findBlock(tempstr, PORT)));
                        account.setHostAddr(findBlock(tempstr, HOST));
                        account.setResource(findBlock(tempstr, RESOURCE));
                        account.setPassword(findBlock(tempstr, PASSWORD));
                        account.setUseSSL((findBlock(tempstr, USE_SSL).equals("1")) ? true : false);
                        account.setPlainAuth((findBlock(tempstr, PLAIN_AUTH).equals("1")) ? true : false);
                        account.setUseCompression((findBlock(tempstr, COMPRESSION).equals("1")) ? true : false);
                        account.setMucOnly((findBlock(tempstr, MUC_ONLY).equals("1")) ? true : false);
                        account.setKeepAlivePeriod(Integer.parseInt(findBlock(tempstr, KEEPALIVEPERIOD)));
                        account.setKeepAliveType(Integer.parseInt(findBlock(tempstr, KEEPALIVE)));

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

    private String findBlock(String source, String needle) {
        String startItem = "<" + needle + ">";
        int start = source.indexOf(startItem);
        int end = source.indexOf("</" + needle + ">");

        if (start > -1 && end > -1 && start != end) {
            return source.substring(start + startItem.length(), end);
        }

        return "";
    }

    private String createBlock(String tag, String value) {
        StringBuffer block = new StringBuffer("<").append(tag).append('>');
        if (value != null) {
            block.append(value);
        }
        block.append("</").append(tag).append('>');

        return block.toString();
    }
}
//#endif
//#endif
