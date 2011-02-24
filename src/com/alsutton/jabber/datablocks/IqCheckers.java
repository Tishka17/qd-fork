/*
 * IqCheckers.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 
  
package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;


public class IqCheckers extends Iq {
    
    public IqCheckers(String to) {
        super(to, Iq.TYPE_GET, "checkers");
        addChildNs("query", "checkers").setAttribute("state", "request");
    }
    
    public IqCheckers(JabberDataBlock request, boolean answer) {
        super(request.getAttribute("from"), Iq.TYPE_RESULT, "checkers" );
        addChildNs("query", "checkers").setAttribute("state", (answer)?"start":"cancel");
    }

    public IqCheckers(String to, boolean answer) {
        super(to, Iq.TYPE_RESULT, "checkers" );
        addChildNs("query", "checkers").setAttribute("state", (answer)?"start":"cancel");
    }
}
*/