/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package account;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;

public class AccountRemoveForm extends DefForm {
    private Account account;

    private boolean confirm;

    public AccountRemoveForm() {
        this(null, true);
    }
    
    public AccountRemoveForm(Account account) {
        this(account, false);
    }

    private AccountRemoveForm(Account account, boolean confirm) {
        super("");

        if (confirm) {
            addControl(new MultiLine(null, SR.get(SR.MS_ACCOUNT_HAS_BEEN_REMOVED)));
        } else {
            this.account = account;

            addControl(new MultiLine(account.getJid(), SR.get(SR.MS_REMOVE_ACCOUNT) + "?"));
        }
    }

    public void cmdOk() {
        if (!confirm) {
            Iq iqdel = new Iq(account.getServer(), Iq.TYPE_SET, "delacc");
            JabberDataBlock qB = iqdel.addChildNs("query", "jabber:iq:register");
            qB.addChild("remove", null);
            midlet.BombusQD.sd.roster.theStream.send(iqdel);
        }
        destroyView();
    }
}
