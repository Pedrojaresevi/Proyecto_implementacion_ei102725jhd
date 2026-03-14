package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Tutor;

import javax.swing.tree.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class TutorRowMapper implements RowMapper<Tutor> {
    public Tutor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tutor tutor = new Tutor();
        tutor.setId(rs.getString("id"));
        tutor.setName(rs.getString("name"));
        tutor.setEmail(rs.getString("email"));
        tutor.setStatus(rs.getString("status"));
        return tutor;
    }
}
