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
package org.apache.jackrabbit.oak.security.authentication.ldap.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalGroup;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentity;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityRef;

/**
 * Implements an identity that is provided by the {@link LdapIdentityProvider}.
 */
public abstract class LdapIdentity implements ExternalIdentity {

    private final LdapIdentityProvider provider;

    private final ExternalIdentityRef ref;

    private final String id;

    private final Map<String, Object> properties = new HashMap<String, Object>();

    protected LdapIdentity(LdapIdentityProvider provider, ExternalIdentityRef ref, String id) {
        this.provider = provider;
        this.ref = ref;
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ExternalIdentityRef getExternalId() {
        return ref;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns the DN as principal name.
     * @return the DN
     */
    @Nonnull
    @Override
    public String getPrincipalName() {
        return ref.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIntermediatePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Iterable<? extends ExternalIdentityRef> getGroups() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LdapIdentity{");
        sb.append("ref=").append(ref);
        sb.append(", id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
