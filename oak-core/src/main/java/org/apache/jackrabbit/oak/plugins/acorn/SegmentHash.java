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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SHA-256 hash of a content segment.
 */
public class SegmentHash {

    private static final String ALGORITHM = "SHA-256";

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private final byte[] hash;

    public SegmentHash(byte[] data, int offset, int length) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(data, offset, length);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITHM + " not supported", e);
        }
    }

    SegmentHash(long a, long b, long c, long d) {
        this.hash = new byte[32];
        ByteBuffer buffer = ByteBuffer.wrap(hash);
        buffer.putLong(a);
        buffer.putLong(b);
        buffer.putLong(c);
        buffer.putLong(d);
    }

    
    //------------------------------------------------------------< Object >--

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SegmentHash) {
            SegmentHash that = (SegmentHash) obj;
            return Arrays.equals(this.hash, that.hash);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ByteBuffer.wrap(hash).getInt();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            builder.append(HEX_DIGITS[(hash[i] >> 4) & 0x0f]);
            builder.append(HEX_DIGITS[hash[i] & 0x0f]);
        }
        return builder.toString();
    }

}
