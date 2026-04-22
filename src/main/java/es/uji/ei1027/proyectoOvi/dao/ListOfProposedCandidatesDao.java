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
        jdbcTemplate.update("INSERT INTO OviUser VALUES (?,?,?,?,?)",
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
}
