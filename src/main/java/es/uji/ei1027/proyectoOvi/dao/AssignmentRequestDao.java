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
        jdbcTemplate.update("INSERT INTO AssignmentRequest VALUES (?,?,?,?,?,?,?,?,?,?)",
                assignmentRequest.getRequest_Id(), assignmentRequest.getRequestDate(), assignmentRequest.getTypeOfService(),
                assignmentRequest.getRequiredStartAvailability(), assignmentRequest.getRequiredEndAvailability(), assignmentRequest.getServiceLocation(),
                assignmentRequest.getRequiredTraining(), assignmentRequest.getRequiredExperience(), assignmentRequest.getRequiredSkills(),
                assignmentRequest.getOviuser_id());
    }

    public void deleteAssignmentRequest(AssignmentRequest assignmentRequest) {
        jdbcTemplate.update("DELETE FROM AssignmentRequest WHERE request_Id=?",
                assignmentRequest.getRequest_Id());
    }
    public void deleteAssignmentRequest(String requestId) {
        jdbcTemplate.update("DELETE FROM AssignmentRequest WHERE request_Id=?",
                requestId);
    }

    public void updateAssignmentRequest(AssignmentRequest assignmentRequest) {
        jdbcTemplate.update("UPDATE AssignmentRequest SET requestDate=?, typeOfService=?, requiredStartAvailability=?, requiredEndAvailability=?, serviceLocation=?, requiredTraining=?, requiredExperience=?, requiredSkills=?, oviuser_id=? WHERE request_Id=?",
                assignmentRequest.getRequestDate(), assignmentRequest.getTypeOfService(),
                assignmentRequest.getRequiredStartAvailability(), assignmentRequest.getRequiredEndAvailability(), assignmentRequest.getServiceLocation(),
                assignmentRequest.getRequiredTraining(), assignmentRequest.getRequiredExperience(), assignmentRequest.getRequiredSkills(),
                assignmentRequest.getOviuser_id(), assignmentRequest.getRequest_Id());
    }

    public AssignmentRequest getAssignmentRequest(String requestId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM AssignmentRequest WHERE request_Id=?",
                    new AssignmentRequestRowMapper(), requestId);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<AssignmentRequest> getAssignmentRequests() {
        try {
            return jdbcTemplate.query("SELECT * FROM AssignmentRequest",
                    new AssignmentRequestRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
}
