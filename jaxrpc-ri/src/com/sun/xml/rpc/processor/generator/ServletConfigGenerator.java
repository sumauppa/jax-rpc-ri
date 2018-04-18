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
 * $Id: ServletConfigGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.util.DirectoryUtil;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ServletConfigGenerator extends GeneratorBase {
    private File configFile;
    private Service currentService;
    private IndentingWriter out;
    private int portCount;
    private TreeSet operations = null;

    public ServletConfigGenerator() {
        configFile = null;
        out = null;
        portCount = 0;
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new ServletConfigGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new ServletConfigGenerator(model, config, properties);
    }

    private ServletConfigGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        configFile = null;
        out = null;
        portCount = 0;
    }

    protected void preVisitService(Service service) throws Exception {
        try {
            currentService = service;
            String className = service.getName().getLocalPart();
            configFile = configFileForClass(className, nonclassDestDir, env);

            /* Here the filename for the ServletConfig to be geenrated is
               retrieved to be set in the GeneratedFileInfo Object */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(configFile);
            fi.setType(GeneratorConstants.FILE_TYPE_SERVLET_CONFIG);
            env.addGeneratedFile(fi);

            out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(configFile)));
            portCount = 0;
            out.pln("# This file is generated by wscompile.");
            out.pln();
        } catch (IOException e) {
            fail("cant.write", configFile.toString());
        }
    }

    protected void postVisitService(Service service) throws Exception {
        try {
            out.pln("portcount=" + portCount);
            closeFile();
        } catch (IOException e) {
            fail("cant.write", configFile.toString());
        } finally {
            currentService = null;
        }
    }

    public void visit(Port port) throws Exception {
        int myPortNum = portCount;
        portCount = myPortNum + 1;
        operations = new TreeSet(new StringLenComparator());
        Iterator operations = port.getOperations();
        while (operations.hasNext()) {
            ((Operation) operations.next()).accept(this);
        }
        try {
            String portID = "port" + myPortNum;
            String servant = null;
            servant = port.getJavaInterface().getImpl();
            if (servant == null) {
                servant =
                    "Please specify the servant class for port:"
                        + port.getName().getLocalPart();
            }
            out.pln(portID + ".tie=" + env.getNames().tieFor(port));
            out.pln(portID + ".servant=" + servant);
            out.pln(portID + ".name=" + port.getName().getLocalPart());
            out.pln(
                portID
                    + ".wsdl.targetNamespace="
                    + model.getTargetNamespaceURI());
            out.pln(
                portID
                    + ".wsdl.serviceName="
                    + currentService.getName().getLocalPart());
            QName portName =
                (QName) port.getProperty(
                    ModelProperties.PROPERTY_WSDL_PORT_NAME);
            out.pln(portID + ".wsdl.portName=" + portName.getLocalPart());
        } catch (IOException e) {
            fail("generator.cant.write", configFile.toString());
        }
    }

    protected void visitOperation(Operation operation) throws Exception {
        operations.add(operation);
    }

    private void closeFile() throws IOException {
        if (out != null) {
            out.close();
            out = null;
        }
    }

    private String getBaseName(String s) {
        if (s.endsWith("Port")) {
            return s.substring(0, s.length() - 4);
        } else {
            return s;
        }
    }

    private String getPortName(String s) {
        return getBaseName(s) + "Port";
    }

    /**
     * Return the File object that should be used as the configuration
     * file for the given Java class, using the supplied destination
     * directory for the top of the package hierarchy.
     */
    protected static File configFileForClass(
        String className,
        File destDir,
        ProcessorEnvironment env)
        throws GeneratorException {
        File packageDir =
            DirectoryUtil.getOutputDirectoryFor(className, destDir, env);
        String outputName = Names.stripQualifier(className);

        String outputFileName = outputName + "_Config.properties";
        return new File(packageDir, outputFileName);
    }

    private static class StringLenComparator implements java.util.Comparator {
        public int compare(Object o1, Object o2) {
            int len1, len2;
            len1 = ((Operation) o1).getName().getLocalPart().length();
            len2 = ((Operation) o2).getName().getLocalPart().length();
            return (len1 <= len2) ? -1 : 1;
        }
    }
}
