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

class BundleRef {

    private long a, b, c, d;

    BundleRef(long a, long b, long c, long d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    //------------------------------------------------------------< Object >--

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof BundleRef) {
            BundleRef that = (BundleRef) obj;
            return this.a == that.a && this.b == that.b
                    && this.c == that.c && this.d == that.d;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) d;
    }

    @Override
    public String toString() {
        return String.format(
                "%08x.%08x.%08x.%08x.%08x.%08x.%08x.%08x",
                a >>> 32, a & ((1L << 32) - 1), b >>> 32, b & ((1L << 32) - 1),
                c >>> 32, c & ((1L << 32) - 1), d >>> 32, d & ((1L << 32) - 1));
    }

}
