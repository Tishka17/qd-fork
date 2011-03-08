/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.alsutton.jabber;
//#ifdef XML_CONSOLE
//# import console.xml.XMLList;
//#endif
import com.alsutton.jabber.datablocks.Iq;
import java.util.*;
import xmpp.XmppError;

/**
 * The dispatcher for blocks that have arrived. Adds new blocks to the
 * dispatch queue, and then dispatches waiting blocks in their own thread to
 * avoid holding up the stream reader.
 */

public class JabberDataBlockDispatcher
{
  /**
   * The recipient waiting on this stream
   */

  private JabberListener listener;
  private JabberStream stream;

  private Vector blockListeners=new Vector(0);

  /**
   * The list of messages waiting to be dispatched
   */

  /**
   * Flag to watch the dispatching loop
   */

  private boolean dispatcherActive;

  boolean isActive() { return dispatcherActive; }

  /**
   * Constructor to start the dispatcher in a thread.
   */

  public JabberDataBlockDispatcher(JabberStream stream)  {
      this.stream=stream;
  }

  /**
   * Set the listener that we are dispatching to. Allows for switching
   * of clients in mid stream.
   *
   * @param _listener The listener to dispatch to.
   */

  public void setJabberListener( JabberListener _listener )
  {
    listener = _listener;
  }

  public void addBlockListener(JabberBlockListener listener) {
      synchronized (blockListeners) {
          //System.out.println(blockListeners.contains(listener) + "/" + blockListeners.size() + " " + blockListeners.toString());
          if (blockListeners.contains(listener)) return;
          blockListeners.addElement(listener);
      }
  }
  public void cancelBlockListener(JabberBlockListener listener) {
      synchronized (blockListeners) {
          try {
              blockListeners.removeElement(listener);
              listener.destroy();
          }
          catch (Exception e) {
              e.printStackTrace();
          }
      }
  }


  public void broadcastJabberDataBlock( JabberDataBlock dataBlock ) { arriveDataBlock(dataBlock); }

  /**
   * The thread loop that handles dispatching any waiting datablocks
   */

    public void arriveDataBlock(JabberDataBlock dataBlock) {
        dispatcherActive = true;
        if (null == dataBlock) return;
            try {
//#ifdef XML_CONSOLE
//#                 if (console.xml.XMLList.enabled) {
//#                     stream.addLog(dataBlock.toString(), 10);
//#                 }
//#endif
                ++midlet.BombusQD.cf.incPacketCount;

                int processResult=JabberBlockListener.BLOCK_REJECTED;
                int block_size = blockListeners.size();
                //System.out.println("  -----S:blockListeners>> " + blockListeners.toString());
                synchronized (blockListeners) {
                    int i=0;
                    while (i<block_size) {
                        processResult=((JabberBlockListener)blockListeners.elementAt(i)).blockArrived(dataBlock);
                        //System.out.println("    processResult>> " + processResult + " name: "+ ((JabberBlockListener)blockListeners.elementAt(i)) );
                        if (processResult==JabberBlockListener.BLOCK_PROCESSED) break;
                        if (processResult==JabberBlockListener.NO_MORE_BLOCKS) {
                            blockListeners.removeElementAt(i);
                            block_size = blockListeners.size();
                            break;
                        }
                        ++i;
                    }
                }
                //System.out.println("  -----E:blockListeners>> " + blockListeners.toString());
                if (processResult==JabberBlockListener.BLOCK_REJECTED && listener != null ) processResult=listener.blockArrived( dataBlock );
                if (processResult==JabberBlockListener.BLOCK_REJECTED) {
                    if (dataBlock instanceof Iq) {
                        String type=dataBlock.getTypeAttribute();
                        if (type.equals("get") || type.equals("set")) {
                            dataBlock.setAttribute("to", dataBlock.getAttribute("from"));
                            dataBlock.setAttribute("from", null);
                            dataBlock.setTypeAttribute("error");
                            dataBlock.addChild(new XmppError(XmppError.FEATURE_NOT_IMPLEMENTED, null).construct());
                            stream.send(dataBlock);
                            dataBlock = null;
                        }
                     }
                    //TODO: reject iq stansas where type =="get" | "set"
                }
                dataBlock.destroy();
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }


  public void restart() {
      //#ifdef DEBUG_CONSOLE
//#        midlet.BombusQD.debug.add("restart dispatcher", 10);
       //#endif
       halt();
       listener = null;
       stream = null;
       resetBlockListners();
  }


  public void cancelBlockListenerByClass(Class removeClass) {
        synchronized (blockListeners) {
            for (int index = blockListeners.size() - 1; 0 <= index; --index) {
                JabberBlockListener list= (JabberBlockListener)blockListeners.elementAt(index);
                if (list.getClass().equals(removeClass)) {
                    blockListeners.removeElementAt(index);
                    list.destroy();
                }
            }
        }
  }

  public void rosterNotify(){
    listener.rosterItemNotify();
  }

  /**
   * Method to stop the dispatcher
   */

  public void halt()
  {
    //setJabberListener( null );
    dispatcherActive = false;
  }


  /**
   * Method to tell the listener the connection has been terminated
   *
   * @param exception The exception that caused the termination. This may be
   * null for the situtations where the connection has terminated without an
   * exception.
   */

  public void broadcastTerminatedConnection( Exception exception )
  {
    halt();
    if( listener != null ) listener.connectionTerminated( exception );
  }

    void resetBlockListners() {
        try {
            synchronized (blockListeners) {
                for (int i = 0; i < blockListeners.size(); ++i) {
                    ((JabberBlockListener)blockListeners.elementAt(i)).destroy();
                }
                blockListeners = new Vector(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /**
   * Method to tell the listener the stream is ready for talking to.
   */

  public void broadcastBeginConversation()
  {
    if( listener != null ) listener.beginConversation();
  }
}
