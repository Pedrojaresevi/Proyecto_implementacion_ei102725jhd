package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NegotiationDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("INSERT INTO Negotiation VALUES (?,?,?,?,?,?)",
                negotiation.getNegotiation_Id(), negotiation.getStatus(),
                negotiation.getRecordOfComunications(), negotiation.getStartDate(),
                negotiation.getEndDate(),
                negotiation.getListId());
    }

    public void deleteNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("DELETE FROM Negotiation WHERE negotiation_Id=?",
                negotiation.getNegotiation_Id());
    }

    public void deleteNegotiation(String negotiation_Id) {
        jdbcTemplate.update("DELETE FROM Negotiation WHERE negotiation_Id=?",
                negotiation_Id);
    }

    public void updateNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("UPDATE Negotiation SET status=?, recordofcommunications=?, startDate=?, endDate=?, list_id=? WHERE negotiation_Id=?",
                negotiation.getStatus(),
                negotiation.getRecordOfComunications(),
                negotiation.getStartDate(),
                negotiation.getEndDate(),
                negotiation.getListId(),
                negotiation.getNegotiation_Id()
        );
    }

    public Negotiation getNegotiation(String negotiation_Id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Negotiation WHERE negotiation_Id=?",
                    new NegotiationRowMapper(), negotiation_Id);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<Negotiation> getNegotiations() {
        try {
            return jdbcTemplate.query("SELECT * FROM Negotiation",
                    new NegotiationRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
    public List<Negotiation> getNegotiationsByUser(String oviuserId) {
        try {
            // Unimos Negotiation -> ListOfProposedCandidates -> AssignmentRequest
            String sql = "SELECT n.* FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.oviuser_id = ?";

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), oviuserId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Negotiation> getNegotiationsByTutor(String tutorDni) {
        try {
            String sql = "SELECT n.* FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.tutor_id = ?";

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), tutorDni);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public Negotiation getNegotiationByListId(String listId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Negotiation WHERE list_id = ?",
                    new NegotiationRowMapper(), listId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
