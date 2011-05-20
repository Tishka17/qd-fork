/*
 * HistoryExportTask.java
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA *
 */

//#if FILE_IO && HISTORY
package history;

import client.Msg;
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import locale.SR;
import ui.SplashScreen;

public class HistoryExportTask implements Runnable {
    private static final long DELAY_ON_ERROR = 1500L;

    private Vector msgs;
    private String path;

    public HistoryExportTask(Vector msgs, String path) {
        this.msgs = msgs;
        this.path = path;
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        SplashScreen screen = new SplashScreen();
        screen.show();

        FileIO file = null;
        OutputStream stream = null;
        try {
            file = FileIO.createConnection(path);
            if (file == null) {
                throw new IOException();
            }

            stream = file.openOutputStream();
            if (stream == null) {
                throw new IOException();
            }

            int size = msgs.size();
            for (int i = 0; i < size; ++i) {
                stream.write(HistoryStorage.msg2byte((Msg)msgs.elementAt(i)));
                stream.flush();

                screen.setProgress(SR.get(SR.MS_SAVING), (100 * (i + 1)) / size);
            }
        } catch (IOException io1) {
            screen.setFailed();
            try {
                Thread.sleep(DELAY_ON_ERROR);
            } catch (InterruptedException ie) {}
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch (IOException io2) {}
        }
        screen.destroyView();
    }
}
//#endif
