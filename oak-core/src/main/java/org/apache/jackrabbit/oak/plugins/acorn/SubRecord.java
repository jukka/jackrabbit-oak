/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.jackrabbit.oak.plugins.acorn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.jackrabbit.oak.plugins.acorn.Record;

class SubRecord implements Record {

    private final Record record;

    private final int offset;

    SubRecord(Record record, int offset) {
        this.record = record;
        this.offset = offset;
    }

    @Override
    public long getLength() {
        return record.getBuffer(offset, 8).getLong() & (1 << 32 - 1);
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public ByteBuffer getBuffer(long offset, int size) {
        assert 0 <= offset && 0 <= size && offset + size <= data.length;
        return ByteBuffer.wrap(data, (int) offset, size).asReadOnlyBuffer();
    }

}
