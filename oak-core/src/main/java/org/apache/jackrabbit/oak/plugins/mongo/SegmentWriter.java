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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

abstract class SegmentWriter {

    private static final RecordId[] NO_RECORD_IDS = new RecordId[0];

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private final UUID[] uuidIndex = new UUID[Segment.UUID_INDEX_SIZE];

    private int uuidCount = 1;

    protected SegmentWriter() {
        uuidIndex[0] = UUID.randomUUID();
    }

    private RecordId prepareRecord() {
        return prepareRecord(NO_RECORD_IDS, 0, 0);
    }

    private RecordId prepareRecord(RecordId[] ids) {
        return prepareRecord(ids, 0, ids.length);
    }

    private synchronized RecordId prepareRecord(
            RecordId[] ids, int off, int length) {
        Set<UUID> oldUUIDs =
                Sets.newHashSet(Arrays.asList(uuidIndex).subList(0, uuidCount));
        Set<UUID> newUUIDs = Sets.newHashSet();
        for (int i = 0; i < ids.length; i++) {
            UUID uuid = ids[i].getUuid();
            if (!oldUUIDs.contains(uuid)) {
                newUUIDs.add(uuid);
            }
        }

        if (buffer.size() > Segment.MAX_OFFSET
                || uuidCount + newUUIDs.size() >= Segment.UUID_INDEX_SIZE) {
            flush();
        }

        return new RecordId(uuidIndex[0], buffer.size());
    }

    protected abstract void save(UUID[] uuids, byte[] data);

    private synchronized void flush() {
        UUID[] uuids = uuidIndex;
        if (uuidCount < uuidIndex.length) {
            uuids = new UUID[uuidCount];
            System.arraycopy(uuidIndex, 0, uuids, 0, uuidCount);
        }

        save(uuids, buffer.toByteArray());

        buffer = new ByteArrayOutputStream();
        uuidIndex[0] = UUID.randomUUID();
        uuidCount = 1;
    }

    private synchronized int mapUUID(UUID uuid) {
        int i = Arrays.asList(uuidIndex).subList(0, uuidCount).indexOf(uuid);
        if (i == -1) {
            i = uuidCount++;
            uuidIndex[i] = uuid;
        }
        return i;
    }

    private synchronized void writeRecordId(RecordId id) {
        int reference = mapUUID(id.getUuid()) << 15 | id.getOffset();
        buffer.write(reference >> 24);
        buffer.write(reference >> 16);
        buffer.write(reference >> 8);
        buffer.write(reference);
    }

    private synchronized RecordId writeRecordIds(RecordId[] ids, int offset, int length) {
        Preconditions.checkNotNull(ids);
        Preconditions.checkArgument(offset >= 0);
        Preconditions.checkArgument(length >= 0);
        Preconditions.checkArgument(offset + length <= ids.length);

        RecordId id = prepareRecord(ids, offset, length);
        for (int i = 0; i < length; i++) {
            writeRecordId(ids[i]);
        }
        return id;
    }

    public RecordId writeStream(InputStream stream) throws IOException {
        byte[] buffer = new byte[Segment.BLOCK_SIZE];
        int n = ByteStreams.read(stream, buffer, 0, buffer.length);
        if (n < buffer.length) {
            return writeBytes(buffer, 0, n);
        } else {
            long length = n;

            List<RecordId> blocks = Lists.newArrayList();
            do {
                blocks.add(writeBytes(buffer));
                n = ByteStreams.read(stream, buffer, 0, buffer.length);
                length += n;
            } while (n == buffer.length);
            if (n > 0) {
                blocks.add(writeBytes(buffer, 0, n));
            }

            RecordId[] ids = blocks.toArray(NO_RECORD_IDS);
            return writeList(0xffffL << 48 | length, ids);
        }
    }

    public RecordId writeString(String data) {
        return writeBytes(data.getBytes(Charsets.UTF_8));
    }

    public RecordId writeBytes(byte[] data) {
        return writeBytes(data, 0, data.length);
    }

    public synchronized RecordId writeBytes(byte[] data, int offset, int length) {
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(offset >= 0);
        Preconditions.checkArgument(length >= 0);
        Preconditions.checkArgument(offset + length <= data.length);

        RecordId id;
        if (length < 0xff) {
            // small record, up to 254 bytes
            id = prepareRecord();
            buffer.write((byte) length);
            buffer.write(data, offset, length);
        } else if (length < 0xff00) {
            // medium record, up to 65279 bytes (~64kB)
            id = prepareRecord();
            buffer.write(0xff);
            buffer.write(length >> 8);
            buffer.write(length);
            buffer.write(data, offset, length);
        } else {
            // large record, up to 256TB, split in a hierarchy of 32kB blocks
            RecordId[] ids = writeAllBlocks(data, offset, length);
            id = writeList(0xffffL << 48 | length, ids);
        }
        return id;
    }

    private synchronized RecordId writeList(long header, RecordId[] ids) {
        while (ids.length > Segment.LEVEL_SIZE) {
            int last = ids.length % Segment.LEVEL_SIZE;
            int full = ids.length / Segment.LEVEL_SIZE;
            int count = full;
            if (last > 0) {
                count++;
            }

            RecordId[] next = new RecordId[count];
            for (int i = 0, p = 0; i < full; i++, p += Segment.LEVEL_SIZE) {
                next[i] = writeRecordIds(ids, p, Segment.LEVEL_SIZE);
            }
            if (last > 0) {
                next[full] = writeRecordIds(ids, ids.length - last, last);
            }
            ids = next;
        }

        RecordId id = prepareRecord(ids);
        buffer.write((int) (header >> 56));
        buffer.write((int) (header >> 48));
        buffer.write((int) (header >> 40));
        buffer.write((int) (header >> 32));
        buffer.write((int) (header >> 24));
        buffer.write((int) (header >> 16));
        buffer.write((int) (header >> 8));
        buffer.write((int) header);
        writeRecordIds(ids, 0, ids.length);

        return id;
    }

    private synchronized RecordId[] writeAllBlocks(
            byte[] data, int offset, int length) {
        int last = length % Segment.BLOCK_SIZE;
        int full = length / Segment.BLOCK_SIZE;
        int count = full;
        if (last > 0) {
            count++;
        }

        RecordId[] blocks = new RecordId[count];
        for (int i = 0, p = offset; i < full; i++, p += Segment.BLOCK_SIZE) {
            blocks[i] = prepareRecord();
            buffer.write(data, p, Segment.BLOCK_SIZE);
        }
        if (last > 0) {
            blocks[full] = prepareRecord();
            buffer.write(data, offset + length - last, last);
        }
        return blocks;
    }

}
