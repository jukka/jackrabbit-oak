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
package org.apache.jackrabbit.oak.plugins.acorn.memory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.jackrabbit.oak.plugins.acorn.Record;

class MemoryRecord implements Record {

    private final byte[] data;

    MemoryRecord(byte[] data) {
        this.data = data;
    }

    @Override
    public long getLength() {
        return data.length;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public ByteBuffer getBuffer(long position, int len) {
        assert 0 <= position && 0 <= len && position + len <= data.length;
        return ByteBuffer.wrap(data, (int) position, len).asReadOnlyBuffer();
    }

    @Override
    public void read(long position, byte[] b, int off, int len) {
        assert 0 <= position && 0 <= len && position + len <= data.length;
        assert 0 <= off && 0 <= len && off + len <= b.length;
        System.arraycopy(data, (int) position, b, off, len);
    }

}
