package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class OviUserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addOviUser(OviUser oviUser) {
        jdbcTemplate.update("INSERT INTO OviUser VALUES (?,?,?,?,?,?,?,?,?,?)",
                oviUser.getId(), oviUser.getName(), oviUser.getAddress(), oviUser.getEmail(), oviUser.getEntityThatIsInvolved(),
                oviUser.getTypeOfFunctionalDiversity(), oviUser.getDateOfAcceptance(), oviUser.getStatus(),
                oviUser.getUserAndPassword(), oviUser.getTutorId());
    }

    public void deleteOviUser(OviUser oviUser) {
        jdbcTemplate.update("DELETE FROM OviUser WHERE id=?",
                oviUser.getId());
    }
    public void deleteOviUser(String id) {
        jdbcTemplate.update("DELETE FROM OviUser WHERE id=?",
                id);
    }

    public void updateOviUser(OviUser oviUser) {
        jdbcTemplate.update("UPDATE OviUser SET name=?, address=?, email=?, entityThatIsEnvolved=?, TypeOfFunctionalDiversity=?, dateOfAcceptance=?, status=?, userAndPassword=?, tutorId=? WHERE id=?",
                oviUser.getName(), oviUser.getAddress(), oviUser.getEmail(), oviUser.getEntityThatIsInvolved(),
                oviUser.getTypeOfFunctionalDiversity(), oviUser.getDateOfAcceptance(), oviUser.getStatus(),
                oviUser.getUserAndPassword(), oviUser.getTutorId(), oviUser.getId());
    }

    public OviUser getOviUser(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT FROM OviUser WHERE id=?",
                    new OviUserRowMapper(), id);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<OviUser> getOviUsers() {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM OviUser",
                    new OviUserRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
}
