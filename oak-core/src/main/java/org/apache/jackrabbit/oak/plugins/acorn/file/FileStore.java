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
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.plugins.acorn.NodeClass;
import org.apache.jackrabbit.oak.plugins.acorn.Record;
import org.apache.jackrabbit.oak.plugins.acorn.SegmentHash;
import org.apache.jackrabbit.oak.plugins.acorn.SegmentHashTest;
import org.apache.jackrabbit.oak.plugins.acorn.SegmentReference;
import org.apache.jackrabbit.oak.plugins.acorn.SegmentStore;
import org.apache.jackrabbit.oak.plugins.acorn.SegmentWriter;

import com.google.common.collect.Maps;

public class FileStore implements SegmentStore {

    private final File directory;

    public FileStore(File directory) {
        this.directory = directory;
    }

    @Override @Nonnull
    public SegmentWriter newSegmentWriter() {
        return null;
    }

    @Override @Nonnull
    public InputStream getBlob(@Nonnull SegmentReference reference) {
        
        return null;
    }

    @Override @Nonnull
    public String getString(@Nonnull SegmentReference reference) {
        return null;
    }

    @Override @CheckForNull
    public SegmentReference findString(@Nonnull String string) {
        return null;
    }

    @Override @Nonnull
    public NodeClass getClass(@Nonnull SegmentReference reference) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override @CheckForNull
    public SegmentReference findClass(@Nonnull NodeClass klass) {
        // TODO Auto-generated method stub
        return null;
    }

}
