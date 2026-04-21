package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.ListOfProposedCandidates;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ListOfProposedCandidatesRowMapper implements RowMapper<ListOfProposedCandidates> {
    public ListOfProposedCandidates mapRow(ResultSet rs, int rowNum) throws SQLException {
        ListOfProposedCandidates listOfProposedCandidates = new ListOfProposedCandidates();
        listOfProposedCandidates.setList_id(rs.getString("list_id"));
        listOfProposedCandidates.setSuitabilityScore(rs.getFloat("suitabilityScore"));
        listOfProposedCandidates.setProposalDate(rs.getDate("proposalDate"));
        listOfProposedCandidates.setPappati_id(rs.getString("pappati_id"));
        listOfProposedCandidates.setRequest_id(rs.getString("request_id"));
        return listOfProposedCandidates;
    }
}
