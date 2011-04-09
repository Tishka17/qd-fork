/*
 * IEUtils.java
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

import io.file.FileIO;
import java.io.UnsupportedEncodingException;

public class IEUtils {
    public static String findBlock(String source, String needle) {
        String startItem = "<" + needle + ">";
        int start = source.indexOf(startItem);
        int end = source.indexOf("</" + needle + ">");

        if (start > -1 && end > -1 && start != end) {
            return source.substring(start + startItem.length(), end);
        }

        return null;
    }

    public static String createBlock(String tag, String value) {
        StringBuffer block = new StringBuffer("<").append(tag).append('>');
        if (value != null) {
            block.append(value);
        }
        block.append("</").append(tag).append(">\n");

        return block.toString();
    }

    public static String readFile(String path) {
        FileIO f = FileIO.createConnection(path);
        byte buf[] = f.fileRead();

        if (buf != null) {
            String data;
            try {
                data = new String(buf, "utf-8");
            } catch (UnsupportedEncodingException e) {
                data = new String(buf);
            }
            return data;
        }
        return null;
    }

    public static void writeFile(String path, String body) {
        byte buf[];
        try {
            buf = body.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            buf = body.getBytes();
        }

        FileIO file = FileIO.createConnection(path);
        file.fileWrite(buf);
    }
}
//#endif
