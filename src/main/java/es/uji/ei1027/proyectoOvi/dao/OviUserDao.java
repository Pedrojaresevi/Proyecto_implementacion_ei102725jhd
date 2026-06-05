package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OviUserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addOviUser(OviUser oviUser) {
        jdbcTemplate.update("INSERT INTO OviUser VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                oviUser.getDni(), oviUser.getName(), oviUser.getAddress(), oviUser.getEmail(), oviUser.getEntityThatIsInvolved(),
                oviUser.getTypeOfFunctionalDiversity(), oviUser.getDateOfAcceptance(),
                oviUser.getPassword(),
                oviUser.getStatus(),
                oviUser.getLifePlan(),
                oviUser.getTutor_id(),
                oviUser.getDateOfBirth());
    }

    public void deleteOviUser(OviUser oviUser) {
        jdbcTemplate.update("DELETE FROM OviUser WHERE dni=?",
                oviUser.getDni());
    }
    public void deleteOviUser(String dni) {
        jdbcTemplate.update("DELETE FROM OviUser WHERE dni=?",
                dni);
    }

    public void updateOviUser(OviUser oviUser) {
        jdbcTemplate.update("UPDATE OviUser SET name=?, address=?, email=?, entityThatIsInvolved=?, typeOfFunctionalDiversity=?, dateOfAcceptance=?, userAndPassword=?, status=?,lifePlan=?, tutor_id=?, dateOfBirth=? WHERE dni=?",
                oviUser.getName(), oviUser.getAddress(), oviUser.getEmail(), oviUser.getEntityThatIsInvolved(),
                oviUser.getTypeOfFunctionalDiversity(), oviUser.getDateOfAcceptance(),
                oviUser.getPassword(), oviUser.getStatus(),
                oviUser.getLifePlan(),
                oviUser.getTutor_id(), oviUser.getDni(), oviUser.getDateOfBirth());
    }

    public OviUser getOviUser(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM OviUser WHERE dni=?",
                    new OviUserRowMapper(), dni);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<OviUser> getOviUsers() {
        try {
            return jdbcTemplate.query("SELECT * FROM OviUser",
                    new OviUserRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<OviUser> getOviUsersByTutor(String tutorDni) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM OviUser WHERE tutor_id = ?",
                    new OviUserRowMapper(),
                    tutorDni
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<OviUser>();
        }
    }
    public List<OviUser> getOviUsersByStatus(String status) {
        try {
            return jdbcTemplate.query("SELECT * FROM OviUser WHERE status=?",
                    new OviUserRowMapper(), status);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    // Para la lista de tutorizados de un tutor (/users/{dni})
    public List<OviUser> getOviUsersByTutorPaginated(String tutorDni, int limit, int offset) {
        String sql = "SELECT * FROM OviUser WHERE tutor_id = ? LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new OviUserRowMapper(), tutorDni, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countOviUsersByTutor(String tutorDni) {
        String sql = "SELECT COUNT(*) FROM OviUser WHERE tutor_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, tutorDni);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}
