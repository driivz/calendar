package io.github.hidroh.calendar.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.TimeZone;

import io.github.hidroh.calendar.CalendarUtils;
import io.github.hidroh.calendar.widget.EventEditView;

/**
 * View model for {@link EventEditView}.
 * Event represented by this class is assumed to be in system timezone.
 */
public class EventModel implements Parcelable {

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel in) {
            return new EventModel(in);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };

    /**
     * Creates an instance of {@link EventModel} that starts at 'earliest' future time
     * @return  an {@link EventModel} instance
     */
    public static EventModel createInstance() {
        return new EventModel();
    }

    /**
     * Builder utility to build an {@link EventModel}
     */
    public static class Builder {
        private final EventModel event = new EventModel();

        /**
         * Sets event ID
         * @param id    event ID
         * @return  this instance (fluent API)
         */
        public Builder id(long id) {
            event.id = id;
            return this;
        }

        /**
         * Sets event calendar ID
         * @param calendarId    event calendar ID
         * @return  this instance (fluent API)
         */
        public Builder calendarId(long calendarId) {
            event.calendarId = calendarId;
            return this;
        }

        /**
         * Sets event title
         * @param title    event title
         * @return  this instance (fluent API)
         */
        public Builder title(String title) {
            event.title = title;
            return this;
        }

        /**
         * Sets event start date time
         * @param timeMillis    start date time in milliseconds
         * @return  this instance (fluent API)
         */
        public Builder start(long timeMillis) {
            event.localStart.setTimeInMillis(timeMillis);
            return this;
        }

        /**
         * Sets event end date time
         * @param timeMillis    end date time in milliseconds
         * @return  this instance (fluent API)
         */
        public Builder end(long timeMillis) {
            event.localEnd.setTimeInMillis(timeMillis);
            return this;
        }

        /**
         * Sets event all day status
         * @param isAllDay    true if event is all day, false otherwise
         * @return  this instance (fluent API)
         */
        public Builder allDay(boolean isAllDay) {
            event.isAllDay = isAllDay;
            return this;
        }

        /**
         * Creates the {@link EventModel} that has been built by this builder
         * @return  an {@link EventModel} instance
         */
        public EventModel build() {
            return event;
        }
    }

    public static final long NO_ID = -1;

    long id = NO_ID;
    long calendarId = NO_ID;
    String title;
    boolean isAllDay = false;
    final Calendar localStart = Calendar.getInstance();
    final Calendar localEnd = Calendar.getInstance();

    EventModel() {
        localStart.add(Calendar.HOUR_OF_DAY, 1);
        localStart.set(Calendar.MINUTE, 0);
        localEnd.add(Calendar.HOUR_OF_DAY, 2);
        localEnd.set(Calendar.MINUTE, 0);
    }

    EventModel(Parcel in) {
        id = in.readLong();
        calendarId = in.readLong();
        title = in.readString();
        isAllDay = in.readByte() != 0;
        localStart.setTimeInMillis(in.readLong());
        localEnd.setTimeInMillis(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(calendarId);
        dest.writeString(title);
        dest.writeByte((byte) (isAllDay ? 1 : 0));
        dest.writeLong(localStart.getTimeInMillis());
        dest.writeLong(localEnd.getTimeInMillis());
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Gets event ID
     * @return  event ID
     */
    public long getId() {
        return id;
    }

    /**
     * Checks if this instance has event ID
     * @return  true for existing events, false otherwise
     */
    public boolean hasId() {
        return id != NO_ID;
    }

    /**
     * Checks if this instance has calendar ID
     * @return  true if have calendar ID, false otherwise
     */
    public boolean hasCalendarId() {
        return calendarId != NO_ID;
    }

    /**
     * Checks if event has non-empty title
     * @return  true if has non-empty title, false otherwise
     */
    public boolean hasTitle() {
        return !TextUtils.isEmpty(title);
    }

    /**
     * Gets event title
     * @return  event title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets event start date time
     * @return  start date time in local timezone, or midnight UTC if event is all day
     */
    public long getStartDateTime() {
        if (isAllDay) {
            return CalendarUtils.toUtcTimeZone(localStart.getTimeInMillis());
        } else {
            return localStart.getTimeInMillis();
        }
    }

    public Calendar getStartDate(){
        return localStart;
    }

    public Calendar getEndDate(){
        return localEnd;
    }

    /**
     * Gets event end date time
     * @return  end date time in local timezone, or midnight UTC if event is all day
     */
    public long getEndDateTime() {
        if (isAllDay) {
            return CalendarUtils.toUtcTimeZone(localEnd.getTimeInMillis());
        } else {
            return localEnd.getTimeInMillis();
        }
    }

    /**
     * Checks if event is all day
     * @return  true if event is all day, false otherwise
     */
    public boolean isAllDay() {
        return isAllDay;
    }

    /**
     * Gets event timezone
     * @return  local system timezone ID, or UTC if event is all day
     */
    public String getTimeZone() {
        return isAllDay ? CalendarUtils.TIMEZONE_UTC : TimeZone.getDefault().getID();
    }

    /**
     * Gets event calendar ID
     * @return  event calendar ID
     */
    public long getCalendarId() {
        return calendarId;
    }

    public void setIsAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
        if (isAllDay) {
            localStart.set(Calendar.HOUR_OF_DAY, 0);
            localStart.set(Calendar.MINUTE, 0);
            localEnd.set(Calendar.HOUR_OF_DAY, 0);
            localEnd.set(Calendar.MINUTE, 0);
            if (localEnd.equals(localStart)) {
                localEnd.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }
}
