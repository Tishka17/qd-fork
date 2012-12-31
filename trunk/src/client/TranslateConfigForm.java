/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//#ifdef TRANSLATE
package client;

import locale.SR;
import xmpp.extensions.IqTranslator;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
//import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
//import ui.keys.UserActions;
import ui.IconTextElement;
import images.MenuIcons;
import images.ActionsIcons;
import images.RosterIcons;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author ad
 */

public class TranslateConfigForm extends DefForm {

    private SimpleString tfWarn;
    private TextInput tfBot;
    private TextInput tfSLang;
    private TextInput tfTLang;
    private TextInput tfSLangR;
    private TextInput tfTLangR;
    private DropChoiceBox tfSLang0;
    private DropChoiceBox tfTLang0;
    private DropChoiceBox tfSLangR0;
    private DropChoiceBox tfTLangR0;
    private Config config;

    public TranslateConfigForm() {
        super(SR.get(SR.MS_TRANSLATE));
        config = Config.getInstance();
        tfWarn=new SimpleString( SR.get(SR.MS_TRANS_MINIBOT) +" (minibot@server.tld/RESOURCE)", true);

        tfBot= new TextInput( "bqdiqt@jabbon.ru/BQDIQT", IqTranslator.bot, TextField.ANY);
        tfSLang= new TextInput( SR.get(SR.MS_TRANS_SLANG) +" " +SR.get(SR.MS_VALUE), IqTranslator.sLang, TextField.ANY);
        tfTLang= new TextInput( SR.get(SR.MS_TRANS_TLANG) +" " +SR.get(SR.MS_VALUE), IqTranslator.tLang, TextField.ANY);
        tfSLangR= new TextInput( SR.get(SR.MS_TRANS_SLANGR) +" " +SR.get(SR.MS_VALUE), IqTranslator.sLangR, TextField.ANY);
        tfTLangR= new TextInput( SR.get(SR.MS_TRANS_TLANGR) +" " +SR.get(SR.MS_VALUE), IqTranslator.tLangR, TextField.ANY);

        tfSLang0= new DropChoiceBox( SR.get(SR.MS_TRANS_SLANG));
        tfSLang0.append(SR.get("Other")); //0
        tfSLang0.append(SR.get("au - Auto")); //1
        tfSLang0.append(SR.get("en - English")); //2
        tfSLang0.append(SR.get("ru - Русский")); //3
        tfSLang0.append(SR.get("ar - Arabic")); //4
        tfSLang0.setSelectedIndex( 0);

        tfTLang0= new DropChoiceBox( SR.get(SR.MS_TRANS_TLANG));
        tfTLang0.append(SR.get("Other")); //0
        tfTLang0.append(SR.get("au - Auto")); //1
        tfTLang0.append(SR.get("en - English")); //2
        tfTLang0.append(SR.get("ru - Русский")); //3
        tfTLang0.append(SR.get("ar - Arabic")); //4
        tfTLang0.setSelectedIndex( 0);

        tfSLangR0= new DropChoiceBox( SR.get(SR.MS_TRANS_SLANGR));
        tfSLangR0.append(SR.get("Other")); //0
        tfSLangR0.append(SR.get("au - Auto")); //1
        tfSLangR0.append(SR.get("en - English")); //2
        tfSLangR0.append(SR.get("ru - Русский")); //3
        tfSLangR0.append(SR.get("ar - Arabic")); //4
        tfSLangR0.setSelectedIndex( 0);

        tfTLangR0= new DropChoiceBox( SR.get(SR.MS_TRANS_TLANGR));
        tfTLangR0.append(SR.get("Other")); //0
        tfTLangR0.append(SR.get("au - Auto")); //1
        tfTLangR0.append(SR.get("en - English")); //2
        tfTLangR0.append(SR.get("ru - Русский")); //3
        tfTLangR0.append(SR.get("ar - Arabic")); //4
        tfTLangR0.setSelectedIndex( 0);
    }

    public void cmdOk() {
        IqTranslator.bot= tfBot.getValue();
        IqTranslator.sLang= tfSLang.getValue();
        IqTranslator.tLang= tfTLang.getValue();
        IqTranslator.sLangR= tfSLangR.getValue();
        IqTranslator.tLangR= tfTLangR.getValue();

        destroyView();
    }

    public void destroyView() {
        config.saveUTF();
        super.destroyView();
    }

    protected void beginPaint(){
        update();
    }

    private void update(){
        itemsList.removeAllElements();

        itemsList.addElement( tfWarn);
        itemsList.addElement( tfBot);

        itemsList.addElement( tfSLang0);
        if( tfSLang0.getSelectedIndex() ==0)
            itemsList.addElement( tfSLang);
        else
            tfSLang.setValue( tfSLang0.getTextValue().substring(0, 2));

        itemsList.addElement( tfTLang0);
        if( tfTLang0.getSelectedIndex() ==0)
            itemsList.addElement( tfTLang);
        else
            tfTLang.setValue( tfTLang0.getTextValue().substring(0, 2));

        itemsList.addElement( tfSLangR0);
        if( tfSLangR0.getSelectedIndex() ==0)
            itemsList.addElement( tfSLangR);
        else
            tfSLangR.setValue( tfSLangR0.getTextValue().substring(0, 2));

        itemsList.addElement( tfTLangR0);
        if( tfTLangR0.getSelectedIndex() ==0)
            itemsList.addElement( tfTLangR);
        else
            tfTLangR.setValue( tfTLangR0.getTextValue().substring(0, 2));
    }
}
//#endif