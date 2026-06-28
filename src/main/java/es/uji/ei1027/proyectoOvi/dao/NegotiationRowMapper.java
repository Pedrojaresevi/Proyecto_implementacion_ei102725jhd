package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Negotiation;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class NegotiationRowMapper implements RowMapper<Negotiation> {
    public Negotiation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Negotiation negotiation = new Negotiation();
        negotiation.setNegotiation_Id(rs.getString("negotiation_Id"));
        negotiation.setStatus(rs.getString("status"));
        negotiation.setRecordOfComunications(rs.getString("recordofcommunications"));
        negotiation.setStartDate(rs.getDate("message_date"));
        negotiation.setEndDate(rs.getDate("endDate"));
        negotiation.setListId(rs.getString("list_id"));
        java.sql.Time time = rs.getTime("hora");
        if (time != null) {
            negotiation.setHora(time.toLocalTime());
        }
        negotiation.setEmisorDni(rs.getString("emisor_dni"));
        try {
            negotiation.setInterlocutorName(rs.getString("interlocutorName"));
        } catch (SQLException e) {
            
            
        }
        return negotiation;
    }
}
