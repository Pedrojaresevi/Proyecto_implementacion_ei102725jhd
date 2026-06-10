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
        jdbcTemplate.update("INSERT INTO PapPati VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                pap_pati.getDni(),pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getPassword(),
                pap_pati.getStartDate(), pap_pati.getEndDate(), pap_pati.getGeographicMobility(),pap_pati.getSkills(),pap_pati.getAssistant_type());
    }
    public void deletePap_Pati(String dni) {
        jdbcTemplate.update("DELETE FROM PapPati WHERE dni = ?", dni);
    }

    public void deletePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("DELETE FROM PapPati WHERE dni = ?", pap_pati.getDni());
    }

    public void updatePap_Pati(Pap_Pati pap_pati) {
        jdbcTemplate.update("UPDATE PapPati SET name=?, surname=?, dateOfBirth=?, address=?, phone=?, email=?, specificTraining=?, typeOfExperience=?, curriculumVitae=?, status=?, password=?, startDate=?, endDate=?, geographicMobility=?, skills=?,assistant_type=? WHERE dni=?",
                pap_pati.getName(), pap_pati.getSurname(), pap_pati.getDateOfBirth(), pap_pati.getAddress(), pap_pati.getPhone(), pap_pati.getEmail(),
                pap_pati.getSpecificTraining(), pap_pati.getTypeOfExperience(), pap_pati.getCurriculumVitae(), pap_pati.getStatus(), pap_pati.getPassword(),
                pap_pati.getStartDate(), pap_pati.getEndDate(), pap_pati.getGeographicMobility(),pap_pati.getSkills(),pap_pati.getAssistant_type(), pap_pati.getDni());
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
    //Metodo para obtener los candidatos propuestos para una solicitud
//    public List<Pap_Pati> getProposalsForRequest(String requestId) {
//        try {
//            // Consulta que cruza la solicitud con los candidatos
//            String sql = "SELECT p.* " +
//                    "FROM PapPati p " +
//                    "JOIN AssignmentRequest r ON r.request_Id = ? " +
//                    "WHERE p.status = 'accepted' " + // Solo activos
//                    // 1. Disponibilidad: El candidato debe empezar antes o igual y terminar después o igual que la petición
//                    "AND p.startDate <= r.requiredStartAvailability " +
//                    "AND p.endDate >= r.requiredEndAvailability " +
//                    // 2. Proximidad: Que la ciudad coincida o que el candidato tenga movilidad total
//                    // Usamos ILIKE para que no importe mayúsculas/minúsculas y % para búsqueda parcial
//                    "AND (p.address ILIKE '%' || r.serviceLocation || '%' OR p.geographicMobility = 'Total') " +
//                    // 3. Preferencias/Habilidades: Que el candidato tenga alguna de las habilidades requeridas
//                    "AND (p.skills ILIKE '%' || r.requiredSkills || '%' OR p.specificTraining ILIKE '%' || r.requiredTraining || '%')";
//
//            return jdbcTemplate.query(sql, new Pap_PatiRowMapper(), requestId);
//        } catch (EmptyResultDataAccessException e) {
//            return new java.util.ArrayList<>();
//        }
//    }
//    public List<Pap_Pati> getProposalsForRequest(String requestId) {
//        try {
//            String sql = "SELECT p.* " +
//                    "FROM PapPati p, AssignmentRequest r " +
//                    "WHERE r.request_Id = ? " +
//                    // Solo candidatos activos
//                    "AND p.status = 'accepted' " +
//                    // 1. Disponibilidad
//                    "AND p.startDate <= r.requiredStartAvailability " +
//                    "AND p.endDate >= r.requiredEndAvailability " +
//                    // 2. Movilidad: ciudad coincide O la provincia del candidato coincide con serviceLocation
//                    "AND (p.address ILIKE '%' || r.serviceLocation || '%' " +
//                    "     OR p.geographicMobility ILIKE '%' || r.serviceLocation || '%') " +
//                    // 3. Habilidades
//                    "AND p.skills ILIKE '%' || r.requiredSkills || '%' " +
//                    // 4. Experiencia
//                    "AND p.typeOfExperience = r.requiredExperience " +
//                    // 5. Formación específica
//                    "AND p.specificTraining ILIKE '%' || r.requiredTraining || '%'";
//
//            return jdbcTemplate.query(sql, new Pap_PatiRowMapper(), requestId);
//        } catch (EmptyResultDataAccessException e) {
//            return new java.util.ArrayList<>();
//        }
//    }
    public List<Pap_Pati> getProposalsForRequest(String requestId) {
        try {
            String sql = "SELECT p.* " +
                    "FROM PapPati p, AssignmentRequest r " +
                    "WHERE r.request_Id = ? " +
                    "AND p.status = 'accepted' " +
                    "AND (" +
                    "  (p.startDate <= r.requiredStartAvailability AND p.endDate >= r.requiredEndAvailability) " +
                    "  OR (p.address ILIKE '%' || r.serviceLocation || '%' " +
                    "      OR p.geographicMobility ILIKE '%' || r.serviceLocation || '%') " +
                    "  OR p.specificTraining ILIKE '%' || r.requiredTraining || '%' " +
                    "  OR p.typeOfExperience = r.requiredExperience " +
                    "  OR p.skills ILIKE '%' || r.requiredSkills || '%' " +
                    ")";

            return jdbcTemplate.query(sql, new Pap_PatiRowMapper(), requestId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }
    //
    public List<Pap_Pati> getPapPatiByStatus(String status) {
        try {
            return jdbcTemplate.query("SELECT * FROM PapPati WHERE status=?",
                    new Pap_PatiRowMapper(), status);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    // Para la lista general de asistentes
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

    // Para las listas filtradas por estado (pending/accepted)
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

