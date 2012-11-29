/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//#ifdef TRANSLATE
package client;

import locale.SR;
import xmpp.extensions.IqTranslator;
import ui.controls.form.DefForm;
//import ui.controls.form.DropChoiceBox;
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
    private Config config;

    public TranslateConfigForm() {
        super(SR.get(SR.MS_TRANSLATE));
        config = Config.getInstance();
        tfWarn=new SimpleString( "Please set FULL minibot jid! (minibot@server.tld/RESOURCE)", true);

        tfBot= new TextInput( "bqdiqt@jabbon.ru/BQDIQT", IqTranslator.bot, TextField.ANY);
        tfSLang= new TextInput( "Source lang in", IqTranslator.sLang, TextField.ANY);
        tfTLang= new TextInput( "Target lang in", IqTranslator.tLang, TextField.ANY);
        tfSLangR= new TextInput( "Source lang out", IqTranslator.sLangR, TextField.ANY);
        tfTLangR= new TextInput( "Target lang out", IqTranslator.tLangR, TextField.ANY);        
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
        itemsList.addElement( tfSLang);
        itemsList.addElement( tfTLang);
        itemsList.addElement( tfSLangR);
        itemsList.addElement( tfTLangR);
    }
}
//#endif