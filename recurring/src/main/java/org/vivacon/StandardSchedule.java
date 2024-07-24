package org.vivacon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class StandardSchedule<T> implements Schedule<T> {

    private final List<ScheduleElement<T>> elements;

    StandardSchedule(List<ScheduleElement<T>> elements) {
        Objects.requireNonNull(elements);
        this.elements = List.copyOf(elements);
    }

    @Override
    public boolean isOccurring(T event, LocalDate date) {
        for (ScheduleElement<T> e : elements) {
            if (e.event().equals(event) && e.isOccurring(date)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<LocalDate> datesInRange(T event, LocalDate start, LocalDate end) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate cursor = start;
        while (cursor.equals(end) || cursor.isBefore(end)) {
            if (isOccurring(event, cursor)) {
                result.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    @Override
    public LocalDate nextOccurrence(T event, LocalDate date) {
        LocalDate cursor = date;
        while (!isOccurring(event, cursor)) {
            cursor = cursor.plusDays(1);
        }
        return cursor;
    }

    @Override
    public LocalDate previousOccurrence(T event, LocalDate date) {
        LocalDate cursor = date;
        while (!isOccurring(event, cursor)) {
            cursor = cursor.minusDays(1);
        }
        return cursor;
    }

    @Override
    public Stream<LocalDate> futureDates(T event, LocalDate start) {
        return Stream.iterate(nextOccurrence(event, start), seed -> nextOccurrence(event, seed.plusDays(1)));
    }

    @Override
    public Stream<LocalDate> pastDates(T event, LocalDate start) {
        return Stream.iterate(previousOccurrence(event, start), seed -> previousOccurrence(event, seed.minusDays(1)));
    }

    @Override
    public String toString() {
        return '[' + this.getClass().getSimpleName() + ": " + elements.stream().map(ScheduleElement::toString).collect(Collectors.joining(", ")) +
                ']';
    }
}