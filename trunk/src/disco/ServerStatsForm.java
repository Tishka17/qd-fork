/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package disco;

import java.util.Vector;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;

public class ServerStatsForm extends DefForm {
    public ServerStatsForm(String server, Vector stats) {
        super(server);

        for (int i = 0; i < stats.size(); ++i) {
            String[] data = (String[])stats.elementAt(i);

            addControl(new MultiLine(data[0], data[1]));
        }
    }

    public void cmdOk() {
        destroyView();
    }
}
