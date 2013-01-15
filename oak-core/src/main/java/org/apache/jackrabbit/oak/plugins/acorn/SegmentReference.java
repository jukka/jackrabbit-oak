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
package org.apache.jackrabbit.oak.plugins.acorn;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

/**
 * Reference to a specific location within a content segment.
 */
public class SegmentReference {

    private final SegmentHash hash;

    private final long offset;

    public SegmentReference(@Nonnull SegmentHash hash, long offset) {
        checkArgument(offset > 0);
        this.hash = checkNotNull(hash);
        this.offset = offset;
    }

    @Nonnull
    public SegmentHash getHash() {
        return hash;
    }

    public long getOffset() {
        return offset;
    }

    //------------------------------------------------------------< Object >--

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SegmentReference) {
            SegmentReference that = (SegmentReference) obj;
            return hash.equals(that.hash) && offset == that.offset;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hash.hashCode() ^ (int) offset;
    }

    @Override
    public String toString() {
        return hash + ":" + offset;
    }

}
