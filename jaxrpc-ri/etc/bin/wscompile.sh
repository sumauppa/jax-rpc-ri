#!/bin/sh

#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
# 
# Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
# 
# The contents of this file are subject to the terms of either the GNU
# General Public License Version 2 only ("GPL") or the Common Development
# and Distribution License("CDDL") (collectively, the "License").  You
# may not use this file except in compliance with the License. You can obtain
# a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
# or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
# language governing permissions and limitations under the License.
# 
# When distributing the software, include this License Header Notice in each
# file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
# Sun designates this particular file as subject to the "Classpath" exception
# as provided by Sun in the GPL Version 2 section of the License file that
# accompanied this code.  If applicable, add the following below the License
# Header, with the fields enclosed by brackets [] replaced by your own
# identifying information: "Portions Copyrighted [year]
# [name of copyright owner]"
# 
# Contributor(s):
# 
# If you wish your version of this file to be governed by only the CDDL or
# only the GPL Version 2, indicate your decision by adding "[Contributor]
# elects to include this software in this distribution under the [CDDL or GPL
# Version 2] license."  If you don't indicate a single choice of license, a
# recipient has the option to distribute your version of this file under
# either the CDDL, the GPL Version 2 or to extend the choice of license to
# its licensees as provided above.  However, if you add GPL Version 2 code
# and therefore, elected the GPL Version 2 license, then the option applies
# only if the new code is made subject to such option by the copyright
# holder.
#

if [ -z "$JAVA_HOME" ]; then
	echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.3)"
	exit 1
fi

if [ -z "$JAXRPC_HOME" ]; then
	echo "ERROR: Set JAXRPC_HOME to the root of a JAXRPC-RI distribution (e.g., /usr/bin/jaxrpc-ri/build)"
	exit 1
fi


CLASSPATH=.:$JAXRPC_HOME/lib/jaxrpc-impl.jar:$JAXRPC_HOME/lib/jaxrpc-api.jar:$JAXRPC_HOME/lib/jaxrpc-spi.jar:$JAXRPC_HOME/lib/activation.jar:$JAXRPC_HOME/lib/saaj-api.jar:$JAXRPC_HOME/lib/saaj-impl.jar:$JAXRPC_HOME/lib/mail.jar:$JAXRPC_HOME/lib/jaxp-api.jar:$JAXRPC_HOME/lib/dom.jar:$JAXRPC_HOME/lib/sax.jar:$JAXRPC_HOME/lib/xalan.jar:$JAXRPC_HOME/lib/xercesImpl.jar:$JAXRPC_HOME/lib/jcert.jar:$JAXRPC_HOME/lib/jnet.jar:$JAXRPC_HOME/lib/jsse.jar:$JAVA_HOME/lib/tools.jar:$JAXRPC_HOME/lib/jax-qname.jar:$JAXRPC_HOME/lib/relaxngDatatype.jar:$JAXRPC_HOME/lib/xsdlib.jar

$JAVA_HOME/bin/java -cp "$CLASSPATH" com.sun.xml.rpc.tools.wscompile.Main "$@"


