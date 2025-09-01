package com.budzio.planmi.ui.calendar;

public class CalendarDay {
    public final String dayText;
    public final boolean isCurrentMonth;
    public final boolean isCurrentDay;

    public CalendarDay(String dayText, boolean isCurrentMonth, boolean isCurrentDay) {
        this.dayText = dayText;
        this.isCurrentMonth = isCurrentMonth;
        this.isCurrentDay = isCurrentDay;
    }
}
