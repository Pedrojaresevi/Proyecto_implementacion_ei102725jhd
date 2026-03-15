package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AssignmentRequestDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAssignmentRequest(AssignmentRequest assignmentRequest) {
        jdbcTemplate.update("INSERT INTO AssignmentRequest VALUES (?,?,?,?,?,?,?,?,?)",
                assignmentRequest.getRequestDate(), assignmentRequest.getTypeOfService(), assignmentRequest.getRequiredAvailability(),
                assignmentRequest.getServiceLocation(), assignmentRequest.getSpecificPreferences(), assignmentRequest.getListOfProposedCandidates(),
                assignmentRequest.getRequestId(), assignmentRequest.getPap_patiId(), assignmentRequest.getOviUserId());
    }

    public void deleteAssignmentRequest(AssignmentRequest assignmentRequest) {
        jdbcTemplate.update("DELETE FROM AssignmentRequest WHERE requestId=?",
                assignmentRequest.getRequestId());
    }
    public void deleteAssignmentRequest(long requestId) {
        jdbcTemplate.update("DELETE FROM AssignmentRequest WHERE requestId=?",
                requestId);
    }

    public void updateAssignmentRequest(AssignmentRequest assignmentRequest) {
        jdbcTemplate.update("UPDATE AssignmentRequest SET requestDate=?, typeOfService=?, requiredAvailability=?, serviceLocation=?, specificPreferences=?, listOfProposedCandidates=?, pap_patiId=?, oviUserId=? WHERE requestId=?",
                assignmentRequest.getRequestDate(), assignmentRequest.getTypeOfService(), assignmentRequest.getRequiredAvailability(),
                assignmentRequest.getServiceLocation(), assignmentRequest.getSpecificPreferences(), assignmentRequest.getListOfProposedCandidates(),
                assignmentRequest.getPap_patiId(), assignmentRequest.getOviUserId(), assignmentRequest.getRequestId());
    }

    public AssignmentRequest getAssignmentRequest(long requestId) {
        try {
            return jdbcTemplate.queryForObject("SELECT FROM AssignmentRequest WHERE requestId=?",
                    new AssignmentRequestRowMapper(), requestId);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<AssignmentRequest> getAssignmentRequests() {
        try {
            return jdbcTemplate.query("SELECT * FROM Contract",
                    new AssignmentRequestRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
}
