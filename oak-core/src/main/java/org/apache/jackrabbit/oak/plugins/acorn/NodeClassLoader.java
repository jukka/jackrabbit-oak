package org.apache.jackrabbit.oak.plugins.acorn;

import java.nio.ByteBuffer;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

/**
 * 
 */
class NodeClassLoader implements NodeResolver {

    private final MemoryRecordStore store;

    NodeClassLoader(MemoryRecordStore store) {
        this.store = store;
    }

    private NodeClass loadClass(IdentifierNodeState node) {
        ByteBuffer nodeRecord = store.getRecord(node.getIdentifier());

        long classIdentifier = nodeRecord.getLong();
        long childNodeCount = nodeRecord.getInt();
        assert childNodeCount >= 0;

        ByteBuffer classRecord = store.getRecord(classIdentifier);
        String[] propertyNames = new String[classRecord.getInt()];
        for (int i = 0; i < propertyNames.length; i++) {
            propertyNames[i] = store.getString(classRecord.getLong());
            assert i == 0 || propertyNames[i].compareTo(propertyNames[i - 1]) > 0;
        }

        return new NodeClass(store, propertyNames, childNodeCount);
    }

    @Override
    public long getPropertyCount(IdentifierNodeState node) {
        return loadClass(node).getPropertyCount(node);
    }

    @Override
    public PropertyState getProperty(IdentifierNodeState node, String name) {
        return loadClass(node).getProperty(node, name);
    }

    @Override
    public Iterable<? extends PropertyState> getProperties(
            IdentifierNodeState node) {
        return loadClass(node).getProperties(node);
    }

    @Override
    public boolean hasChildNode(IdentifierNodeState node, String name) {
        return loadClass(node).hasChildNode(node, name);
    }

    @Override
    public NodeState getChildNode(IdentifierNodeState node, String name) {
        return loadClass(node).getChildNode(node, name);
    }

    @Override
    public long getChildNodeCount(IdentifierNodeState node) {
        return loadClass(node).getChildNodeCount(node);
    }

    @Override
    public Iterable<String> getChildNodeNames(IdentifierNodeState node) {
        return loadClass(node).getChildNodeNames(node);
    }

    @Override
    public Iterable<? extends ChildNodeEntry> getChildNodeEntries(
            IdentifierNodeState node) {
        return loadClass(node).getChildNodeEntries(node);
    }

    @Override
    public void compareAgainstBaseState(IdentifierNodeState node,
            NodeState base, NodeStateDiff diff) {
        loadClass(node).compareAgainstBaseState(node, base, diff);
    }

    @Override
    public boolean equals(IdentifierNodeState a, NodeState b) {
        return loadClass(a).equals(a, b);
    }

    @Override
    public int hashCode(IdentifierNodeState node) {
        return loadClass(node).hashCode(node);
    }

    @Override
    public String toString(IdentifierNodeState node) {
        return loadClass(node).toString(node);
    }


}
