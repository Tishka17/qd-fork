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
//# package util;
//# 
//# import client.Msg;
//# 
//# public final class ClipBoard {
//#     private static String clipBoard = "";
//# 
//#     private ClipBoard() {
//#         // empty;
//#     }
//# 
//#     public static String getClipBoard() {
//#         return clipBoard;
//#     }
//# 
//#     public static void setClipBoard(String str) {
//#         clipBoard = (str.length() > 4096) ? str.substring(0, 4095) : str;
//#     }
//# 
//#     public static void addToClipBoard(String str) {
//#         String tmp = clipBoard;
//# 
//#         clipBoard = tmp + "\n\n" + str;
//#     }
//# 
//#     public static boolean isEmpty() {
//#         boolean empty = true;
//#         if (clipBoard != null && clipBoard.length() > 0) {
//#             return false;
//#         }
//#         return empty;
//#     }
//# 
//#     public static void add(Msg msg) {
//#         clipBoard = StringUtils.quoteString(msg);
//#     }
//# 
//#     public static void append(Msg msg) {
//#         StringBuffer buf = new StringBuffer(clipBoard);
//# 
//#         buf.append("\n\n");
//#         buf.append(StringUtils.quoteString(msg));
//# 
//#         clipBoard = buf.toString();
//#     }
//# }
//#endif
