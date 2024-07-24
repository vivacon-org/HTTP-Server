package org.vivacon;

import java.time.LocalDate;

public interface TemporalExpression {
    boolean includes(LocalDate theDate);
}