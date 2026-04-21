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
        jdbcTemplate.update("INSERT INTO PapPati VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDni(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getUserAndPassword(),
                pap_pati.getStartDate(), pap_pati.getEndDate(), pap_pati.getGeographicMobility(),pap_pati.getSkills());
    }
    public void deletePap_Pati(String dni) {
        jdbcTemplate.update("DELETE FROM PapPati WHERE dni = ?", dni);
    }

    public void deletePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("DELETE FROM PapPati WHERE dni = ?", pap_pati.getDni());
    }

    public void updatePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("UPDATE PapPati SET name=?, surname=?, dateOfBirth=?, address=?, phone=?, email=?, specificTraining=?, typeOfExperience=?, curriculumVitae=?, status=?, userAndPassword=?, startDate=?, endDate=?, geographicMobility=?, skills=? WHERE dni=?",
                pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getUserAndPassword(),
                pap_pati.getStartDate(), pap_pati.getEndDate(), pap_pati.getGeographicMobility(),pap_pati.getSkills(), pap_pati.getDni());
    }

    public Pap_Pati getPap_Pati(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM PapPati WHERE dni=?", new Pap_PatiRowMapper(), dni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Pap_Pati> getAllPap_Pati() {
        try {
            return jdbcTemplate.query("SELECT * FROM PapPati",
                    new Pap_PatiRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

