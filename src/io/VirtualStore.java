/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

//#ifdef FILE_IO
import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import io.file.FileIO;
import io.file.FileIO.*;

import javax.microedition.rms.*;

/**
 *
 * @author Mars
 */

public class VirtualStore {
    private static String fname, if_name;
    private static FileIO dfile= null, ifile= null;

    private static int vr_total; // total rocords
    private static int data_total; // total data file size
    private static int vr_curoff; // current offset in data file
    private static int vr_icuroff; // current offset in index file

    // index file record structure
    private static int ir_pos= 0; // start data of record in data file
    private static int ir_size= 0; // size data
    private static int ir_res1= 0;
    private static int ir_res2= 0;
    private static int ir_res3= 0;
    private static int ir_res4= 0;
    private static int ir_res5= 0;
    private static int ir_res6= 0;

    // size index file record
    final static byte I_RECORDSIZE = 32;

    public VirtualStore( ) {}

    public static void openVirtualStore( String name, boolean dummy) throws RecordStoreException{
        if( name ==null) return;
        fname= name +".data";
        if_name= name +".index";

        dfile= FileIO.createConnection( fname);
        if( dfile ==null) throw new RecordStoreNotOpenException( "VirtualStore not open");
        ifile= FileIO.createConnection( if_name);
        if( ifile ==null) throw new RecordStoreNotOpenException( "VirtualStore not open");

        OutputStream os= null, i_os= null;
        try {
            os= dfile.openOutputStream( 0);
            i_os= ifile.openOutputStream( 0);
            data_total= (int)dfile.fileSize();
            vr_total= (int)(ifile.fileSize() /I_RECORDSIZE);
            os.close(); i_os.close();
        }
        catch( IOException ioe){
            try{
                os= dfile.openOutputStream();
                i_os= ifile.openOutputStream();
                os.close(); i_os.close();

                os= dfile.openOutputStream( 0);
                i_os= ifile.openOutputStream( 0);
                data_total= (int)dfile.fileSize();
                vr_total= (int)ifile.fileSize() /I_RECORDSIZE;
                os.close(); i_os.close();
            }
            catch( IOException ioe2){
                throw new RecordStoreNotOpenException( "VirtualStore internal error");
            }
        }// try
    }

    public static void closeVirtualStore() throws RecordStoreException{
        if( dfile != null && ifile != null)
            try{
                dfile.close();
                ifile.close();
                dfile= null; ifile= null;
            }catch( IOException ioe){ throw new RecordStoreException( "VirtualStore internal error");}
        return;
    }

    public static int getNumRecords( ) throws RecordStoreNotOpenException {
        try {
            InputStream i_is= ifile.openInputStream();
            vr_total= (int)(ifile.fileSize() /I_RECORDSIZE);
            i_is.close();
            return vr_total;
        }
        catch( IOException ioe){
            throw new RecordStoreNotOpenException( "VirtualStore not open");
        }//try
    }

    public static byte[] getRecord( int vrId) throws RecordStoreException {
        InputStream is, i_is;
        //OutputStream os, i_os;
        DataInputStream dis, i_dis;
        //OutputStream dos, i_dos;

        byte[] vrData = null;

        dis= null; i_dis= null;
        is= null; i_is= null;

        try{
            is= dfile.openInputStream();
            i_is= ifile.openInputStream();
            vr_total= (int)(ifile.fileSize() /I_RECORDSIZE);
        }catch( IOException ioe2){ throw new RecordStoreException( "VirtualStore not open");}

        if( vrId<1 || vrId>vr_total) throw new InvalidRecordIDException( "VirtualStore hasn't this record");
        vr_icuroff= (vrId -1) *I_RECORDSIZE; // todo: make real zero record for internal use

//#ifdef DEBUG
//#         System.out.println(vrId + " try find in VS IDIS at " +vr_icuroff);
//#endif
        i_dis= new DataInputStream( i_is);

        try{
            i_dis.skip( vr_icuroff);
            ir_pos= i_dis.readInt();
            ir_size= i_dis.readInt();
            ir_res1= i_dis.readInt();
            ir_res2= i_dis.readInt();
            ir_res3= i_dis.readInt();
            ir_res4= i_dis.readInt();
            ir_res5= i_dis.readInt();
            ir_res6= i_dis.readInt();
            i_dis.close();
//#ifdef DEBUG
//#             System.out.println("Readed data index from IDIS: " +ir_pos +"/" +ir_size);
//#endif
        }
        catch( EOFException eofe){ throw new InvalidRecordIDException( "VirtualStore hasn't this record");}
        catch( IOException ioe){ throw new RecordStoreException( "VirtualStore internal error");}
        i_dis= null;

        vrData= new byte[ ir_size];

//#ifdef DEBUG
//#         System.out.println(vrId + " Data try find in DIS at " +vr_icuroff);
//#endif
        dis= new DataInputStream( is);
        try{
            dis.skip( ir_pos);
            dis.read( vrData);
            dis.close();
//#ifdef DEBUG
//#             System.out.println("Readed data from DIS: " +vrData.length +" bytes ->" +vrData.toString());
//#endif
        }
        catch( IOException ioe){ throw new RecordStoreException( "VirtualStore internal error");}
        dis= null;

        return vrData;
    }

    public static void deleteVirtualStore( String name) throws RecordStoreException {
        if( name ==null) return;
        fname= name +".data";
        if_name= name +".index";

        dfile= FileIO.createConnection( fname);
        ifile= FileIO.createConnection( if_name);
        fname= null; if_name= null;

        if( dfile ==null) throw new RecordStoreNotOpenException( "VirtualStore not open");
        if( ifile ==null) throw new RecordStoreNotOpenException( "VirtualStore not open");
        try{
            dfile.close();
            ifile.close();
            dfile.delete();
            ifile.delete();
        }catch( IOException ioe){ throw new RecordStoreException();}

        return;
    }// deleteVirtualStore

    public static int addVirtualRecord( byte[] data, int off, int len) throws RecordStoreException {
        //InputStream is, i_is;
        OutputStream os, i_os;
        //DataInputStream dis, i_dis;
        DataOutputStream dos, i_dos;

        if( dfile ==null || ifile ==null) throw new RecordStoreNotOpenException( "VirtualStore not open");

        try {
            os= dfile.openOutputStream( 0);
            i_os= ifile.openOutputStream( 0);
            data_total= (int)dfile.fileSize();
            vr_total= (int)(ifile.fileSize() /I_RECORDSIZE);
        }catch( IOException ioe){
            try{
                os= dfile.openOutputStream();
                i_os= ifile.openOutputStream();
                os.close(); i_os.close();

                os= dfile.openOutputStream( 0);
                i_os= ifile.openOutputStream( 0);
                data_total= (int)dfile.fileSize();
                vr_total= (int)(ifile.fileSize() /I_RECORDSIZE);
            }catch( IOException ioe2){ throw new RecordStoreNotOpenException( "VirtualStore internal error");}
        }

        i_dos= null;
        i_dos= new DataOutputStream( i_os);

        ir_pos= data_total;
        ir_size= len;

        try{
            i_dos.writeInt( ir_pos);
            i_dos.writeInt( ir_size);
            i_dos.writeInt( ir_res1);
            i_dos.writeInt( ir_res2);
            i_dos.writeInt( ir_res3);
            i_dos.writeInt( ir_res4);
            i_dos.writeInt( ir_res5);
            i_dos.writeInt( ir_res6);
            i_dos.close();
        }
        catch( IOException ioe){
            throw new RecordStoreException( "VirtualStore internal error");
        }
        i_dos= null;
        vr_total++;
        vr_icuroff= vr_total *I_RECORDSIZE;

        try{
            os.write( data, off, len);
            os.close();
        }
        catch( IOException ioe){
            throw new RecordStoreException( "VirtualStore internal error");
        }
        os= null;

        return vr_total;
    }
}
//#endif