package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TutorDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addTutor(Tutor tutor) {
        jdbcTemplate.update("INSERT INTO Tutor VALUES (?,?,?,?)",
                tutor.getId(), tutor.getName(), tutor.getEmail(), tutor.getStatus());
    }

    public void deleteTutor(Tutor tutor) {
        jdbcTemplate.update("DELETE FROM Tutor WHERE id=?",
                tutor.getId());
    }
    public void deleteTutor(String id) {
        jdbcTemplate.update("DELETE FROM Tutor WHERE id=?",
                id);
    }

    public void updateTutor(Tutor tutor) {
        jdbcTemplate.update("UPDATE Tutor SET name=?, email=?, status=? WHERE id=?",
                tutor.getName(), tutor.getEmail(), tutor.getStatus(), tutor.getId());
    }

    public Tutor getTutor(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Tutor WHERE id=?",
                    new TutorRowMapper(), id);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<Tutor> getTutors() {
        try {
            return jdbcTemplate.query("SELECT * FROM Tutor",
                    new TutorRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
}
