package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.ListOfProposedCandidates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ListOfProposedCandidatesDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addListOfProposedCandidates(ListOfProposedCandidates listOfProposedCandidates) {
        jdbcTemplate.update("INSERT INTO ListOfProposedCandidates VALUES (?,?,?,?,?)",
                listOfProposedCandidates.getList_id(),
                listOfProposedCandidates.getSuitabilityScore(),
                listOfProposedCandidates.getProposalDate(),
                listOfProposedCandidates.getPappati_id(),
                listOfProposedCandidates.getRequest_id());
    }
    public void deleteListOfProposedCandidates(String list_id) {
        jdbcTemplate.update("DELETE FROM ListOfProposedCandidates WHERE list_id=?",
                list_id);
    }
    public void updateListOfProposedCandidates(ListOfProposedCandidates listOfProposedCandidates) {
        jdbcTemplate.update("UPDATE ListOfProposedCandidates SET suitabilityScore=?, proposalDate=?, pappati_id=?, request_id=? WHERE list_id=?",
                listOfProposedCandidates.getSuitabilityScore(),
                listOfProposedCandidates.getProposalDate(),
                listOfProposedCandidates.getPappati_id(),
                listOfProposedCandidates.getRequest_id(),listOfProposedCandidates.getList_id());
    }

    public ListOfProposedCandidates getListOfProposedCandidates(String list_id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM ListOfProposedCandidates WHERE list_id=?",
                    new ListOfProposedCandidatesRowMapper(), list_id);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<ListOfProposedCandidates> getListOfProposedCandidates() {
        try {
            return jdbcTemplate.query("SELECT * FROM ListOfProposedCandidates",
                    new ListOfProposedCandidatesRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
    
    public void deleteCandidatesByRequestId(String requestId) {
        
        String sql = "DELETE FROM listofproposedcandidates WHERE request_id = ?";

        
        jdbcTemplate.update(sql, requestId);
    }
    public List<ListOfProposedCandidates> getProposalsByRequestId(String requestId) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM ListOfProposedCandidates WHERE request_id = ? ORDER BY suitabilityScore DESC",
                    new ListOfProposedCandidatesRowMapper(),
                    requestId
            );
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<ListOfProposedCandidates> getProposalsByPapPati(String dni) {
        String sql = "SELECT * FROM listofproposedcandidates WHERE pappati_id = ?";
        return jdbcTemplate.query(sql, new ListOfProposedCandidatesRowMapper(), dni);
    }

    public List<ListOfProposedCandidates> getProposalsByRequestIdPaginated(String requestId, int limit, int offset) {
        try {
            
            String sql = "SELECT * FROM ListOfProposedCandidates WHERE request_id = ? ORDER BY list_id LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, new ListOfProposedCandidatesRowMapper(), requestId, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public int countProposalsByRequestId(String requestId) {
        String sql = "SELECT COUNT(*) FROM ListOfProposedCandidates WHERE request_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, requestId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}
