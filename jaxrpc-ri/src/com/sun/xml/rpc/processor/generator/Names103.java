/*
 * $Id: Names103.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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
package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 * @author JAX-RPC Development Team
 *
 * JAXRPC 1.0.3 Names class
 */
public class Names103 extends Names {
    public String holderClassName(Port port, JavaType type) {
        if (type.getHolderName() != null)
            return type.getHolderName();
        return holderClassName(port, type.getName());
    }

    protected String holderClassName(Port port, String typeName) {
        String holderTypeName = (String) holderClassNames.get(typeName);
        if (holderTypeName == null) {
            // not a built-in holder class
            String className = port.getJavaInterface().getName();
            String packageName = getPackageName(className);
            if (packageName.length() > 0) {
                packageName += ".holders.";
            } else {
                packageName = "holders.";
            }
            //                if (!(typeName.startsWith("java.") || typeName.startsWith("javax."))) {
            if (!isInJavaOrJavaxPackage(typeName)) {
                typeName = stripQualifier(typeName);
            }
            int idx = typeName.indexOf(BRACKETS);
            while (idx > 0) {
                typeName =
                    typeName.substring(0, idx)
                        + ARRAY
                        + typeName.substring(idx + 2);
                idx = typeName.indexOf(BRACKETS);
            }
            holderTypeName = packageName + typeName + HOLDER_SUFFIX;
        }
        return holderTypeName;
    }

}
