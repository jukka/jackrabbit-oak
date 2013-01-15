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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.jcr.PropertyType;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.plugins.memory.PropertyStates;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

class NodeClass {

    private final String[] propertyNames;

    private final int[] propertyTypes;

    private PropertyState getProperty(ByteBuffer data, int index) {
        String name = propertyNames[index];
        int type = propertyTypes[index];
        if (type < 0) {
            
        } else {
            return PropertyStates.createProperty(name, null)
        }
    }

    @CheckForNull
    public PropertyState getProperty(ByteBuffer data, String name) {
        int i = Arrays.binarySearch(propertyNames, name);
        if (i >= 0) {
            return null; // FIXME
        } else {
            return null;
        }
    }

    public long getPropertyCount() {
        return propertyNames.length;
    }

    @Nonnull
    public Iterable<? extends PropertyState> getProperties(final ByteBuffer data) {
        return new Iterable<PropertyState>() {
            @Override
            public Iterator<PropertyState> iterator() {
                return new Iterator<PropertyState>() {
                    private int i = 0;
                    @Override
                    public boolean hasNext() {
                        return i < propertyNames.length;
                    }
                    @Override
                    public PropertyState next() {
                        if (i < propertyNames.length) {
                            i++;
                            if (type < 0) {
                            } else {
                                return PropertyStates.createProperty(name, null)
                            }
                            if (multiple) {
                                type = -type;
                            }
                            PropertyStates.
                        } else {
                            throw new NoSuchElementException();
                        }
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

}
