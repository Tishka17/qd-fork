/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package conference;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.TextInput;

public class ConferenceRemoveForm extends DefForm {
    private TextInput input;

    private String conference;

    public ConferenceRemoveForm(String conference) {
        super("");

        this.conference = conference;

        input = new TextInput(SR.get(SR.MS_REASON), null, TextField.ANY);
        addControl(input);
    }

    public void cmdOk() {
        Iq iq = new Iq(conference, 0, "destroyroom");
        JabberDataBlock x = iq.addChild("query",null);
        x.setNameSpace("http://jabber.org/protocol/muc#owner");

        JabberDataBlock destroy = x.addChild("destroy", null);
        destroy.setAttribute("jid", conference);
        destroy.addChild("reason", input.getValue());

        midlet.BombusQD.sd.roster.theStream.send(iq);
        destroyView();
    }
}
