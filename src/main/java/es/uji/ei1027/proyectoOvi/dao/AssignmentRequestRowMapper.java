package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AssignmentRequestRowMapper implements RowMapper<AssignmentRequest> {
    public AssignmentRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssignmentRequest assignmentRequest = new AssignmentRequest();
        assignmentRequest.setRequestDate(rs.getDate("requestDate"));
        assignmentRequest.setTypeOfService(rs.getString("typeOfService"));
        assignmentRequest.setRequiredStartAvailability(rs.getDate("requiredStartAvailability"));
        assignmentRequest.setRequiredEndAvailability(rs.getDate("requiredEndAvailability"));
        assignmentRequest.setServiceLocation(rs.getString("serviceLocation"));
        assignmentRequest.setRequiredTraining(rs.getString("requiredTraining"));
        assignmentRequest.setRequiredExperience(rs.getString("requiredExperience"));
        assignmentRequest.setRequiredSkills(rs.getString("requiredSkills"));
        assignmentRequest.setRequest_Id(rs.getString("request_Id"));
        assignmentRequest.setOviuser_id(rs.getString("oviuser_id"));
        assignmentRequest.setStatus(rs.getString("status"));
        return assignmentRequest;
    }
}
