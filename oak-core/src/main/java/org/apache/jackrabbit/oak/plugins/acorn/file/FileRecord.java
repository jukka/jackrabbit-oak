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
package org.apache.jackrabbit.oak.plugins.acorn.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.apache.jackrabbit.oak.plugins.acorn.Record;

class FileRecord implements Record {

    private final File file;

    FileRecord(File file) {
        this.file = file;
    }

    @Override
    public long getLength() {
        return file.length();
    }

    @Override
    public InputStream getStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Unable to find file record: " + file.getPath(), e);
        }
    }

    @Override
    public ByteBuffer getBuffer(long position, int len) {
        assert 0 <= position && 0 <= len && position + len <= file.length();
        byte[] buffer = new byte[len];
        read(position, buffer, 0, len);
        return ByteBuffer.wrap(buffer).asReadOnlyBuffer();
    }

    @Override
    public void read(long position, byte[] b, int off, int len) {
        assert 0 <= position && 0 <= len && position + len <= file.length();
        assert 0 <= off && 0 <= len && off + len <= b.length;
        try {
            RandomAccessFile random = new RandomAccessFile(file, "r");
            try {
                random.seek(position);
                random.read(b, off, len);
            } finally {
                random.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to read file record: " + file.getPath(), e);
        }
    }

}
