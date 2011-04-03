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
package io;
import java.util.Vector; 
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import util.StringLoader;
import client.*;
import ui.controls.form.LinkString;

public class TranslateSelect extends DefForm {
    private DropChoiceBox langFrom; 
    private DropChoiceBox langTo; 
    private LinkString fastTr;
    private LinkString fastTr_;
    
    private Vector langs[];
    StaticData sd=StaticData.getInstance();
    Config cf;
    private Contact to;
    private String text;
    private String fromMucNick;
    private boolean trCMsgList;
    private int cursor;    
    
    public TranslateSelect(Contact to,String text,String fromMucNick,
            boolean trCMsgList,int cursor) {
        super(SR.get(SR.MS_TRANSLATE));
        this.to=to;
        this.text=text;
        this.trCMsgList=trCMsgList;
        this.cursor=cursor;
        this.fromMucNick=fromMucNick;
        cf=Config.getInstance();
        
	langs=new StringLoader().stringLoader("/lang/translate.txt",3);
        itemsList.addElement(new SimpleString(text.length()>50?text.substring(0,38)+"..":text, true));
        
        if(cf.langpair.length()>0){
          fastTr=new LinkString(cf.langpair)
                { public void doAction() { runTranslate(true); }}; //en==>ru def
          fastTr_=new LinkString(cf.langpair.substring(5,7)+"==>"+cf.langpair.substring(0,2))
                { public void doAction() { runTranslate(false); }};//ru==>en
          itemsList.addElement(fastTr);
          if(cf.langpair.startsWith("au")==false){
            itemsList.addElement(fastTr_);
          }
        }

        if (langs[0].size()>1) {
            itemsList.addElement(new SpacerItem(12));
            langFrom=new DropChoiceBox("from");
            for (int i=0; i<langs[0].size(); i++) {
                String label=(String) langs[1].elementAt(i);
                String langCode=(String) langs[0].elementAt(i);
                langFrom.append(label);
                  langFrom.setSelectedIndex(0);
            }
            itemsList.addElement(langFrom);
            
            itemsList.addElement(new SpacerItem(5));
            langTo=new DropChoiceBox("to");
            for (int i=0; i<langs[0].size(); i++) {
                String label=(String) langs[1].elementAt(i);
                String langCode=(String) langs[0].elementAt(i);
                langTo.append(label);
                langTo.setSelectedIndex(3);
            }
            itemsList.addElement(langTo);            
        }
    }
    
    private void runTranslate(boolean pair){
        TranslateText tr = new TranslateText();
        tr.runTranslate(getParentView(),to,text,
            pair?cf.langpair.substring(0,2):cf.langpair.substring(5,7),
            pair?cf.langpair.substring(5,7):cf.langpair.substring(0,2),
          fromMucNick,trCMsgList,cursor);        
    }
    
    public void cmdOk() {
        if (langs[0].size()>1) {
            cf.lang=(String)langs[0].elementAt(langFrom.getSelectedIndex());
            TranslateText tr = new TranslateText();
            
           if(((String)langs[0].elementAt(langTo.getSelectedIndex())).indexOf("au")>-1){
           } else{
            tr.runTranslate(getParentView(),to,text,
                    (String)langs[0].elementAt(langFrom.getSelectedIndex()),
                    (String)langs[0].elementAt(langTo.getSelectedIndex()),fromMucNick,trCMsgList,cursor);
            cf.langpair=(String)langs[0].elementAt(langFrom.getSelectedIndex())+
                    "==>"+(String)langs[0].elementAt(langTo.getSelectedIndex());
            }
        }
    }
}

