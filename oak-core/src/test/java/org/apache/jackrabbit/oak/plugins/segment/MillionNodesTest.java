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
package org.apache.jackrabbit.oak.plugins.segment;

import org.apache.jackrabbit.oak.spi.state.NodeBuilder;

import com.mongodb.Mongo;

public class MillionNodesTest {

    // @Test
    public void testChangesFromRoot() throws Exception {
        Mongo mongo = new Mongo();
        try {
            SegmentStore store = new MongoStore(mongo.getDB("Oak2"), 100 * 1024 * 1024);
            SegmentWriter writer = new SegmentWriter(store);
            Journal journal = store.getJournal("root");

            RecordId head = journal.getHead();
            NodeBuilder builder = new SegmentNodeState(store, head).builder();
            NodeBuilder top = builder.child("content").child("node0");
            for (int i = 0; i < 1000; i++) {
                NodeBuilder middle = top.child("node" + i);
                middle.setProperty("jcr:primaryType", "oak:unstructured");
                for (int j = 0; j < 1000; j++) {
                    middle.child("node" + j)
                        .setProperty("jcr:primaryType", "oak:unstructured");
                }
                System.out.println(i);
            }
            SegmentNodeState root = writer.writeNode(builder.getNodeState());
            writer.flush();
            journal.setHead(head, root.getRecordId());
            head = root.getRecordId();
            System.out.println("done");
        } finally {
            mongo.close();
        }
    }

}
