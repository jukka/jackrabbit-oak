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
package org.apache.jackrabbit.oak.plugins.blob.cloud;

import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;

import java.io.IOException;
import java.util.Map;

import org.apache.jackrabbit.mk.blobs.AbstractBlobStore;
import org.apache.jackrabbit.mk.util.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.io.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

/**
 * Implementation of the {@link BlobStore} to store blobs in a cloud blob store.
 * <p>
 * Extends {@link AbstractBlobStore} and breaks the the binary to chunks for
 * easier management.
 */
public class CloudBlobStore extends AbstractBlobStore {
    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CloudBlobStore.class);

    /** Cloud Store context */
    private BlobStoreContext context;

    /** The bucket. */
    private String cloudContainer;

    private String accessKey;

    private String secretKey;

    private String cloudProvider;

    protected String getCloudContainer() {
        return cloudContainer;
    }

    public void setCloudContainer(String cloudContainer) {
        this.cloudContainer = cloudContainer;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    /**
     * Instantiates a connection to the cloud blob store.
     * 
     * @param cloudProvider
     *            the cloud provider
     * @param accessKey
     *            the access key
     * @param secretKey
     *            the secret key
     * @param cloudContainer
     *            the bucket
     * @throws Exception
     */
    public void init() throws Exception {
        try {
            this.context =
                    ContextBuilder.newBuilder(cloudProvider)
                            .credentials(accessKey, secretKey)
                            .buildView(BlobStoreContext.class);
            context.getBlobStore().createContainerInLocation(null, cloudContainer);

            LOG.info("Using bucket: " + cloudContainer);
        } catch (Exception e) {
            LOG.error("Error creating S3BlobStore : ", e);
            throw e;
        }
    }

    /**
     * Uploads the block to the cloud service.
     */
    @Override
    protected void storeBlock(byte[] digest, int level, byte[] data) throws IOException {
        Preconditions.checkNotNull(context);

        String id = StringUtils.convertBytesToHex(digest);

        org.jclouds.blobstore.BlobStore blobStore = context.getBlobStore();

        if (!blobStore.blobExists(cloudContainer, id)) {
            Map<String, String> metadata = Maps.newHashMap();
            metadata.put("level", String.valueOf(level));

            Blob blob = blobStore.blobBuilder(id)
                    .payload(data)
                    .userMetadata(metadata)
                    .build();
            String etag = blobStore.putBlob(cloudContainer, blob, multipart());
            LOG.debug("Blob " + id + " created with cloud tag : " + etag);
        } else {
            LOG.debug("Blob " + id + " already exists");
        }
    }

    /**
     * Reads the data from the actual cloud service.
     */
    @Override
    protected byte[] readBlockFromBackend(BlockId blockId) throws Exception {
        Preconditions.checkNotNull(context);

        String id = StringUtils.convertBytesToHex(blockId.getDigest());

        Blob cloudBlob = context.getBlobStore().getBlob(cloudContainer, id);
        if (cloudBlob == null) {
            String message = "Did not find block " + id;
            LOG.error(message);
            throw new IOException(message);
        }

        Payload payload = cloudBlob.getPayload();
        try {
            byte[] data = ByteStreams.toByteArray(payload.getInput());

            if (blockId.getPos() == 0) {
                return data;
            }

            int len = (int) (data.length - blockId.getPos());
            if (len < 0) {
                return new byte[0];
            }
            byte[] d2 = new byte[len];
            System.arraycopy(data, (int) blockId.getPos(), d2, 0, len);
            return d2;
        } finally {
            payload.close();
        }
    }

    /**
     * Delete the cloud container and all its contents.
     * 
     */
    public void deleteBucket() {
        Preconditions.checkNotNull(context);

        if (context.getBlobStore().containerExists(cloudContainer)) {
            context.getBlobStore().deleteContainer(cloudContainer);
        }
        context.close();
    }

    @Override
    public void startMark() throws IOException {
        // No-op
    }

    @Override
    protected void mark(BlockId id) throws Exception {
        // No-op
    }

    @Override
    public int sweep() throws IOException {
        return 0;
    }

    @Override
    protected boolean isMarkEnabled() {
        return false;
    }
}
