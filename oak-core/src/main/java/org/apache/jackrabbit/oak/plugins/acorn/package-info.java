/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.jackrabbit.oak.plugins.acorn;

/**
 * This package contains a MicroKernel implementation designed and optimized
 * for the requirements and access patterns of oak-core.
 * 
 * <h2></h2>
 * <p>
 * 
 * 
 * <h2>Records</h2>
 * <h2>Bundles</h2>
 * <p>Bundles are collections of node data, stored as records. Each bundle
 * consists of the following sections:</p>
 * <ol>
 *   <li>Bundle references</li>
 *   <li>Node data</li>
 * </ol>
 *
 * <h3>Bundle references</h3>
 * <p>The nodes in this bundle may reference nodes and other data (strings,
 * multi-valued properties, node classes, etc.) contained in other, earlier
 * bundles. Such data references are expressed as (bundle hash, offset) pairs
 * that identify the bundle containing the referenced data and the relevant
 * offset within that bundle. To avoid repeatedly storing the 32 byte hash
 * for each such reference, the hashes of all referenced bundles are stored
 * as a lookup table at the beginning of a bundle. This way all references
 * can be stored in 8 bytes as 64-bit values with the following structure:
 * </p>
 * <table>
 *   <thead>
 *     <tr><th>Bits</th><th>Description</th></tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>63-38</td><td></td>
 *       <td>32-0</td><td>/td>
 *     </tr>
 *   </tbody>
 * </table>
 */
class foo {}
// package org.apache.jackrabbit.oak.plugins.acorn;
