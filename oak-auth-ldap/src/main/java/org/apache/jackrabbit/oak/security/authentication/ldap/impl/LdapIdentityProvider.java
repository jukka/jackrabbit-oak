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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.PoolableLdapConnectionFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalGroup;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentity;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityException;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityRef;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code LdapIdentityProvider} implements an external identity provider that reads users and groups from an ldap
 * source.
 *
 * Please refer to {@link LdapProviderConfig} for configuration options.
 */
@Component(
        // note that the metatype information is generated from LdapProviderConfig
        policy = ConfigurationPolicy.REQUIRE
)
@Service
public class LdapIdentityProvider implements ExternalIdentityProvider {

    /**
     * default logger
     */
    private static final Logger log = LoggerFactory.getLogger(LdapIdentityProvider.class);

    /**
     * internal configuration
     */
    private LdapProviderConfig config;

    /**
     * the connection pool with connections authenticated with the bind DN
     */
    private LdapConnectionPool adminPool;

    /**
     * the connection pool with unbound connections
     */
    private UnboundLdapConnectionPool userPool;

    /**
     * Default constructor for OSGi
     */
    public LdapIdentityProvider() {
    }

    /**
     * Constructor for non-OSGi cases.
     * @param config the configuration
     */
    public LdapIdentityProvider(LdapProviderConfig config) {
        this.config = config;
        init();
    }

    @Activate
    private void activate(Map<String, Object> properties) {
        ConfigurationParameters cfg = ConfigurationParameters.of(properties);
        config = LdapProviderConfig.of(cfg);
        init();
    }

    @Deactivate
    private void deactivate() {
        close();
    }

    /**
     * Initializes the ldap identity provider.
     */
    private void init() {
        if (adminPool != null) {
            throw new IllegalStateException("Provider already initialized.");
        }

        // setup admin connection pool
        LdapConnectionConfig cc = new LdapConnectionConfig();
        cc.setLdapHost(config.getHostname());
        cc.setLdapPort(config.getPort());
        if (!config.getBindDN().isEmpty()) {
            cc.setName(config.getBindDN());
            cc.setCredentials(config.getBindPassword());
        }
        cc.setUseSsl(config.useSSL());
        PoolableLdapConnectionFactory factory = new PoolableLdapConnectionFactory(cc);
        adminPool = new LdapConnectionPool(factory);
        adminPool.setTestOnBorrow(true);
        adminPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);

        // setup unbound connection pool. let's create a new version of the config
        cc = new LdapConnectionConfig();
        cc.setLdapHost(config.getHostname());
        cc.setLdapPort(config.getPort());
        cc.setUseSsl(config.useSSL());
        userPool = new UnboundLdapConnectionPool(new PoolableUnboundConnectionFactory(cc));
        userPool.setTestOnBorrow(true);
        userPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
    }

    /**
     * Closes this provider and releases the internal pool. This should be called by Non-OSGi users of this provider.
     */
    public void close() {
        if (adminPool != null) {
            try {
                adminPool.close();
            } catch (Exception e) {
                log.warn("Error while closing LDAP connection pool", e);
            }
            adminPool = null;
        }
        if (userPool != null) {
            try {
                userPool.close();
            } catch (Exception e) {
                log.warn("Error while closing LDAP connection pool", e);
            }
            userPool = null;
        }
    }



    @Nonnull
    @Override
    public String getName() {
        return config.getName();
    }

    @Override
    public ExternalIdentity getIdentity(@Nonnull ExternalIdentityRef ref) throws ExternalIdentityException {
        if (!isMyRef(ref)) {
            return null;
        }

        LdapConnection connection = connect();
        try {
            Entry entry = connection.lookup(ref.getId(), "*");
            if (entry.hasObjectClass(config.getUserConfig().getObjectClasses())) {
                return createUser(entry, null);
            } else if (entry.hasObjectClass(config.getGroupConfig().getObjectClasses())) {
                return createGroup(entry, null);
            } else {
                log.warn("referenced identity is neither user or group: {}", ref.getString());
                return null;
            }
        } catch (LdapException e) {
            log.error("Error during ldap lookup", e);
            throw new ExternalIdentityException("Error during ldap lookup.", e);
        } finally {
            disconnect(connection);
        }
    }

    @Override
    public ExternalUser getUser(@Nonnull String userId) throws ExternalIdentityException {
        long t0 = System.nanoTime();
        LdapConnection connection = connect();
        long t1 = System.nanoTime();
        try {
            Entry entry = getEntry(connection, config.getUserConfig(), userId);
            long t2 = System.nanoTime();
            if (log.isDebugEnabled()) {
                log.debug("getUser({}) connect: {}us, lookup: {}us", new Object[]{userId, (t1-t0)/1000, (t2-t1)/1000});
            }
            if (entry != null) {
                return createUser(entry, userId);
            } else {
                return null;
            }
        } catch (LdapException e) {
            log.error("Error during ldap lookup", e);
            throw new ExternalIdentityException("Error during ldap lookup.", e);
        } catch (CursorException e) {
            log.error("Error during ldap lookup", e);
            throw new ExternalIdentityException("Error during ldap lookup.", e);
        } finally {
            disconnect(connection);
        }
    }

    @Override
    public ExternalGroup getGroup(@Nonnull String name) throws ExternalIdentityException {
        LdapConnection connection = connect();
        try {
            Entry entry = getEntry(connection, config.getGroupConfig(), name);
            if (entry != null) {
                return createGroup(entry, name);
            } else {
                return null;
            }
        } catch (LdapException e) {
            log.error("Error during ldap lookup", e);
            throw new ExternalIdentityException("Error during ldap lookup.", e);
        } catch (CursorException e) {
            log.error("Error during ldap lookup", e);
            throw new ExternalIdentityException("Error during ldap lookup.", e);
        } finally {
            disconnect(connection);
        }
    }

    private Entry getEntry(LdapConnection connection, LdapProviderConfig.Identity idConfig, String id)
            throws CursorException, LdapException {
        String searchFilter = idConfig.getSearchFilter(id);

        // Create the SearchRequest object
        SearchRequest req = new SearchRequestImpl();
        req.setScope(SearchScope.SUBTREE);
        req.addAttributes(SchemaConstants.ALL_USER_ATTRIBUTES);
        req.setTimeLimit(config.getSearchTimeout());
        req.setBase(new Dn(idConfig.getBaseDN()));
        req.setFilter(searchFilter);

        // Process the request
        SearchCursor searchCursor = connection.search(req);
        while (searchCursor.next()) {
            Response response = searchCursor.get();

            // process the SearchResultEntry
            if (response instanceof SearchResultEntry) {
                Entry resultEntry = ((SearchResultEntry) response).getEntry();
                if (searchCursor.next()) {
                    log.warn("search for {} returned more than one entry. discarding additional ones.", searchFilter);
                }
                if (log.isDebugEnabled()) {
                    log.debug("search below {} with {} found {}",
                            new Object[]{idConfig.getBaseDN(), searchFilter, resultEntry.getDn()});
                }
                return resultEntry;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("search below {} with {} found 0 entries.", idConfig.getBaseDN(), searchFilter);
        }
        return null;
    }

    private ExternalUser createUser(Entry e, String id)
            throws LdapInvalidAttributeValueException {
        ExternalIdentityRef ref = new ExternalIdentityRef(e.getDn().getName(), this.getName());
        if (id == null) {
            id = e.get(config.getUserConfig().getIdAttribute()).getString();
        }
        LdapUser user = new LdapUser(this, ref, id, null);
        Map<String, Object> props = user.getProperties();
        for (Attribute attr: e.getAttributes()) {
            if (attr.isHumanReadable()) {
                props.put(attr.getId(), attr.getString());
            }
        }
        return user;
    }

    private ExternalGroup createGroup(Entry e, String name)
            throws LdapInvalidAttributeValueException {
        ExternalIdentityRef ref = new ExternalIdentityRef(e.getDn().getName(), this.getName());
        if (name == null) {
            name = e.get(config.getGroupConfig().getIdAttribute()).getString();
        }
        LdapGroup group = new LdapGroup(this, ref, name);
        Map<String, Object> props = group.getProperties();
        for (Attribute attr: e.getAttributes()) {
            if (attr.isHumanReadable()) {
                props.put(attr.getId(), attr.getString());
            }
        }
        return group;

    }

    private LdapConnection connect() throws ExternalIdentityException {
        try {
            return adminPool.getConnection();
        } catch (Throwable e) {
            log.error("Error while connecting to the ldap server.", e);
            throw new ExternalIdentityException("Error while connecting and binding to the ldap server", e);
        }
    }

    private void disconnect(LdapConnection connection) throws ExternalIdentityException {
        try {
            adminPool.releaseConnection(connection);
        } catch (Exception e) {
            log.warn("Error while disconnecting from the ldap server.", e);
        }
    }

    @Override
    public ExternalUser authenticate(@Nonnull Credentials credentials) throws ExternalIdentityException, LoginException {
        if (!(credentials instanceof SimpleCredentials)) {
            log.debug("LDAP IDP can only authenticate SimpleCredentials.");
            return null;
        }
        final SimpleCredentials creds = (SimpleCredentials) credentials;
        final ExternalUser user = getUser(creds.getUserID());
        if (user != null) {
            // authenticate
            LdapConnection connection = null;
            try {
                long t0 = System.nanoTime();
                connection = userPool.getConnection();
                long t1 = System.nanoTime();
                connection.bind(user.getExternalId().getId(), new String(creds.getPassword()));
                long t2 = System.nanoTime();
                if (log.isDebugEnabled()) {
                    log.debug("authenticate({}) connect: {}us, bind: {}us", new Object[]{user.getId(), (t1-t0)/1000, (t2-t1)/1000});
                }
            } catch (LdapAuthenticationException e) {
                throw new LoginException("Unable to authenticate against LDAP server: " + e.getMessage());
            } catch (Exception e) {
                throw new ExternalIdentityException("Error while binding user credentials", e);
            } finally {
                if (connection != null) {
                    try {
                        userPool.releaseConnection(connection);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        return user;
    }

    private boolean isMyRef(ExternalIdentityRef ref) {
        final String refProviderName = ref.getProviderName();
        return refProviderName == null || refProviderName.length() == 0 || getName().equals(refProviderName);
    }

}