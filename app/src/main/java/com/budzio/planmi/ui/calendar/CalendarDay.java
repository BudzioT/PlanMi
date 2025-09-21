package com.budzio.planmi.ui.calendar;

import java.time.LocalDate;

public class CalendarDay {
    public final String dayText;
    public final boolean isCurrentMonth;
    public final boolean isCurrentDay;
    public final LocalDate date;

    public CalendarDay(String dayText, boolean isCurrentMonth, boolean isCurrentDay) {
        this.date = null;
        this.dayText = dayText;
        this.isCurrentMonth = isCurrentMonth;
        this.isCurrentDay = isCurrentDay;
    }

    public CalendarDay(LocalDate date, String dayText, boolean isCurrentMonth, boolean isCurrentDay) {
        this.date = date;
        this.dayText = dayText;
        this.isCurrentMonth = isCurrentMonth;
        this.isCurrentDay = isCurrentDay;
    }
}
