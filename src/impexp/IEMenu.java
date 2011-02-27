/*
 * IEMenu.java
 *
 * Created on 24.01.2008, 21:55
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
//#ifdef IMPORT_EXPORT 
//# package impexp;
//# 
//# import io.file.browse.Browser;
//# import io.file.browse.BrowserListener;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import locale.SR;
//# import menu.Menu;
//# import menu.MenuItem;
//# 
//# /**
//#  *
//#  * @author ad
//#  */
//# public class IEMenu extends Menu implements BrowserListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_IE");
//#endif
//# 
//#     public static final int ARCHIVE_EXPORT = 0;
//#     public static final int ARCHIVE_IMPORT = 1;
//#     public static final int ACCOUNT_EXPORT = 2;
//#     public static final int ACCOUNT_IMPORT = 3;
//# 
//#     public IEMenu(Display display, Displayable pView) {
//#         super(SR.get(SR.MS_IMPORT_EXPORT), null, null);
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive) {
//#endif
//#         addItem(SR.get(SR.MS_ARCHIVE) + ": " + SR.get(SR.MS_LOAD_FROM_FILE), ARCHIVE_IMPORT);
//#         addItem(SR.get(SR.MS_ARCHIVE) + ": " + SR.get(SR.MS_SAVE_TO_FILE), ARCHIVE_EXPORT);
//#ifdef PLUGINS
//#         }
//#endif
//# 
//#         addItem(SR.get(SR.MS_ACCOUNTS) + ": " + SR.get(SR.MS_LOAD_FROM_FILE), ACCOUNT_IMPORT);
//#         addItem(SR.get(SR.MS_ACCOUNTS) + ": " + SR.get(SR.MS_SAVE_TO_FILE), ACCOUNT_EXPORT);
//# 
//#         attachDisplay(display);
//#         this.parentView = pView;
//#     }
//# 
//#     public void eventOk() {
//#         MenuItem mItem = (MenuItem) getFocusedObject();
//#         
//#         switch (mItem.index) {
//#             case ARCHIVE_IMPORT:
//#             case ACCOUNT_IMPORT:
//#                 new Browser(null, display, this, this, false);
//#                 break;
//#             case ARCHIVE_EXPORT:
//#             case ACCOUNT_EXPORT:
//#                 new Browser(null, display, this, this, true);
//#                 break;
//#         }
//#     }
//# 
//#     public void BrowserFilePathNotify(String path) {
//#         MenuItem mItem = (MenuItem) getFocusedObject();
//#         
//#         switch (mItem.index) {
//#             case ARCHIVE_EXPORT:
//#             case ARCHIVE_IMPORT:
//#                 new ArchiveTemplates(path, mItem.index);
//#                 break;
//#             case ACCOUNT_EXPORT:
//#             case ACCOUNT_IMPORT:
//#                 new Accounts(path, mItem.index);
//#                 break;
//#         }
//#         setWobble(3, null, SR.get(SR.MS_DONE));
//#     }
//# }
//#endif
