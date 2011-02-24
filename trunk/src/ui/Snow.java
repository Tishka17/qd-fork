/*
 * Snow.java
 *
 * Created on 27 Р”РµРєР°Р±СЂСЊ 2008 Рі., 15:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
/*
package ui;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import util.ArrayLoader;
import java.util.*;
import Colors.ColorTheme;

**
 *
 * @author evgs,vinz@,aqent
 *
public class Snow extends GameCanvas
{
    Display display;
    
    private static int PARTICLES = 0;
    private static int DPARTICLES = 6;
    private int width;
    private int height;    
    
    public Snow(Display d) { //first init
        super(false);
        this.display = d;
        
        PARTICLES = 500;
        initSnowObjects();
        init();
        
        runned = true;
        repaint();
    }
    
    private static int px[];
    private static int rx[];
    private static int py[];
    private static int dx[];
    private static int dy[];
    private static int phase[];
    private static int df[];
    private static byte sinus[];
    private Random r;   
    private Random rSnowObject;
    private void init() {
        px=new int[PARTICLES];
        rx=new int[PARTICLES];
        py=new int[PARTICLES];
        dx=new int[PARTICLES];
        dy=new int[PARTICLES];
        phase=new int[PARTICLES];
        df=new int[PARTICLES];
        r=new Random(System.currentTimeMillis());
        rSnowObject = new Random();
    }
    
    
    private SnowRun snowRun;
    private class SnowRun extends TimerTask {
        private Snow snow;
        public SnowRun(Snow snow) {
            this.snow=snow;
        }
        
        private int changeBgnd = 0;
        public void run() {
           snow.update();
           if(changeBgnd==50) {
               changeBgnd = 0;
               snow.changeBgndColor();
           }
           ++changeBgnd;
           ((GameCanvas)display.getCurrent()).repaint();
        }
    }    
    
    
    public void destroyView() {
        stopTimer();
        snowRun = null;
        snowObjects = null;
        tempSnowObject = null;
        
        lineSnow = new int[0];
        snowObjects = new int[0][0];
        tempSnowObject = new int[0];

        sinus = null;
        px=new int[0];
        rx=new int[0];
        py=new int[0];
        dx=new int[0];
        dy=new int[0];
        phase=new int[0];
        df=new int[0];
        
        
    }
    
    private static Timer timer;
    public void startTimer(int repaintTime){
        //System.out.println("START timer("+repaintTime+")");
        if(timer==null){
            timer = new Timer();
            snowRun = null;
            snowRun = new SnowRun(this);
            timer.schedule(snowRun, 0 , repaintTime);
        }
    }

    public void stopTimer(){
        //System.out.println("STOP timer");
        if(timer!=null){
          timer.cancel();
          timer=null;
        }
    }      
    
                                           //{	0,	1,	2,	3,	4,	5,	6,	7,	8,	9	};
    private final static int[] snowMap =   {	100,	200,	200,	300,	300,	400,	450,	500,	500,	500	};
    private final static int[] snowSpeed = {	60,	60,	50,	50,	40,	40,	40,	40,	36,	32	};
    public void changeSnowProcess(int type) {//todo? плавное переключение снегопада
        //lineSnow = null;
        //lineSnow = new int[0][0]; //"clear" snow
        stopTimer();
        PARTICLES = snowMap[type];
        startTimer(snowSpeed[type]);
    }
    
    private void newParticle(int i) {
        if (width<=0) return;
        py[i]=1;
        px[i]=r.nextInt() % width;
        dx[i]=(r.nextInt() & 31) + 1;
        dy[i]=(r.nextInt() & 3) + 1;
        phase[i]=r.nextInt() & 255;
        df[i]=(r.nextInt() & 15) + 3;
    }    
    
    public void update() {
        if (sinus==null) sinus = new ArrayLoader().readByteArray("/themes/snow");
        int fallParticles=DPARTICLES;
        if (width>0 & height >0)
        for (int i=0; i<PARTICLES; ++i) {
            py[i]+=dy[i];
            rx[i]=dx[i]*sinus[phase[i]] >>7;
            phase[i]=(phase[i]+df[i]) & 0xff;
            if (py[i]>height) py[i]=0;
            if (py[i]==0 && fallParticles>0) {
                newParticle(i);
                fallParticles--;
            }
        }
    }

    
    private final static byte MAX_LINES_SNOW = 60;
    private static int lineSnow[] = new int[0];
    private static int checkPos = 0;
    private static boolean runned;    
    private final static byte[] tempMapSize = { 11,3,7,7,10,9 };
    private final static byte[][] tempMap = {
        {//0
              1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1,
              0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0,
              1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1,
              0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0,
              0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0,
              1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
              0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0,
              0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0,
              1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1,
              0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0,
              1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1
        },
        {//1
              0, 1, 0,
              1, 1, 1,
              0, 1, 0
        },
        {//2
              0, 1, 0, 0, 0, 1, 0,
              1, 1, 0, 1, 0, 1, 1,
              0, 0, 1, 0, 1, 0, 0,
              0, 1, 0, 1, 0, 1, 0,
              0, 0, 1, 0, 1, 0, 0,
              1, 1, 0, 1, 0, 1, 1,
              0, 1, 0, 0, 0, 1, 0
        },
        {//3 - aqent
              1, 0, 0, 0, 0, 0, 1,
              0, 1, 0, 1, 0, 1, 0,
              0, 0, 1, 1, 1, 0, 0,
              0, 1, 1, 0, 1, 1, 0,
              0, 0, 1, 1, 1, 0, 0,
              0, 1, 0, 1, 0, 1, 0,
              1, 0, 0, 0, 0, 0, 1
        },
        
        {//4 - Winn
              0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
              0, 0, 1, 0, 1, 1, 0, 1, 0, 0,
              0, 1, 1, 0, 1, 1, 0, 1, 1, 0,
              0, 0, 0, 1, 1, 1, 1, 0, 0, 0,
              1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
              1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
              0, 0, 0, 1, 1, 1, 1, 0, 0, 0,
              0, 1, 1, 0, 1, 1, 0, 1, 1, 0,
              0, 0, 1, 0, 1, 1, 0, 1, 0, 0,
              0, 0, 0, 0, 1, 1, 0, 0, 0, 0
        },
        {//5 - Tishka17
              1, 0, 1, 0, 0, 0, 1, 0, 1,
              0, 1, 0, 0, 0, 0, 0, 1, 0,
              1, 0, 1, 0, 0, 0, 1, 0, 1,
              0, 0, 0, 1, 0, 1, 0, 0, 0,
              0, 0, 1, 0, 1, 0, 1, 0, 0,
              0, 0, 0, 1, 0, 1, 0, 0, 0,
              1, 0, 1, 0, 0, 0, 1, 0, 1,
              0, 1, 0, 0, 0, 0, 0, 1, 0,
              1, 0, 1, 0, 0, 0, 1, 0, 1
        }
    };
    private static int[][] snowObjects = new int[0][0];
    private static int[] tempSnowObject = new int[0];
    private int snowObjNum;
    //private int colorBgnd; //цвет фона
    private int colorSnow; //цвет снега
    
    
    private int getARGB(int color, int alpha_) {
      int red, green,blue,alpha;
      long tmp; 
      red = (color >> 16) & 0xFF;
      green = (color >> 8) & 0xFF;
      blue = color& 0xFF;
      tmp = (alpha_ << 24) | (red << 16) | (green << 8) | blue;
      return (int)tmp;
    } 
    
    
    int red,green,blue,alpha = 255;
    int dynamicBgndColor = 0x0000ff;
    long tmp; 
    boolean inverse = false;
    private void changeBgndColor() {//dinamic change bgnd color
      if(dynamicBgndColor == 0x000000){
          System.out.println("INVERSE!");
          inverse = !inverse;
      }
      red = (dynamicBgndColor >> 16) & 0xFF;
      green = (dynamicBgndColor >> 8) & 0xFF;
      blue = dynamicBgndColor& 0xFF;
      if(inverse) {//5
          if(dynamicBgndColor == 0x0000ff) return;
          blue+=7;   
        if(blue >= 255) blue = 255;
      }
      else {
        blue-=7;
        if(blue <= 0) blue = 0;
      }
      tmp = red << 16 | green << 8 | blue;
      dynamicBgndColor = (int)tmp;
      
      int snowObjectCount = tempMapSize.length;
      int sizeMultiple = 0;
      for(int snowObject = 0; snowObject < snowObjectCount; ++snowObject){
          sizeMultiple = tempMapSize[snowObject];
          int size = tempMap[snowObject].length;
          for(int i = 0; i < size; ++i) { 
                 if(tempMap[snowObject][i] == 1) 
                     snowObjects[snowObject][i] = colorSnow; 
                 else snowObjects[snowObject][i] = dynamicBgndColor;
          }
      }
      int counter = 0;
      int alpha = 0;
      int addValue = 255/MAX_LINES_SNOW;
      for(int line = 0; line < MAX_LINES_SNOW; ++line) {
         if(alpha>=240) alpha = 250;
         for(int index = 0; index < width; ++index) {
            lineSnow[counter] = getARGB(dynamicBgndColor, alpha);
            counter++;
         }  alpha += addValue;
      }
    }   
    

    private void initSnowObjects() {
         //init objects
	  //colorBgnd = ColorTheme.getColor(ColorTheme.BLK_BGND);
          colorSnow = 0xffffff;
          tempSnowObject = null;
          int sizeMultiple = 0;
          int snowObjectCount = tempMapSize.length;
          
          snowObjects = new int[snowObjectCount][0];
          for(int snowObject = 0; snowObject < snowObjectCount; ++snowObject){
             sizeMultiple = tempMapSize[snowObject];
             snowObjects[snowObject] = tempSnowObject = new int[sizeMultiple*sizeMultiple];
             int size = tempMap[snowObject].length;
             for(int i = 0; i < size; ++i) { 
                 if(tempMap[snowObject][i] == 1) 
                     snowObjects[snowObject][i] = colorSnow; 
                 else snowObjects[snowObject][i] = dynamicBgndColor;
             }
          }
    }

    
    public void paint(final Graphics g, int w, int h) {
        if(!runned) return;
        width=w;
        height=h;
        
        g.setColor(dynamicBgndColor);
        g.fillRect(0,0, width, height);
           
        if(lineSnow.length == 0) {
            int inputSize = width * MAX_LINES_SNOW;
            //System.out.println("init snowLinesRGB ");
            checkPos = height - 20;
            lineSnow = new int[inputSize];

            int counter = 0;
            int alpha = 0;
            int addValue = 255/MAX_LINES_SNOW;
            for(int line = 0; line < MAX_LINES_SNOW; ++line) {
                if(alpha>=240) alpha = 250;
                //System.out.println("alpha:" + line + "->" + alpha);
                for(int index = 0; index < width; ++index) {
                   lineSnow[counter] = getARGB(dynamicBgndColor, alpha);
                   counter++;
                } 
                alpha += addValue;
            }
        }

        g.setColor(0xffffff);
        int x;
        int y;
        byte showSnow = 20;
        for (int i=0; i<PARTICLES; ++i) {
            x = rx[i] + px[i];
            y = py[i];
            //if(y > 300) continue;//great effect! :) bigSnow = 5;
            switch(showSnow) {
                case 10:
                    snowObjNum = tempMapSize[0];
                    g.drawRGB(snowObjects[0], 0, snowObjNum, x, y, snowObjNum, snowObjNum, false);
                    break;
                case 18:
                case 9:
                    snowObjNum = tempMapSize[4];
                    g.drawRGB(snowObjects[4], 0, snowObjNum, x, y, snowObjNum, snowObjNum, false);
                    break;
                case 19:
                case 12:
                    snowObjNum = tempMapSize[5];
                    g.drawRGB(snowObjects[5], 0, snowObjNum, x, y, snowObjNum, snowObjNum, false);
                    break;
                case 17:
                case 11:
                case 4:
                    snowObjNum = tempMapSize[3];
                    g.drawRGB(snowObjects[3], 0, snowObjNum, x, y, snowObjNum, snowObjNum, false);
                    break;
                case 14:
                case 7:
                case 2:
                    snowObjNum = tempMapSize[2];
                    g.drawRGB(snowObjects[2], 0, snowObjNum, x, y, snowObjNum, snowObjNum, false);
                    break;
                case 16:
                case 3:
                    snowObjNum = tempMapSize[1];
                    g.drawRGB(snowObjects[1], 0, snowObjNum, x, y, snowObjNum, snowObjNum, false);
                    break;
                case 0:
                    showSnow = 20;
                    break;
                default:
                    g.drawLine(x, y, x, y);
            }
            --showSnow;
        }
        g.drawRGB(lineSnow, 0, width, 0, height - MAX_LINES_SNOW , width, MAX_LINES_SNOW, true);   
    }
}

 */