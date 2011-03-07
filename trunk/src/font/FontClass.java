/*
 * FontClass.java
 *
 * created June 16 2008
 * author magdelphi
 * magdelphi@rambler.ru
 *
 */

package font;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import client.Config;

public class FontClass {

    private static FontClass df;

    public static byte buff[] = new byte[768];//������ ������� �������� �� ����� xxxxx.dat
    public static Image fontImage = null;
    public int[] buf;//������ ������ �������
    public int width = 0;
    public int Color = 0;
    public int h_char;//������ ��������
    public int width_char;//������ ��������


    public int italic =0;//���� ����� �������� italic
    private String name_font="";

    public static FontClass getInstance(){
        if (df==null)
            df=new FontClass();
        return df;
    }


    public FontClass() {
    };

    public void Init(String name_font) {
        this.name_font=name_font;
        try {//----- �������� image �������� ---------------
            this.fontImage = Image.createImage("/images/fonts/"+name_font+".png");
        InputStream is = getClass().getResourceAsStream("/images/fonts/"+name_font+".dat");
        int off = 0;
        int readBytes = 0;
        int n_buf;
          while ( (readBytes = is.read(buff, off, buff.length)) > -1) {}//�������� � �����
          h_char=buff[0];//������ ��������
           if (buff[1] ==1) {italic=h_char/4;}//���� fontstyle = [italic] ����������� ������ �������
       n_buf =h_char*h_char;// ���-�� ���� 1 ����������
       this.buf = new int[n_buf];
       is.close();
       is=null;
       System.gc();
      } catch (Exception e) {
          Config.getInstance().use_drawed_font=false;
          //System.out.println("error fonts loading");
          //Config.getInstance().saveToStorage();//?
      }
    }

    public boolean isCheck()
    {
      if(name_font.indexOf("no")>-1) {
        return false;
      }  else{
        return Config.getInstance().use_drawed_font;
      }
    }

    public int getFontHeight()
    {
      return h_char;
    }


    int lenght_str = 0;
    public int stringWidth(String s)
    {
        int lenght_str = 0;
        int ind;
        int w_char;
        int length=s.length();
        for (int i = 0; i < length; i++) {
            lenght_str+=getCharWidth(s.charAt(i));
        }

        return lenght_str;
    }

 //  private final static char[] maplen= {
 //       'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
 //       'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
 //       '0','1','2','3','4','5','6','7','8','9',',','_',':',' ','(',')','.','+','-','@','/'
 //   };

   public int getCharWidth(char c) {
       int result=-1;
        if (fontImage != null) {
             int ch = c;
                    ch = ch == 0x401 ? 0xa8 : ch == 0x451 ? 0xb8 : ch; //401-�,451-�
                    ch = ch > 0x400 ? ch - 0x350 : ch;
                    //!- 0x21,� - 0x44f
                    //0xa7 - ��������

             int ind = ((int)(ch)-0x20)*3;//�������� ������ � ������� xxxxx.dat
             int len=0;//�������� � ������� xxxxx.png
           //  int maplenth = maplen.length;

           //for(int i=0;i<maplenth;i++)
           //{
           //   if(c==maplen[i]) {
           //     result=1;
           //   }
           //}

           //  if(result>=1){
              int hlen = (buff[ind+1] & 0x00ff)<<8;//������� ����
              len=(buff[ind] & 0x00ff)+hlen; //�������� � ������� xxxxx.png
              width_char= buff[ind+2]+italic;//������ �������
               if (c==' '){width_char=h_char>>2;}//���� ������
                result=width_char;
           //  }
        }
        return result;
    }


    //���������� �������� ����� �� ������������ alpha-�����, RGB
    private int toBGR(int a, int r, int g, int b){
        return (b|(g<<8)|(r<<16)|(a<<24));
    }

    //������������� ������� ���� ����������� ���� �� ������������ alpha-a�����, RGB
    public void setColor(int a, int r, int g, int b){
        Color=toBGR(a,r,g,b);
    }

    public void setColor(int a,int color){
        Color=toBGR(a,getR(color),getG(color),getB(color));
    }



    public static int getR(int color) {
        return ((color >> 16) & 0xFF);
    }
    public static int getG(int color) {
        return ((color >> 8) & 0xFF);
    }
    public static int getB(int color) {
        return (color& 0xFF);
    }



   public int drawChar(Graphics g, char c, int x, int y) {
        int result=0;
        if (fontImage != null) {
          //String s=String.valueOf(c);
          //  unicode to ansi
            int ch = c;
                    ch = ch == 0x401 ? 0xa8 : ch == 0x451 ? 0xb8 : ch; //401-�,451-�
                    ch = ch > 0x400 ? ch - 0x350 : ch;
            int ind = ((int)(ch)-0x20)*3;//�������� ������ � ������� xxxxx.dat
            int len=0;//�������� � ������� xxxxx.png
            int hlen = (buff[ind+1] & 0x00ff)<<8;//������� ����
            len=(buff[ind] & 0x00ff)+hlen;  //�������� � ������� xxxxx.png
            width_char= buff[ind+2]+italic;//������ �������
              fontImage.getRGB(buf, 0, width_char, len-2, 0,width_char, h_char);//������� � �����
                   for(int i=0;i<buf.length;i++)
                   {
                        int color = (buf[i] &0x00ffffff);//������ ������ RGB
                        if (color == 0) color =  Color;//���� ������ ������ � ����
                        buf[i] = color;
                    }
              g.drawRGB(buf, 0, width_char, x, y, width_char, h_char, true);
              //System.out.println(y);
              if (c==' '){width_char=h_char>>2;} //���� ������
                result=width_char;
         }
        return result;
    }


   public void drawString(Graphics g, String s, int x, int y) {

        int w = 0;
        int i = 0;

        int len = s.length();
        if(s.endsWith("   ")){
              len-=3;
              //System.out.println("DRAWED: "+s);
        }
        for (i = 0; i < len; i++) {
          w = drawChar(g, s.charAt(i), x, y);
          x=x+w;
        }
    }

    public void Destroy(){
        buff = null;
        buf = null;
        fontImage = null;
    }
 }
