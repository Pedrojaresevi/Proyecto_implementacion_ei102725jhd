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
        jdbcTemplate.update("INSER INTO Contract VALUES (?,?,?,?,?,?,?)",
                contract.getContractId(), contract.getRequestId(), contract.getStartDate(), contract.getEndDate(),
                contract.getStatus(), contract.getPlaceWhereThePDFIsGonnaBeSaved(), contract.getPap_patiID());
    }

    public void deleteContract(Contract contract) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contractId=?",
                contract.getContractId());
    }

    public void deleteContract(String contractId) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contractId=?",
                contractId);
    }

    public void updateContract(Contract contract) {
        jdbcTemplate.update("UPDATE Contract SET requestId=?, startDate=?, endDate=?, status=?, PlaceWhereThePDFIsGonnaBeSaved=?, pap_patiId=? WHERE contractId=?",
                contract.getRequestId(), contract.getStartDate(), contract.getEndDate(),
                contract.getStatus(), contract.getPlaceWhereThePDFIsGonnaBeSaved(), contract.getPap_patiID(), contract.getContractId());
    }

    public Contract getContract(String contractId) {
        try {
            return jdbcTemplate.queryForObject("SELECT FROM Contract WHERE contractId=?",
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

}
