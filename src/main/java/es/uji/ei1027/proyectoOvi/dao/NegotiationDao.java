package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
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
        String sql = "INSERT INTO Negotiation (negotiation_id, status, recordofcommunications, message_date, enddate, list_id, hora, emisor_dni) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                negotiation.getNegotiation_Id(),
                negotiation.getStatus(),
                negotiation.getRecordOfComunications(),
                negotiation.getStartDate(), // Seguimos usando el getter original en Java
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
        jdbcTemplate.update("UPDATE Negotiation SET status=?, recordofcommunications=?, message_date=?, endDate=?, list_id=? WHERE negotiation_Id=?",
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
            // CORREGIDO: Cambiado a message_date ASC antes de hora ASC
            String sql = "SELECT * FROM Negotiation WHERE negotiation_Id=? ORDER BY message_date ASC, hora ASC";
            return jdbcTemplate.query(sql, new NegotiationRowMapper(), negotiation_Id);
        } catch (EmptyResultDataAccessException e)  {
            return new java.util.ArrayList<>();
        }
    }

    public Negotiation getNegotiationByListId(String listId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Negotiation WHERE list_id = ? ORDER BY hora DESC LIMIT 1",
                    new NegotiationRowMapper(), listId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer getNegotiationIdByRequestAndAssistant(String requestId, String papPatiDni) {
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
            String sql = "SELECT * FROM Negotiation n " +
                    "WHERE n.hora = (SELECT MAX(hora) FROM Negotiation n2 WHERE n2.negotiation_Id = n.negotiation_Id) " +
                    "AND n.status != 'accepted' " + // <-- AÑADIDO
                    "ORDER BY n.hora DESC";
            return jdbcTemplate.query(sql, new NegotiationRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }


    public List<Negotiation> getNegotiationsByUserPaginated(String oviuserId, int limit, int offset, String statusFilter) {
        try {
            String sql = "SELECT n1.*, (p.name || ' ' || p.surname) AS interlocutorName " +
                    "FROM Negotiation n1 " +
                    "JOIN ListOfProposedCandidates l ON n1.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "JOIN PapPati p ON l.pappati_id = p.dni " +
                    "WHERE r.oviuser_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(oviuserId);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n1.status = ? ";
                params.add(statusFilter);
            }

            sql += "AND NOT EXISTS (" +
                    "    SELECT 1 FROM Negotiation n2 " +
                    "    WHERE n2.negotiation_Id = n1.negotiation_Id " +
                    "    AND (n2.message_date > n1.message_date OR (n2.message_date = n1.message_date AND n2.hora > n1.hora))" +
                    ") " +
                    "ORDER BY n1.message_date DESC, n1.hora DESC LIMIT ? OFFSET ?";

            params.add(limit);
            params.add(offset);

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countNegotiationsByUser(String oviuserId, String statusFilter) {
        try {
            String sql = "SELECT COUNT(DISTINCT n.negotiation_Id) FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.oviuser_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(oviuserId);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n.status = ? ";
                params.add(statusFilter);
            }

            return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }


    public List<Negotiation> getNegotiationsByUser(String oviuserId, String statusFilter) {
        try {
            String sql = "SELECT n.* FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.oviuser_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(oviuserId);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n.status = ? ";
                params.add(statusFilter);
            }

            sql += "AND n.hora = (SELECT MAX(hora) FROM Negotiation n2 WHERE n2.negotiation_Id = n.negotiation_Id) " +
                    "ORDER BY n.hora DESC";

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Negotiation> getNegotiationsByTutorPaginated(String tutorDni, int limit, int offset, String statusFilter) {
        try {
            String sql = "WITH Ranked AS (" +
                    "  SELECT n1.*, (p.name || ' ' || p.surname) AS interlocutorName, " +
                    "  ROW_NUMBER() OVER(PARTITION BY n1.list_id ORDER BY n1.message_date DESC, n1.hora DESC) as rn " +
                    "  FROM Negotiation n1 " +
                    "  JOIN ListOfProposedCandidates l ON n1.list_id = l.list_id " +
                    "  JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "  JOIN PapPati p ON l.pappati_id = p.dni " +
                    "  WHERE r.tutor_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(tutorDni);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n1.status = ? ";
                params.add(statusFilter);
            }

            sql += ") SELECT * FROM Ranked WHERE rn = 1 ORDER BY message_date DESC, hora DESC LIMIT ? OFFSET ?";

            params.add(limit);
            params.add(offset);

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countNegotiationsByTutor(String tutorDni, String statusFilter) {
        try {
            String sql = "SELECT COUNT(DISTINCT n.negotiation_Id) FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "WHERE r.tutor_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(tutorDni);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n.status = ? ";
                params.add(statusFilter);
            }

            return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public List<Negotiation> getNegotiationsByTutor(String tutorDni, String statusFilter) {
        try {
            String sql = "WITH Ranked AS (" +
                    "  SELECT n1.*, " +
                    "  ROW_NUMBER() OVER(PARTITION BY n1.negotiation_Id ORDER BY n1.message_date DESC, n1.hora DESC) as rn " +
                    "  FROM Negotiation n1 " +
                    "  JOIN ListOfProposedCandidates l ON n1.list_id = l.list_id " +
                    "  JOIN AssignmentRequest r ON l.request_Id = r.request_Id " +
                    "  WHERE r.tutor_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(tutorDni);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n1.status = ? ";
                params.add(statusFilter);
            }

            sql += ") SELECT * FROM Ranked WHERE rn = 1 ORDER BY message_date DESC, hora DESC";

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Negotiation> getNegotiationsByPapPati(String papPatiDni, String statusFilter) {
        try {
            String sql = "SELECT n.* FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "WHERE l.pappati_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(papPatiDni);

            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql += "AND n.status = ? ";
                params.add(statusFilter);
            }

            sql += "AND n.hora = (SELECT MAX(hora) FROM Negotiation n2 WHERE n2.negotiation_Id = n.negotiation_Id) " +
                    "ORDER BY n.hora DESC";

            return jdbcTemplate.query(sql, new NegotiationRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public void closeNegotiation(String negotiationId, java.util.Date endDate) {
        String sql = "UPDATE Negotiation SET status = 'accepted', enddate = ? WHERE negotiation_id = ?";
        jdbcTemplate.update(sql, endDate, negotiationId);
    }

    public void updateNegotiationStatus(String negotiationId, String status) {
        String sql = "UPDATE Negotiation SET status = ? WHERE negotiation_id = ?";
        jdbcTemplate.update(sql, status, negotiationId);
    }

    public List<String> getNegotiationIdsByRequestId(String requestId) {
        try {
            String sql = "SELECT DISTINCT n.negotiation_Id FROM Negotiation n " +
                    "JOIN ListOfProposedCandidates l ON n.list_id = l.list_id " +
                    "WHERE l.request_id = ?";
            return jdbcTemplate.queryForList(sql, String.class, requestId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

}