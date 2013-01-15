package org.apache.jackrabbit.oak.plugins.acorn;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.jackrabbit.oak.plugins.acorn.SegmentReference.REFERENCE_BITS;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.jackrabbit.oak.plugins.memory.ModifiedNodeState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class SegmentWriter {

    private static final int REFERENCE_BITS = 48;

    private static final int PADDING = (1 << 3) - 1;

    private static final String ALGORITHM = "SHA-256";

    private static final int INITIAL_BUFFER_SIZE = 1 << 16;

    private final SegmentStore store;

    private final MessageDigest digest;

    private final DataOutputStream output;

    private final Map<SegmentHash, Long> segments = Maps.newHashMap();

    private final Map<String, Long> strings = Maps.newHashMap();

    public SegmentWriter(SegmentStore store, OutputStream output) {
        try {
            this.store = checkNotNull(store);
            this.digest = MessageDigest.getInstance(ALGORITHM);
            this.output = new DataOutputStream(
                    new DigestOutputStream(checkNotNull(output), digest));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITHM + " not supported", e);
        }
    }

    private void pad() throws IOException {
        while ((output.size() & SegmentReference.PADDING) != 0) {
            output.write(0);
        }
    }

    private Long addHashReference(SegmentReference ref) {
        Long reference = segments.get(ref.getHash());
        if (reference == null) {
            reference = ((long) segments.size() + 1) << REFERENCE_BITS;
            segments.put(ref.getHash(), reference);
        }
        return reference | ref.getOffset();
    }

    public long writeString(String string) throws IOException {
        // Is the string already stored in this segment?
        Long offset = strings.get(string);
        if (offset == null) {
            // Or perhaps in one of the previous segments?
            SegmentReference reference = store.findString(string);
            if (reference != null) {
                offset = addHashReference(reference);
            } else {
                offset = (long) output.size();
                output.writeUTF(string);
                pad();
                strings.put(string, offset);
            }
        }
        return offset;
    }

    private int writeNodeClass(NodeClass klass) throws IOException {
        
    }

    public long writeNode()
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
