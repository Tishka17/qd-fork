/*
 * ChangePasswordForm.java
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

            addControl(new MultiLine(SR.get(SR.MS_NEW_PASSWORD), password + "\n" + SR.get(SR.MS_EDIT_ACCOUNT_MSG)));
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
