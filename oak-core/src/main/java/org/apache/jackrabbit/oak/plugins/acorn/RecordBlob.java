package org.apache.jackrabbit.oak.plugins.acorn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.jackrabbit.oak.api.Blob;

public class RecordBlob implements Blob {

    private static final long MAX_BLOCK_LENGTH = 1024 * 1024;

    private final RecordStore store;

    private final long identifier;

    @Override
    public long length() {
        return store.getRecord(identifier).getLong();
    }

    @Override
    public byte[] sha256() {
        byte[] sha256 = new byte[256 / 8];
        ByteBuffer record = store.getRecord(identifier);
        record.position(8); // skip length
        record.get(sha256);
        return sha256;
    }

    @Override
    public InputStream getNewStream() {
        ByteBuffer record = store.getRecord(identifier);
        
        record.position(8 + 256 / 8); // skip length + sha256
        return new InputStream() {
            @Override
            public int read() throws IOException {
                byte[] 
                return 0;
            }
        };
        
        return null;
    }

    //----------------------------------------------------------< Comparable >

    @Override
    public int compareTo(Blob that) {
        
        return 0;
    }

    //-------------------------------------------------------====---< Object >

    
}
