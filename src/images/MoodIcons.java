/*
 * MoodIcons.java
 *
 * Created on 3.12.2005, 20:07
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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

//#ifdef PEP
package images;

/**
 *
 * @author EvgS
 */

public class MoodIcons extends ImageList {
    private final static String MOOD_ICONS_PATH = "/images/moods.png";

    private final static int ICONS_IN_ROW = 16;
    private final static int ICONS_IN_COL = 6;

    private static ImageList instance;

    public static ImageList getInstance() {
	if (instance == null) {
            instance = new MoodIcons();
        }
	return instance;
    }

    private MoodIcons() {
	super(MOOD_ICONS_PATH, ICONS_IN_COL, ICONS_IN_ROW);
    }  
}
//#endif