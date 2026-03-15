package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Pap_Pati;

import javax.swing.tree.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Pap_PatiRowMapper implements RowMapper<Pap_Pati> {
    public Pap_Pati mapRow(ResultSet rs, int rowNum) throws SQLException {
        Pap_Pati pap_pati = new Pap_Pati();
        pap_pati.setName(rs.getString("name"));
        pap_pati.setSurname(rs.getString("surname"));
        pap_pati.setDni(rs.getString("dni"));
        pap_pati.setDateOfBirth(rs.getDate("dateOfBirth"));
        pap_pati.setAddress(rs.getString("address"));
        pap_pati.setPhone(rs.getInt("phone"));
        pap_pati.setEmail(rs.getString("email"));
        pap_pati.setSpecificTraining(rs.getString("specificTraining"));
        pap_pati.setTypeOfExperience(rs.getString("typeOfExperience"));
        pap_pati.setCurriculumVitae(rs.getString("curriculumVitae"));
        pap_pati.setStatus(rs.getString("status"));
        pap_pati.setUserAndPassword(rs.getString("userAndPassword"));
        return pap_pati;
    }
}
