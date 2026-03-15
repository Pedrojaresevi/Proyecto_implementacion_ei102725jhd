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
                negotiation.getStatus(), negotiation.getRecordOfComunications(), negotiation.getStartDate(),
                negotiation.getEndDate(), negotiation.getPap_patiId(), negotiation.getRequestId());
    }

    public void deleteNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("DELETE FROM Negotiation WHERE pap_patiId=? AND requestId=?",
                negotiation.getPap_patiId(), negotiation.getRequestId());
    }

    public void deleteNegotiation(long requestId, String pap_patiId) {
        jdbcTemplate.update("DELETE FROM Negotiation WHERE pap_patiId=? AND requestId=?",
                pap_patiId, requestId);
    }

    public void updateNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("UPDATE Negotiation SET status=?, recordOfComunications=?, startDate=?, endDate=? WHERE pap_patiId=? AND requestId=?",
                negotiation.getStatus(), negotiation.getRecordOfComunications(), negotiation.getStartDate(),
                negotiation.getEndDate(), negotiation.getPap_patiId(), negotiation.getRequestId());
    }

    public Negotiation getNegotiation(long requestId, String pap_patiId) {
        try {
            return jdbcTemplate.queryForObject("SELECT FROM Negotiation WHERE pap_patiId=? AND requestId=?",
                    new NegotiationRowMapper(), pap_patiId, requestId);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<Negotiation> getNegotiationByPap_Pati(String pap_patiId) {
        try {
            return jdbcTemplate.queryForObject("SELECT FROM Negotiation WHERE pap_patiId=?",
                    new NegotiationRowMapper(), pap_patiId);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<Negotiation> getNegotiations() {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Negotiation",
                    new NegotiationRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
}
