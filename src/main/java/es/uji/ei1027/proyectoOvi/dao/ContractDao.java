package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ContractDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addContract(Contract contract) {
        jdbcTemplate.update("INSERT INTO Contract VALUES (?,?,?,?,?,?,?)",
                contract.getContract_Id(), contract.getStartDate(), contract.getEndDate(),
                contract.getStatus(), contract.getPlaceWhereThePDFIsGonnaBeSaved(), contract.getRequest_Id(), contract.getPappati_id());
    }

    public void deleteContract(Contract contract) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contract_Id=?",
                contract.getContract_Id());
    }

    public void deleteContract(String contractId) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contract_Id=?",
                contractId);
    }

    public void updateContract(Contract contract) {
        jdbcTemplate.update("UPDATE Contract SET request_Id=?, startDate=?, endDate=?, status=?, PlaceWhereThePDFIsGonnaBeSaved=?, pappati_id=? WHERE contract_Id=?",
                contract.getRequest_Id(), contract.getStartDate(), contract.getEndDate(),
                contract.getStatus(), contract.getPlaceWhereThePDFIsGonnaBeSaved(), contract.getPappati_id(), contract.getContract_Id());
    }

    public Contract getContract(String contractId) {
        try {
            return jdbcTemplate.queryForObject("SELECT *  FROM Contract WHERE contract_Id=?",
                    new ContractRowMapper(), contractId);
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    public List<Contract> getContracts() {
        try {
            return jdbcTemplate.query("SELECT * FROM Contract",
                    new ContractRowMapper());
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }
    //
    public List<Contract> getContractsByUser(String oviuserId) {
        try {
            // Unimos Contract con AssignmentRequest usando el request_Id
            String sql = "SELECT c.* FROM Contract c " +
                    "JOIN AssignmentRequest r ON r.request_id = c.request_id " +
                    "WHERE r.oviuser_id = ?";
            return jdbcTemplate.query(sql, new ContractRowMapper(), oviuserId);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    public String generateNextContractId() {
        try {
            String sql = "SELECT MAX(contract_Id) FROM Contract";
            String maxId = jdbcTemplate.queryForObject(sql, String.class);

            if (maxId == null) {
                return "CTR001";
            }

            int num = Integer.parseInt(maxId.substring(3));
            num++;

            return String.format("CTR%03d", num);

        } catch (Exception e) {
            return "CTR001";
        }
    }

}
