package org.apache.jackrabbit.oak.plugins.acorn;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.jackrabbit.oak.api.Blob;
import org.apache.jackrabbit.oak.plugins.memory.ArrayBasedBlob;

import com.google.common.base.Charsets;

enum RecordType {

    SMALL_LIST, // list
    LARGE_LIST, // length + list

    // node: normal + modified, small + large
    // non-node:
    //       list, normal + modified
    //       string

}