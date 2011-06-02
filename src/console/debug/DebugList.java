/*
 * DebugList.java
 *
 * Created on 7.04.2008, 16:05
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

//#ifdef DEBUG_CONSOLE
//# package console.debug;
//# 
//# import client.MsgItem;
//# import java.util.Vector;
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# 
//# public class DebugList {
//#     Vector stanzas = new Vector(0);
//#     private static DebugList instance;
//# 
//#     public static DebugList get() {
//#         if (instance == null) {
//#             instance = new DebugList();
//#         }
//#         return instance;
//#     }
//# 
//#     public MsgItem getMessage(int index) {
//#         try {
//#             return (MsgItem) stanzas.elementAt(index);
//#         } catch (Exception e) {}
//#         return null;
//#     }
//# 
//#     public void add(String msg, int type) {
//#         if (midlet.BombusQD.cf.debug) {
//#             try {
//#                 int free = (int)Runtime.getRuntime().freeMemory() >> 10;
//#                 int total = (int)Runtime.getRuntime().totalMemory() >> 10;
//#                 MsgItem stanza = new MsgItem((byte)type, "debug", null, "[" + free + "/" + total + "]\t" + msg.toString());
//#                 stanza.itemCollapsed = false;
//#                 stanzas.addElement(stanza);
//#                 stanza = null;
//#             } catch (Exception e) {
//#             }
//#         }
//#     }
//# 
//#     public int size() {
//#         return stanzas.size();
//#     }
//# }
//#endif