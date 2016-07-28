package de.zazaz.iot.bosch.indego;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({ "sel_cal", "cals" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceCalendar {
    
    @JsonPropertyOrder({ "En", "StHr", "StMin", "EnHr", "EnMin" })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceCalendarDaySlot {
        
        private boolean enabled;
        
        private int startHour;
        
        private int startMinute;
        
        private int endHour;
        
        private int endMinute;

        @JsonGetter("En")
        public boolean isEnabled ()
        {
            return enabled;
        }

        @JsonSetter("En")
        public void setEnabled (boolean enabled)
        {
            this.enabled = enabled;
        }

        @JsonGetter("StHr")
        public int getStartHour ()
        {
            return startHour;
        }

        @JsonSetter("StHr")
        public void setStartHour (int startHour)
        {
            this.startHour = startHour;
        }

        @JsonGetter("StMin")
        public int getStartMinute ()
        {
            return startMinute;
        }

        @JsonSetter("StMin")
        public void setStartMinute (int startMinute)
        {
            this.startMinute = startMinute;
        }

        @JsonGetter("EnHr")
        public int getEndHour ()
        {
            return endHour;
        }

        @JsonSetter("EnHr")
        public void setEndHour (int endHour)
        {
            this.endHour = endHour;
        }

        @JsonGetter("EnMin")
        public int getEndMinute ()
        {
            return endMinute;
        }

        @JsonSetter("EnMin")
        public void setEndMinute (int endMinute)
        {
            this.endMinute = endMinute;
        }
        
    }
    
    @JsonPropertyOrder({ "day", "slots" })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceCalendarDayEntry {
        
        private int number;
        
        private DeviceCalendarDaySlot[] slots;

        @JsonGetter("day")
        public int getNumber ()
        {
            return number;
        }

        @JsonSetter("day")
        public void setNumber (int number)
        {
            this.number = number;
        }

        @JsonGetter("slots")
        public DeviceCalendarDaySlot[] getSlots ()
        {
            return slots;
        }

        @JsonSetter("slots")
        public void setSlots (DeviceCalendarDaySlot[] slots)
        {
            this.slots = slots;
        }
        
    }
    
    @JsonPropertyOrder({ "cal", "days" })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceCalendarEntry {
        
        private int number;
        
        private DeviceCalendarDayEntry[] days;

        @JsonGetter("cal")
        public int getNumber ()
        {
            return number;
        }

        
        @JsonSetter("cal")
        public void setNumber (int number)
        {
            this.number = number;
        }

        @JsonGetter("days")
        public DeviceCalendarDayEntry[] getDays ()
        {
            return days;
        }

        @JsonSetter("days")
        public void setDays (DeviceCalendarDayEntry[] days)
        {
            this.days = days;
        }
        
    }
    
    private int selectedEntryNumber;
    
    private DeviceCalendarEntry[] entries;

    @JsonGetter("sel_cal")
    public int getSelectedEntryNumber ()
    {
        return selectedEntryNumber;
    }

    @JsonSetter("sel_cal")
    public void setSelectedEntryNumber (int selectedEntryNumber)
    {
        this.selectedEntryNumber = selectedEntryNumber;
    }

    @JsonGetter("cals")
    public DeviceCalendarEntry[] getEntries ()
    {
        return entries;
    }

    @JsonSetter("cals")
    public void setEntries (DeviceCalendarEntry[] entries)
    {
        this.entries = entries;
    }

}
