/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.plugins.nodetype;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.jackrabbit.JcrConstants.JCR_CHILDNODEDEFINITION;
import static org.apache.jackrabbit.JcrConstants.JCR_HASORDERABLECHILDNODES;
import static org.apache.jackrabbit.JcrConstants.JCR_ISMIXIN;
import static org.apache.jackrabbit.JcrConstants.JCR_NODETYPENAME;
import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYITEMNAME;
import static org.apache.jackrabbit.JcrConstants.JCR_PROPERTYDEFINITION;
import static org.apache.jackrabbit.JcrConstants.JCR_SUPERTYPES;
import static org.apache.jackrabbit.oak.api.Type.BOOLEAN;
import static org.apache.jackrabbit.oak.api.Type.NAME;
import static org.apache.jackrabbit.oak.api.Type.NAMES;
import static org.apache.jackrabbit.oak.plugins.nodetype.NodeTypeConstants.JCR_IS_QUERYABLE;
import static org.apache.jackrabbit.oak.plugins.nodetype.NodeTypeConstants.JCR_NODE_TYPES;
import static org.apache.jackrabbit.oak.plugins.nodetype.NodeTypeConstants.NODE_TYPES_PATH;
import static org.apache.jackrabbit.oak.plugins.nodetype.NodeTypeConstants.OAK_NODE_TYPES;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.spi.commit.DefaultEditor;
import org.apache.jackrabbit.oak.spi.commit.Validator;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;

/**
 * Editor that validates the consistency of the in-content node type registry
 * under {@code /jcr:system/jcr:nodeTypes} and maintains the access-optimized
 * version uncer {@code /jcr:system/oak:nodeTypes}.
 *
 * <ul>
 *     <li>validate new definitions</li>
 *     <li>detect collisions,</li>
 *     <li>prevent circular inheritance,</li>
 *     <li>reject modifications to definitions that render existing content invalid,</li>
 *     <li>prevent un-registration of built-in node types.</li>
 * </ul>
 */
class RegistrationEditor extends DefaultEditor {

    private final NodeBuilder system;

    private boolean processed = false;

    RegistrationEditor(NodeBuilder system) {
        this.system = checkNotNull(system);
    }

    private Validator processChangedTypes(String name)
            throws CommitFailedException {
        if (!processed) {
            if (JCR_NODE_TYPES.equals(name) || OAK_NODE_TYPES.equals(name)) {
                system.removeNode(OAK_NODE_TYPES);
                if (system.hasChildNode(JCR_NODE_TYPES)) {
                    processChangedTypes(
                            system.child(JCR_NODE_TYPES),
                            system.child(OAK_NODE_TYPES));
                }
                processed = true;
            }
        }
        // TODO: For changes under /jcr:system/jcr:nodeTypes, we should
        // return a validator that makes sure that all affected content
        // in the repository is still valid with the new type definitions.
        // Something like this:
        // if (JCR_NODE_TYPES.equals(name)) {
        //     return new TypeChangeValidator();
        // }
        return null;
    }

    private void processChangedTypes(NodeBuilder jcr, NodeBuilder oak)
            throws CommitFailedException {
        for (String name : jcr.getChildNodeNames()) {
            String path = NODE_TYPES_PATH + "/" + name;
            NodeBuilder jcrType = jcr.child(name);
            NodeBuilder oakType = oak.child(name);

            // - jcr:nodeTypeName (NAME) protected mandatory
            PropertyState nodeTypeName = validateProperty(
                    path, jcrType, JCR_NODETYPENAME, NAME);
            if (!name.equals(nodeTypeName.getValue(NAME))) {
                throw new CommitFailedException(
                        "Invalid " + JCR_NODETYPENAME + " in " + path);
            }

            // - jcr:supertypes (NAME) protected multiple
            PropertyState supertypes = validateProperty(
                    path, jcrType, JCR_SUPERTYPES, NAMES);
            for (String supertype : supertypes.getValue(NAMES)) {
                if (!jcr.hasChildNode(supertype)) {
                    throw new CommitFailedException(
                            "Missing supertype " + supertype + " in " + path);
                }
            }
            oakType.setProperty(supertypes);

            // - jcr:isAbstract (BOOLEAN) protected mandatory
            PropertyState queryable = validateProperty(
                    path, jcrType, JCR_IS_QUERYABLE, BOOLEAN);
            oakType.setProperty(queryable);

            // - jcr:isMixin (BOOLEAN) protected mandatory
            PropertyState mixin = validateProperty(
                    path, jcrType, JCR_ISMIXIN, BOOLEAN);
            oakType.setProperty(mixin);

            // - jcr:hasOrderableChildNodes (BOOLEAN) protected mandatory
            PropertyState orderable = validateProperty(
                    path, jcrType, JCR_HASORDERABLECHILDNODES, BOOLEAN);
            oakType.setProperty(orderable);

            // - jcr:primaryItemName (NAME) protected
            PropertyState primary = jcrType.getProperty(JCR_PRIMARYITEMNAME);
            if (primary != null) {
                if (primary.getType() != NAME) {
                    throw new CommitFailedException(
                            "Invalid " + JCR_PRIMARYITEMNAME + " in " + path);
                }
                oakType.setProperty(primary);
            }

            // + jcr:propertyDefinition (nt:propertyDefinition) = nt:propertyDefinition protected sns
            // + jcr:childNodeDefinition (nt:childNodeDefinition) = nt:childNodeDefinition protected sns
            for (String childName : jcrType.getChildNodeNames()) {
                if (childName.startsWith(JCR_PROPERTYDEFINITION)) {
                } else if (childName.startsWith(JCR_CHILDNODEDEFINITION)) {
                } else {
                    throw new CommitFailedException(
                            "Invalid child node " + childName + " in " + path);
                }
            }
        }
    }

    private void processPropertyDefinition(
            NodeBuilder oakType, NodeBuilder definition)
            throws CommitFailedException {
    }

    private PropertyState validateProperty(
            String path, NodeBuilder type,
            String propertyName, Type<?> propertyType)
            throws CommitFailedException {
        PropertyState property = type.getProperty(propertyName);
        if (property != null && property.getType() == propertyType) {
            return property;
        } else {
            throw new CommitFailedException(
                    "Invalid property " + propertyName + " in " + path);
        }
    }

    //------------------------------------------------------------< Editor >--

    @Override
    public Validator childNodeAdded(String name, NodeState after)
            throws CommitFailedException {
        return processChangedTypes(name);
    }

    @Override
    public Validator childNodeChanged(
            String name, NodeState before, NodeState after)
            throws CommitFailedException {
        return processChangedTypes(name);
    }

    @Override
    public Validator childNodeDeleted(String name, NodeState before)
            throws CommitFailedException {
        return processChangedTypes(name);
    }

}