/*
 * DebugList.java
 *
 */
//#ifdef CONSOLE
//# package console;
//#  
//# import client.Msg;
//# import java.util.Vector;
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public class DebugList {
//# 
//#     Vector stanzas=new Vector(0);
//#     private static DebugList instance;
//#     
//#     public static DebugList get(){
//# 	if (instance==null) {
//# 	    instance=new DebugList();
//# 	}
//# 	return instance;
//#     }
//#     
//#     public Msg msg(int index){
//# 	try {
//#             Msg msg=(Msg)stanzas.elementAt(index);
//# 	    return msg;
//# 	} catch (Exception e) {}
//# 	return null;
//#     }
//# 
//#     public void add(String msg, int type) {
//#       if (midlet.BombusQD.cf.debug) {
//# 	  try {
//#             int free = (int)Runtime.getRuntime().freeMemory()>>10;
//#             int total = (int)Runtime.getRuntime().totalMemory()>>10;
//#             Msg stanza=new Msg((byte)type, "debug", null, "[" + free + "/" + total + "]\t" + msg.toString());
//#             stanza.itemCollapsed=false;
//#             stanzas.addElement(stanza);
//#             stanza=null;
//# 	  } catch (Exception e) {}
//#        }
//#     }
//# 
//#     public int size(){
//# 	return stanzas.size();
//#     }
//# }
//#endif