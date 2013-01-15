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

class StringRef {

    private final SegmentHash bundleRef;

    private final int offset;

    private int hash;

    private int length;

    private String string = null;

    public synchronized String getString(SegmentStore store) {
        if (string == null) {
            Bundle bundle = bundleRef.getBundle(store);
        }
        return string;
    }

    //------------------------------------------------------------< Object >--

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof StringRef) {
            StringRef that = (StringRef) obj;
            return offset == that.offset && bundleRef.equals(that.bundleRef);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return bundleRef.hashCode() ^ offset;
    }

    @Override
    public String toString() {
        return String.format("%s:S%d", bundleRef, offset);
    }

}
