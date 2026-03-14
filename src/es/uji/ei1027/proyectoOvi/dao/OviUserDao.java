package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.OviUser;

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
                oviUser.getDni(), oviUser.getName(), oviUser.getAddress(), oviUser.getEmail(), oviUser.getEntityThatIsInvolved(),
                oviUser.getTypeOfFunctionalDiversity(), oviUser.getDateOfAcceptance(), oviUser.getStatus(),
                oviUser.getUserAndPassword(), oviUser.getTutorId());
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
        jdbcTemplate.update("UPDATE OviUser SET name=?, address=?, email=?, entityThatIsEnvolved=?, TypeOfFunctionalDiversity=?, dateOfAcceptance=?, status=?, userAndPassword=?, tutorId=? WHERE dni=?",
                oviUser.getName(), oviUser.getAddress(), oviUser.getEmail(), oviUser.getEntityThatIsInvolved(),
                oviUser.getTypeOfFunctionalDiversity(), oviUser.getDateOfAcceptance(), oviUser.getStatus(),
                oviUser.getUserAndPassword(), oviUser.getTutorId(), oviUser.getDni());
    }

    public OviUser getOviUser(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT FROM OviUser WHERE dni=?",
                    new OviUserRowMapper(), dni);
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
