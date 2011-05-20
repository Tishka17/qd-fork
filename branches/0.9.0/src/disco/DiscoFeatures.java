/*
 * DiscoFeatures.java
 *
 * Created on 6.07.2006, 23:30
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
//#ifdef SERVICE_DISCOVERY 
package disco;

import java.util.Vector;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;

/**
 *
 * @author Eugene Stahov
 */

public class DiscoFeatures extends DefForm {
    public DiscoFeatures(String entity, Vector features) {
        super(entity);

        int size = features.size();
        for (int i = 0; i < size; ++i) {
            SimpleString item = new SimpleString((String)features.elementAt(i));
            item.setSelectable(true);

            addControl(item);
        }
    }
}
//#endif