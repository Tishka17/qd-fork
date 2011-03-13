/*
 * SmilesIcons.java
 *
 * Created on 21.05.2008, 22:24
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */

//#ifdef SMILES
package images;

import client.Config;
import message.MessageParser;

/**
 *
 * @author EvgS
 */
public class SmilesIcons {

    private static String res= "/images/smiles/smiles.png";
    private final static int SMILES_IN_ROW=16;
    private static int cols;

    private static ImageList animatedInstance = null;
    private static ImageList staticInstance = null;

    public static ImageList getStaticInstance() {
        if (staticInstance == null) {
            try {
                int smilesCount = MessageParser.getInstance().getSmileTable().size();
                cols = ceil(SMILES_IN_ROW, smilesCount);
            } catch (Exception e) {

            }
            staticInstance = new ImageList(res, cols, SMILES_IN_ROW);
        }
        return staticInstance;
    }

    public static ImageList getInstance() {
        if (Config.hasAniSmiles) {
            if (null == animatedInstance) {
                int smilesCount = -1;
                try {
                    smilesCount = MessageParser.getInstance().getSmileTable().size();
                    cols = ceil(SMILES_IN_ROW, smilesCount);
                } catch (Exception e) {

                }

                animatedInstance = new AniImageList();
                boolean load = ((AniImageList)animatedInstance).load("/images/smiles");
                if (load && animatedInstance.getWidth() != 0) {
                    return animatedInstance;
                } else {
                    Config.hasAniSmiles = false;
                    Config.animatedSmiles = false;
                    animatedInstance = null;

                    // for SE
                    MessageParser.getInstance().restart();
                    return getStaticInstance();
                }
            }

            return animatedInstance;
        } else {
            return getStaticInstance();
        }
    }

    private static int ceil(int rows, int count){
        int tempCols=count/rows;
        if (count>(tempCols*rows))
            tempCols++;
        return tempCols;
    }
}
//#endif
