/*
 * $Id: EncoderUtils.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
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

package com.sun.xml.rpc.encoding.simpletype;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EncoderUtils {
    public static boolean needsCollapsing(String str) {
        int len = str.length();
        int spanLen = 0;

        for (int idx = 0; idx < len; ++idx) {
            if (Character.isWhitespace(str.charAt(idx))) {
                ++spanLen;
            } else if (spanLen > 0) {
                if (spanLen == idx) {
                    // leading whitespace
                    return true;
                } else {
                    // non-leading, non-trailing whitespace
                    if (str.charAt(idx - spanLen) != ' ') {
                        // first whitespace character is not a space
                        return true;
                    }
                    if (spanLen > 1) {
                        // there is a span of multiple whitespace characters
                        return true;
                    }
                }

                spanLen = 0;
            }
        }

        if (spanLen > 0) {
            // trailing whitespace
            return true;
        }

        return false;
    }

    public static String collapseWhitespace(String str) {
        if (!needsCollapsing(str)) {
            return str;
        }

        // the assumption is that most strings will not need to be collapsed,
        // so the code below will usually not be reached

        int len = str.length();
        char[] buf = new char[len];
        str.getChars(0, len, buf, 0);

        int leadingWSLen = 0;
        int trailingWSLen = 0;
        int spanLen = 0;

        for (int idx = 0; idx < len; ++idx) {
            if (Character.isWhitespace(buf[idx])) {
                ++spanLen;
            } else if (spanLen > 0) {
                if (spanLen == idx) {
                    // leading whitespace
                    leadingWSLen = spanLen;
                } else {
                    // non-leading, non-trailing whitespace

                    // ensure that the first whitespace character is a space
                    int firstWSIdx = idx - spanLen;
                    buf[firstWSIdx] = ' ';

                    if (spanLen > 1) {
                        // remove all but the first whitespace character
                        System.arraycopy(
                            buf,
                            idx,
                            buf,
                            firstWSIdx + 1,
                            len - idx);
                        len -= (spanLen - 1);
                        idx = firstWSIdx + 1;
                    }
                }

                spanLen = 0;
            }
        }

        if (spanLen > 0) {
            // trailing whitespace
            trailingWSLen = spanLen;
        }

        return new String(
            buf,
            leadingWSLen,
            len - leadingWSLen - trailingWSLen);
    }

    public static String removeWhitespace(String str) {
        int len = str.length();
        StringBuffer buf = new StringBuffer();
        int firstNonWS = 0;
        int idx = 0;
        for (; idx < len; ++idx) {
            if (Character.isWhitespace(str.charAt(idx))) {
                if (firstNonWS < idx)
                    buf.append(str.substring(firstNonWS, idx));
                firstNonWS = idx + 1;
            }
        }
        if (firstNonWS < idx)
            buf.append(str.substring(firstNonWS, idx));
        return buf.toString();
    }
}
