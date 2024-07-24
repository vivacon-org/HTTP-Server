package org.vivacon;

import java.time.LocalDate;
import java.util.List;

public class Union implements TemporalExpression {

    private List<TemporalExpression> elements;

    public Union(List<TemporalExpression> elements) {
        this.elements = elements;
    }

    @Override
    public boolean includes(LocalDate theDate) {

        for (TemporalExpression element : elements) {
            if (element.includes(theDate))
                return true;
        }

        return false;
    }
}
