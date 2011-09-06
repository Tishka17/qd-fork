/*
 * ArchiveTemplates.java
 *
 * Created on 2.06.2008, 20:21
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

//#if IMPORT_EXPORT && ARCHIVE && FILE_IO
package impexp;

import client.Msg;
import archive.MessageArchive;
import java.util.Enumeration;
import java.util.Vector;
import util.StringUtils;
import util.Time;

/**
 *
 * @author ad
 */

public class Archive {
    private static final String ARCHIVE_FILE = "archive.txt";

    private final static String START_ITEM = "<START_ITEM>";
    private final static String END_ITEM = "<END_ITEM>";
    private final static String START_DATE = "<START_DATE>";
    private final static String END_DATE = "<END_DATE>";
    private final static String START_FROM = "<START_FROM>";
    private final static String END_FROM = "<END_FROM>";
    private final static String START_SUBJ = "<START_SUBJ>";
    private final static String END_SUBJ = "<END_SUBJ>";
    private final static String START_BODY = "<START_BODY>";
    private final static String END_BODY = "<END_BODY>";

    private MessageArchive archive;

    public Archive(String path, int action) {
        archive = new MessageArchive();

        switch (action) {
            case ImportExportForm.ARCHIVE_EXPORT:
                exportArchive(path);
                break;
            case ImportExportForm.ARCHIVE_IMPORT:
                importArchive(path);
                break;
        }

        archive.close();
    }

    private void importArchive(String path) {
        Vector vector = new Vector();
        String raw = IEUtils.readFile(path);

        if (raw != null) {
            int pos = 0;
            int start_pos = 0;
            int end_pos = 0;

            while (true) {
                start_pos = raw.indexOf(START_ITEM, pos);
                end_pos = raw.indexOf(END_ITEM, pos);

                if (start_pos > -1 && end_pos > -1) {
                    String tempstr = raw.substring(start_pos + START_ITEM.length(), end_pos);

                    String date = findBlock(tempstr, START_DATE, END_DATE);
                    String from = findBlock(tempstr, START_FROM, END_FROM);
                    String subj = findBlock(tempstr, START_SUBJ, END_SUBJ);
                    String body = findBlock(tempstr, START_BODY, END_BODY);

                    Msg msg = new Msg(Msg.INCOMING, from, subj, body);
                    msg.setDayTime(date);
                    vector.insertElementAt(msg, 0);
                } else {
                    break;
                }

                pos = end_pos + END_ITEM.length();
            }

            for (Enumeration e = vector.elements(); e.hasMoreElements();) {
                MessageArchive.store((Msg) e.nextElement());
            }
        }
    }

    private String findBlock(String source, String _start, String _end) {
        String block = "";
        int start = source.indexOf(_start);
        int end = source.indexOf(_end);
        if (start < 0 || end < 0) {
            return block;
        }

        return source.substring(start + _start.length(), end);
    }

    private void exportArchive(String path) {
        StringBuffer body = new StringBuffer(28);

        for (int i = 0; i < archive.size(); ++i) {
            Msg m = archive.msg(i);
            body.append(START_ITEM).append("\r\n");
            body.append(START_DATE).append(m.getDayTime()).append(END_DATE).append("\r\n");
            body.append(START_FROM).append(m.getFrom()).append(END_FROM).append("\r\n");
            body.append(START_SUBJ);
            if (m.getSubject() != null) {
                body.append(m.getSubject());
            }
            body.append(END_SUBJ).append("\r\n");
            body.append(START_BODY).append(m.getBody()).append(END_BODY).append("\r\n");
            body.append(END_ITEM).append("\r\n\r\n");
        }

        String fname = StringUtils.getFileName(path + ARCHIVE_FILE);
        IEUtils.writeFile(fname, body.toString());
    }

    private String getDate() {
        long dateGmt = Time.utcTimeMillis();
        return Time.dayLocalString(dateGmt).trim();
    }
}
//#endif
