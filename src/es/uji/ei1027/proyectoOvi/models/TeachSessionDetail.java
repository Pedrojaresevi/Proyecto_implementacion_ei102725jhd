package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class TeachSessionDetail {
    private Date date;
    private int eventDuration;
    private String subject;
    private int attendanceCount;
    private long eventId;
    private String educatorId;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEducatorId() {
        return educatorId;
    }

    public void setEducatorId(String educatorId) {
        this.educatorId = educatorId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(int eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(int attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    @Override
    public String toString() {
        return "TeachSessionDetail{" +
                "eventId=" + eventId +
                ", educatorId='" + educatorId + '\'' +
                ", date=" + date +
                ", eventDuration=" + eventDuration +
                ", subject='" + subject + '\'' +
                ", attendanceCount=" + attendanceCount +
                '}';
    }
}
