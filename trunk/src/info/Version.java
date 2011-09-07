/*
 * Version.java
 *
 * Created on 23.04.2005, 22:44
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
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

package info;

import locale.SR;
import midlet.BombusQD;

public final class Version {
    public final static String NAME = "BombusQD";
    public final static String BOMBUS_SITE_URL = "http://bombusmod-qd.wen.ru";

    private static String version = "$APP_VERSION$ (r$APP_REVISION$)";

    public static String getVersionString(boolean showLang) {
        StringBuffer buf = new StringBuffer();
        
        buf.append(version);
        if (showLang) {
            buf.append(" (").append(SR.get(SR.MS_IFACELANG)).append(')');
        }
        String build = BombusQD.getStrProperty("BombusQD-Build", "0");
        if (!build.equals("0")) {
            buf.append(" [").append(build).append(']');
        }
        return buf.toString();
    }

    public static String getVersionNumber() {
        return version;
    }

    public static String getUrl() {
        return BOMBUS_SITE_URL;
    }
}
