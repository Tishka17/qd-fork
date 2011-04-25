/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package account;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.TextField;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif

public class ChangePasswordForm extends DefForm {
    private Account account;

    private TextInput textbox;

    private boolean locally;
    private String password;

    public ChangePasswordForm(Account account) {
        this(account, false, null);
    }

    public ChangePasswordForm(String newPassword) {
        this(null, true, newPassword);
    }

    private ChangePasswordForm(Account account, boolean locally, String password) {
        super(SR.get(SR.MS_CHANGE_PASSWORD));

        if (locally) {
            this.password = password;

            MultiLine line;
            line = new MultiLine(SR.get(SR.MS_NEW_PASSWORD), password + "\n" + SR.get(SR.MS_EDIT_ACCOUNT_MSG), getWidth());
            line.setSelectable(true);
            addControl(line);

            addControl(new SimpleString(SR.get(SR.MS_COPY) + "?", false));
        } else {
            this.account = account;

            textbox = new TextInput(SR.get(SR.MS_PASSWORD), null, TextField.ANY);
            addControl(textbox);
        }
    }

    public void cmdOk() {
        if (!locally) {
            String pass = textbox.getValue();

            Iq iqdel = new Iq(account.getServer(), Iq.TYPE_SET,"changemypass");
            JabberDataBlock qB = iqdel.addChildNs("query", "jabber:iq:register");
            qB.addChild("username", account.getUserName());
            qB.addChild("password", pass);
            BombusQD.sd.roster.theStream.send(iqdel);
        } else {
//#ifdef CLIPBOARD
            ClipBoard.setClipBoard(password);
//#endif
        }
        destroyView();
    }
}
