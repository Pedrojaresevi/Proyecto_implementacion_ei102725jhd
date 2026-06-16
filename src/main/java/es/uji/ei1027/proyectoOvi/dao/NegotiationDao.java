package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NegotiationDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addNegotiation(Negotiation negotiation) {
        // AÑADIDO: emisor_dni al INSERT y el octavo parámetro (?)
        String sql = "INSERT INTO negotiation (negotiation_id, status, recordofcommunications, startdate, enddate, list_id, hora, emisor_dni) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                negotiation.getNegotiation_Id(),
                negotiation.getStatus(),
                negotiation.getRecordOfComunications(),
                negotiation.getStartDate(),
                negotiation.getEndDate(),
                negotiation.getListId(),
                negotiation.getHora(),
                negotiation.getEmisorDni() // AÑADIDO: Pasamos el DNI del emisor a la BBDD
        );
    }

    public void deleteNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("DELETE FROM Negotiation WHERE negotiation_Id=?",
                negotiation.getNegotiation_Id());
    }

    public void deleteNegotiation(String negotiation_Id) {
        jdbcTemplate.update("DELETE FROM Negotiation WHERE negotiation_Id=?",
                negotiation_Id);
    }

    public void updateNegotiation(Negotiation negotiation) {
        jdbcTemplate.update("UPDATE Negotiation SET status=?, recordofcommunications=?, startDate=?, endDate=?, list_id=? WHERE negotiation_Id=?",
                negotiation.getStatus(),
                negotiation.getRecordOfComunications(),
                negotiation.getStartDate(),
                negotiation.getEndDate(),
                negotiation.getListId(),
                negotiation.getNegotiation_Id()
        );
    }

    public Negotiation getNegotiation(String negotiation_Id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Negotiation WHERE negotiation_Id=? ORDER BY hora DESC LIMIT 1",
                    new NegotiationRowMapper(), negotiation_Id);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<Negotiation> getMessagesByNegotiationId(String negotiation_Id) {
        try {
            // CORREGIDO: Añadido startDate ASC antes de hora ASC
            String sql = "SELECT * FROM Negotiation WHERE negotiation_Id=? ORDER BY startDate ASC, hora ASC";
            return jdbcTemplate.query(sql, new NegotiationRowMapper(), negotiation_Id);
        } catch (EmptyResultDataAccessException e)  {
            return new java.util.ArrayList<>();
        }
    }

//    public List<Negotiation> getNegotiations() {
//        try {
//            return jdbcTemplate.query("SELECT * FROM Negotiation",
//                    new NegotiationRowMapper());
//        } catch (EmptyResultDataAccessException e)  {
//            return null;
//        }
//    }

//    public List<Negotiation> getNegotiationsByUser(String oviuserId) {
//        try {
//            String sql = "SELECT n.* FROM Negotiation n " +
//                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
//                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
//                    "WHERE r.oviuser_id = ?";
//
//            return jdbcTemplate.query(sql, new NegotiationRowMapper(), oviuserId);
//        } catch (EmptyResultDataAccessException e) {
//            return new java.util.ArrayList<>();
//        }
//    }

//    public List<Negotiation> getNegotiationsByTutor(String tutorDni) {
//        try {
//            String sql = "SELECT n.* FROM Negotiation n " +
//                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
//                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
//                    "WHERE r.tutor_id = ?";
//
//            return jdbcTemplate.query(sql, new NegotiationRowMapper(), tutorDni);
//        } catch (EmptyResultDataAccessException e) {
//            return new java.util.ArrayList<>();
//        }
//    }

//    public Negotiation getNegotiationByListId(String listId) {
//        try {
//            return jdbcTemplate.queryForObject(
//                    "SELECT * FROM Negotiation WHERE list_id = ?",
//                    new NegotiationRowMapper(), listId);
//        } catch (EmptyResultDataAccessException e) {
//            return null;
//        }
//    }

    public Negotiation getNegotiationByListId(String listId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Negotiation WHERE list_id = ? ORDER BY hora DESC LIMIT 1",
                    new NegotiationRowMapper(), listId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
//    public List<Negotiation> getNegotiationsByUserPaginated(String oviuserId, int limit, int offset) {
//        try {
//            String sql = "SELECT n.* FROM Negotiation n " +
//                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
//                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
//                    "WHERE r.oviuser_id = ? LIMIT ? OFFSET ?";
//            return jdbcTemplate.query(sql, new NegotiationRowMapper(), oviuserId, limit, offset);
//        } catch (EmptyResultDataAccessException e) {
//            return new java.util.ArrayList<>();
//        }
//    }
//
//    public int countNegotiationsByUser(String oviuserId) {
//        try {
//            String sql = "SELECT COUNT(n.*) FROM Negotiation n " +
//                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
//                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
//                    "WHERE r.oviuser_id = ?";
//            return jdbcTemplate.queryForObject(sql, Integer.class, oviuserId);
//        } catch (EmptyResultDataAccessException e) {
//            return 0;
//        }
//    }
//
//    public List<Negotiation> getNegotiationsByTutorPaginated(String tutorDni, int limit, int offset) {
//        try {
//            String sql = "SELECT n.* FROM Negotiation n " +
//                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
//                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
//                    "WHERE r.tutor_id = ? LIMIT ? OFFSET ?";
//            return jdbcTemplate.query(sql, new NegotiationRowMapper(), tutorDni, limit, offset);
//        } catch (EmptyResultDataAccessException e) {
//            return new java.util.ArrayList<>();
//        }
//    }
//
//    public int countNegotiationsByTutor(String tutorDni) {
//        try {
//            String sql = "SELECT COUNT(n.*) FROM Negotiation n " +
//                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
//                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
//                    "WHERE r.tutor_id = ?";
//            return jdbcTemplate.queryForObject(sql, Integer.class, tutorDni);
//        } catch (EmptyResultDataAccessException e) {
//            return 0;
//        }
//    }

    public Integer getNegotiationIdByRequestAndAssistant(int requestId, String papPatiDni) {
        try {
            String sql = "SELECT n.negotiation_Id FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "WHERE l.request_Id = ? AND l.pap_pati_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, requestId, papPatiDni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer getListIdByRequestAndAssistant(int requestId, String papPatiDni) {
        try {
            String sql = "SELECT list_id FROM ListOfProposedCandidates WHERE request_Id = ? AND pap_pati_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, requestId, papPatiDni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    // ---------------------------------------------------------
    // MÉTODOS GENERALES (Afecta al listado global)
    // ---------------------------------------------------------
    public List<Negotiation> getNegotiations() {
        try {
            // Obtenemos solo el mensaje más reciente de cada negociación
            String sql = "SELECT * FROM Negotiation n " +
                    "WHERE n.hora = (SELECT MAX(hora) FROM Negotiation n2 WHERE n2.negotiation_Id = n.negotiation_Id) " +
                    "ORDER BY n.hora DESC";
            return jdbcTemplate.query(sql, new NegotiationRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    // ---------------------------------------------------------
    // MÉTODOS PARA EL USUARIO (Paginado)
    // ---------------------------------------------------------
    public List<Negotiation> getNegotiationsByUserPaginated(String oviuserId, int limit, int offset) {
        try {
            String sql = "SELECT n1.*, (p.name || ' ' || p.surname) AS interlocutorName " +
                    "FROM Negotiation n1 " +
                    "JOIN ListOfProposedCandidates l ON n1.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "JOIN PapPati p ON l.pappati_id = p.dni " +
                    "WHERE r.oviuser_id = ? " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM Negotiation n2 " +
                    "    WHERE n2.negotiation_Id = n1.negotiation_Id " +
                    "    AND (n2.startDate > n1.startDate OR (n2.startDate = n1.startDate AND n2.hora > n1.hora))" +
                    ") " +
                    "ORDER BY n1.startDate DESC, n1.hora DESC LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, new NegotiationRowMapper(), oviuserId, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countNegotiationsByUser(String oviuserId) {
        try {
            // Contamos los IDs de negociación únicos, no el total de mensajes
            String sql = "SELECT COUNT(DISTINCT n.negotiation_Id) FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.oviuser_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, oviuserId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // ---------------------------------------------------------
    // MÉTODOS PARA EL TUTOR (Paginado) - CORREGIDO CON ROW_NUMBER()
    // ---------------------------------------------------------
    public List<Negotiation> getNegotiationsByTutorPaginated(String tutorDni, int limit, int offset) {
        try {
            String sql = "WITH Ranked AS (" +
                    "  SELECT n1.*, (p.name || ' ' || p.surname) AS interlocutorName, " +
                    "  ROW_NUMBER() OVER(PARTITION BY n1.list_id ORDER BY n1.startDate DESC, n1.hora DESC) as rn " +
                    "  FROM Negotiation n1 " +
                    "  JOIN ListOfProposedCandidates l ON n1.list_id = l.list_id " +
                    "  JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "  JOIN PapPati p ON l.pappati_id = p.dni " +
                    "  WHERE r.tutor_id = ?" +
                    ") " +
                    "SELECT * FROM Ranked WHERE rn = 1 ORDER BY startDate DESC, hora DESC LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, new NegotiationRowMapper(), tutorDni, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countNegotiationsByTutor(String tutorDni) {
        try {
            // Contamos los IDs de negociación únicos
            String sql = "SELECT COUNT(DISTINCT n.negotiation_Id) FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.tutor_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, tutorDni);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public List<Negotiation> getNegotiationsByUser(String oviuserId) {
        try {
            String sql = "SELECT n.* FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.oviuser_id = ? " +
                    "AND n.hora = (SELECT MAX(hora) FROM Negotiation n2 WHERE n2.negotiation_Id = n.negotiation_Id) " +
                    "ORDER BY n.hora DESC";
            return jdbcTemplate.query(sql, new NegotiationRowMapper(), oviuserId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    // ---------------------------------------------------------
    // MÉTODO EXTRA PARA EL TUTOR (Sin paginar) - CORREGIDO CON ROW_NUMBER()
    // ---------------------------------------------------------
    public List<Negotiation> getNegotiationsByTutor(String tutorDni) {
        try {
            String sql = "WITH Ranked AS (" +
                    "  SELECT n1.*, " +
                    "  ROW_NUMBER() OVER(PARTITION BY n1.negotiation_Id ORDER BY n1.startDate DESC, n1.hora DESC) as rn " +
                    "  FROM Negotiation n1 " +
                    "  JOIN ListOfProposedCandidates l ON n1.list_id = l.list_id " +
                    "  JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "  WHERE r.tutor_id = ?" +
                    ") " +
                    "SELECT * FROM Ranked WHERE rn = 1 ORDER BY startDate DESC, hora DESC";
            return jdbcTemplate.query(sql, new NegotiationRowMapper(), tutorDni);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Negotiation> getNegotiationsByPapPati(String papPatiDni) {
        try {
            String sql = "SELECT n.* FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "WHERE l.pappati_id = ? " +
                    "AND n.hora = (SELECT MAX(hora) FROM Negotiation n2 WHERE n2.negotiation_Id = n.negotiation_Id) " +
                    "ORDER BY n.hora DESC";

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), papPatiDni);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

}