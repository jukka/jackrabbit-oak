package org.apache.jackrabbit.oak.plugins.acorn;

import java.io.IOException;
import java.io.InputStream;

public class RecordInputStream extends InputStream {

    private final Record record;

    private long position = 0;

    private long mark = -1;

    public RecordInputStream(Record record) {
        this.record = record;
    }

    @Override
    public long skip(long n) {
        long m = Math.min(n, record.getLength() - position);
        position += m;
        return m;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        if (read(b, 0, 1) == 1) {
            return b[0] & 0xff;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        long remaining = record.getLength() - position;
        if (remaining > 0) {
            int n = (int) Math.min(len, remaining);
            record.read(position, b, off, n);
            position += n;
            return n;
        } else {
            return -1;
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int limit) {
        mark = position;
    }

    @Override
    public void reset() throws IOException {
        if (mark != -1) {
            position = mark;
        } else {
            throw new IOException("Unable to reset an unmarked stream");
        }
    }

    @Override
    public void close() {
        position = record.getLength();
        mark = -1;
    }

}
