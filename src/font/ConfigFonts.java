/*
 * ConfigFonts.java
 *
 * Created on 20.05.2008, 15:37
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */

package font; 
import client.Config;
import client.StaticData;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import util.StringLoader;
import java.util.Vector;
import ui.controls.form.MultiLine;
import ui.controls.form.CheckBox;
import ui.controls.form.SimpleString;

public class ConfigFonts 
        extends DefForm {
    
    private Display display;
    
    private DropChoiceBox font1;
    private DropChoiceBox font2;
    private DropChoiceBox font3;
    private DropChoiceBox font4;
    private DropChoiceBox font5;
    
    StaticData sd=StaticData.getInstance();
    
    Config cf;
    
    private Vector[] files;
    //private DropChoiceBox drawedfonts;   
    MultiLine item=null;  
    

    private CheckBox cursivUse;
    
    /** Creates a new instance of ConfigFonts */
    public ConfigFonts(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_FONTS_OPTIONS));
        this.display=display;
        
        cf=Config.getInstance();
        itemsList.addElement(new SimpleString("(!)"+SR.get(SR.MS_RESTART_APP), true));

        font1=new DropChoiceBox(display, SR.get(SR.MS_ROSTER_FONT));
        font1.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        font1.append(SR.get(SR.MS_FONTSIZE_SMALL));
        font1.append(SR.get(SR.MS_FONTSIZE_LARGE));
        font1.setSelectedIndex(cf.rosterFont/8);
        itemsList.addElement(font1);

        font2=new DropChoiceBox(display, SR.get(SR.MS_MESSAGE_FONT));
        font2.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        font2.append(SR.get(SR.MS_FONTSIZE_SMALL));
        font2.append(SR.get(SR.MS_FONTSIZE_LARGE));
        font2.setSelectedIndex(cf.msgFont/8);
        itemsList.addElement(font2);
 
        font3=new DropChoiceBox(display, SR.get(SR.MS_BAR_FONT));
        font3.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        font3.append(SR.get(SR.MS_FONTSIZE_SMALL));
        font3.append(SR.get(SR.MS_FONTSIZE_LARGE));
        font3.setSelectedIndex(cf.barFont/8);
        itemsList.addElement(font3);
        
        font4=new DropChoiceBox(display, SR.get(SR.MS_POPUP_FONT));
        font4.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        font4.append(SR.get(SR.MS_FONTSIZE_SMALL));
        font4.append(SR.get(SR.MS_FONTSIZE_LARGE));
        font4.setSelectedIndex(cf.baloonFont/8);
        itemsList.addElement(font4);
        
        font5=new DropChoiceBox(display, SR.get(SR.MS_MENU_FONT));
        font5.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        font5.append(SR.get(SR.MS_FONTSIZE_SMALL));
        font5.append(SR.get(SR.MS_FONTSIZE_LARGE));
        font5.setSelectedIndex(cf.graphicsMenuFont);
        itemsList.addElement(font5);
        
        cursivUse = new CheckBox(SR.get(SR.MS_Italic), cf.cursivUse); itemsList.addElement(cursivUse); 
         /*
        item=new MultiLine(SR.get(SR.MS_DRAWED_FONT)+":", (cf.use_drawed_font)?cf.drwd_fontname:"<..>", super.superWidth); 
        item.selectable=true;
        itemsList.addElement(item);    
       
        try {
            files=new StringLoader().stringLoader("/images/fonts/fonts.txt",2);
            if (files[0].size()>0) {
                drawedfonts=new DropChoiceBox(display, SR.get(SR.MS_SELECT));
                for (int i=0; i<files[0].size(); i++) {
                    drawedfonts.append((String)files[1].elementAt(i));
                }
                drawedfonts.setSelectedIndex(0);
                itemsList.addElement(drawedfonts);
            }
        } catch (Exception e) {}  
         */

        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void cmdOk() {
        FontCache.roster=cf.rosterFont=font1.getValue()*8; //roster
        FontCache.msg=cf.msgFont=font2.getValue()*8; //msg
        FontCache.bar=cf.barFont=font3.getValue()*8; //bar
        FontCache.baloon=cf.baloonFont=font4.getValue()*8; //balloon

        cf.graphicsMenuFont=font5.getValue();
        cf.cursivUse=cursivUse.getValue();
        /*
        try {
            if (drawedfonts.getSelectedIndex()>-1) {
                cf.drwd_fontname = (String)files[0].elementAt(drawedfonts.getSelectedIndex());
            }
        } catch (Exception ex) {}
         

        if(cf.drwd_fontname.equals("no")==true){
           cf.use_drawed_font = false;
        }else{
           cf.use_drawed_font = true;
           FontClass.getInstance().Init(cf.drwd_fontname);
        }
        */
        
        sd.roster.updateBarsFont();
        sd.roster.reEnumRoster();
        destroyView();
    }
}