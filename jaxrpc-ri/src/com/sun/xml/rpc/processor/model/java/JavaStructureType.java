/*
 * $Id: JavaStructureType.java,v 1.3 2007-07-13 23:36:05 ofung Exp $
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

package com.sun.xml.rpc.processor.model.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.xml.rpc.processor.model.ModelException;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaStructureType extends JavaType {
    
    public JavaStructureType() {}
    
    public JavaStructureType(String name, boolean present, Object owner) {
        super(name, present, "null");
        this.owner = owner;
    }
    
    public void add(JavaStructureMember m) {
        if (membersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness.javastructuretype",
                new Object[] {m.getName(), getRealName()});
        }
        members.add(m);
        membersByName.put(m.getName(), m);
    }
    
    
    public JavaStructureMember getMemberByName(String name) {
        if (membersByName.size() != members.size()) {
            initializeMembersByName();
        }
        return (JavaStructureMember) membersByName.get(name);
    }
    
    public Iterator getMembers() {
        return members.iterator();
    }
    
    public int getMembersCount() {
        return members.size();
    }
    
    /* serialization */
    public List getMembersList() {
        return members;
    }
    
    /* serialization */
    public void setMembersList(List l) {
        members = l;
    }
    
    private void initializeMembersByName() {
        membersByName = new HashMap();
        if (members != null) {
            for (Iterator iter = members.iterator(); iter.hasNext();) {
                JavaStructureMember m = (JavaStructureMember) iter.next();
                if (m.getName() != null &&
                    membersByName.containsKey(m.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                membersByName.put(m.getName(), m);
            }
        }
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    
    public JavaStructureType getSuperclass() {
        return superclass;
    }
    
    public void setSuperclass(JavaStructureType superclassType) {
        superclass = superclassType;
    }
    
    public void addSubclass(JavaStructureType subclassType) {
        subclasses.add(subclassType);
        subclassType.setSuperclass(this);
    }
    
    public Iterator getSubclasses() {
        if (subclasses == null || subclasses.size() == 0) {
            return null;
        }
        return subclasses.iterator();
    }
    
    public Set getSubclassesSet() {
        return subclasses;
    }
    
    /* serialization */
    public void setSubclassesSet(Set s) {
        subclasses = s;
        for (Iterator iter = s.iterator(); iter.hasNext();) {
            ((JavaStructureType) iter.next()).setSuperclass(this);
        }
    }
    
    public Iterator getAllSubclasses() {
        Set subs = getAllSubclassesSet();
        if (subs.size() == 0) {
            return null;
        }
        return subs.iterator();
    }
    
    public Set getAllSubclassesSet() {
        Set transitiveSet = new HashSet();
        Iterator subs = subclasses.iterator();
        while (subs.hasNext()) {
            transitiveSet.addAll(
                ((JavaStructureType)subs.next()).getAllSubclassesSet());
        }
        transitiveSet.addAll(subclasses);
        return transitiveSet;
    }
    
    public Object getOwner() {

        // usually a SOAPStructureType
        return owner;
    }
    
    public void setOwner(Object owner) {

        // usually a SOAPStructureType
        this.owner = owner;
    }
    
    private List members = new ArrayList();
    private Map membersByName = new HashMap();

    // known subclasses of this type
    private Set subclasses = new HashSet();
    private JavaStructureType superclass;

    // usually a SOAPStructureType
    private Object owner;
    private boolean isAbstract = false;
}
