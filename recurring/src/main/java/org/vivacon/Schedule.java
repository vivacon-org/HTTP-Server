package org.vivacon;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

interface Schedule<T> {

    boolean isOccurring(T event, LocalDate date);

    List<LocalDate> datesInRange(T event, LocalDate start, LocalDate end);

    Stream<LocalDate> futureDates(T event, LocalDate start);

    Stream<LocalDate> pastDates(T event, LocalDate start);

    LocalDate nextOccurrence(T event, LocalDate date);

    LocalDate previousOccurrence(T event, LocalDate date);

    @SafeVarargs
    static <T> Schedule<T> of(ScheduleElement<T>... elements) {
        for (ScheduleElement<T> e : elements) {
            Objects.requireNonNull(e);
        }
        return new StandardSchedule<T>(Arrays.asList(elements));
    }

    static <T> Schedule<T> of(List<ScheduleElement<T>> elements) {
        Objects.requireNonNull(elements);
        return new StandardSchedule<T>(elements);
    }
}
