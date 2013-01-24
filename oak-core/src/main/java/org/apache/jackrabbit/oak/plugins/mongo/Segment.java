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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

class Segment {

    /*
     * Maximum size of a segment. Segments are stored as MongoDB BSON
     * objects, so they are subject to the 16 MB size limit imposed by
     * MongoDB. We use roughly 8MB to leave space for extra metadata like the
     * UUID index.
     */
    static final int OFFSET_BITS = 23;
    static final int MAX_OFFSET = 1 << OFFSET_BITS - 1; // 8MB

    static final int UUID_INDEX_BITS = 32 - OFFSET_BITS;
    static final int UUID_INDEX_SIZE = 1 << UUID_INDEX_BITS; // 512

    /*
     * Maximum size of a block of a large record. Blocks should be large
     * enough to keep access overhead low, but small enough to avoid too
     * much copying of data when just small parts of a binary are get
     * modified. We use 64kB to make blocks just larger than inline values.
     */
    static final int BLOCK_BITS = 16;
    static final int BLOCK_SIZE = 1 << BLOCK_BITS; // 16kB

    static final int LEVEL_BITS = 8;
    static final int LEVEL_SIZE = 1 << LEVEL_BITS; // 256 words, 1kB

    private final UUID[] uuids;

    private final ByteBuffer data;

    Segment(UUID[] uuids, ByteBuffer data) {
        this.uuids = uuids;
        this.data = data;
    }

    int size() {
        return data.limit();
    }

    RecordId getRecordId(int offset) {
        int id = data.getInt(offset);
        UUID uuid = uuids[id >>> OFFSET_BITS];
        return new RecordId(uuid, id & ((1 << OFFSET_BITS) - 1));
    }

    String getString(int offset) {
        byte[] buffer = getBytes(offset);
        return new String(buffer, Charsets.UTF_8);
    }

    private long getLength(int offset) {
        byte b = data.get(offset++);
        long length = b & 0x7f;
        while ((b & 0x80) != 0) {
            b = data.get(offset++);
            length = (length << 7) | (b & 0x7f);
        }
        
    }
    byte[] getBytes(int offset) {
        byte[] buffer;
        int length = data.get(offset) & 0xff;
        if (length < 0xff) {
            buffer = new byte[length];
            System.arraycopy(data, offset + 1, buffer, 0, length);
        } else {
            int next = data.get(offset + 1) & 0xff;
            if (next < 0xff) {
                length = next << 8 | (data.get(offset + 2) & 0xff);
                buffer = new byte[length];
                System.arraycopy(data, offset + 1, buffer, 0, length);
            } else {
                if (data.getShort(offset + 2) != 0) {
                    throw new IllegalStateException("too large binary");
                }
                length = data.getInt(offset + 4);
                buffer = new byte[length];
                
            }
        }
        return buffer;
    }

    InputStream getInputStream(int offset) {
        int length = data.get(offset) & 0xff;
        if (length < 0xff) {
            byte[] buffer = new byte[length];
            System.arraycopy(data, offset + 1, buffer, 0, length);
            return new ByteArrayInputStream(buffer);
        } else {
            int next = data.get(offset + 1) & 0xff;
            if (next < 0xff) {
                length = next << 8 | (data.get(offset + 2) & 0xff);
                byte[] buffer = new byte[length];
                System.arraycopy(data, offset + 1, buffer, 0, length);
                return new ByteArrayInputStream(buffer);
            } else {
                if (data.getShort(offset + 2) != 0) {
                    throw new IllegalStateException("too large binary");
                }
                length = data.getInt(offset + 4);
                buffer = new byte[length];
            }
        }
        return buffer;
    }

}
