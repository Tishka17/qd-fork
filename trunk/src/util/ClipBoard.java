/*
 * ClipBoard.java
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
 *
 */
//#ifdef CLIPBOARD
package util;


//#if Android
//# import android.text.ClipboardManager;
//# import org.bombusqd.BombusQDActivity;
//#endif


public final class ClipBoard {
    
//#if Android
//#   private static ClipboardManager manager;
//#   public static final ClipBoard instance = new ClipBoard();
//#else 
    private static String clipBoard = "";    
//#endif
    

    private ClipBoard() {}
    
//#if Android
//#    public void init() {
//#        manager = (ClipboardManager)BombusQDActivity.getInstance().getApplicationContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
//#    }
//#endif    

    public static String getClipBoard() {
//#if Android
//#   return manager.getText().toString();
//#else
        return clipBoard;
//#endif
    }

    public static void setClipBoard(String str) {
//#if Android
//#   manager.setText(str);
//#else
        clipBoard = (str.length() > 4096) ? str.substring(0, 4095) : str;
//#endif
    }

    public static void addToClipBoard(String str) {
        String tmp = getClipBoard();
        setClipBoard(tmp + "\n\n" + str);

    }

    public static boolean isEmpty() {
//#if Android
//#   return !manager.hasText();
//#else
        if (clipBoard != null && clipBoard.length() > 0) {
            return false;
        }
        return true;
//#endif
    }
}
//#endif
