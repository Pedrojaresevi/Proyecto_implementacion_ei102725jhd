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

    // 1. AÑADIR CONTRATO
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

    // 2. BORRAR CONTRATO POR OBJETO
    public void deleteContract(Contract contract) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contract_id=?", contract.getContract_Id());
    }

    // 3. BORRAR CONTRATO POR ID
    public void deleteContract(String contractId) {
        jdbcTemplate.update("DELETE FROM Contract WHERE contract_id=?", contractId);
    }

    // 4. ACTUALIZAR CONTRATO
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

    // 5. OBTENER UN CONTRATO POR ID
    public Contract getContract(String contractId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Contract WHERE contract_id=?",
                    new ContractRowMapper(), contractId); // Usa tu archivo externo
        } catch (EmptyResultDataAccessException e)  {
            return null;
        }
    }

    // 6. OBTENER TODOS LOS CONTRATOS
    public List<Contract> getContracts() {
        try {
            return jdbcTemplate.query("SELECT * FROM Contract", new ContractRowMapper()); // Usa tu archivo externo
        } catch (EmptyResultDataAccessException e)  {
            return new ArrayList<>();
        }
    }

    // 7. OBTENER CONTRATOS POR USUARIO
    public List<Contract> getContractsByUser(String dni) {
        String sql = "SELECT c.* " +
                "FROM Contract c " +
                "JOIN AssignmentRequest ar ON c.request_id = ar.request_id " +
                "WHERE c.pappati_id = ? OR ar.oviuser_id = ? OR ar.tutor_id = ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), dni, dni, dni); // Usa tu archivo externo
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // 8. GENERAR EL PRÓXIMO ID
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

    // 9. OBTENER EL ÚLTIMO ID DIRECTAMENTE
    public String getLastContractId() {
        try {
            String sql = "SELECT contract_id FROM Contract ORDER BY contract_id DESC LIMIT 1";
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // OBTENER CONTRATOS POR USUARIO PAGINADOS
    public List<Contract> getContractsByUserPaginated(String dni, int limit, int offset) {
        String sql = "SELECT c.* " +
                "FROM Contract c " +
                "JOIN AssignmentRequest ar ON c.request_id = ar.request_id " +
                "WHERE c.pappati_id = ? OR ar.oviuser_id = ? OR ar.tutor_id = ? " +
                "LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), dni, dni, dni, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // CONTAR CONTRATOS POR USUARIO
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
    // 10. OBTENER CONTRATOS POR REQUEST_ID
    public List<Contract> getContractsByRequestId(String requestId) {
        String sql = "SELECT * FROM Contract WHERE request_id = ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), requestId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // 11. OBTENER CONTRATOS POR REQUEST_ID PAGINADOS
    public List<Contract> getContractsByRequestIdPaginated(String requestId, int limit, int offset) {
        String sql = "SELECT * FROM Contract WHERE request_id = ? LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new ContractRowMapper(), requestId, limit, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // 12. CONTAR CONTRATOS POR REQUEST_ID
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