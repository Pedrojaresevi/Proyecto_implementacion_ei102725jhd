package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class Event {
    private long eventId;
    private String description;
    private String eventType;
    private Date endDate;
    private Date startDate;
    private String location;
    private String educatorId;
    private int participantLimit;
    private String eventStatus;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducatorId() {
        return educatorId;
    }

    public void setEducatorId(String educatorId) {
        this.educatorId = educatorId;
    }

    public int getParticipantLimit() {
        return participantLimit;
    }

    public void setParticipantLimit(int participantLimit) {
        this.participantLimit = participantLimit;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", description='" + description + '\'' +
                ", eventType='" + eventType + '\'' +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                ", location='" + location + '\'' +
                ", educatorId='" + educatorId + '\'' +
                ", participantLimit=" + participantLimit +
                ", eventStatus='" + eventStatus + '\'' +
                '}';
    }
}
