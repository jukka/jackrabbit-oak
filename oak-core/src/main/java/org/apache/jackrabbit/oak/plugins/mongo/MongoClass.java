/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.plugins.mongo;

import java.util.Arrays;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

class MongoClass {

    private final MongoStore store;

    private final String[] propertyNames;

    private final byte[] propertyTypes;

    private final String childNodeName;

    private int findProperty(String name) {
        return Arrays.binarySearch(propertyNames, name);
    }

    @CheckForNull
    PropertyState getProperty(String name, MongoRecord record) {
        int i = findProperty(name);
        if (i >= 0) {
            MongoRecord value = store.getRecord(record, 4 + 4 * i);
            return new MongoProperty(propertyNames[i], propertyTypes[i], value);
        }
        for (int i = 0; i < propertyNames.length; i++) {
            if (name.equals(propertyNames[i])) {
                
            }
        }
        return ;
    }

    @Override
    public long getPropertyCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Nonnull
    Iterable<? extends PropertyState> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasChildNode(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @CheckForNull
    NodeState getChildNode(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getChildNodeCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Iterable<String> getChildNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    Iterable<? extends ChildNodeEntry> getChildNodeEntries() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    NodeBuilder builder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void compareAgainstBaseState(NodeState base, NodeStateDiff diff) {
        // TODO Auto-generated method stub

    }

}
