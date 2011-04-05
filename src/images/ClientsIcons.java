/*
 * ClientsIcons.java
 *
 * Created on 9.06.2008, 22:45
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

//#ifdef CLIENTS_ICONS
package images;

/**
 *
 * @author ad
 */

public class ClientsIcons extends ImageList {
    private final static String ACTIVITY_ICONS_PATH = "/images/clients.png";

    private final static int ICONS_IN_ROW = 16;
    private final static int ICONS_IN_COL = 0;

    private static ImageList instance;

    private ClientsIcons() {
        super(ACTIVITY_ICONS_PATH, ICONS_IN_COL, ICONS_IN_ROW);
    }
    
    public static ImageList getInstance() {
	if (instance == null) {
            instance = new ClientsIcons();
        }
	return instance;
    }
}
//#endif
