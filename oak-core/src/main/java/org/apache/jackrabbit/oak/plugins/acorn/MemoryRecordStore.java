package org.apache.jackrabbit.oak.plugins.acorn;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jackrabbit.oak.api.Blob;
import org.apache.jackrabbit.oak.plugins.memory.ArrayBasedBlob;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 */
class MemoryRecordStore implements RecordStore {

    // TODO: Permanent storage + cache
    private final List<NodeBundle> bundles = new ArrayList<NodeBundle>();

    private final Cache<Long, String> stringCache =
            CacheBuilder.newBuilder().maximumSize(1000).build();

    public ByteBuffer getRecord(long identifier) {
        int bundleIdentifier = (int) (identifier >>> 32);
        int recordIdentifier = (int) (identifier & (1 << 32 - 1)); 
        NodeBundle bundle = bundles.get(bundleIdentifier);
        return bundle.getRecord(recordIdentifier);
    }

    public long findString(String string) {
        stringCache.
    }

    @Override
    public String getString(long identifier) {
        String string = stringCache.getIfPresent(identifier);
        if (string == null) {
            string = new String(getBytes(identifier), Charsets.UTF_8);
            stringCache.put(identifier, string);
        }
        return string;
    }

    public Blob getBlob(long identifier) {
        byte type = identifier & 0x0f;
        ByteBuffer record = getRecord(identifier);
        if (type == )
    }


    public byte[] getBytes(long identifier) {
        ByteBuffer record = getRecord(identifier);
        byte[] buffer = new byte[record.limit()];
        record.get(buffer);
        return buffer;
    }

    public Iterable<String> getStrings(long identifier) {
        ByteBuffer buffer = getRecord(identifier);
        String[] array = new String[buffer.limit() / 8];
        for (int i = 0; i < array.length; i++) {
            array[i] = getString(buffer.getLong());
        }
        return Arrays.asList(array);
    }

    public Iterable<Long> getLongs(long identifier) {
        ByteBuffer buffer = getRecord(identifier);
        Long[] array = new Long[buffer.limit() / 8];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.getLong();
        }
        return Arrays.asList(array);
    }

    public Iterable<Double> getDoubles(long identifier) {
        ByteBuffer buffer = getRecord(identifier);
        Double[] array = new Double[buffer.limit() / 8];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.getDouble();
        }
        return Arrays.asList(array);
    }

    public Iterable<Boolean> getBooleans(long identifier) {
        ByteBuffer buffer = getRecord(identifier);
        Boolean[] array = new Boolean[buffer.limit()];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.get() != 0;
        }
        return Arrays.asList(array);
    }

    public Iterable<BigDecimal> getBigDecimals(long identifier) {
        ByteBuffer buffer = getRecord(identifier);
        BigDecimal[] array = new BigDecimal[buffer.limit() / 8];
        for (int i = 0; i < array.length; i++) {
            array[i] = new BigDecimal(getString(buffer.getLong()));
        }
        return Arrays.asList(array);
    }

    public Iterable<Blob> getBlobs(long identifier) {
        ByteBuffer buffer = getRecord(identifier);
        Blob[] array = new Blob[buffer.limit() / 8];
        for (int i = 0; i < array.length; i++) {
            array[i] = new ArrayBasedBlob(getBytes(buffer.getLong()));
        }
        return Arrays.asList(array);
    }

}
