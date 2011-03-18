/*
 * ShowFile.java
 *
 * Created on 9.10.2006, 14:00
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

//#ifdef FILE_IO
package io.file.browse;

import client.Config;
import io.file.FileIO;
import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import locale.SR;
import util.Strconv;
import javax.microedition.io.ConnectionNotFoundException;
import client.Config;
//import Mood.EventPublish;
import ui.controls.AlertBox;
import ui.VirtualList;
import ui.controls.AlertBox;
import images.ImageList;

/**
 *
 * @author User
 */
public class ShowFile implements CommandListener{

    private Display display;
    private Displayable parentView;

    private Command back;
    private Command stop;
    //private Command appliedRES = new Command("Yes", Command.BACK, 4);

    private int len;

    private byte[] b;

    private Player pl;

    private Config cf;
    private long length=0;
    boolean play=false;

    int width;
    int height;
    public ShowFile(Display display, final String fileName, int type,String trackname, int width, int height) {
        this.display=display;
        this.width=width;
        this.height=height;

        back = new Command(SR.get(SR.MS_BACK), Command.BACK, 2);
        stop = new Command(SR.get(SR.MS_STOP), Command.BACK, 3);

        parentView=display.getCurrent();
        cf=Config.getInstance();

        load(fileName);
        if (type==1) { //sounds
          play(fileName, "", true);
        }
        if (type==2) view(fileName); //images
        if (type==3) {
            AlertBox alert = new AlertBox( "Info", "Windows cp1251?" , display, parentView, false) {
               public void yes() { cf.cp1251 = true; read(fileName);  }
               public void no() { cf.cp1251 = false; read(fileName);  }
            };
            alert = null;
        }
    }

    private ChoiceGroup resType;
    private int replyIndex;       // Index of "reply" in choice group

    private void load(String file) {
        try {
            FileIO f=FileIO.createConnection(file);
            b = f.fileRead();
            length = f.fileSize();
            len = b.length;
            f.close();
        } catch (Exception e) {}
    }


    private void view(String file) {
          ImageList il = new ImageList();
          Image photoImg = null;
              try {
                photoImg = Image.createImage(b, 0, len);
                if(photoImg.getWidth() > width) {
                   int newW = photoImg.getWidth();
                   int newH = photoImg.getHeight();
                   while(newW > width) {
                       newW-=(newW*10)/100;
                       newH-=(newH*10)/100;
                   }
                   photoImg = VirtualList.resizeImage(photoImg, newW, newH);
                }
              }  catch(OutOfMemoryError eom) {
              }  catch (Exception e) {
              }
        if (null == photoImg) return;
        Form form = new Form(file);
        form.append(new ImageItem(null, photoImg, ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_BEFORE, "[image]"));
        form.addCommand(back);
        form.setCommandListener(this);
        display.setCurrent(form);
    }

    private void read(String file) {
       Form form = new Form("");
       TextField tf = new TextField(file+" ("+len+" bytes)", null, len, TextField.ANY);
       form.append(tf);
       form.addCommand(back);
       form.setCommandListener(this);
        if (len > 0) {
           String s=new String();
            try {
                int maxSize=tf.getMaxSize();

                if (maxSize>len){
                    s=new String(b, 0, len);
                } else {
                    s=new String(b, 0, maxSize);
                }
            } catch (Exception e) {}

            if (cf.cp1251) tf.setString(Strconv.convCp1251ToUnicode(s));
            else {
               tf.setString(s);
            }
        }
       display.setCurrent(form);
    }

    private void play(String file,String trackname,boolean play) {
        try {
            pl = Manager.createPlayer("file://" + file);
            pl.realize();
            pl.start();
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (MediaException ex) {
            //ex.printStackTrace();
        }

        Alert a = new Alert("Play", "Playing" + " " + file, null, null);
        a.addCommand(stop);
        a.addCommand(back);
        a.setCommandListener(this);
        display.setCurrent(a);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==back) display.setCurrent(parentView);
        if (c==stop) {
            try {
                pl.stop();
                pl.close();
            } catch (Exception e) { }
        }
    }
}
//#endif
