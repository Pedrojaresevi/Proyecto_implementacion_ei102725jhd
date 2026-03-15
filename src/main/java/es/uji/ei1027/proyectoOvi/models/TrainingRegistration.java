package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class TrainingRegistration {
    private long registrationId;
    private long eventId;
    private String participantName;
    private String participantSurnames;
    private long participantId;
    private String email;
    private long phone;
    private Date registrationDate;
    private String attendanceStatus;
    private String oviuserId;
    private String pap_patiId;
    private String tutorId;

    public long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(long registrationId) {
        this.registrationId = registrationId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantSurnames() {
        return participantSurnames;
    }

    public void setParticipantSurnames(String participantSurnames) {
        this.participantSurnames = participantSurnames;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getOviuserId() {
        return oviuserId;
    }

    public void setOviuserId(String oviuserId) {
        this.oviuserId = oviuserId;
    }

    public String getPap_patiId() {
        return pap_patiId;
    }

    public void setPap_patiId(String pap_patiId) {
        this.pap_patiId = pap_patiId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    @Override
    public String toString() {
        return "TrainingRegistration{" +
                "registrationId=" + registrationId +
                ", eventId=" + eventId +
                ", participantName='" + participantName + '\'' +
                ", participantSurnames='" + participantSurnames + '\'' +
                ", participantId=" + participantId +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                ", registrationDate=" + registrationDate +
                ", attendanceStatus='" + attendanceStatus + '\'' +
                ", oviuserId='" + oviuserId + '\'' +
                ", pap_patiId='" + pap_patiId + '\'' +
                ", tutorId='" + tutorId + '\'' +
                '}';
    }
}
