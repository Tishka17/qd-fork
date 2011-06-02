/*
 * StanzasList.java
 *
 * Created on 7.04.2008, 13:48
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

//#ifdef XML_CONSOLE
//# package console.xml;
//# 
//# import client.Msg;
//# import java.util.Vector;
//# 
//# /**
//#  *
//#  * @author ad
//#  */
//# 
//# public class XMLList {
//#     Vector stanzas = new Vector(0);
//#     public static boolean enabled = false;
//#     private static XMLList instance;
//# 
//#     public static XMLList getInstance() {
//#         if (instance == null) {
//#             instance = new XMLList();
//#         }
//#         return instance;
//#     }
//# 
//#     public Msg getMessage(int index) {
//#         try {
//#             return (Msg) stanzas.elementAt(index);
//#         } catch (Exception e) {}
//#         return null;
//#     }
//# 
//#     public void add(String msg, int type) {
//#         if (enabled) {
//#             try {
//#                 if (msg.length() > 0) {
//#                     Msg stanza = new Msg((byte) type, "local", null, msg.toString());
//#                     stanza.collapse();
//#                     stanzas.addElement(stanza);
//#                 }
//#             } catch (Exception e) {}
//#         }
//#     }
//# 
//#     public int size() {
//#         return stanzas.size();
//#     }
//# }
//#endif