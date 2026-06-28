package es.uji.ei1027.proyectoOvi.dao;

import es.uji.ei1027.proyectoOvi.models.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContractDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    
    public void addContract(Contract contract) {
        String sql = "INSERT INTO Contract (contract_id, startdate, enddate, status, placewherethepdfisgonnabesaved, request_id, pappati_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                contract.getContract_Id(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getStatus(),
                contract.getPlaceWhereThePDFIsGonnaBeSaved(),
                contract.getRequest_Id(),
                contract.getPappati_id());
    }

    
    public void deleteContract(Contract contract) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contract_id=?", contract.getContract_Id());
    }

    
    public void deleteContract(String contractId) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contract_id=?", contractId);
    }

    
    public void updateContract(Contract contract) {
        String sql = "UPDATE Contract SET request_id=?, startdate=?, enddate=?, status=?, placewherethepdfisgonnabesaved=?, pappati_id=? WHERE contract_id=?";
        jdbcTemplate.update(sql,
                contract.getRequest_Id(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getStatus(),
                contract.getPlaceWhereThePDFIsGonnaBeSaved(),
                contract.getPappati_id(),
                contract.getContract_Id());
    }

    
    public Contract getContract(String contractId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Contract WHERE contract_id=?",
                    new ContractRowMapper(), contractId); 
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    
    public List<Contract> getContracts() {
        try {
            return jdbcTemplate.query("SELECT * FROM Contract", new ContractRowMapper()); 
        } catch (EmptyResultDataAccessException e)  {
            return new ArrayList<>();
        }
    }

    
    public List<Contract> getContractsByUser(String dni) {
        String sql = "SELECT c.* " +
                "FROM Contract c " +
                "JOIN AssignmentRequest ar ON c.request_id = ar.request_id " +
                "WHERE c.pappati_id = ? OR ar.oviuser_id = ? OR ar.tutor_id = ? " +
                "ORDER BY SUBSTRING(c.contract_id FROM 4)::INTEGER DESC";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), dni, dni, dni); 
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    
    public String generateNextContractId() {
        try {
            String sql = "SELECT MAX(contract_id) FROM Contract";
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

    
    public String getLastContractId() {
        try {
            String sql = "SELECT contract_id FROM Contract ORDER BY contract_id DESC LIMIT 1";
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    
    public List<Contract> getContractsByUserPaginated(String dni, int limit, int offset) {
        String sql = "SELECT c.* " +
                "FROM Contract c " +
                "JOIN AssignmentRequest ar ON c.request_id = ar.request_id " +
                "WHERE c.pappati_id = ? OR ar.oviuser_id = ? OR ar.tutor_id = ? " +
                "ORDER BY SUBSTRING(c.contract_id FROM 4)::INTEGER DESC " +
                "LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), dni, dni, dni, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    
    public int countContractsByUser(String dni) {
        String sql = "SELECT COUNT(c.contract_id) " +
                "FROM Contract c " +
                "JOIN AssignmentRequest ar ON c.request_id = ar.request_id " +
                "WHERE c.pappati_id = ? OR ar.oviuser_id = ? OR ar.tutor_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, dni, dni, dni);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public List<Contract> getContractsByRequestId(String requestId) {
        String sql = "SELECT * FROM Contract WHERE request_id = ? ORDER BY SUBSTRING(contract_id FROM 4)::INTEGER DESC";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), requestId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    
    public List<Contract> getContractsByRequestIdPaginated(String requestId, int limit, int offset) {
        String sql = "SELECT * FROM Contract WHERE request_id = ? ORDER BY SUBSTRING(contract_id FROM 4)::INTEGER DESC LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), requestId, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    
    public int countContractsByRequestId(String requestId) {
        String sql = "SELECT COUNT(*) FROM Contract WHERE request_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, requestId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public int finalizeExpiredContracts() {
        String sql = "UPDATE Contract SET status = 'finalized' WHERE status != 'finalized' AND enddate < CURRENT_DATE";
        return jdbcTemplate.update(sql);
    }
}