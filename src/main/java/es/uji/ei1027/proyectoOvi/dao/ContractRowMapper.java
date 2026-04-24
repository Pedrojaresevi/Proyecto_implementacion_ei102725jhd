package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Contract;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ContractRowMapper implements RowMapper<Contract> {
    public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contract contract = new Contract();
        contract.setContract_Id("contractId");
        contract.setRequest_Id("request_Id");
        contract.setStartDate(rs.getDate("startDate"));
        contract.setEndDate(rs.getDate("endDate"));
        contract.setPlaceWhereThePDFIsGonnaBeSaved(rs.getString("PlaceWhereThePDFIsGonnaBeSaved"));
        contract.setStatus(rs.getString("status"));
        contract.setPappati_id(rs.getString("pappati_id"));
        return contract;
    }
}
