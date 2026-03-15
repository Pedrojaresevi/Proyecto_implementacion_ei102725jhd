package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.OviUser;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class OviUserRowMapper implements RowMapper<OviUser> {
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser oviUser = new OviUser();
        oviUser.setId(rs.getString("id"));
        oviUser.setName(rs.getString("name"));
        oviUser.setAddress(rs.getString("address"));
        oviUser.setEmail(rs.getString("email"));
        oviUser.setEntityThatIsInvolved(rs.getString("entityThatIsInvolved"));
        oviUser.setTypeOfFunctionalDiversity(rs.getString("typeOfFunctionalDiversity"));
        oviUser.setDateOfAcceptance(rs.getDate("dateOfAcceptance"));
        oviUser.setStatus(rs.getString("status"));
        oviUser.setUserAndPassword(rs.getString("userAndPassword"));
        oviUser.setTutorId(rs.getString("tutorId"));
        return oviUser;
    }
}
