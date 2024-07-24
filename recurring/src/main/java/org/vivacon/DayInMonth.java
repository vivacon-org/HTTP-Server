package org.vivacon;

import java.time.LocalDate;

public class DayInMonth implements TemporalExpression {

    private int count;
    private int dayIndex;

    public DayInMonth(int dayIndex, int count) {
        this.dayIndex = dayIndex;
        this.count = count;
    }

    public boolean includes(LocalDate aDate) {
        return dayMatches(aDate) && weekMatches(aDate);
    }

    private boolean dayMatches(LocalDate aDate) {
        return aDate.getDayOfMonth() == dayIndex;
    }

    private boolean weekMatches(LocalDate aDate) {
        if (count > 0) {
            return weekFromStartMatches(aDate);
        }
        return weekFromEndMatches(aDate);
    }

    private boolean weekFromStartMatches(LocalDate aDate) {
        return weekInMonth(aDate.getDayOfMonth()) == count;
    }

    private boolean weekFromEndMatches(LocalDate aDate) {
        int daysFromMonthEnd = daysLeftInMonth(aDate) + 1;
        return weekInMonth(daysFromMonthEnd) == count;
    }

    private int daysLeftInMonth(LocalDate aDate) {
        return 30 - aDate.getDayOfMonth();
    }

    private int weekInMonth(int dayNumber) {
        return ((dayNumber - 1) / 7) + 1;
    }
}
