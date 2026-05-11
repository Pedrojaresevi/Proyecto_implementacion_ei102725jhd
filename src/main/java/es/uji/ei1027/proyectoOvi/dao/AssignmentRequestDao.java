package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import es.uji.ei1027.proyectoOvi.models.ListOfProposedCandidates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AssignmentRequestDao  {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAssignmentRequest(AssignmentRequest assignmentRequest) {
        jdbcTemplate.update("INSERT INTO AssignmentRequest VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                assignmentRequest.getRequest_Id(), assignmentRequest.getRequestDate(), assignmentRequest.getTypeOfService(),
                assignmentRequest.getRequiredStartAvailability(), assignmentRequest.getRequiredEndAvailability(), assignmentRequest.getServiceLocation(),
                assignmentRequest.getRequiredTraining(), assignmentRequest.getRequiredExperience(), assignmentRequest.getRequiredSkills(),
                assignmentRequest.getOviuser_id(),assignmentRequest.getStatus(), assignmentRequest.getTutor_id());
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
        jdbcTemplate.update("UPDATE AssignmentRequest SET requestDate=?, typeOfService=?, requiredStartAvailability=?, requiredEndAvailability=?, serviceLocation=?, requiredTraining=?, requiredExperience=?, requiredSkills=?, oviuser_id=?, status=?, tutor_id=?, rejectReason=? WHERE request_Id=?",
                assignmentRequest.getRequestDate(), assignmentRequest.getTypeOfService(),
                assignmentRequest.getRequiredStartAvailability(), assignmentRequest.getRequiredEndAvailability(), assignmentRequest.getServiceLocation(),
                assignmentRequest.getRequiredTraining(), assignmentRequest.getRequiredExperience(), assignmentRequest.getRequiredSkills(),
                assignmentRequest.getOviuser_id(),assignmentRequest.getStatus(), assignmentRequest.getTutor_id() ,assignmentRequest.getRejectReason(), assignmentRequest.getRequest_Id());
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

    // Nuevo metodo para filtrar solicitudes por el DNI del OviUser
    public List<AssignmentRequest> getRequestsByOviUser(String oviuserId) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AssignmentRequest WHERE oviuser_id=?",
                    new AssignmentRequestRowMapper(),
                    oviuserId
            );
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<AssignmentRequest>();
        }
    }
    //
    public List<AssignmentRequest> getRequestsByTutor(String tutorId) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AssignmentRequest WHERE tutor_id=?",
                    new AssignmentRequestRowMapper(),
                    tutorId
            );
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<AssignmentRequest>();
        }
    }

    public List<AssignmentRequest> getRequestsByPappati(String pappatiId) {
        try {
            String sql = "SELECT ar.* FROM assignmentrequest ar " +
                    "JOIN listofproposedcandidates lp ON ar.request_id = lp.request_id " +
                    "WHERE lp.pappati_id = ?";

            return jdbcTemplate.query(sql, new AssignmentRequestRowMapper(), pappatiId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<AssignmentRequest>();
        }
    }

    public AssignmentRequest getAssignmentRequestWithOviUser(String id) {
        String sql = """
        SELECT ar.*, ou.name as oviuser_name, ou.email as oviuser_email, 
               ou.address as oviuser_address, ou.typeoffunctionaldiversity,
               ou.entitythatisinvolved
        FROM assignmentrequest ar
        LEFT JOIN oviuser ou ON ar.oviuser_id = ou.dni
        WHERE ar.request_id = ?
    """;
        return jdbcTemplate.queryForObject(sql, new AssignmentRequestRowMapper(), id);
    }

    public String getLastRequestId() {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT request_id FROM AssignmentRequest ORDER BY CAST(SUBSTRING(request_id FROM 4) AS INTEGER) DESC LIMIT 1",
                    String.class
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    //
    public List<AssignmentRequest> getAssignmentRequestsByStatus(String status) {
        try {
            return jdbcTemplate.query("SELECT * FROM AssignmentRequest WHERE status=?",
                    new AssignmentRequestRowMapper(), status);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }
}
