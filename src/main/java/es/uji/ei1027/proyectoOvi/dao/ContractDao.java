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
    public List<Contract> getContractsByUser(String dni) {
        // Hacemos un JOIN con assignmentrequest para poder comprobar los 3 tipos de DNI
        String sql = "SELECT c.* " +
                "FROM contract c " +
                "JOIN assignmentrequest ar ON c.request_id = ar.request_id " +
                "WHERE c.pappati_id = ? OR ar.oviuser_id = ? OR ar.tutor_id = ?";

        // Pasamos el DNI 3 veces, una para cada interrogación (?)
        return jdbcTemplate.query(sql, new ContractRowMapper(), dni, dni, dni);
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

    public List<Contract> getContractsPaginated(int limit, int offset) {
        String sql = "SELECT * FROM Contract LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new java.util.ArrayList<Contract>();
        }
    }

    public int countContracts() {
        String sql = "SELECT COUNT(*) FROM Contract";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

}
