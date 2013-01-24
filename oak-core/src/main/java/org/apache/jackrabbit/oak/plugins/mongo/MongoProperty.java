package org.apache.jackrabbit.oak.plugins.mongo;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.api.Type;

class MongoProperty implements PropertyState {

    private final String name;

    private final int type;

    private final boolean multiple;

    private final MongoRecord record;

    MongoProperty(String name, byte type, MongoRecord record) {
        this.name = name;
        this.type = type &0x0f;
        this.multiple = (type & 0x10) != 0;
        this.record = record;
    }

    @Override @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public boolean isArray() {
        return multiple;
    }

    @Override
    public Type<?> getType() {
        return Type.fromTag(type, multiple);
    }

    @Override @Nonnull
    public <T> T getValue(Type<T> type) {
        return null;
    }

    @Override @Nonnull
    public <T> T getValue(Type<T> type, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long size() {
        if (multiple) {
            throw new IllegalStateException();
        } else {
            return record.size();
        }
    }

    @Override
    public long size(int index) {
        if (multiple) {
            return 0;
        }
    }

    @Override
    public int count() {
        if (multiple) {
            return record.getInt(0);
        } else {
            return 1;
        }
    }

}
