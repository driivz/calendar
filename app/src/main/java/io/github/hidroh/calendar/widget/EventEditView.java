package io.github.hidroh.calendar.widget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import io.github.hidroh.calendar.CalendarUtils;
import io.github.hidroh.calendar.R;
import io.github.hidroh.calendar.ViewUtils;
import io.github.hidroh.calendar.content.CalendarCursor;
import io.github.hidroh.calendar.content.EventModel;

/**
 * Edit view for an event in {@link android.provider.CalendarContract.Events}
 */
public class EventEditView extends RelativeLayout {

    private final SwitchCompat mSwitchAllDay;
    private final TextView mTextViewStartDate;
    private final TextView mTextViewStartTime;
    private final TextView mTextViewEndDate;
    private final TextView mTextViewEndTime;
    private final int[] mColors;
    private final int mTransparentColor;
    private EventModel mEvent = EventModel.createInstance();

    public EventEditView(Context context) {
        this(context, null);
    }

    public EventEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.event_edit_view, this);
        int horizontalPadding = context.getResources()
                .getDimensionPixelSize(R.dimen.horizontal_padding),
                verticalPadding = context.getResources()
                        .getDimensionPixelSize(R.dimen.vertical_padding);
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        mSwitchAllDay = (SwitchCompat) findViewById(R.id.switch_all_day);
        mTextViewStartDate = (TextView) findViewById(R.id.text_view_start_date);
        mTextViewStartTime = (TextView) findViewById(R.id.text_view_start_time);
        mTextViewEndDate = (TextView) findViewById(R.id.text_view_end_date);
        mTextViewEndTime = (TextView) findViewById(R.id.text_view_end_time);
        mTransparentColor = ContextCompat.getColor(context, android.R.color.transparent);
        if (isInEditMode()) {
            mColors = new int[]{mTransparentColor};
        } else {
            mColors = ViewUtils.getCalendarColors(context);
        }
        setupViews();
        setEvent(mEvent);
    }

    /**
     * Sets view model for this view
     * @param event    view model representing event to edit
     */
    public void setEvent(@NonNull EventModel event) {
        mEvent = event;
        mSwitchAllDay.setChecked(event.isAllDay());
        setCalendarId(mEvent.getCalendarId());
        setDate(true);
        setDate(false);
        setTime(true);
        setTime(false);
    }

    /**
     * Gets view model representing event being edited
     * @return  view model representing editing event
     */
    @NonNull
    public EventModel getEvent() {
        return mEvent;
    }

    /**
     * Sets data source for calendars from {@link android.provider.CalendarContract.Calendars}
     * @param cursor    cursor to access list of calendars
     */
    public void swapCalendarSource(CalendarCursor cursor) {
    }

    /**
     * Sets name of selected event calendar
     * @param calendarName    selected calendar name
     */
    public void setSelectedCalendar(String calendarName) {
    }

    private void setupViews() {
        findViewById(R.id.text_view_all_day).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchAllDay.toggle();
            }
        });
        mSwitchAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mEvent.isAllDay() == isChecked) {
                    return;
                }
                mEvent.setIsAllDay(isChecked);
                if (isChecked) {
                    setDate(true);
                    setDate(false);
                    setTime(true);
                    setTime(false);
                }
            }
        });
        mTextViewStartDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        mTextViewEndDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(false);
            }
        });
        mTextViewStartTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(true);
            }
        });
        mTextViewEndTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(false);
            }
        });
    }

    private void setDate(boolean startDate) {
        TextView label = startDate ? mTextViewStartDate : mTextViewEndDate;
        Calendar dateTime = startDate ? mEvent.getStartDate() : mEvent.getEndDate();
        label.setText(CalendarUtils.toDayString(getContext(), dateTime.getTimeInMillis()));
        ensureValidDates(startDate);
    }

    private void setTime(boolean startTime) {
        TextView label = startTime ? mTextViewStartTime : mTextViewEndTime;
        Calendar dateTime = startTime ? mEvent.getStartDate() : mEvent.getEndDate();
        label.setText(CalendarUtils.toTimeString(getContext(), dateTime.getTimeInMillis()));
        ensureValidTimes(startTime);
    }

    @VisibleForTesting
    void changeCalendar(int selection) {
    }

    private void setCalendarId(long calendarId) {
        mEvent.setCalendarId(calendarId);
        if (calendarId == EventModel.NO_ID) {
            setBackgroundColor(mTransparentColor);
        } else {
            setBackgroundColor(mColors[(int) (Math.abs(mEvent.getCalendarId()) % mColors.length)]);
        }
    }

    private void ensureValidDates(boolean startDateChanged) {
        if (startDateChanged) {
            if (mEvent.getStartDate().after(mEvent.getEndDate())) {
                mEvent.getEndDate().setTimeInMillis(mEvent.getStartDate().getTimeInMillis());
                setDate(false);
                setTime(false);
            }
        } else {
            if (mEvent.getEndDate().before(mEvent.getStartDate())) {
                mEvent.getStartDate().setTimeInMillis(mEvent.getEndDate().getTimeInMillis());
                setDate(true);
                setTime(true);
            }
        }
    }

    private void ensureValidTimes(boolean startTimeChanged) {
        if (startTimeChanged) {
            if (mEvent.getStartDate().after(mEvent.getEndDate())) {
                mEvent.getEndDate().setTimeInMillis(mEvent.getStartDate().getTimeInMillis());
                setTime(false);
            }
        } else {
            if (mEvent.getEndDate().before(mEvent.getStartDate())) {
                mEvent.getStartDate().setTimeInMillis(mEvent.getEndDate().getTimeInMillis());
                setTime(true);
            }
        }
    }

    private void showDatePicker(final boolean startDate) {
        final Calendar dateTime = startDate ? mEvent.getStartDate() : mEvent.getEndDate();
        new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateTime.set(year, monthOfYear, dayOfMonth);
                        mSwitchAllDay.setChecked(false);
                        setDate(startDate);
                    }
                },
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void showTimePicker(final boolean startTime) {
        final Calendar dateTime = startTime ? mEvent.getStartDate() : mEvent.getEndDate();
        new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);
                        mSwitchAllDay.setChecked(false);
                        setTime(startTime);
                    }
                },
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE),
                false)
                .show();
    }

}
