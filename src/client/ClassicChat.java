/*
 * ClassicChat.java
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
 */

package client;
import javax.microedition.lcdui.*;
import java.util.Timer;
import ui.Gradient;
import colors.ColorTheme;
import font.*;
import ui.ImageList;
import ui.GMenuConfig;
import ui.VirtualList;

public class ClassicChat extends CustomItem implements ItemCommandListener
{
    Display display;
    Displayable pView;
  
    public void setDisplay(Display display,Displayable pView){
      this.display=display;
      this.pView=pView;
      }
  
    public String[] msgs;
  
    int maxCount;
    int fontHeight;
    int maxScreenLines;
  
    public int count = 0;  
  
    protected int getMinContentHeight() { return height; }
  
    protected int getMinContentWidth() { return width; }
  
    protected int getPrefContentHeight(int w){ return height; }
  
    protected int getPrefContentWidth(int h) { return width; }
  
    public void commandAction(Command c, Item i) { }
  
  
    int line_count = Config.getInstance().lineCount;
  
    boolean up, down;
    int firstScreenMsg = 0;   
    public int msg_count = 1;

    boolean upKeyPressed = false;
    boolean downKeyPressed = false;
  
    Contact contact;  

    public Font getFont() {
          return FontCache.getFont(false, FontCache.msg);
    }
  
    private Image offscreen = null;
    public int width, height;
  
  
    public ClassicChat(String title, int w, int h,Contact contact)
    {
        super(title);
        this.contact=contact;
        width = w; height = h;
        fontHeight = getFont().getHeight();
        maxScreenLines = (height-(3+1))/fontHeight;
        maxCount = (int)line_count;
        msgs = new String[maxCount];
        up = false; 
        down = false;
        setItemCommandListener(this);
    }

   
    private void drawScrollBar(Graphics g, int w, int h)
      {
          g.setColor(0,0,0);
          if (up) {
            g.fillTriangle(w-8,6,
                           w-4,14, 
                           w-12,14);        
           }
          if (down) {
            g.fillTriangle(w-8,h-6,
                           w-4,h-14,
                           w-12,h-14);       
           }
      }
  
    
   Gradient fon; 
   GMenuConfig gm = GMenuConfig.getInstance();

 
   protected void paint(Graphics g, int w, int h) {
     Graphics saved = g;    
     if( offscreen != null ){  g = offscreen.getGraphics(); }   

       g.setColor(0,0,0);
       g.drawRect(0, 0, w-1, h-1);
        Image imgJimm = null;
        Image imgBgnd = null;
//#ifdef BACK_IMAGE
//#         imgJimm = VirtualList.getImage(1);
//#         imgBgnd = VirtualList.getImage(3);
//#endif
        if(midlet.BombusQD.cf.bgnd_image==1 && imgJimm!=null) {
                int imgWidth = imgJimm.getWidth();
                int imgHeight = imgJimm.getHeight();
		for (int xx = 0; xx < width; xx += imgWidth){
		     for (int yy = 0; yy < height; yy += imgHeight) g.drawImage(imgJimm, xx, yy, Graphics.LEFT|Graphics.TOP);
                }
        }
        else if(midlet.BombusQD.cf.bgnd_image==2){ 
                if(!Config.getInstance().usePhoneTheme) {
                  fon=new Gradient(1, 1, w - 1, h - 1,
                        ColorTheme.getColor(ColorTheme.GRADIENT_BGND_LEFT),ColorTheme.getColor(ColorTheme.GRADIENT_BGND_RIGHT), true);
                  fon.paint(g);
                }else{
                  g.setClip(0,0,w,h);  
                }
        }
        else if(midlet.BombusQD.cf.bgnd_image==3 && imgBgnd!=null) { 
                ImageList il = new ImageList();
                int imgWidth = imgBgnd.getWidth();
                int imgHeight = imgBgnd.getHeight();
                if(width <= imgWidth && height<imgHeight) {
                   imgBgnd = VirtualList.resizeImage(imgBgnd, width, height);
                }  
                g.drawImage(imgBgnd, 0, 0, Graphics.LEFT|Graphics.TOP);
        } else{
            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0, 0, width, height);        
        } 

       int i1 = firstScreenMsg + maxScreenLines;
       int i2 = count<(firstScreenMsg + maxScreenLines) ? count:i1;
    
       drawScrollBar(g, w, h);

        g.setColor(ColorTheme.getColor(ColorTheme.MESSAGE_IN));
        g.setFont(getFont());
        
      int y = 1;
      for (int i = firstScreenMsg; i < i2; i++) {
        if (i == 0){
          g.drawString(msgs[i], 3, y,Graphics.TOP|Graphics.LEFT);
        }
        else {
          g.drawString(msgs[i], 3, y, Graphics.TOP|Graphics.LEFT);
        }
        y += fontHeight;
     }
     if( g != saved ){ saved.drawImage( offscreen, 0, 0, Graphics.LEFT | Graphics.TOP ); }    
   }

  
   public void storeMessage(String msg){
     checkIt();
     msgs[count] = msg;
     msg_count++; count++;
     int offh = count - (firstScreenMsg + maxScreenLines);
     if (offh > 0) {
      firstScreenMsg += offh;
      if (!up) {
        up = !up;//true
      }
     }
     if ((count > 1) && !down) { down = true; }
     repaint();
   } 

 
   private void checkIt()
   {
    if (count == maxCount){
      for (int i=0; i < (maxCount-1); i++)
        msgs[i] = msgs[i+1]; count--;
        if (firstScreenMsg > 0){
          firstScreenMsg--;
       }
    }
   } 

   public void up(){
    firstScreenMsg -= maxScreenLines;
    if(firstScreenMsg<=0) {
       firstScreenMsg=0;
     }
   }
  
  
   public void down(){
    firstScreenMsg += maxScreenLines-1;
     if (firstScreenMsg >= count-1) {
       firstScreenMsg=count-3;
     }
   }  

  
   protected void keyPressed(int keyCode)
   {
     int action = getGameAction(keyCode);
     if(keyCode==Canvas.KEY_NUM3){
       midlet.BombusQD.sd.roster.showActiveContacts(pView, contact);
     }      
          switch (action) {
              case Canvas.LEFT:
                  upKeyPressed = true; 
                  up();
                  break;
              case Canvas.RIGHT:
                  downKeyPressed = true;
                  down();
                  break;
              case Canvas.UP:
                  upKeyPressed = true;
                  msgsUp();
                  break;
              case Canvas.DOWN:
                  downKeyPressed = true;
                  msgsDown();
                  break;
              case Canvas.FIRE:
                  midlet.BombusQD.sd.roster.showActiveContacts(pView, contact);
                  break;
          }
      repaint();
   }

  
   private void msgsUp()
   {
    if (firstScreenMsg > 0) {
      firstScreenMsg--;
      if (firstScreenMsg == 0) { 
          up = false; 
      }
      if (!down) {  
          down = true;
      }
    }
   }
  
 
   private void msgsDown()
   {
    if (firstScreenMsg < count-1) {
      firstScreenMsg++;
      if (firstScreenMsg == count-1) {
          down = false; 
      } 
      if (up==false) {
          up = true;  
      }
     }
   } 

} 

