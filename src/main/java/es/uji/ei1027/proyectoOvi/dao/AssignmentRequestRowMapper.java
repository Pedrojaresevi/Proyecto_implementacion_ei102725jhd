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
        assignmentRequest.setRequiredAvailability(rs.getString("requiredAvailability"));
        assignmentRequest.setServiceLocation(rs.getString("serviceLocation"));
        assignmentRequest.setSpecificPreferences(rs.getString("specificPreferences"));
        assignmentRequest.setListOfProposedCandidates(rs.getString("listOfProposedCandidates"));
        assignmentRequest.setRequestId(rs.getLong("requestId"));
        assignmentRequest.setPap_patiId(rs.getString("pap_patiId"));
        assignmentRequest.setOviUserId(rs.getString("oviUserId"));
        return assignmentRequest;
    }
}
