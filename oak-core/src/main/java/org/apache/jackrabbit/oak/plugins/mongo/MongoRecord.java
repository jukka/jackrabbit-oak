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
import java.util.UUID;

import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

class MongoRecord {

    private static MongoRecord createRecord(DB db, byte[] data) {
        UUID uuid = UUID.randomUUID();

        DBObject object = new BasicDBObject();
        object.put("_id", uuid.toString());
        object.put("data", data);

        DBCollection collection = db.getCollection("segments");
        collection.insert(WriteConcern., null)(object);
    }
    
    private static MongoRecord createRecord(DB db, byte[] buffer, int offset, int length) {
    }

    static MongoRecord createRecord(DB db, InputStream stream) throws IOException {
        byte[] buffer = new byte[MAX_SEGMENT_LENGTH];
        int n = ByteStreams.read(stream, buffer, 0, buffer.length);
        if (n < MAX_SEGMENT_LENGTH) {
            return createRecord(db, buffer, 0, n);
        }
    }

    private final MongoStore store;

    private final UUID uuid;

    private final int base;

    MongoRecord(MongoStore store, UUID uuid, int base) {
        this.store = store;
        this.uuid = uuid;
        this.base = base;
    }

    public int getInt(int offset) {
        return store.getInt(uuid, base + offset);
    }

}
