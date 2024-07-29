package com.poisonednpcs.combat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Supplier;

public class HitTracker {

    private final Deque<Hit> hits = new ArrayDeque<>();
    private final Supplier<Boolean> isActive;

    private Hit pastHit = null;

    public HitTracker(Supplier<Boolean> isActive) {
        this.isActive = isActive;
    }

    public void trackHit(Hit hit) {
        // TODO: this seems like a good opportunity to use an interface to make this a special case thing.
        if (!hit.getWeapon().map(weapon -> weapon.getPoisonType().isPresent()).orElse(false)) {
            // The weapon isn't poisoned, so don't even bother tracking the hit.
            return;
        }
        hits.add(hit);
    }

    public Optional<Hit> getOldestHitAfter(Instant time) {
        while (!hits.isEmpty() && hits.peek().getOccurredAt().isBefore(time)) {
            pastHit = hits.poll();
        }
        return hasHits() ? Optional.of(hits.peek()) : Optional.empty();
    }

    public boolean hasHits() {
        return isActive.get() && !hits.isEmpty();
    }

    public int getNumHits() {
        return hits.size();
    }

    synchronized Optional<Hit> getClosestTrackedHitTo(Instant instant, Duration within) {
        Optional<Hit> past = Optional.ofNullable(pastHit);
        Optional<Hit> next = hits.isEmpty() ? Optional.empty() : Optional.of(hits.peek());

        Optional<Duration> pastDifference = past.map(ph -> Duration.between(instant, ph.getOccurredAt()).abs());
        Optional<Duration> nextDifference = next.map(nh -> Duration.between(instant, nh.getOccurredAt()).abs());

        if (pastDifference.isPresent() && nextDifference.isPresent()) {
            int cmp = pastDifference.get().compareTo(nextDifference.get());
            if (cmp < 0 && pastDifference.get().compareTo(within) < 0) {
                return past;
            } else if (nextDifference.get().compareTo(within) < 0) {
                return next;
            }
        } else if (pastDifference.map(pd -> pd.compareTo(within) < 0).orElse(false)) {
            return past;
        } else if (nextDifference.map(nd -> nd.compareTo(within) < 0).orElse(false)) {
            return next;
        }

        return Optional.empty();
    }
}
