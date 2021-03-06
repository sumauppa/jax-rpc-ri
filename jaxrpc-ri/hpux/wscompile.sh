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

# Script to run WSCompile
#

if [ -z "$JAVA_HOME" ]; then
	echo "ERROR: Set JAVA_HOME to the path where the J2SE (JDK) is installed (e.g., /usr/java/jdk1.4)"
	exit 1
fi

bin_dir=`dirname $0`
WEBSERVICES_LIB=`cd $bin_dir/../share/lib; pwd`
WEBSERVICES_PRIVATE_LIB=`cd $bin_dir/../private/share/lib; pwd`

# Set CLASSPATH
CLASSPATH=$WEBSERVICES_LIB/jaxrpc-impl.jar:$WEBSERVICES_LIB/jaxrpc-api.jar:$WEBSERVICES_PRIVATE_LIB/jaxrpc-spi.jar:$WEBSERVICES_LIB/activation.jar:$WEBSERVICES_LIB/saaj-api.jar:$WEBSERVICES_LIB/saaj-impl.jar:$WEBSERVICES_LIB/mail.jar:$WEBSERVICES_LIB/endorsed/dom.jar:$WEBSERVICES_LIB/endorsed/xalan.jar:$WEBSERVICES_LIB/endorsed/xercesImpl.jar:$WEBSERVICES_LIB/endorsed/lib/jaxp-api.jar:$WEBSERVICES_LIB/endorsed/sax.jar:$JAVA_HOME/lib/tools.jar:$WEBSERVICES_PRIVATE_LIB/relaxngDatatype.jar:$WEBSERVICES_PRIVATE_LIB/xsdlib.jar

$JAVA_HOME/bin/java $WSCOMPILE_OPTS -classpath "$CLASSPATH" com.sun.xml.rpc.tools.wscompile.Main "$@"
