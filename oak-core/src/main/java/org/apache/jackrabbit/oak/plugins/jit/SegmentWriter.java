/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.plugins.jit;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Charsets;

class SegmentWriter {

    private byte[] buffer;

    private int position = 0;


    public int writeString(String string) {
        byte[] b = string.getBytes(Charsets.UTF_8);
        
    }

    public int writeStream(InputStream input) throws IOException {
        try {
            byte[] bytes = new byte[1 << 16];
            int length = 0;
            while (length < bytes.length) {
                int n = input.read(bytes, length, bytes.length - length);
                if (n == -1) {
                    return writeBytes(bytes, 0, length);
                } else {
                    length += n;
                }
            }
            
        } finally {
            input.close();
        }
    }

    public int writeBytes(byte[] bytes, int offset, int length) {
        
    }

}
