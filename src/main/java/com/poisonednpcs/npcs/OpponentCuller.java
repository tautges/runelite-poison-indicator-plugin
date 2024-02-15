package com.poisonednpcs.npcs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.function.Predicate;

/**
 * Allows many different {@link Predicate}s on an {@link Opponent} to be grouped together as a single predicate, where
 * FULFILLING ANY SINGLE PREDICATE within the grouping will count as passing the aggregated predicate.
 */
public class OpponentCuller implements Predicate<Opponent> {

    private final ImmutableList<Predicate<Opponent>> predicates;

    private OpponentCuller(ImmutableList<Predicate<Opponent>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(Opponent opponent) {
        // ANY predicate passing passes the whole thing
        return Iterables.any(predicates, p -> p.test(opponent));
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final ImmutableList.Builder<Predicate<Opponent>> predicates = ImmutableList.builder();

        private Builder() {}

        public Builder add(Predicate<Opponent> predicate) {
            predicates.add(predicate);
            return this;
        }

        public OpponentCuller build() {
            return new OpponentCuller(predicates.build());
        }
    }
}
