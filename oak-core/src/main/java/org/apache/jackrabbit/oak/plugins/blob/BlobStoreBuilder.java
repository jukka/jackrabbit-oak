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
package org.apache.jackrabbit.oak.plugins.blob;

import org.apache.jackrabbit.mk.blobs.BlobStore;

import com.google.common.base.Optional;

/**
 * Interface for building blob stores.
 */
public interface BlobStoreBuilder {

    /**
     * Builds the appropriate BlobStore.
     * 
     * @param config
     *            the config
     * @return the blob store wrapped as {@link Optional} to indicate that the
     *         value might be null
     * @throws Exception
     *             the exception
     */
    public Optional<BlobStore> build(BlobStoreConfiguration config) throws Exception;
}
