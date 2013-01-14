package org.apache.jackrabbit.oak.plugins.acorn;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.jcr.PropertyType;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.plugins.memory.BinaryPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.BooleanPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.DecimalPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.DoublePropertyState;
import org.apache.jackrabbit.oak.plugins.memory.GenericPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.LongPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiBinaryPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiBooleanPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiDecimalPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiDoublePropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiGenericPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiLongPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.MultiStringPropertyState;
import org.apache.jackrabbit.oak.plugins.memory.StringPropertyState;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.spi.state.NodeStateDiff;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

/**
 * 
 */
class NodeClass implements NodeResolver {

    static NodeClass loadClass(MemoryRecordStore store, IdentifierNodeState node) {
        ByteBuffer nodeRecord = store.getRecord(node.getIdentifier());

        long classIdentifier = nodeRecord.getLong();
        long childNodeCount = nodeRecord.getLong();
        assert childNodeCount >= 0;

        ByteBuffer classRecord = store.getRecord(classIdentifier);
        String[] propertyNames = new String[classRecord.getInt()];
        for (int i = 0; i < propertyNames.length; i++) {
            propertyNames[i] = store.getString(classRecord.getLong());
            assert i == 0 || propertyNames[i].compareTo(propertyNames[i - 1]) > 0;
        }

        return new NodeClass(store, propertyNames, childNodeCount);
    }

    private final MemoryRecordStore store;

    private final String[] propertyNames;

    private final long childNodeCount;

    private final int hashCode;

    NodeClass(MemoryRecordStore store, String[] propertyNames, long childNodeCount) {
        this.store = store;
        this.propertyNames = propertyNames;
        this.childNodeCount = childNodeCount;

        Hasher hasher = Hashing.goodFastHash(32).newHasher();
        hasher.putLong(childNodeCount);
        for (String name : propertyNames) {
            hasher.putString(name);
        }
        this.hashCode = hasher.hash().asInt();
    }

    @Override
    public long getPropertyCount(IdentifierNodeState node) {
        return propertyNames.length;
    }

    @Override
    public PropertyState getProperty(IdentifierNodeState node, String name) {
        int index = Arrays.binarySearch(propertyNames, name);
        if (index >= 0) {
            return getProperty(node.getIdentifier(), index);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<PropertyState> getProperties(final IdentifierNodeState node) {
        return new Iterable<PropertyState>() {
            @Override
            public Iterator<PropertyState> iterator() {
                return new Iterator<PropertyState>() {
                    private int index = 0;
                    @Override
                    public boolean hasNext() {
                        return index < propertyNames.length;
                    }
                    @Override
                    public PropertyState next() {
                        if (index < propertyNames.length) {
                            return getProperty(node.getIdentifier(), index++);
                        } else  {
                            throw new NoSuchElementException();
                        }
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public boolean hasChildNode(IdentifierNodeState node, String name) {
        return getChildNode(node, name) != null;
    }

    @Override
    public NodeState getChildNode(IdentifierNodeState node, String name) {
        if (childNodeCount > 0) {
            // FIXME
            for (ChildNodeEntry entry : getChildNodeEntries(node)) {
                if (name.equals(entry.getName())) {
                    return entry.getNodeState();
                }
            }
        }
        return null;
    }

    @Override
    public long getChildNodeCount(IdentifierNodeState node) {
        return childNodeCount;
    }

    @Override
    public Iterable<String> getChildNodeNames(IdentifierNodeState node) {
        if (childNodeCount == 0) {
            return Collections.emptyList();
        }

        final ByteBuffer record = store.getRecord(node.getIdentifier());
        record.position(8 + propertyNames.length * 12);
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    @Override
                    public boolean hasNext() {
                        return record.position() < record.limit();
                    }
                    @Override
                    public String next() {
                        if (hasNext()) {
                            long name = record.getLong();
                            record.getLong(); // skip identifier
                            return store.getString(name);
                        } else {
                            throw new NoSuchElementException();
                        }
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Iterable<ChildNodeEntry> getChildNodeEntries(IdentifierNodeState node) {
        if (childNodeCount == 0) {
            return Collections.emptyList();
        }

        final ByteBuffer record = store.getRecord(node.getIdentifier());
        record.position(8 + propertyNames.length * 12);
        return new Iterable<ChildNodeEntry>() {
            @Override
            public Iterator<ChildNodeEntry> iterator() {
                return new Iterator<ChildNodeEntry>() {
                    @Override
                    public boolean hasNext() {
                        return record.position() < record.limit();
                    }
                    @Override
                    public ChildNodeEntry next() {
                        if (hasNext()) {
                            final long name = record.getLong();
                            final long identifier = record.getLong();
                            return new ChildNodeEntry() {
                                @Override
                                public String getName() {
                                    return store.getString(name);
                                }
                                @Override
                                public NodeState getNodeState() {
                                    return new IdentifierNodeState(
                                            new NodeClassLoader(store), identifier);
                                }
                            };
                        } else {
                            throw new NoSuchElementException();
                        }
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public void compareAgainstBaseState(
            IdentifierNodeState node, NodeState base, NodeStateDiff diff) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean equals(IdentifierNodeState a, NodeState b) {
        if (a == b) {
            return true;
        } else if (b instanceof IdentifierNodeState) {
            // FIXME
        } else {
            return b.equals(a);
        }
    }

    @Override
    public int hashCode(IdentifierNodeState node) {
        return hashCode();
    }

    @Override
    public String toString(IdentifierNodeState node) {
        return toString();
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        return Arrays.toString(propertyNames);
    }

    private PropertyState getProperty(long identifier, int index) {
        ByteBuffer record = store.getRecord(identifier);
        record.position(8 + index * 12); // childNodeCount + n*(type + value)

        String name = propertyNames[index];
        int type = record.getInt();
        long value = record.getLong();
        switch (type) {
        case PropertyType.STRING:
            return StringPropertyState.stringProperty(name, store.getString(value));
        case -PropertyType.STRING:
            return MultiStringPropertyState.stringProperty(name, store.getStrings(value));
        case PropertyType.BINARY:
            return BinaryPropertyState.binaryProperty(name, store.getBytes(value));
        case -PropertyType.BINARY:
            return MultiBinaryPropertyState.binaryPropertyFromBlob(name, store.getBlobs(value));
        case PropertyType.LONG:
            return LongPropertyState.createLongProperty(name, value);
        case -PropertyType.LONG:
            return MultiLongPropertyState.createLongProperty(name, store.getLongs(value));
        case PropertyType.DOUBLE:
            return DoublePropertyState.doubleProperty(name, (double) value);
        case -PropertyType.DOUBLE:
            return MultiDoublePropertyState.doubleProperty(name, store.getDoubles(value));
        case PropertyType.DATE:
            return LongPropertyState.createDateProperty(name, value);
        case -PropertyType.DATE:
            return MultiLongPropertyState.createDateProperty(name, store.getStrings(value));
        case PropertyType.BOOLEAN:
            return BooleanPropertyState.booleanProperty(name, value != 0);
        case -PropertyType.BOOLEAN:
            return MultiBooleanPropertyState.booleanProperty(name, store.getBooleans(value));
        case PropertyType.NAME:
            return GenericPropertyState.nameProperty(name, store.getString(value));
        case -PropertyType.NAME:
            return MultiGenericPropertyState.nameProperty(name, store.getStrings(value));
        case PropertyType.PATH:
            return GenericPropertyState.pathProperty(name, store.getString(value));
        case -PropertyType.PATH:
            return MultiGenericPropertyState.pathProperty(name, store.getStrings(value));
        case PropertyType.REFERENCE:
            return GenericPropertyState.referenceProperty(name, store.getString(value));
        case -PropertyType.REFERENCE:
            return MultiGenericPropertyState.referenceProperty(name, store.getStrings(value));
        case PropertyType.WEAKREFERENCE:
            return GenericPropertyState.weakreferenceProperty(name, store.getString(value));
        case -PropertyType.WEAKREFERENCE:
            return MultiGenericPropertyState.weakreferenceProperty(name, store.getStrings(value));
        case PropertyType.URI:
            return GenericPropertyState.uriProperty(name, store.getString(value));
        case -PropertyType.URI:
            return MultiGenericPropertyState.uriProperty(name, store.getStrings(value));
        case PropertyType.DECIMAL:
            return DecimalPropertyState.decimalProperty(name, new BigDecimal(store.getString(identifier)));
        case -PropertyType.DECIMAL:
            return MultiDecimalPropertyState.decimalProperty(name, store.getBigDecimals(value));
        default:
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

}
