package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Tutor;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class TutorRowMapper implements RowMapper<Tutor> {
    public Tutor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tutor tutor = new Tutor();
        tutor.setDni(rs.getString("dni"));
        tutor.setName(rs.getString("name"));
        tutor.setEmail(rs.getString("email"));
        tutor.setStatus(rs.getString("status"));
        tutor.setPassword(rs.getString("password"));
        tutor.setRejectReason(rs.getString("rejectReason"));

        return tutor;
    }
}
