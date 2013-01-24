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

import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

class MongoWriter extends SegmentWriter {

    private final DBCollection collection;

    MongoWriter(DBCollection collection) {
        this.collection = collection;
    }

    @Override
    protected synchronized void save(UUID uuid, byte[] data, UUID[] uuids) {
        String[] strings = new String[uuids.length];
        for (int i = 0; i < uuids.length; i++) {
            strings[i] = uuids[i].toString();
        }

        BasicDBObject object = new BasicDBObject();
        object.put("_id", uuid.toString());
        object.put("data", data);
        object.put("uuids", strings);
        collection.insert(object);
    }

}
