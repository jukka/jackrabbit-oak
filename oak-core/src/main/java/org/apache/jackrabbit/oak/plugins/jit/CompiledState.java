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
package org.apache.jackrabbit.oak.plugins.jit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

public class CompiledState implements NodeState {

    private final StateClass klass;

    private final byte[] data;

    @Override @CheckForNull
    public PropertyState getProperty(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getPropertyCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Nonnull
    public Iterable<? extends PropertyState> getProperties() {
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
    public NodeState getChildNode(String name) {
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
    public Iterable<? extends ChildNodeEntry> getChildNodeEntries() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public NodeBuilder builder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void compareAgainstBaseState(NodeState base, NodeStateDiff diff) {
        // TODO Auto-generated method stub

    }

}
