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

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStoreBranch;

class MongoBranch implements NodeStoreBranch {

    private MongoState base;

    private NodeState root;

    public MongoBranch(MongoState base) {
        this.base = base;
        this.root = base;
    }

    @Override @Nonnull
    public synchronized NodeState getBase() {
        return base;
    }

    @Override @Nonnull
    public synchronized NodeState getRoot() {
        return root;
    }

    @Override
    public synchronized void setRoot(NodeState newRoot) {
        this.root = newRoot;
    }

    @Override
    public synchronized boolean move(String source, String target) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public synchronized boolean copy(String source, String target) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override @Nonnull
    public synchronized NodeState merge() throws CommitFailedException {
        MongoState newRoot = null; // FIXME
        this.base = newRoot;
        this.root = newRoot;
        return newRoot;
    }

}
