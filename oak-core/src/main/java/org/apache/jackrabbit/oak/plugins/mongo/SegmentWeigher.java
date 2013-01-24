package org.apache.jackrabbit.oak.plugins.mongo;

import java.util.UUID;

import com.google.common.cache.Weigher;

public class SegmentWeigher implements Weigher<UUID, Segment> {

    @Override
    public int weigh(UUID key, Segment value) {
        return value.size();
    }

}
