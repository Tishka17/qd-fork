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
    private NumberInput tfPriority;
    private TextInput tfMessage;
    private TextInput tfAutoRespondMessage;
    private ExtendedStatus status;
    private CheckBox autoRespond;

    public StatusEditForm(ExtendedStatus status) {
        super(status.getScreenName());

        this.status = status;

        tfMessage = new TextInput(SR.get(SR.MS_MESSAGE), status.getMessage(), "ex_status_list", TextField.ANY); //, 100, TextField.ANY "ex_status_list"
        addControl(tfMessage);

        tfPriority = new NumberInput(SR.get(SR.MS_PRIORITY), status.getPriority(), -128, 128); //, 100, TextField.ANY "ex_status_list"
        addControl(tfPriority);
        if (status.getImageIndex() < 5) {
            addControl(new SpacerItem(10));
            tfAutoRespondMessage = new TextInput(SR.get(SR.MS_AUTORESPOND), status.getAutoRespondMessage(), "autorespond", TextField.ANY); //, 100, 0
            addControl(tfAutoRespondMessage);
            autoRespond = new CheckBox(SR.get(SR.MS_ENABLE_AUTORESPOND), status.getAutoRespond());
            addControl(autoRespond);
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
