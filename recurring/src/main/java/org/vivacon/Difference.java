package org.vivacon;

import java.time.LocalDate;

public class Difference implements TemporalExpression {

    private TemporalExpression included;

    private TemporalExpression excluded;

    public Difference(TemporalExpression included, TemporalExpression excluded) {
        this.included = included;
        this.excluded = excluded;
    }

    @Override
    public boolean includes(LocalDate theDate) {
        return included.includes(theDate) && !excluded.includes(theDate);
    }
}
