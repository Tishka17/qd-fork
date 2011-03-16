/*
 * GMenu.java
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
 */
package ui;
import images.ImageList;
import javax.microedition.lcdui.*;
import client.Config;
import colors.ColorTheme;
import locale.SR;
import midlet.BombusQD;
import menu.MenuListener;
import menu.Command;
import java.util.*;

public class GMenu extends Canvas {

   public GMenu() {};
   private Image offscreen = null;

   public void init(Graphics g, int width, int height,VirtualList view) {
        this.height=height;
        this.width=width;
        if (!isDoubleBuffered()){
            offscreen=Image.createImage(width, height);
        }
    }
   public void paint(Graphics g){
       paintCustom(g,gm.itemGrMenu);
   }

   Display display;
   Displayable parentView;
   GMenuConfig gm = GMenuConfig.getInstance();
   BombusQD bm = BombusQD.getInstance();


   public final static byte MAIN_MENU_ROSTER=1;
   public final static byte ACCOUNT_SELECT_MENU=2;
   public final static byte ALERT_CUSTOMIZE_FORM=3;
   public final static byte ALERT_PROFILE=4;
   public final static byte ACTIVE_CONTACTS=5;//
   public final static byte CONFIG_FORM=6;
   public final static byte CONTACT_MSGS_LIST=7;//
   public final static byte SEARCH_FORM=8;
   public final static byte SMILE_PEAKER=9;
   public final static byte STATUS_SELECT=10;
   public final static byte APPEND_NICK=11;
   public final static byte BOOKMARKS=12;
   public final static byte CONFERENCE_FORM=13;
   public final static byte HISTORY_CONFIG=14;
   public final static byte INFO_WINDOW=16;
   public final static byte MESSAGE_LIST=17;
   public final static byte PRIVACY_MODIFY_LIST=18;
   public final static byte PRIVACY_SELECT=19;
   public final static byte SERVICE_DISCOVERY=20;
   public final static byte STATS_WINDOW=21;
   public final static byte VCARD_EDIT=22;
   public final static byte VCARD_VIEW=23;
   public final static byte BROWSER=24;
   public final static byte TRANSFER_MANAGER=25;
   public final static byte DEF_FORM=26;
   public final static byte TEXTLISTBOX=27;
   public final static byte USERKEYSLIST=28;
   public final static byte RECONNECT=30;
   public final static byte NEWVECTORCHAT=31;
   public final static byte AFFILIATIONS_EDIT=32;



   private int width;
   private int height;
   private Font font;
   private int fh;
   private int size;
   private static int x1,y1,x2,y2;


   private void updateFont() {
       switch(midlet.BombusQD.cf.graphicsMenuFont){
           case 0: font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM); break;
           case 1: font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);  break;
           case 2: font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE);  break;
       }
       fh = font.getHeight();
   }

   public GMenu(Display display, Displayable parentView, MenuListener menuListener, ImageList il, Vector menuCommands) {
        gm.ml=menuListener;
        this.parentView=parentView;
        this.display=display;
        if(null == menuCommands) return;
        size = menuCommands.size();
        gm.commandslist = new String[size];//3
        gm.menuCommands=menuCommands;
        for (int index=0; index<size; index++) {
            Command c=(Command)menuCommands.elementAt(index);
            gm.commandslist[index]=c.getName();
        }
        updateFont();
   }


   public GMenu(Display display,Displayable parentView,MenuListener menuListener,ImageList il,Vector menuCommands,
           Vector cmdfirstList,Vector cmdsecondList,Vector cmdThirdList){
        gm.ml=menuListener;
        this.parentView=parentView;
        this.display=display;
        if(null == menuCommands){
            gm.menuCommands=null;
            return;
        }
        size = menuCommands.size();
        gm.commandslist = new String[size];//3
        gm.menuCommands=menuCommands;
        for (int index=0; index<size; index++) {
            Command c=(Command)menuCommands.elementAt(index);
            gm.commandslist[index]=c.getName();
        }
        gm.cmdfirstList=cmdfirstList;
        gm.cmdsecondList=cmdsecondList;
        gm.cmdThirdList=cmdThirdList;
        updateFont();
   }


   private boolean GMenuIn(Vector getList) {
         size = getList.size();
          gm.commandslistIn = new String[size];
          for (int index=0; index<size; index++) {
            Command c=(Command)getList.elementAt(index);
            gm.commandslistIn[index]=c.getName();
          }
          gm.menuCommandsIn=getList;
        return true;
   }


   public void select(boolean inmenu){
      try {
       Command cmd;
       gm.itemGrMenu=-1;
        if(inmenu){
          cmd = (Command)gm.menuCommandsIn.elementAt(gm.itemCursorIndexIn);
          gm.ml.commandAction(cmd, parentView);
          gm.inMenuSelected=false;//���������� ����
        }else{
          cmd = (Command)gm.menuCommands.elementAt(gm.itemCursorIndex);
          gm.ml.commandAction(cmd, parentView);
        }
       gm.ml=null;
      } catch (Exception e) { /* IndexOutOfBounds */
          //#ifdef DEBUG_CONSOLE
        midlet.BombusQD.debug.add("GMenu::select IndexOutOfBounds->" + e.getMessage(), 10);
        //#endif
      }
      gm.itemCursorIndexIn = 0;
      gm.itemCursorIndex = 0;
      cursorY = 0;
    }


  void drawImage(Graphics g, int index, int x, int y){
     int ho=g.getClipHeight();
     int wo=g.getClipWidth();
     int xo=g.getClipX();
     int yo=g.getClipY();
     int iy=y-bm.himg_menu*(int)(index>>4);
     int ix=x-bm.wimg_menu*(index&0x0f);
     g.clipRect(x,y, bm.wimg_menu,bm.himg_menu);
     g.drawImage(bm.imageArr[0],ix,iy,Graphics.TOP|Graphics.LEFT);
     g.setClip(xo,yo, wo, ho);
   };


  public void paintCustom(Graphics g,int itemGrMenu) {
        Graphics graphics=(offscreen==null)? g: offscreen.getGraphics();
//long s1 = System.currentTimeMillis();
          if(eventMenu){
           if(gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_NEW_ACCOUNT))>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_REGISTERING))>-1
              //|| gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_MY_JABBER))>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_SERVICE))>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_SORT_TYPE))>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_HISTORY_OPTIONS))>-1
              ){
              drawAllItems(g,gm.menuCommandsIn,gm.commandslistIn,gm.itemCursorIndexIn);
           }
         }else{
            drawAllItems(g,gm.menuCommands,gm.commandslist,gm.itemCursorIndex);
         }
/*
        long s2 = System.currentTimeMillis();
        int ws = g.getFont().stringWidth(Long.toString(s2-s1)+"msec") + 5;
        int fh = g.getFont().getHeight();
        int xpos = width >> 1 -ws >> 1 ;
        g.setColor(255,255,0);
        g.fillRect(xpos,1,ws,fh);
        g.setColor(0,0,0);
        g.drawRect(xpos,1,ws-1,fh-1);
        g.drawString(Long.toString(s2-s1)+"msec", xpos+2, 2, g.LEFT|g.TOP);
 */
        if (graphics != g) g.drawImage(offscreen, 0, 0, Graphics.LEFT | Graphics.TOP);
  }

   boolean eventMenu=false;
   Vector inMenuItems = new Vector(0);
//#ifdef GRADIENT
   Gradient fon;
//#endif

   private void eventOk(){
    try {
      cursorY=0;
      if (gm.itemCursorIndex>=gm.commandslist.length || gm.itemCursorIndex<0) {
          gm.itemCursorIndex = 0;
          return;
      }

      if((gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_NEW_ACCOUNT))>-1 ||
        //gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_MY_JABBER))>-1 ||
        gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_SORT_TYPE))>-1)  &&
        gm.cmdfirstList.size()>0){
          GMenuIn(gm.cmdfirstList); eventMenu=true; return;
      }
      else if((gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_REGISTERING))>-1
          || gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_HISTORY_OPTIONS))>-1) &&
          gm.cmdsecondList.size()>0) {
          GMenuIn(gm.cmdsecondList); eventMenu=true; return;
      }
      else if (gm.commandslist[gm.itemCursorIndex].indexOf(SR.get(SR.MS_SERVICE))>-1 &&
              gm.cmdThirdList.size()>0){
          GMenuIn(gm.cmdThirdList); eventMenu=true; return;
      } else{
          gm.itemGrMenu=-1;
      }
    } catch (Exception e) {
        /* IndexOutOfBounds */
        //#ifdef DEBUG_CONSOLE
        midlet.BombusQD.debug.add("GMenu::eventOk Exception->" + e.getMessage(), 10);
        //#endif
    }
   }


   int pointerX = 0;
   int maxwidth = 0;
   int[] pointerY = null;

   void drawAllItems(Graphics g,Vector menuCommands,String[] drawCommands,int itemCursorIndex){

        fh = bm.himg_menu>fh?bm.himg_menu:fh;
        if(drawCommands == null || menuCommands == null) return;
        size = drawCommands.length-1;
        int hitem = 0;
        int maxHeight=drawCommands.length;
		if (fh < midlet.BombusQD.cf.minItemHeight)
			fh = midlet.BombusQD.cf.minItemHeight;

        maxwidth=0;
        int len_str=0;
        for (int index=size; index>=0; index--) {
             if(midlet.BombusQD.cf.executeByNum){
                  len_str  = font.stringWidth(index+"-"+drawCommands[index]);
             }else{
                  len_str  = font.stringWidth(drawCommands[index]);
             }
             if(len_str>maxwidth){
               maxwidth=len_str;
             }
       }

       int mHfh = maxHeight*fh + 1;
       gm.maxHeight=mHfh;
       int w = maxwidth + bm.wimg_menu + 10;
       gm.maxWidth=w;
       hitem=mHfh;
        int bgnd_menu=ColorTheme.getARGB(false);
        if (bgnd_menu!=-1){
          int[] pixelArray = new int[width * height];
          int lengntp = pixelArray.length;
          for(int i = 0; i < lengntp; i++){
            pixelArray[i] = bgnd_menu;
          }
          g.drawRGB(pixelArray, 0, width, 0 , 0 , width, height, true);
          //g.drawRoundRect(-1,-1,width+1,height+1,10,10);
          pixelArray = null;
          pixelArray = new int[0];
        }

       switch(midlet.BombusQD.cf.graphicsMenuPosition){
           case 0:
               gm.xcoodr = (g.getClipWidth() - w) >> 1 ;
               gm.ycoodr = (g.getClipHeight()- mHfh) >> 1 ;
               break;
           case 1:
               gm.xcoodr=7;
               gm.ycoodr= g.getClipHeight() - hitem - 10;
               break;
           case 2:
               gm.xcoodr= g.getClipWidth() - w - 7;
               gm.ycoodr= g.getClipHeight() - hitem - 10;
               break;
       }
       //for pointer pressed  function
       {
           x1=gm.xcoodr;
           y1=gm.ycoodr;
           x2=x1+w;
           y2=y1+mHfh;
       }
       int alpha_menu=ColorTheme.getARGB(true);
        if (alpha_menu!=-1){
          int[] pixelArray = new int[w * mHfh];
          int lengntp = pixelArray.length;
          for(int i = 0; i < lengntp; i++){
            pixelArray[i] = alpha_menu;
          }
          g.drawRGB(pixelArray, 0, w, gm.xcoodr , gm.ycoodr , w, mHfh, true);
          g.drawRoundRect(gm.xcoodr,gm.ycoodr,w,mHfh,10,10);
          pixelArray = null;
          pixelArray = new int[0];
        }else{
          g.setColor(ColorTheme.getColor(ColorTheme.GRAPHICS_MENU_BGNG_ARGB));
          //g.fillRoundRect(1 , 1 , w - 1, mHfh - 1,10,10);
          g.fillRoundRect(gm.xcoodr+1 , gm.ycoodr+1, w - 1, mHfh - 1,10,10);//Tishka17
        }

         g.setColor(0x000000);
         g.drawRoundRect(gm.xcoodr,gm.ycoodr , w, mHfh,10,10);
         g.drawRoundRect(gm.xcoodr - 2, gm.ycoodr - 2, w + 4, mHfh + 4, 10,10);
         g.setColor(0xffffff);
         g.drawRoundRect(gm.xcoodr - 1, gm.ycoodr - 1, w + 2, mHfh + 2, 10,10);

         g.translate(gm.xcoodr, gm.ycoodr);
         g.setClip(0,0,w+1,mHfh+40);//?
//#ifdef GRADIENT
        if(midlet.BombusQD.cf.gradient_cursor){ //Tishka17
            int yc = 1 + (itemCursorIndex*fh);
            fon=new Gradient(0, yc, w+2, yc+fh, ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_1),
                  ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_2), false);
            fon.paintHRoundRect(g, 4);
             g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE));
             g.drawRoundRect(0, (itemCursorIndex*fh), w , fh+1, 8, 8);
            //fon.paint(g);
            g.setColor(0x000000);
        }else
//#endif
	{
            g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_BGND));
            g.fillRoundRect(0, (itemCursorIndex*fh), w  , fh+1, 8, 8);
            g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE));
            g.drawRoundRect(0, (itemCursorIndex*fh), w , fh+1, 8, 8);

        }

        g.setFont(font);
        g.setColor(ColorTheme.getColor(ColorTheme.GRAPHICS_MENU_FONT));

        int x_start = 3 + bm.wimg_menu;//3
	int ty;
        for (int index=0; index<=size; index++) {
           if(gm.itemGrMenu!=GMenu.DEF_FORM){
             Command cmd = (Command)menuCommands.elementAt(index);
	     ty=(fh-bm.himg_menu)>>1;
             if(bm.imageArr[0]!=null){
               drawImage(g,cmd.getImg(),3, fh*index + 1 + ty );
             }
             cmd=null;
           }
	   ty=(fh-g.getFont().getHeight())>>1;
            if(midlet.BombusQD.cf.executeByNum){
                g.drawString( (index<=9 ? Integer.toString(index)+"-" : "") + drawCommands[index], x_start, fh*index + 1 + ty, g.LEFT|g.TOP);
           } else {
                g.drawString(drawCommands[index], x_start, fh*index + 1 + ty, g.LEFT|g.TOP);
           }
        }
   }


//#ifdef TOUCH
   private void touchSelect(int x, int y) {
       if (x<x1 || y<y1 || x> x2 || y>y2) {
           gm.itemGrMenu=-1;
           gm.ml=null;
           cursorY=0;
           return;
       }
       if (eventMenu==true) {
            gm.itemCursorIndexIn=(y-y1)/fh;
            return;
       }
       gm.itemCursorIndex=(y-y1)/fh;
   }


   protected void pointerPressed(int x, int y) {
       touchSelect(x,y);
       return;
   }

   protected void pointerReleased(int x, int y) {
       touchSelect(x,y);
       if (eventMenu==true) {
           closeEvent();
           return;
       }
       eventOk();
       return;
   }
//#endif

   //private Timer timer;
   int cursorY=0;
   long s1,s2;
   boolean isDown;

   public void keyPressed(int keyCode) {
     if (eventMenu==true) {
         eventMenu = sendEvent(keyCode);
         return;
     }else{
         if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
            eventOk();
            cursorY=0;
         }
         else if (keyCode==Config.SOFT_RIGHT || keyCode==')' || keyCode == -11 || keyCode == -8) {//SE: 11-back,-8-�������
            gm.itemGrMenu=-1;
            gm.ml=null;
            gm.itemCursorIndex=0;
            cursorY=0;
            //return;
         }
         else
         {
            if(midlet.BombusQD.cf.executeByNum){
              switch (keyCode)
              {
                case KEY_NUM0: gm.itemCursorIndex=0; eventOk(); break;
                case KEY_NUM1: if(gm.commandslist.length>1) {gm.itemCursorIndex=1; eventOk();} break;
                case KEY_NUM2: if(gm.commandslist.length>2) {gm.itemCursorIndex=2; eventOk();} break;
                case KEY_NUM3: if(gm.commandslist.length>3) {gm.itemCursorIndex=3; eventOk();} break;
                case KEY_NUM4: if(gm.commandslist.length>4) {gm.itemCursorIndex=4; eventOk();} break;
                case KEY_NUM5: if(gm.commandslist.length>5) {gm.itemCursorIndex=5; eventOk();} break;
                case KEY_NUM6: if(gm.commandslist.length>6) {gm.itemCursorIndex=6; eventOk();} break;
                case KEY_NUM7: if(gm.commandslist.length>7) {gm.itemCursorIndex=7; eventOk();} break;
                case KEY_NUM8: if(gm.commandslist.length>8) {gm.itemCursorIndex=8; eventOk();} break;
                case KEY_NUM9: if(gm.commandslist.length>9) {gm.itemCursorIndex=9; eventOk();} break;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:
                             gm.itemCursorIndex--;
                              if(gm.itemCursorIndex<0){
                                gm.itemCursorIndex=size;
                              }
                             //startTimer(false);
                             break;
                        case LEFT: break;
                        case RIGHT: break;
                        case DOWN:
                            gm.itemCursorIndex++;
                             if(gm.itemCursorIndex>size){
                                gm.itemCursorIndex=0;
                            }
                            //startTimer(true);
                            break;
                        case FIRE: eventOk(); break;
                    }
                  } catch (Exception e) {}
              }
            } else {
              switch (keyCode)
              {
                case KEY_NUM2:
                    gm.itemCursorIndex--;
                     if(gm.itemCursorIndex<0){
                        gm.itemCursorIndex=size;
                     }
                     //startTimer(false);
                     break;
                case KEY_NUM8:
                     gm.itemCursorIndex++;
                     if(gm.itemCursorIndex>size){
                        gm.itemCursorIndex=0;
                     }
                     //startTimer(true);
                     break;
                case KEY_NUM5:
                    gm.itemCursorIndexIn=0;
                    eventOk();
                    //startTimer(true);
                    break;
                case KEY_NUM1:
                     gm.itemCursorIndex=0;
                    break;
                case KEY_NUM7:
                     gm.itemCursorIndex=size;
                    break;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:
                             gm.itemCursorIndex--;
                              if(gm.itemCursorIndex<0){
                                gm.itemCursorIndex=size;
                              }
                             //startTimer(false);
                             break;
                        case LEFT: break;
                        case RIGHT: break;
                        case DOWN:
                            gm.itemCursorIndex++;
                             if(gm.itemCursorIndex>size){
                                gm.itemCursorIndex=0;
                            }
                            //startTimer(true);
                            break;
                        case FIRE:
                            eventOk();
                            //startTimer(true);
                            break;
                    }
                  } catch (Exception e) {}
                }
            }//midlet.BombusQD.cf.executeByNum end
         }
     }
    }

   private void closeEvent(){
     gm.inMenuSelected=true; gm.itemGrMenu=-1;
   }

   public boolean sendEvent(int keyCode) {
         if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
                     //gm.itemGrMenu=-1;
                     closeEvent();
                     cursorY=0;
                     return false;
         }
        else if (keyCode==Config.SOFT_RIGHT || keyCode==')' || keyCode == -11 || keyCode == -8) {
            gm.itemGrMenu=-1;
            gm.ml=null;
            cursorY=0;
            return false;
         }
         else
         {
            if(midlet.BombusQD.cf.executeByNum){
              switch (keyCode)
              {
                case KEY_NUM0: gm.itemCursorIndexIn=0; closeEvent(); return false;
                case KEY_NUM1: if(gm.commandslistIn.length>1) {gm.itemCursorIndexIn=1; closeEvent();} return false;
                case KEY_NUM2: if(gm.commandslistIn.length>2) {gm.itemCursorIndexIn=2; closeEvent();} return false;
                case KEY_NUM3: if(gm.commandslistIn.length>3) {gm.itemCursorIndexIn=3; closeEvent();} return false;
                case KEY_NUM4: if(gm.commandslistIn.length>4) {gm.itemCursorIndexIn=4; closeEvent();} return false;
                case KEY_NUM5: if(gm.commandslistIn.length>5) {gm.itemCursorIndexIn=5; closeEvent();} return false;
                case KEY_NUM6: if(gm.commandslistIn.length>6) {gm.itemCursorIndexIn=6; closeEvent();} return false;
                case KEY_NUM7: if(gm.commandslistIn.length>7) {gm.itemCursorIndexIn=7; closeEvent();} return false;
                case KEY_NUM8: if(gm.commandslistIn.length>8) {gm.itemCursorIndexIn=8; closeEvent();} return false;
                case KEY_NUM9: if(gm.commandslistIn.length>9) {gm.itemCursorIndexIn=9; closeEvent();} return false;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:
                             gm.itemCursorIndexIn--;
                              if(gm.itemCursorIndexIn<0){
                                gm.itemCursorIndexIn=size;
                              }
                             //startTimer(false);
                             return true;
                        case LEFT: gm.itemCursorIndexIn=0; cursorY=0; break;
                        //case RIGHT: return true;
                        case DOWN:
                            gm.itemCursorIndexIn++;
                             if(gm.itemCursorIndexIn>size){
                                gm.itemCursorIndexIn=0;
                            }
                            //startTimer(true);
                            return true;
                        case FIRE: closeEvent();
                            return false;
                    }
                  } catch (Exception e) {}
              }
            } else {
              switch (keyCode) {
                case KEY_NUM4: gm.itemCursorIndexIn=0; cursorY=gm.itemCursorIndex*fh;
                      break;
              //case KEY_NUM6:
              //     return true;
                case KEY_NUM2:
                     gm.itemCursorIndexIn--;
                     if(gm.itemCursorIndexIn<0){
                        gm.itemCursorIndexIn=size;
                     }
                     //startTimer(false);
                     return true;
                case KEY_NUM8:
                     gm.itemCursorIndexIn++;
                     if(gm.itemCursorIndexIn>size){
                        gm.itemCursorIndexIn=0;
                     }
                     //startTimer(true);
                     return true;
                case KEY_NUM5:
                    closeEvent();
                    return false;
                case KEY_NUM1:
                     gm.itemCursorIndexIn=0;
                    return true;
                case KEY_NUM7:
                     gm.itemCursorIndexIn=size;
                    return true;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:
                             gm.itemCursorIndexIn--;
                              if(gm.itemCursorIndexIn<0){
                                gm.itemCursorIndexIn=size;
                              }
                             //startTimer(false);
                             return true;
                        case LEFT: gm.itemCursorIndexIn=0; cursorY=gm.itemCursorIndex*fh;
                             break;
                        //case RIGHT: return true;
                        case DOWN:
                            gm.itemCursorIndexIn++;
                             if(gm.itemCursorIndexIn>size){
                                gm.itemCursorIndexIn=0;
                            }
                            //startTimer(true);
                            return true;
                        case FIRE: gm.inMenuSelected=true; gm.itemGrMenu=-1;
                            return false;
                    }
                  } catch (Exception e) {}
              }
            }//midlet.BombusQD.cf.executeByNum
         }
       return false;
    }
}
