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

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class SegmentHashTest {

    @Test
    public void testToString() {
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", new SegmentHash(0, 0, 0, 0).toString());
        assertEquals("0000000000000001000000000000000200000000000000030000000000000004", new SegmentHash(1, 2, 3, 4).toString());
        assertEquals("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", new SegmentHash(-1, -1, -1, -1).toString());
    }

}
