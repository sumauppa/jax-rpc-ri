/*
 * $Id: StreamingHandlerState.java,v 1.3 2007-07-13 23:36:22 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.server;

import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;

import com.sun.xml.rpc.client.StubPropertyConstants;

/**
 * The internal state of an otherwise stateless StreamingHandler. 
 *
 * @author JAX-RPC Development Team
 */
public class StreamingHandlerState {
        
    public StreamingHandlerState(SOAPMessageContext context) {
        _context = context;
        _request = new InternalSOAPMessage(_context.getMessage());
        _response = null;
    }

    public boolean isFastInfoset() {
        return _context.isFastInfoset();
    }
    
    public boolean acceptFastInfoset() {
        return _context.acceptFastInfoset();
    }
    
    public SOAPMessageContext getMessageContext() {
        return _context;
    }

    public boolean isFailure() {
        if (_response == null) {
            return false;
        } else {
            return _response.isFailure();
        }
    }

    public InternalSOAPMessage getRequest() {
        return _request;
    }

    public InternalSOAPMessage getResponse() {
        if (_response == null) {
            // Create FI response if accepted by client
            _response = new InternalSOAPMessage(
                _context.createMessage(_context.acceptFastInfoset(), 
                                       _context.acceptFastInfoset())); 
        }

        return _response;
    }

    public void setResponse(InternalSOAPMessage msg) {
        _response = msg;
    }

    public InternalSOAPMessage resetResponse() {
        _response = null;
        return getResponse();
    }

    private SOAPMessageContext _context;
    private InternalSOAPMessage _request;
    private InternalSOAPMessage _response;

    public static final int CALL_NO_HANDLERS = -1;
    public static final int CALL_FAULT_HANDLERS = 0;
    public static final int CALL_RESPONSE_HANDLERS = 1;

    int handlerFlag = CALL_RESPONSE_HANDLERS;
    
    public void setHandlerFlag(int handlerFlag) {
        this.handlerFlag = handlerFlag;
    }

    public int getHandlerFlag() {
        return handlerFlag;
    }
    
}
