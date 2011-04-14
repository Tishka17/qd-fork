/*
 * AlertBox.java
 *
 * Created on 17.05.2008, 14:35
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

package ui.controls;

import client.Config;
import colors.ColorTheme;
import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#endif
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import font.FontCache;
import ui.CanvasEx;
import util.StringUtils;
//#ifdef GRADIENT
import ui.Gradient;
//#endif

/**
 *
 * @author ad
 */

public abstract class AlertBox extends CanvasEx

//#ifndef MENU_LISTENER
//#         implements CommandListener
//#endif
    {
//#ifndef MENU_LISTENER
//#     protected Command cmdOk=new Command(SR.get(SR.MS_OK), Command.OK, 1);
//#     protected Command cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.BACK, 2);
//#endif

    public boolean isShowing;

    Font messageFont;
    Font barFont;

    private String left=SR.get(SR.MS_OK);
    private String right=SR.get(SR.MS_CANCEL);

    boolean init;
    private String mainbar;
    private String text;

    private Vector lines=null;

    private int topColor=ColorTheme.getColor(ColorTheme.BAR_BGND);
//#ifdef GRADIENT
    private int bottomColor=ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM);
    private Gradient gr=null;
    private Gradient gr2=null;
//#endif

    private Progress pb;

    int pos=0;
    int steps=1;

    public AlertBox(String mainbar, String text, boolean optionsMaster) {
        if(optionsMaster){
            left=SR.get(SR.MS_YES);
            right=SR.get(SR.MS_NO);
        }

        messageFont=FontCache.getFont(false, Config.msgFont);
        barFont=FontCache.getFont(false, Config.barFont);

        this.text=text;
        this.mainbar=mainbar;
        isShowing=true;
//#ifndef MENU_LISTENER
//#         addCommand(cmdOk);
//#         addCommand(cmdCancel);
//#
//#         setCommandListener(this);
//#endif
    }

//#ifndef MENU_LISTENER
//#     public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdOk) {
//#             yes();
//#         } else {
//#             no();
//#         }
//#         destroyView();
//#     }
//#endif

    public void destroyView()	{
        isShowing=false;

        super.destroyView();
    }

    private void getLines(int width) {
        if (lines==null) {
            lines=StringUtils.parseMessage(text, width-4, messageFont);
            text=null;
        }
    }

    protected void paint(Graphics g) {
        if (isShowing) {
            int oldColor=g.getColor();

            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0,0, width, height); //fill back

            int fh=0;
            if (mainbar!=null) {
                fh=getBarFontHeight();
//#ifdef GRADIENT
                if (gr==null) {
                    gr=new Gradient(0, 0, width, fh, ColorTheme.getColor(ColorTheme.BAR_BGND), bottomColor, false);
                }
                gr.paint(g);
//#else
//#             g.setColor(topColor);
//#             g.fillRect(0, 0, width, fh);
//#endif
                g.setFont(barFont);
                g.setColor(ColorTheme.getColor(ColorTheme.BAR_INK));
                g.drawString(mainbar, width/2, 0, Graphics.TOP|Graphics.HCENTER);
            }


            fh=getBarFontHeight();
//#ifdef GRADIENT
            if (gr2==null) {
                gr2=new Gradient(0, height-fh, width, height, topColor, bottomColor, false);
            }
            gr2.paint(g);
//#else
//#         g.setColor(topColor);
//#         g.fillRect(0, height-fh, width, fh);
//#endif
            g.setFont(barFont);
            g.setColor(ColorTheme.getColor(ColorTheme.BAR_INK));
            g.drawString(left, 2, height-fh, Graphics.TOP|Graphics.LEFT);
            g.drawString(right, width-2, height-fh, Graphics.TOP|Graphics.RIGHT);

            getLines(width-4);
            drawAllStrings(g, 2, fh);

            if (pos>0)
                drawProgress (g, width, height-fh);

            g.setColor(oldColor);
        }
    }

    private void drawAllStrings(Graphics g, int x, int y) {
        if (lines==null)
            return;
        if (lines.size()<1)
            return;

        g.setFont(messageFont);
        int fh=getFontHeight();
        g.setColor(ColorTheme.getColor(ColorTheme.LIST_INK));
        String str;
	for (int line=0; line<lines.size(); line++){
            str = (String)lines.elementAt(line);
            if (str!=null && str.length()>0)
                g.drawString(str, x, y, Graphics.TOP|Graphics.LEFT);
            y += fh;
	}
    }

    private int getBarFontHeight() {
        return barFont.getHeight();
    }

    private int getFontHeight() {
        return messageFont.getHeight();
    }

    public void drawProgress (Graphics g, int width, int height) {
        int filled=pos*width/steps;

        if (pb==null)
            pb=new Progress(0, height, width);
        Progress.draw(g, filled, Integer.toString(steps-pos));
    }

    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        if (keyCode==Config.SOFT_LEFT || keyCode==FIRE) {
            destroyView();
            yes();
        } else if (keyCode==Config.SOFT_RIGHT || keyCode==Config.KEY_BACK) {
            destroyView();
            no();
        }
    }

//#ifdef TOUCH
    protected void pointerPressed(int x, int y) {

        if (height - y < getBarFontHeight()) {
            if (x<width/2){
                destroyView();
                yes();
                return;
            } else {
                destroyView();
                no();
                return;
            }
        }
    }
//#endif

    public abstract void yes();

    public abstract void no();
}

