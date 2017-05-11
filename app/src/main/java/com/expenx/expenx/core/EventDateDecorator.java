package com.expenx.expenx.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.expenx.expenx.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class EventDateDecorator implements DayViewDecorator {

    private Drawable highlightDrawable;
    private Context context;
    private CalendarDay eventDay;

    public EventDateDecorator(Context context, CalendarDay eventDay) {
        this.context = context;
        highlightDrawable = this.context.getResources().getDrawable(R.drawable.calendar_event_background);
        this.eventDay = eventDay;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(eventDay);
    }
    
    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }

}