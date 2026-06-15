package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.OviUser;

import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class OviUserRowMapper implements RowMapper<OviUser> {
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser oviUser = new OviUser();
        oviUser.setDni(rs.getString("dni"));
        oviUser.setName(rs.getString("name"));
        oviUser.setAddress(rs.getString("address"));
        oviUser.setEmail(rs.getString("email"));
        oviUser.setEntityThatIsInvolved(rs.getString("entityThatIsInvolved"));
        oviUser.setTypeOfFunctionalDiversity(rs.getString("typeOfFunctionalDiversity"));
        oviUser.setPassword(rs.getString("password"));
        oviUser.setStatus(rs.getString("status"));
        oviUser.setLifePlan(rs.getString("lifePlan"));
        oviUser.setTutor_id(rs.getString("tutor_id"));
        oviUser.setDateOfBirth(rs.getObject("dateOfBirth", LocalDate.class));
        oviUser.setRejectReason(rs.getString("rejectReason"));

        return oviUser;
    }
}
