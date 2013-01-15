package org.apache.jackrabbit.oak.plugins.acorn;

import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface SegmentStore {

    /**
     * Creates and returns a writer for a new segment.
     *
     * @return new segment writer
     */
    @Nonnull
    SegmentWriter newSegmentWriter();

    /**
     * Returns the referenced blob. The client is expected to
     * close the returned stream.
     *
     * @param reference blob reference
     * @return referenced blob
     */
    @Nonnull
    InputStream getBlob(@Nonnull SegmentReference reference);

    /**
     * Returns the referenced string.
     *
     * @param reference string reference
     * @return referenced string
     */
    @Nonnull
    String getString(@Nonnull SegmentReference reference);

    /**
     * Checks if the given string is already stored in one of the existing
     * segments, and returns a reference to the stored string if it is found.
     * This is a best-effort operation designed for optimization, so a
     * {@code null} return value does <em>not</em> guarantee that the given
     * string exists in <em>none</em> of the existing segments.
     *
     * @param string string to be looked up
     * @return string reference, or {@code null}
     */
    @CheckForNull
    SegmentReference findString(@Nonnull String string);

    /**
     * Returns the referenced node class.
     *
     * @param reference class reference
     * @return referenced class
     */
    @Nonnull
    NodeClass getClass(@Nonnull SegmentReference reference);

    /**
     * Checks if the given node class is already stored in one of the existing
     * segments, and returns a reference to the stored class if it is found.
     * This is a best-effort operation designed for optimization, so a
     * {@code null} return value does <em>not</em> guarantee that the given
     * class exists in <em>none</em> of the existing segments.
     *
     * @param klass node class to be looked up
     * @return class reference, or {@code null}
     */
    @CheckForNull
    SegmentReference findClass(@Nonnull NodeClass klass);

}
