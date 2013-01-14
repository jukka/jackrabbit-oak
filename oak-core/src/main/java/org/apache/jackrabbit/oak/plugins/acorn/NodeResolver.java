package org.apache.jackrabbit.oak.plugins.acorn;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

public interface NodeResolver {

    long getPropertyCount(IdentifierNodeState node);

    PropertyState getProperty(IdentifierNodeState node, String name);

    Iterable<? extends PropertyState> getProperties(IdentifierNodeState node);

    boolean hasChildNode(IdentifierNodeState node, String name);

    NodeState getChildNode(IdentifierNodeState node, String name);

    long getChildNodeCount(IdentifierNodeState node);

    Iterable<String> getChildNodeNames(IdentifierNodeState node);

    Iterable<? extends ChildNodeEntry> getChildNodeEntries(
            IdentifierNodeState node);

    void compareAgainstBaseState(
            IdentifierNodeState node, NodeState base, NodeStateDiff diff);

    boolean equals(IdentifierNodeState a, NodeState b);

    int hashCode(IdentifierNodeState node);

    String toString(IdentifierNodeState node);

}
