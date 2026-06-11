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
        jdbcTemplate.update("INSERT INTO Tutor VALUES (?,?,?,?,?,?)",
                tutor.getDni(), tutor.getName(), tutor.getEmail(), tutor.getStatus(),tutor.getPassword(),tutor.getRejectReason());
    }

    public void deleteTutor(Tutor tutor) {
        jdbcTemplate.update("DELETE FROM Tutor WHERE dni=?",
                tutor.getDni());
    }
    public void deleteTutor(String dni) {
        jdbcTemplate.update("DELETE FROM Tutor WHERE dni=?",
                dni);
    }

    public void updateTutor(Tutor tutor) {
        jdbcTemplate.update("UPDATE Tutor SET name=?, email=?, status=?,password=?,rejectReason=? WHERE dni=?",
                tutor.getName(), tutor.getEmail(), tutor.getStatus(),tutor.getPassword(),tutor.getRejectReason(), tutor.getDni());
    }

    public Tutor getTutor(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Tutor WHERE dni=?",
                    new TutorRowMapper(), dni);
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
    //
    public List<Tutor> getTutorsByStatus(String status) {
        try {
            return jdbcTemplate.query("SELECT * FROM Tutor WHERE status=?",
                    new TutorRowMapper(), status);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    // Para la lista general de tutores (/list)
    public List<Tutor> getTutorsPaginated(int limit, int offset) {
        String sql = "SELECT * FROM Tutor LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new TutorRowMapper(), limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countTutors() {
        String sql = "SELECT COUNT(*) FROM Tutor";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // Para la lista de tutores pendientes (/pending)
    public List<Tutor> getTutorsByStatusPaginated(String status, int limit, int offset) {
        String sql = "SELECT * FROM Tutor WHERE status=? ORDER BY dni LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new TutorRowMapper(), status, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countTutorsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Tutor WHERE status=?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, status);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}
