package org.apache.jackrabbit.oak.plugins.acorn;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.jackrabbit.oak.api.Blob;
import org.apache.jackrabbit.oak.plugins.memory.ArrayBasedBlob;

import com.google.common.base.Charsets;

interface RecordStore {

    ByteBuffer getRecord(long identifier);

    String getString(long identifier);

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

    Iterable<Blob> getBlobs(long identifier);

}