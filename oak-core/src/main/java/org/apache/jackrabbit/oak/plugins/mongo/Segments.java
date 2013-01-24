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
package org.apache.jackrabbit.oak.plugins.mongo;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;

class Segments {

    private static final int MAX_CACHE_SIZE = 32 * 1024 * 1024;

    private final Cache<UUID, Segment> cache =
            CacheBuilder.newBuilder()
            .weigher(new Weigher<UUID, Segment>() {
                @Override
                public int weigh(UUID key, Segment value) {
                    return value.size();
                }
            })
            .maximumWeight(MAX_CACHE_SIZE)
            .build;

    synchronized Segment getSegment(UUID uuid) {
        try {
            return cache.get(uuid, new Callable<Segment>() {
                @Override
                public Segment call() throws Exception {
                    return null;
                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
