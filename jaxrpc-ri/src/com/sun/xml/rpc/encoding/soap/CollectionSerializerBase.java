/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

/*
 * $Id: CollectionSerializerBase.java,v 1.3 2007-07-13 23:36:00 ofung Exp $
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

package com.sun.xml.rpc.encoding.soap;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.encoding.SerializerCallback;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class CollectionSerializerBase
    extends SerializerBase
    implements InternalEncodingConstants {

    protected QName elemName;
    protected QName elemType;
    protected Class elemClass;
    protected int rank = -1;
    protected int[] dims;
    protected int[] null_dims;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        this.soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    protected CollectionSerializerBase(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName elemName,
        QName elemType,
        Class elemClass,
        int rank,
        int[] dims) {

        this(
            type,
            encodeType,
            isNullable,
            encodingStyle,
            elemName,
            elemType,
            elemClass,
            rank,
            dims,
            SOAPVersion.SOAP_11);
    }

    protected CollectionSerializerBase(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName elemName,
        QName elemType,
        Class elemClass,
        int rank,
        int[] dims,
        SOAPVersion ver) {
            
        super(type, encodeType, isNullable, encodingStyle);
        init(ver); // Initialize SOAP version
        if (elemType == null) {
            throw new IllegalArgumentException();
        }

        this.elemName = elemName;
        this.elemType = elemType;
        this.elemClass = elemClass;
        this.rank = rank;
        this.dims = dims;

        if (dims != null) {
            null_dims = dims;
        } else if (rank >= 0) {
            null_dims = new int[rank];
        } else {
            null_dims = new int[0];
        }
    }

    public void serialize(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {
            
        boolean pushedEncodingStyle = false;
        try {
            if (obj == null) {
                if (!isNullable) {
                    throw new SerializationException("soap.unexpectedNull");
                }

                serializeNull(name, writer, context);
            } else {
                String attrVal;

                writer.startElement((name != null) ? name : type);
                if (callback != null) {
                    callback.onStartTag(obj, name, writer, context);
                }

                pushedEncodingStyle =
                    context.pushEncodingStyle(encodingStyle, writer);

                if (encodeType) {
                    attrVal = XMLWriterUtil.encodeQName(writer, type);
                    writer.writeAttributeUnquoted(
                        XSDConstants.QNAME_XSI_TYPE,
                        attrVal);
                    // instead of writing the actual array type (as above), always
                    // use soapenc:Array as the value of xsi:type, for compatibility
                    // with some SOAP implementations out there (e.g. Apache SOAP)
                    //                    attrVal = XMLWriterUtil.encodeQName(writer, soapEncodingConstants.QNAME_ENCODING_ARRAY);
                    //                    writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);
                }

                Object[] objArray = convertToArray(obj);
                int[] dims =
                    ((this.dims != null)
                        ? this.dims
                        : getArrayDimensions(objArray));
                String encodedDims = encodeArrayDimensions(dims);
                attrVal =
                    XMLWriterUtil.encodeQName(writer, elemType) + encodedDims;
                writer.writeAttributeUnquoted(
                    soapEncodingConstants.getQNameEncodingArraytype(),
                    attrVal);

                serializeArrayInstance(objArray, dims, writer, context);

                writer.endElement();
            }
        } catch (SerializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        } catch (Exception e) {
            throw new SerializationException(
                new LocalizableExceptionAdapter(e));
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    protected abstract Object[] convertToArray(Object obj) throws Exception;

    protected void serializeNull(
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
            
        String attrVal;
        boolean pushedEncodingStyle = false;
        writer.startElement((name != null) ? name : type);

        pushedEncodingStyle = context.pushEncodingStyle(encodingStyle, writer);

        if (encodeType) {
            attrVal = XMLWriterUtil.encodeQName(writer, type);
            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }

        String encodedDims = encodeArrayDimensions(null_dims);
        attrVal = XMLWriterUtil.encodeQName(writer, elemType) + encodedDims;
        writer.writeAttributeUnquoted(
            soapEncodingConstants.getQNameEncodingArraytype(),
            attrVal);

        writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, "1");

        writer.endElement();
        if (pushedEncodingStyle) {
            context.popEncodingStyle();
        }
    }

    protected abstract void serializeArrayInstance(
        Object obj,
        int[] dims,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception;

    public Object deserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context) {

        boolean pushedEncodingStyle = false;
        try {
            pushedEncodingStyle = context.processEncodingStyle(reader);
            context.verifyEncodingStyle(encodingStyle);

            if (name != null) {
                verifyName(reader, name);
            }

            boolean isNull = getNullStatus(reader);
            if (!isNull) {
                // NOTE - just calling
                //      verifyType(reader);
                // here is too strict, because instead of, say, <myns:ArrayOfFoo> a client
                // can send <soap-enc:Array> (as long as the array type matches, but that is
                // tested later in the code)

                QName actualType = getType(reader);
                if (actualType != null) {
                    if (!actualType.equals(type)
                        && !actualType.equals(
                            soapEncodingConstants.getQNameEncodingArray())) {
                        throw new DeserializationException(
                            "soap.unexpectedElementType",
                            new Object[] {
                                type.toString(),
                                actualType.toString()});
                    }
                }

                int[] dims = verifyArrayType(reader);

                Object rslt = deserializeArrayInstance(reader, context, dims);
                XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
                return rslt;
            } else {
                if (!isNullable) {
                    throw new DeserializationException("soap.unexpectedNull");
                }

                skipEmptyContent(reader);

                return null;
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    protected abstract Object deserializeArrayInstance(
        XMLReader reader,
        SOAPDeserializationContext context,
        int[] dims)
        throws Exception;

    public static boolean isEmptyDimensions(int[] dims) {
        return (dims.length == 0);
    }

    public static int[] getArrayElementPosition(XMLReader reader, int[] dims)
        throws Exception {
            
        return getArrayElementPosition(reader, dims, SOAPVersion.SOAP_11);
    }

    public static int[] getArrayElementPosition(
        XMLReader reader,
        int[] dims,
        SOAPVersion ver)
        throws Exception {
            
        int[] elemPos = null;
        String attrVal = null;

        Attributes attrs = reader.getAttributes();

        if (ver == SOAPVersion.SOAP_11)
            attrVal =
                attrs.getValue(
                    com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING,
                    "position");
        else if (ver == SOAPVersion.SOAP_12)
            attrVal =
                attrs.getValue(
                    com.sun.xml.rpc.encoding.soap.SOAP12Constants.URI_ENCODING,
                    "position");

        if (attrVal != null) {
            elemPos = decodeArrayDimensions(attrVal);
            if ((isEmptyDimensions(dims) && elemPos.length != 1)
                || (!isEmptyDimensions(dims) && elemPos.length != dims.length)) {
                throw new DeserializationException(
                    "soap.illegalArrayElementPosition",
                    attrVal);
            }
        }

        return elemPos;
    }

    public static int[] getArrayOffset(XMLReader reader, int[] dims)
        throws Exception {
            
        return getArrayOffset(reader, dims, SOAPVersion.SOAP_11);
    }

    public static int[] getArrayOffset(
        XMLReader reader,
        int[] dims,
        SOAPVersion ver)
        throws Exception {
            
        int[] offset = null;
        String attrVal = null;

        Attributes attrs = reader.getAttributes();
        if (ver == SOAPVersion.SOAP_11)
            attrVal =
                attrs.getValue(
                    com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING,
                    "offset");
        else if (ver == SOAPVersion.SOAP_12)
            attrVal =
                attrs.getValue(
                    com.sun.xml.rpc.encoding.soap.SOAP12Constants.URI_ENCODING,
                    "offset");

        if (attrVal != null) {
            offset = decodeArrayDimensions(attrVal);
            if ((isEmptyDimensions(dims) && offset.length != 1)
                || (!isEmptyDimensions(dims) && offset.length != dims.length)) {
                throw new DeserializationException(
                    "soap.illegalArrayOffset",
                    attrVal);
            }
        }

        return offset;
    }

    protected int[] verifyArrayType(XMLReader reader) throws Exception {

        String arrayType = null;
        Attributes attrs = reader.getAttributes();
        arrayType =
            attrs.getValue(soapEncodingConstants.getURIEncoding(), "arrayType");
        if (arrayType == null) {
            throw new DeserializationException(
                "soap.malformedArrayType",
                "<arrayType attribute missing>");
        }

        verifyArrayElementType(arrayType, reader);
        return verifyArrayDimensions(arrayType, reader);
    }

    protected void verifyArrayElementType(String arrayType, XMLReader reader)
        throws Exception {

        QName actualElemType = getArrayElementType(arrayType, reader);
        if (!actualElemType.equals(elemType)) {
            throw new DeserializationException(
                "soap.unexpectedArrayElementType",
                new Object[] { elemType.toString(), actualElemType.toString()});
        }
    }

    public static QName getArrayElementType(String arrayType, XMLReader reader)
        throws Exception {

        QName elemType = null;
        boolean malformed = true;

        int idx = arrayType.indexOf('[');
        if (idx >= 0) {
            String elemTypeStr = arrayType.substring(0, idx).trim();
            if (elemTypeStr.length() > 0) {
                elemType = XMLReaderUtil.decodeQName(reader, elemTypeStr);
                malformed = false;
            }
        }

        if (malformed) {
            throw new DeserializationException(
                "soap.malformedArrayType",
                arrayType);
        }

        return elemType;
    }

    protected int[] verifyArrayDimensions(String arrayType, XMLReader reader)
        throws Exception {

        int[] actualDims = getArrayDimensions(arrayType, reader);

        if (rank >= 0) {
            if ((isEmptyDimensions(actualDims) && rank != 1)
                || (!isEmptyDimensions(actualDims)
                    && actualDims.length != rank)) {
                throw new DeserializationException(
                    "soap.unexpectedArrayRank",
                    new Object[] {
                        Integer.toString(rank),
                        Integer.toString(actualDims.length),
                        arrayType });
            }
        }

        if (dims != null) {
            if (actualDims.length > 0 && !Arrays.equals(dims, actualDims)) {
                throw new DeserializationException(
                    "soap.unexpectedArrayDimensions",
                    new Object[] {
                        encodeArrayDimensions(dims),
                        encodeArrayDimensions(actualDims),
                        arrayType });
            }
        }

        return actualDims;
    }

    public static int[] getArrayDimensions(String arrayType, XMLReader reader)
        throws Exception {

        int startIdx = arrayType.lastIndexOf('[');
        int endIdx = arrayType.lastIndexOf(']');
        if (startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new DeserializationException(
                "soap.malformedArrayType",
                arrayType);
        }

        String dimStr = arrayType.substring(startIdx, endIdx + 1);

        return decodeArrayDimensions(dimStr);
    }

    protected int getArrayRank(Object obj) {
        int rank = 0;
        Class type = obj.getClass();
        while (type != elemClass) {
            ++rank;
            type = type.getComponentType();
        }
        return rank;
    }

    protected int[] getArrayDimensions(Object obj) {
        int rank = ((this.rank >= 0) ? this.rank : getArrayRank(obj));
        return getArrayDimensions(obj, rank);
    }

    public static int[] getArrayDimensions(Object obj, int rank) {
        int[] dims = new int[rank];
        //findMaxArrayDimensions(obj, 0, dims);
        Object arr = obj;
        for (int i = 0; i < rank; ++i) {
            dims[i] = Array.getLength(arr);
            if (dims[i] == 0) {
                // if any dimension size is zero, then so are all the remaining ones
                break;
            }
            arr = Array.get(arr, 0);
        }
        return dims;
    }

    public static int[] decodeArrayDimensions(String dimStr) throws Exception {
        String str = dimStr.trim();
        if (str.charAt(0) != '[' || str.charAt(str.length() - 1) != ']') {
            throw new DeserializationException(
                "soap.malformedArrayDimensions",
                dimStr);
        }
        str = str.substring(1, str.length() - 1).trim();
        int strLen = str.length();

        int dimCount = 0;
        if (strLen > 0) {
            ++dimCount;
            int commaIdx = -1;
            while ((commaIdx = str.indexOf(',', commaIdx + 1)) >= 0) {
                ++dimCount;
            }
        }

        int[] dims = new int[dimCount];

        int idx = 0;
        char c;
        for (int i = 0; i < dimCount; ++i) {
            while (idx < strLen && Character.isWhitespace(str.charAt(idx))) {
                ++idx;
            }

            int startIdx = idx;
            int dim = 0;
            while (idx < strLen && Character.isDigit(c = str.charAt(idx))) {
                dim = dim * 10 + (c - '0');
                ++idx;
            }

            if (idx > startIdx) {
                dims[i] = dim;
            } else {
                throw new DeserializationException(
                    "soap.malformedArrayDimensions",
                    dimStr);
            }

            while (idx < strLen && Character.isWhitespace(str.charAt(idx))) {
                ++idx;
            }

            if (i < dimCount - 1) {
                if (!(idx < strLen && str.charAt(idx) == ',')) {
                    throw new DeserializationException(
                        "soap.malformedArrayDimensions",
                        dimStr);
                }
                ++idx;
            } else {
                if (idx != strLen) {
                    throw new DeserializationException(
                        "soap.malformedArrayDimensions",
                        dimStr);
                }
            }
        }

        return dims;
    }

    public static String encodeArrayDimensions(int[] dims) throws Exception {
        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < dims.length; ++i) {
            if (i > 0) {
                buf.append(',');
            }
            buf.append(dims[i]);
        }
        buf.append(']');
        return buf.toString();
    }

    public static boolean isPositionWithinBounds(int[] position, int[] dims) {
        for (int i = 0; i < position.length; ++i) {
            if (position[i] >= dims[i]) {
                return false;
            }
        }
        return true;
    }

    public static void incrementPosition(int[] position, int[] dims)
        throws Exception {

        for (int i = position.length - 1; i >= 0; --i) {
            if (++position[i] < dims[i]) {
                break;
            } else {
                if (i == 0) {
                    throw new DeserializationException(
                        "soap.outOfBoundsArrayElementPosition",
                        encodeArrayDimensions(position));
                } else {
                    position[i] = 0;
                }
            }
        }
    }

    public static int[] getDimensionOffsets(int[] dims) {
        int[] dimOffsets = null;

        if (isEmptyDimensions(dims)) {
            dimOffsets = new int[] { 1 };
        } else {
            dimOffsets = new int[dims.length];
            dimOffsets[dimOffsets.length - 1] = 1;
            for (int i = dimOffsets.length - 2; i >= 0; --i) {
                dimOffsets[i] = dims[i + 1] * dimOffsets[i + 1];
            }
        }

        return dimOffsets;
    }

    public static int indexFromPosition(int[] position, int[] dimOffsets) {
        int index = 0;
        for (int i = 0; i < position.length; ++i) {
            index += (position[i] * dimOffsets[i]);
        }
        return index;
    }

    public static int[] positionFromIndex(int index, int[] dimOffsets) {
        int[] position = new int[dimOffsets.length];
        int tmp = index;
        for (int i = 0; i < position.length; ++i) {
            position[i] = tmp / dimOffsets[i];
            tmp %= dimOffsets[i];
        }
        return position;
    }

}
