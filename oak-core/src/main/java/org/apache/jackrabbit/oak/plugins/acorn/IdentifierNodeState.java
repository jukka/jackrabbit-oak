package org.apache.jackrabbit.oak.plugins.acorn;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MemoryNodeBuilder;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

/**
 * 
 */
class IdentifierNodeState implements NodeState {

    public static boolean isInStore(NodeState state, SegmentStore store) {
        return (state instanceof IdentifierNodeState)
            && ((IdentifierNodeState) state).isInStore(store);
    }

    private NodeResolver resolver;

    private final long identifier;

    IdentifierNodeState(NodeResolver resolver, long identifier) {
        this.resolver = resolver;
        this.identifier = identifier;
    }

    NodeResolver getResolver() {
        return resolver;
    }

    void setResolver(NodeResolver resolver) {
        this.resolver = resolver;
    }

    long getIdentifier() {
        return identifier;
    }

    private boolean isInStore(SegmentStore store) {
        return resolver.isInStore(store);
    }

    //---------------------------------------------------------< NodeState >--

    @Override
    public long getPropertyCount() {
        return resolver.getPropertyCount(this);
    }

    @Override
    public PropertyState getProperty(String name) {
        return resolver.getProperty(this, name);
    }

    @Override
    public Iterable<? extends PropertyState> getProperties() {
        return resolver.getProperties(this);
    }

    @Override
    public boolean hasChildNode(String name) {
        return resolver.hasChildNode(this, name);
    }

    @Override
    public NodeState getChildNode(String name) {
        return resolver.getChildNode(this, name);
    }

    @Override
    public long getChildNodeCount() {
        return resolver.getChildNodeCount(this);
    }

    @Override
    public Iterable<String> getChildNodeNames() {
        return resolver.getChildNodeNames(this);
    }

    @Override
    public Iterable<? extends ChildNodeEntry> getChildNodeEntries() {
        return resolver.getChildNodeEntries(this);
    }

    @Override
    public NodeBuilder builder() {
        return new MemoryNodeBuilder(this);
    }

    @Override
    public void compareAgainstBaseState(NodeState base, NodeStateDiff diff) {
        resolver.compareAgainstBaseState(this, base, diff);
    }

    //------------------------------------------------------------< Object >--

    public boolean equals(Object that) {
        return (that instanceof NodeState)
                && resolver.equals(this, (NodeState) that);
    }

    public int hashCode() {
        return resolver.hashCode(this);
    }

    public String toString() {
        return resolver.toString(this);
    }

}
