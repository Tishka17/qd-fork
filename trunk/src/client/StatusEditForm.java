package client;

import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TextInput;

public class StatusEditForm extends DefForm {
    private static final String STATUSTEXT_RECENT_ID = "status-text";
    private static final String AUTORESPOND_RECENT_ID = "autorespond-text";

    private NumberInput tfPriority;
    private TextInput tfMessage;
    private TextInput tfAutoRespondMessage;
    private ExtendedStatus status;
    private CheckBox autoRespond;

    public StatusEditForm(ExtendedStatus status) {
        super(status.getScreenName());

        this.status = status;

        tfMessage = new TextInput(SR.get(SR.MS_MESSAGE), status.getMessage(), STATUSTEXT_RECENT_ID, TextField.ANY);
        addControl(tfMessage);

        tfPriority = new NumberInput(SR.get(SR.MS_PRIORITY), status.getPriority(), -128, 128);
        addControl(tfPriority);
        if (status.getImageIndex() < 5) {
            addControl(new SpacerItem(10));
            autoRespond = new CheckBox(SR.get(SR.MS_ENABLE_AUTORESPOND), status.getAutoRespond());
            addControl(autoRespond);
            tfAutoRespondMessage = new TextInput(SR.get(SR.MS_AUTORESPOND), status.getAutoRespondMessage(), AUTORESPOND_RECENT_ID, TextField.ANY);
            addControl(tfAutoRespondMessage);
        }
        addControl(new SpacerItem(10));
        addControl(new SimpleString("%t - time", false));
        addControl(new SimpleString("%dt - date time", false));
    }

    public void cmdOk() {
        if (status.getImageIndex() < 5) {
            status.setAutoRespondMessage(tfAutoRespondMessage.getValue());
            status.setAutoRespond(autoRespond.getValue());
        }
        status.setMessage(tfMessage.getValue());
        status.setPriority(tfPriority.getIntValue());
        StatusSelect.save();

        destroyView();
    }
}
