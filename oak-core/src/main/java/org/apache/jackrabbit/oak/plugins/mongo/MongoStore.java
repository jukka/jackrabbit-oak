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

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.Blob;
import org.apache.jackrabbit.oak.plugins.memory.ArrayBasedBlob;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.apache.jackrabbit.oak.spi.state.NodeStoreBranch;

import com.google.common.io.ByteStreams;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoStore implements NodeStore {

    private static final int MAX_RECORD_SIZE = 256 * 1024;

    private final Mongo mongo;

    private final DB db;

    private MongoState root;

    public MongoStore() throws UnknownHostException {
        this.mongo = new Mongo();
        this.db = mongo.getDB("Oak");
    }

    public void close() {
        mongo.close();
    }

    //---------------------------------------------------------< NodeStore >--

    @Override @Nonnull
    public synchronized NodeState getRoot() {
        return root;
    }

    @Override @Nonnull
    public NodeStoreBranch branch() {
        return new MongoBranch(getRoot());
    }

    @Override
    public Blob createBlob(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[MAX_RECORD_SIZE];
        do {
            int n = ByteStreams.read(inputStream, buffer, 0, buffer.length);
            if (n < buffer.length) {
            }
            byte[] data = new byte[n];
            System.arraycopy(buffer, 0, data, 0, n);
            return new ArrayBasedBlob(data);
        } else {
            
        }
        return null;
    }

    //---------------------------------------------------< package private >--

    

}
