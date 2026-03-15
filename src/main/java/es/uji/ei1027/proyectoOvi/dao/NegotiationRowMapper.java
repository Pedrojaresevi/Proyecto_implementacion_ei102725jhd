package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Negotiation;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class NegotiationRowMapper implements RowMapper<Negotiation> {
    public Negotiation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Negotiation negotiation = new Negotiation();
        negotiation.setPap_patiId(rs.getString("pap_patiId"));
        negotiation.setRequestId(rs.getLong("requestId"));
        negotiation.setStatus(rs.getString("status"));
        negotiation.setRecordOfComunications(rs.getString("recordOfComunications"));
        negotiation.setStartDate(rs.getDate("startDate"));
        negotiation.setEndDate(rs.getDate("endDate"));
        return negotiation;
    }
}
