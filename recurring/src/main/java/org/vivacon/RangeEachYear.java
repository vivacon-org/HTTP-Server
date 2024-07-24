package org.vivacon;

import java.time.LocalDate;

public class RangeEachYear {
    private int startMonth;
    private int endMonth;
    private int startDay;
    private int endDay;

    public RangeEachYear(int startMonth, int endMonth,
                         int startDay, int endDay) {
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public RangeEachYear(int startMonth, int endMonth) {
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.startDay = 0;
        this.endDay = 0;
    }

    public RangeEachYear(int month) {
        this.startMonth = month;
        this.endMonth = month;
        this.startDay = 0;
        this.endDay = 0;
    }

    public boolean includes(LocalDate aDate) {
        return monthsInclude(aDate) ||
                startMonthIncludes(aDate) ||
                endMonthIncludes(aDate);
    }

    private boolean monthsInclude(LocalDate aDate) {
        int month = aDate.getMonth().getValue();
        return month > startMonth && month < endMonth;
    }

    private boolean startMonthIncludes(LocalDate aDate) {
        if (aDate.getMonth().getValue() != startMonth) {
            return false;
        }

        if (startDay == 0) {
            return true;
        }

        return aDate.getDayOfMonth() >= startDay;
    }

    private boolean endMonthIncludes(LocalDate aDate) {
        if (aDate.getMonth().getValue() != endMonth) {
            return false;
        }

        if (endDay == 0) {
            return true;
        }

        return aDate.getDayOfMonth() <= endDay;
    }
}
