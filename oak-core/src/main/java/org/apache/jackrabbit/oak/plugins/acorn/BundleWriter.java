package org.apache.jackrabbit.oak.plugins.acorn;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import org.apache.jackrabbit.oak.plugins.memory.ModifiedNodeState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import com.google.common.base.Charsets;

public class BundleWriter {

    private static final int PADDING_BITS = 4;

    private final RecordStore store;

    private final ByteBuffer bundle;

    public BundleWriter(RecordStore store) {
        this.store = store;
    }

    private void pad() {
        int position = bundle.position();
        int lsb = position & (1 << PADDING_BITS - 1);
        if (lsb !=  0) {
            bundle.position(position - lsb + (1 << PADDING_BITS));
        }
    }

    public long writeString(String string) {
        long identifier = store.findString(string);
        if (identifier < 0) {
            int position = bundle.position();
            byte[] utf8 = string.getBytes(Charsets.UTF_8);
            bundle.put("UTF8".getBytes(Charsets.UTF_8));
            bundle.putInt(utf8.length);
            bundle.put(utf8);
            pad();
        }
    }

    public IdentifierNodeState addNodeState(NodeState state) {
        if (IdentifierNodeState.isInStore(state, store)) {
            return state; // Already stored, no need to do anything.
        }

        if (state instanceof ModifiedNodeState) {
            addModifiedNodeState(null)ModifiedNodeState state = ModifiedNodeState.collapse((ModifiedNodeState) state);
            ((ModifiedNodeState) state).getBaseState();
        } else {
            
        }
    }

    private void addModifiedNodeState(ModifiedNodeState state) {
        NodeState base = state.getBaseState();
        IdentifierNodeState 
        state.compareAgainstBaseState(base, diff);
    }

    private void addGenericNodeState(NodeState state) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(buffer);
        for (ChildNodeEntry entry : state.getChildNodeEntries()) {
            
        }
    }

}
