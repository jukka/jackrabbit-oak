package org.apache.jackrabbit.oak.plugins.acorn;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.ByteBuffer;

/**
 * 
 */
class NodeBundle {

    private final ByteBuffer data;

    NodeBundle(byte[] data) {
        this.data = ByteBuffer.wrap(data).asReadOnlyBuffer();
    }

    ByteBuffer getRecord(int identifier) {
        checkArgument(identifier > 0 && identifier < data.limit());
        checkArgument((identifier & 7) == 0);

        long head = data.getLong(identifier);
        int length = (int) (head & (1 << 32 - 1));

        ByteBuffer buffer = data.duplicate();
        buffer.position(identifier + 8);
        buffer.limit(identifier + 8 + length);
        return buffer.slice();
    }

}
