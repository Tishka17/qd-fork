/*
 * ImageBuffer.java
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
 
package ui; 
import javax.microedition.lcdui.Image;

public class ImageBuffer {
    

    public ImageBuffer() { }
    
    public Image bgnd_checkers=null;
    public Image checkers_white=null;
    public Image checkers_black=null;
    
    private static ImageBuffer instance;
    
   
    public static ImageBuffer getInstance(){
	if (instance==null) {
	    instance=new ImageBuffer();
	}
	return instance;
    }      
    
}
 */
