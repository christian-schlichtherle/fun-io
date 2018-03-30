/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util.builder;

import java.util.*;
import static java.util.Collections.*;
import javax.annotation.Nullable;

/**
 * A builder for immutable lists.
 *
 * @param <I> The type of the list items.
 * @param <P> The type of the parent builder, if defined.
 */
public class ImmutableListBuilder<I, P> extends AbstractBuilder<P> {

    public static <I> ImmutableListBuilder<I, Void> create() {
        return new ImmutableListBuilder<I, Void>();
    }

    private final List<I> items = new LinkedList<I>();

    public final ImmutableListBuilder<I, P> clear() {
        items.clear();
        return this;
    }

    public final ImmutableListBuilder<I, P> add(final @Nullable I item) {
        items.add(item);
        return this;
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    public final ImmutableListBuilder<I, P> add(final I... items) {
        for (I item : items) this.items.add(item);
        return this;
    }

    public final ImmutableListBuilder<I, P> set(@Nullable I item) {
        return clear().add(item);
    }

    public final ImmutableListBuilder<I, P> set(I... items) {
        return clear().add(items);
    }

    public final ImmutableListBuilder<I, P> addAll(
            final Collection<? extends I> collection) {
        items.addAll(collection);
        return this;
    }

    public final ImmutableListBuilder<I, P> setAll(
            Collection<? extends I> collection) {
        return clear().addAll(collection);
    }

    /** Builds an immutable list with the added items. */
    @SuppressWarnings("unchecked")
    @Override public final List<I> build() {
        final int size = items.size();
        return 0 == size
                ? EMPTY_LIST
                : 1 == size
                    ? singletonList(items.get(0))
                    : unmodifiableList(new ArrayList<I>(items));
    }
}
