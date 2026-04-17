package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class Pap_PatiDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addPap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("INSERT INTO Pap_Pati VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDni(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getUserAndPassword());
    }
    public void deletePap_Pati(String dni) {
        jdbcTemplate.update("DELETE FROM Pap_Pati WHERE dni = ?", dni);
    }

    public void deletePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("DELETE FROM Pap_Pati WHERE dni = ?", pap_pati.getDni());
    }

    public void updatePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("UPDATE Pap_Pati SET name=?, surname=?, dateOfBirth=?, address=?, phone=?, email=?, specificTraining=?, typeOfExperience=?, curriculumVitae=?, status=?, userAndPassword=? WHERE dni=?",
                pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getUserAndPassword(), pap_pati.getDni());
    }

    public Pap_Pati getPap_Pati(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Pap_Pati WHERE dni=?", new Pap_PatiRowMapper(), dni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Pap_Pati> getAllPap_Pati() {
        try {
            return jdbcTemplate.query("SELECT * FROM Pap_Pati",
                    new Pap_PatiRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

