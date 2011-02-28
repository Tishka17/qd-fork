/*
 * InfoWindow.java
 *
 * Created on 25.05.2008, 19:29
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package info;

import client.Config;
import client.StaticData;
import javax.microedition.io.ConnectionNotFoundException;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
import ui.controls.form.LinkString;
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
import ui.controls.AlertBox;

/**
 *
 * @author ad
 */
public class InfoWindow
        extends DefForm {

    Object name;
    StaticData sd=StaticData.getInstance();

//#ifndef MENU
    public Command cmdOk;
//#endif

    /**
     * Creates a new instance of InfoWindow
     */

    private void showMsg() {
          String authMsg = "";
          if(midlet.BombusQD.cf.userAppLevel == 0){
              midlet.BombusQD.cf.userAppLevel = 1;
              authMsg = "Advanced Mode now ON";
          } else {
            midlet.BombusQD.cf.userAppLevel = 0;
            authMsg = "Advanced Mode now OFF!";
          }
          midlet.BombusQD.cf.saveInt();
          new AlertBox( "Info", authMsg , display, parentView, false) {
             public void yes() {}
             public void no() {}
          };
    }


    int auth = 0;
    public void keyPressed(int keyCode){
      switch(auth){
          /*1*/ case 0: if(keyCode == KEY_NUM5) auth++; break;
          /*2*/ case 1: if(keyCode == KEY_NUM1) auth++; else auth = 0; break;
          /*3*/ case 2: if(keyCode == KEY_NUM2) auth++; else auth = 0; break;
      }
      if( auth == 3 ) {
          showMsg();
          auth = 0;
      }
      super.keyPressed(keyCode);
    }

    private Displayable infoWindow;
    public InfoWindow(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_ABOUT));

        cmdOk = new Command(SR.get(SR.MS_COPY), Command.SCREEN, 1);
        this.display=display;

        name=new MultiLine("Current Threads: ", Integer.toString(Thread.activeCount()), super.superWidth);
        midlet.BombusQD.sd.roster.systemGC();

        ((MultiLine)name).selectable=true;
        //itemsList.addElement(name);

        name=new MultiLine(Version.getName(), Version.getVersionNumber()+
                "\n"+Config.platformName+
                "\nMobile Jabber client" , super.superWidth);

        ((MultiLine)name).selectable=true;
        itemsList.addElement(name);

        infoWindow = this;
        name = new LinkString("QD ".concat(SR.get(SR.MS_QD_NEWS))){
             public void doAction() {
               new GetFileServer(midlet.BombusQD.getInstance().display, infoWindow);
             }
        };
        itemsList.addElement(name);

        if(midlet.BombusQD.sd.roster.isLoggedIn()) {
           name = new LinkString(SR.get(SR.MS_SUPPORT)){
             public void doAction() {
                 new conference.ConferenceForm(
                         midlet.BombusQD.getInstance().display,
                         midlet.BombusQD.sd.roster,
                         "BombusQD@",
                         "qd@conference.jabber.ru", null, false);
             }
           };
           itemsList.addElement(name);
        }

//#ifdef STATS
//#         name = new LinkString(SR.get(SR.MS_STATS)){
//#              public void doAction() {
//#                  new stats.StatsWindow(midlet.BombusQD.getInstance().display);
//#              }
//#         };
//#         itemsList.addElement(name);
//#endif

        /*
        itemsList.addElement(new LinkString("Create NullPointer") {
           public void doAction() {
               //try{
                 String text = null;
                 int pos = text.indexOf("text");
               //} catch (Exception e) { e.printStackTrace(); }
           }}
        );
        itemsList.addElement(new LinkString("Create ArrayIndexOutOfBounds") {
           public void doAction() {
               //try{
                 int[] arr = new int[2];
                 for(int i = 0; i <= 3; ++i) arr[i] = i;
               //} catch (Exception e) { e.printStackTrace(); }
           }}
        );
        itemsList.addElement(new LinkString("Create OutOfMemory") {
           public void doAction() {
               //try{
               Vector array = new Vector();
               for (int i = 0; i < 30; ++i) {
                  for (int k = 0; k < 20; ++k) {
                      for (int m = 0; m < 10; ++m) {
                          Object add = array.toString();
                          array.addElement(add);
                      }
                  }
               }
               //} catch (Exception e) { e.printStackTrace(); }
           }}
        );
         */
        if(midlet.BombusQD.cf.isTouchPhone) {
          name=new MultiLine("Easter Egg", "Press link under this text", super.superWidth);
          ((MultiLine)name).selectable=false;
          itemsList.addElement(name);
          name = new LinkString("Yes,give me egg"){
              public void doAction() { showMsg(); }
          };
          itemsList.addElement(name);
          itemsList.addElement(new SpacerItem(5));
        } else {
             name=new MultiLine("Easter Egg:", "Press 5-1-2 keys to lock/unlock new options", super.superWidth);
             ((MultiLine)name).selectable=false;
             itemsList.addElement(name);
        }

        name=new MultiLine("Copyright (c) 2005-2010",
                "Eugene Stahov (evgs,Bombus);\nDaniel Apatin (ad,BombusMod);\nAlexej Kotov(aqent,BombusQD);\n" +
                "Andrey Tikhonov(Tishka17,BombusQD)\n" +
                "Distributed under GPL v2 License \n", super.superWidth);
        ((MultiLine)name).selectable=true;
        itemsList.addElement(name);

        name = new MultiLine("Thanks to:","Testing: zaetz,balor,demon(Dmitry Krylov),magnit,Sniffy,NNn,DsXack and many others\n" +
                "Patches: Vladimir Krukov (aspro),vinz@\n" +
                "Graphics: Xa,Makasim\n" +
                "Actions icons: Rederick Asher\n" +
                "Site managment: BiLLy\n" +
                "Jimm Dev's for back.png ;)\n" +
                "Windows Fonts: magdelphi(mobilab.ru)" +
                "\nMathFP library" +
                "\nSmiles Author: Copyright (c) Aiwan. Kolobok smiles",super.superWidth);
        ((MultiLine)name).selectable=false;
        itemsList.addElement(name);
        itemsList.addElement(new LinkString("http://www.kolobok.us")
                { public void doAction() { try { BombusQD.getInstance().platformRequest("http://www.kolobok.us"); }
                  catch (ConnectionNotFoundException ex) { }}}
        );

        itemsList.addElement(new LinkString("http://bombusmod-qd.wen.ru")
                { public void doAction() { try { BombusQD.getInstance().platformRequest("http://bombusmod-qd.wen.ru"); }
                  catch (ConnectionNotFoundException ex) { }}}
        );
        name=new LinkString("http://bombusmod.net.ru"){ public void doAction() { try { BombusQD.getInstance().platformRequest("http://bombusmod.net.ru"); } catch (ConnectionNotFoundException ex) { }}};
        itemsList.addElement(name);

        itemsList.addElement(new SpacerItem(10));

        StringBuffer memInfo=new StringBuffer(SR.get(SR.MS_FREE));
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10)
               .append("\n")
               .append(SR.get(SR.MS_TOTAL))
               .append(Runtime.getRuntime().totalMemory()>>10);
        name=new MultiLine(SR.get(SR.MS_MEMORY), memInfo.toString(), super.superWidth);
        ((MultiLine)name).selectable=true;
        itemsList.addElement(name);
        memInfo=null;


//#ifndef MENU
        super.removeCommand(midlet.BombusQD.commands.cmdOk);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             addCommand(cmdOk); cmdOk.setImg(0x43);
//#         }
//#endif
//#endif
        attachDisplay(display);
        enableListWrapping(false);
        this.parentView=pView;
    }


//#ifdef CLIPBOARD
//#     public void cmdOk(){
//#         StringBuffer memory=new StringBuffer(SR.get(SR.MS_FREE));
//#         System.gc();
//#         memory.append(Runtime.getRuntime().freeMemory()>>10)
//#                .append("\n")
//#                .append(SR.get(SR.MS_TOTAL))
//#                .append(Runtime.getRuntime().totalMemory()>>10);
//#ifdef DEBUG
//#         System.out.println(ClipBoard.getClipBoard());
//#endif
//#         destroyView();
//# 
//#     }
//#endif




    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifndef GRAPHICS_MENU
    super.commandState();
//#endif
        removeCommand(cmdCancel);
        removeCommand(cmdOk);
    }


//#ifdef MENU_LISTENER
    /*
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU; }

//#ifdef GRAPHICS_MENU
//#     public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this,null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.INFO_WINDOW;
//#         redraw();
//#         return GMenu.INFO_WINDOW;
//#     }
//#      */
//#else
    public void touchLeftPressed(){
        showMenu();
    }

    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_STATS), null, menuCommands);
   }
//#endif


//#endif


    public void commandAction(Command command, Displayable displayable) {
	if (command==cmdOk) {
	    cmdOk();
	}
        super.commandAction(command, displayable);
    }

    /*
    private String getAbilities() {
        Vector abilitiesList=new Vector(0);
//#ifdef ADHOC
//#ifdef PLUGINS
//#         if (sd.Adhoc)
//#endif
//#             abilitiesList.addElement((String)"ADHOC");
//#endif
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (sd.Archive)
//#endif
            abilitiesList.addElement((String)"ARCHIVE");
//#endif
//#ifdef AUTOSTATUS
//#         abilitiesList.addElement((String)"AUTOSTATUS");
//#endif
//#ifdef AUTOTASK
//#         abilitiesList.addElement((String)"AUTOTASK");
//#endif
//#ifdef BACK_IMAGE
//#         abilitiesList.addElement((String)"BACK_IMAGE");
//#endif
//#ifdef CAPTCHA
//#         abilitiesList.addElement((String)"CAPTCHA");
//#endif
//#ifdef CHANGE_TRANSPORT
//#ifdef PLUGINS
//#         if (sd.ChangeTransport)
//#endif
//#             abilitiesList.addElement((String)"CHANGE_TRANSPORT");
//#endif
//#ifdef CHECK_VERSION
//#ifdef PLUGINS
//#         if (sd.Upgrade)
//#endif
//#             abilitiesList.addElement((String)"CHECK_VERSION");
//#endif
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
            abilitiesList.addElement((String)"CLIENTS_ICONS");
//#endif
//#ifdef CLIPBOARD
//#         abilitiesList.addElement((String)"CLIPBOARD");
//#endif
//#ifdef CONSOLE
//#ifdef PLUGINS
//#         if (sd.Console)
//#endif
//#             abilitiesList.addElement((String)"CONSOLE");
//#endif
//#ifdef COLOR_TUNE
//#ifdef PLUGINS
//#         if (sd.Colors)
//#endif
//#             abilitiesList.addElement((String)"COLOR_TUNE");
//#endif
//#ifdef DETRANSLIT
//#         abilitiesList.addElement((String)"DETRANSLIT");
//#endif
//#ifdef ELF
//#         abilitiesList.addElement((String)"ELF");
//#endif
//#ifdef FILE_IO
        abilitiesList.addElement((String)"FILE_IO");
//#endif
//#ifdef FILE_TRANSFER
//#ifdef PLUGINS
//#         if (sd.FileTransfer)
//#endif
            abilitiesList.addElement((String)"FILE_TRANSFER");
//#endif
//#ifdef GRADIENT
//#         abilitiesList.addElement((String)"GRADIENT");
//#endif

//#ifdef IMPORT_EXPORT
//#ifdef PLUGINS
//#         if (sd.IE)
//#endif
//#             abilitiesList.addElement((String)"IMPORT_EXPORT");
//#endif
//#ifdef LAST_MESSAGES
//# 
//#endif
//#ifdef LOGROTATE
//#         abilitiesList.addElement((String)"LOGROTATE");
//#endif
//#ifdef MENU_LISTENER
        abilitiesList.addElement((String)"MENU_LISTENER");
//#endif
//#ifdef NICK_COLORS
        abilitiesList.addElement((String)"NICK_COLORS");
//#endif
//#ifdef NON_SASL_AUTH
//#         abilitiesList.addElement((String)"NON_SASL_AUTH");
//#endif
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement((String)"PEP");
//#endif
//#ifdef PEP_ACTfIVITY
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             //abilitiesList.addElement((String)"PEP_ACTIVITY");
//#endif
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement((String)"PEP_TUNE_ACTIVITY_MOOD");
//#endif
//#ifdef PLUGINS
//#         abilitiesList.addElement((String)"PLUGINS");
//#endif
//#ifdef POPUPS
        abilitiesList.addElement((String)"POPUPS");
//#endif
//#ifdef REQUEST_VOICE
//#         abilitiesList.addElement((String)"REQUEST_VOICE");
//#endif
//#ifdef PRIVACY
//#ifdef PLUGINS
//#         if (sd.Privacy)
//#endif
            abilitiesList.addElement((String)"PRIVACY");
//#endif
//#ifdef SASL_XGOOGLETOKEN
//#         abilitiesList.addElement((String)"SASL_XGOOGLETOKEN");
//#endif
//#ifdef SE_LIGHT
//#         abilitiesList.addElement((String)"SE_LIGHT");
//#endif
//#ifdef SERVICE_DISCOVERY
        abilitiesList.addElement((String)"SERVICE_DISCOVERY");
//#endif
//#ifdef SMILES
        abilitiesList.addElement((String)"SMILES");
//#endif
//#ifdef STATS
//#ifdef PLUGINS
//#         if (sd.Stats)
//#endif
//#             abilitiesList.addElement((String)"STATS");
//#endif
//#ifdef USER_KEYS
//#         abilitiesList.addElement((String)"USER_KEYS");
//#endif
//#ifdef USE_ROTATOR
        if(midlet.BombusQD.cf.useLowMemory_userotator==false){    abilitiesList.addElement((String)"USE_ROTATOR");   }
//#endif
//#ifdef WMUC
//#         abilitiesList.addElement((String)"WMUC");
//#endif
//#ifdef WSYSTEMGC
//#         //abilitiesList.addElement((String)"WSYSTEMGC");
//#endif
//#ifdef ZLIB
        abilitiesList.addElement((String)"ZLIB");
//#endif

        StringBuffer abilities=new StringBuffer();

	for (Enumeration ability=abilitiesList.elements(); ability.hasMoreElements(); ) {
            abilities.append((String)ability.nextElement());
            abilities.append(", ");
	}
        String ab=abilities.toString();
        abilities=null;
        abilitiesList=null;
        return ab.substring(0, ab.length()-2);
    }
     */
}
