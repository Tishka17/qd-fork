/*
 * HistoryConfig.java
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

//#ifdef HISTORY
//# package history;
//# 
//# import io.NvStorage;
//# import java.io.DataInputStream;
//# import java.io.DataOutputStream;
//# import java.io.IOException;
//# import java.io.InputStream;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
//# import menu.Command;
//#endif
//# import ui.controls.form.CheckBox;
//# import ui.controls.form.DefForm;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.lcdui.TextField;
//# import ui.controls.form.TextInput;
//# import locale.SR;
//# import ui.controls.form.DropChoiceBox;
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
//# import io.file.browse.Browser;
//# import io.file.browse.BrowserListener;
//# 
//# /**
//#  *
//#  * @author aqent
//#  */
//# public class HistoryConfig extends DefForm implements BrowserListener {
//#     public static final int TYPE_RMS = 0;
//#     public static final int TYPE_FS = 1;
//# 
//#     private static HistoryConfig instance;
//#     public void BrowserFilePathNotify(String pathSelected) {
//#        historyFolder.setValue(pathSelected);
//#     }
//# 
//#     Command cmdPath;
//# 
//#     private DropChoiceBox historyType;
//#     private TextInput historyFolder;
//#     private CheckBox windows1251;
//#     private CheckBox translit;
//# 
//#     public HistoryConfig(){}
//#     public static HistoryConfig getInstance() {
//#        if (instance == null) instance = new HistoryConfig();
//#        return instance;
//#     }
//# 
//#     public static boolean msgLogConf = false;
//#     public static int historyTypeIndex = 0;
//#     public static String historyPath = "";
//#     public static boolean cp1251 = false;
//#     public static boolean transliterateFilenames = false;
//# 
//# 
//#     /** Creates a new instance of HistoryConfig */
//#     public HistoryConfig(Display display, Displayable pView) {
//#         super(display, pView, SR.get(SR.MS_HISTORY_OPTIONS));
//#         cmdPath=new Command(SR.get(SR.MS_SELECT_HISTORY_FOLDER), Command.SCREEN, 2);
//#         loadFromStorage();
//# 
//#            windows1251 = new CheckBox(SR.get(SR.MS_1251_CORRECTION), cp1251);
//#            translit = new CheckBox(SR.get(SR.MS_1251_TRANSLITERATE_FILENAMES), transliterateFilenames);
//# 
//#            historyFolder = new TextInput(display, SR.get(SR.MS_HISTORY_FOLDER), historyPath, null, TextField.ANY);
//#            historyType=new DropChoiceBox(display, SR.get(SR.MS_HISTORY_TYPE));
//#            historyType.append(SR.get(SR.MS_HISTORY_RMS));//0
//#            historyType.append('*' + SR.get(SR.MS_HISTORY_FS));//1
//#            //historyType.append('*' + SR.get(SR.MS_HISTORY_SERVER));//2
//#            historyType.setSelectedIndex(historyTypeIndex);
//#            itemsList.addElement(historyType);
//# 
//#         attachDisplay(display);
//#         this.parentView=pView;
//#     }
//# 
//#     protected void beginPaint(){
//#        if(historyType != null) {
//#           boolean isHistoryFs = itemsList.contains(historyFolder);
//#           int index = historyTypeIndex = historyType.getSelectedIndex();
//#           switch(index){
//#             case 2:
//#             case 0:
//#               if(contains(midlet.BombusQD.commands.cmdOk)) removeCommand(midlet.BombusQD.commands.cmdOk);
//#               if(isHistoryFs) {
//#                   itemsList.removeElement(historyFolder);
//#                   itemsList.removeElement(windows1251);
//#                   itemsList.removeElement(translit);
//#                   if(contains(cmdPath)) removeCommand(cmdPath);
//#               }
//#               break;
//#             case 1:
//#               addCommand(midlet.BombusQD.commands.cmdOk);
//#               if(!isHistoryFs) {
//#                   itemsList.addElement(historyFolder);
//#                   itemsList.addElement(windows1251);
//#                   itemsList.addElement(translit);
//#                   addCommand(cmdPath);
//#                   cmdPath.setImg(0x60);
//#               }
//#               break;
//#           }
//#        }
//#     }
//# 
//#     public void cmdOk() {
//#         destroyView();
//#     }
//# 
//# 
//#     public void destroyView(){
//#        saveToStorage();
//#         windows1251 = translit = null;
//#         historyType = null;
//#         historyFolder = null;
//#         itemsList.removeAllElements();
//#        display.setCurrent(parentView);
//#     }
//# 
//#     public void loadFromStorage(){
//#         DataInputStream inputStream = NvStorage.ReadFileRecord("history_storage", 0);
//#         try {
//#             historyPath=inputStream.readUTF();
//#             historyTypeIndex=inputStream.readInt();
//#             cp1251=inputStream.readBoolean();
//#             transliterateFilenames=inputStream.readBoolean();
//#             inputStream.close();
//#             inputStream=null;
//# 	} catch (Exception e) {
//#             try {
//#                 if (inputStream!=null) {
//#                     inputStream.close();
//#                     inputStream=null;
//#                 }
//#             } catch (IOException ex) {}
//# 	}
//#     }
//# 
//#     public void saveToStorage(){
//# 	try {
//#             DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
//#              cp1251 = windows1251.getValue();
//#              historyTypeIndex = historyType.getSelectedIndex();
//#              if(itemsList.contains(historyFolder)){
//#                historyPath = historyFolder.getValue();
//#              }
//#              transliterateFilenames = translit.getValue();
//#             outputStream.writeUTF(historyPath);
//#             outputStream.writeInt(historyTypeIndex);
//#             outputStream.writeBoolean(cp1251);
//#             outputStream.writeBoolean(transliterateFilenames);
//# 
//#             NvStorage.writeFileRecord(outputStream, "history_storage", 0, true);
//#             outputStream=null;
//# 	} catch (IOException e) { }
//#     }
//# 
//# 
//#     public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdPath) {
//#             new Browser(null, display, this, this, true);
//#             return;
//#         }
//#         destroyView();
//#     }
//# 
//# 
//#      public int showGraphicsMenu() {
//#          if(menuCommands.size()==0){
//#              cmdOk();
//#              return -1;
//#          }
//#          menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#          GMenuConfig.getInstance().itemGrMenu = GMenu.HISTORY_CONFIG;
//#          redraw();
//#          return GMenu.HISTORY_CONFIG;
//#      }
//#      public void touchLeftPressed(){
//#          showGraphicsMenu();
//#      }
//#      public String touchLeftCommand(){ return menuCommands.size() == 0 ? SR.get(SR.MS_OK) : SR.get(SR.MS_MENU); }
//# }
//#endif
