/*
 * $Id: LocalizableExceptionAdapter.java,v 1.3 2007-07-13 23:36:40 ofung Exp $
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

package com.sun.xml.rpc.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.Localizer;
import com.sun.xml.rpc.util.localization.NullLocalizable;

/**
 * LocalizableExceptionAdapter
 *
 * @author JAX-RPC Development Team
 */
public class LocalizableExceptionAdapter
	extends Exception
	implements Localizable {
	protected Localizable localizablePart;
	protected Throwable nestedException;

	public LocalizableExceptionAdapter(Throwable nestedException) {
		this.nestedException = nestedException;
		if (nestedException instanceof Localizable) {
			localizablePart = (Localizable) nestedException;
		} else {
			localizablePart = new NullLocalizable(nestedException.toString());
		}
	}

	public String getKey() {
		return localizablePart.getKey();
	}

	public Object[] getArguments() {
		return localizablePart.getArguments();
	}

	public String getResourceBundleName() {
		return localizablePart.getResourceBundleName();
	}

	public String toString() {
		// for debug purposes only
		return nestedException.toString();
	}

	public String getLocalizedMessage() {
		if (nestedException == localizablePart) {
			Localizer localizer = new Localizer();
			return localizer.localize(localizablePart);
		} else {
			return nestedException.getLocalizedMessage();
		}
	}

	public String getMessage() {
		return getLocalizedMessage();
	}

	public Throwable getNestedException() {
		return nestedException;
	}

	public void printStackTrace() {
		nestedException.printStackTrace();
	}

	public void printStackTrace(PrintStream s) {
		nestedException.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter s) {
		nestedException.printStackTrace(s);
	}
}
