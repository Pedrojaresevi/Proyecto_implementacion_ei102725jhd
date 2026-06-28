package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.AssignmentRequest;
import es.uji.ei1027.proyectoOvi.models.Pap_Pati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PapPatiDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addPap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("INSERT INTO PapPati VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                pap_pati.getDni(),pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getPassword(),
                pap_pati.getStartDate(), pap_pati.getEndDate(), pap_pati.getGeographicMobility(),pap_pati.getSkills(),pap_pati.getAssistant_type(),pap_pati.getRejectReason());
    }
    public void deletePap_Pati(String dni) {
        jdbcTemplate.update("DELETE FROM PapPati WHERE dni = ?", dni);
    }

    public void deletePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("DELETE FROM PapPati WHERE dni = ?", pap_pati.getDni());
    }

    public void updatePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("UPDATE PapPati SET name=?, surname=?, dateOfBirth=?, address=?, phone=?, email=?, specificTraining=?, typeOfExperience=?, curriculumVitae=?, status=?, password=?, startDate=?, endDate=?, geographicMobility=?, skills=?,assistant_type=?,rejectReason=? WHERE dni=?",
                pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getPassword(),
                pap_pati.getStartDate(), pap_pati.getEndDate(), pap_pati.getGeographicMobility(),pap_pati.getSkills(),pap_pati.getAssistant_type(),pap_pati.getRejectReason(), pap_pati.getDni());
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
    public List<Pap_Pati> getProposalsForRequest(String requestId) {
        try {
            String sql = "SELECT p.* " +
                    "FROM PapPati p " +
                    "JOIN AssignmentRequest r ON r.request_Id = ? " +
                    "WHERE p.status = 'accepted' " +
                    "AND p.assistant_type = r.typeOfService " +
                    
                    "AND p.startDate <= r.requiredEndAvailability " +
                    "AND p.endDate >= r.requiredStartAvailability " +
                    "AND (" +
                    "  (p.address ILIKE '%' || r.serviceLocation || '%' " +
                    "      OR p.geographicMobility ILIKE '%' || r.serviceLocation || '%') " +
                    "  OR p.specificTraining ILIKE '%' || r.requiredTraining || '%' " +
                    "  OR p.typeOfExperience = r.requiredExperience " +
                    "  OR string_to_array(replace(p.skills, ', ', ','), ',') && string_to_array(replace(r.requiredSkills, ', ', ','), ',')" +
                    ")";

            return jdbcTemplate.query(sql, new Pap_PatiRowMapper(), requestId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }
    
    public List<Pap_Pati> getPapPatiByStatus(String status) {
        try {
            return jdbcTemplate.query("SELECT * FROM PapPati WHERE status=?",
                    new Pap_PatiRowMapper(), status);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    
    public List<Pap_Pati> getAllPap_PatiPaginated(int limit, int offset) {
        String sql = "SELECT * FROM PapPati LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new Pap_PatiRowMapper(), limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countAllPap_Pati() {
        String sql = "SELECT COUNT(*) FROM PapPati";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    
    public List<Pap_Pati> getPapPatiByStatusPaginated(String status, int limit, int offset) {
        String sql = "SELECT * FROM PapPati WHERE status=? ORDER BY dni LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new Pap_PatiRowMapper(), status, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countPapPatiByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM PapPati WHERE status=?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, status);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

}

