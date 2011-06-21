/*
 * TranslateSelect.java
 *
 *
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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
 *
 */

//#ifdef TRANSLATE
package io;

import client.Config;
import client.Contact;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.SpacerItem;
import util.StringLoader;
import ui.controls.form.LinkString;
import ui.controls.form.TextInput;

public class TranslateSelect extends DefForm {
    private static final int MAX_TEXT_LENGTH = 1024;

    private TextInput input;
    private DropChoiceBox langFrom;
    private DropChoiceBox langTo;

    private Vector langs[];
    private Config cf;
    private Contact to;
    private String text;

    public TranslateSelect(Contact to,String text) {
        super(SR.get(SR.MS_TRANSLATE));
        this.to = to;
        cf = Config.getInstance();

        langs=new StringLoader().stringLoader("/lang/translate.txt",3);
        if (text != null) {
            if (text.length() > MAX_TEXT_LENGTH) {
                this.text = text.substring(0, MAX_TEXT_LENGTH);
            } else {
                this.text = text;
            }
        }
        input = new TextInput("Text for translation", this.text, TextField.ANY);
        addControl(input);

        addControl(new SpacerItem(6));

        if (cf.langpair.length() > 0) {
            addControl(new LinkString(cf.langpair) {
                public void doAction() {
                    runTranslate(true);
                }
            });
            if (!cf.langpair.startsWith("au")) {
                addControl(new LinkString(cf.langpair.substring(5, 7) + "==>" + cf.langpair.substring(0, 2)) {
                    public void doAction() {
                        runTranslate(false);
                    }
                });
            }
        }

        if (langs[0].size() > 1) {
            addControl(new SpacerItem(6));
            langFrom = new DropChoiceBox("from");
            for (int i = 0; i < langs[0].size(); i++) {
                String label = (String)langs[1].elementAt(i);
                langFrom.append(label);
            }
            langFrom.setSelectedIndex(0);
            addControl(langFrom);

            addControl(new SpacerItem(5));
            langTo = new DropChoiceBox("to");
            for (int i = 0; i < langs[0].size(); i++) {
                String label = (String)langs[1].elementAt(i);
                langTo.append(label);
            }
            langTo.setSelectedIndex(3);
            addControl(langTo);
        }
    }

    private void runTranslate(boolean pair){
        TranslateText tr = new TranslateText();
        tr.runTranslate(to, input.getValue(),
            pair?cf.langpair.substring(0,2):cf.langpair.substring(5,7),
            pair?cf.langpair.substring(5,7):cf.langpair.substring(0,2));
    }

    public void cmdOk() {
        if (langs[0].size()>1) {
            cf.lang=(String)langs[0].elementAt(langFrom.getSelectedIndex());
            TranslateText tr = new TranslateText();

           if(((String)langs[0].elementAt(langTo.getSelectedIndex())).indexOf("au")>-1){
           } else{
            tr.runTranslate(to, input.getValue(),
                    (String)langs[0].elementAt(langFrom.getSelectedIndex()),
                    (String)langs[0].elementAt(langTo.getSelectedIndex()));
            cf.langpair=(String)langs[0].elementAt(langFrom.getSelectedIndex())+
                    "==>"+(String)langs[0].elementAt(langTo.getSelectedIndex());
            }
        }
    }
}
//#endif
