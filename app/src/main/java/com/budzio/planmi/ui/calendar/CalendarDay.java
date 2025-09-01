package com.budzio.planmi.ui.calendar;

public class CalendarDay {
    public final String dayText;
    public final boolean isCurrentMonth;

    public CalendarDay(String dayText, boolean isCurrentMonth) {
        this.dayText = dayText;
        this.isCurrentMonth = isCurrentMonth;
    }
}
