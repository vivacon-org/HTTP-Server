package org.vivacon;

import java.time.LocalDate;

public class ScheduleElement<T> {

    private final T event;

    private final TemporalExpression expression;

    private ScheduleElement(T event, TemporalExpression expression) {
        this.event = event;
        this.expression = expression;
    }

    public static <T> ScheduleElement<T> of(T event, TemporalExpression expression) {
        return new ScheduleElement<T>(event, expression);
    }

    public boolean isOccurring(LocalDate date) {
        return expression.includes(date);
    }

    public T event() {
        return event;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(this.getClass().getSimpleName()).append(": event='").append(event).append("' expression=").append(expression).append(']');
        return sb.toString();
    }
}
