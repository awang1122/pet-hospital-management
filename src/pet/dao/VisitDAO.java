package pet.dao;

import pet.config.DatabaseConfig;
import pet.model.Visit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 就诊记录数据访问对象 — 封装 visit 表所有 CRUD 操作
 */
public class VisitDAO {

    private final DatabaseConfig dbConfig;

    public VisitDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /** 添加就诊记录 */
    public int insert(Visit visit) throws SQLException {
        String sql = "INSERT INTO visit (pet_id, visit_time, diagnosis, prescription, record) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, visit.getPetId());
            pst.setTimestamp(2, visit.getVisitTime());
            pst.setString(3, visit.getDiagnosis());
            pst.setString(4, visit.getPrescription());
            pst.setString(5, visit.getRecord());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /** 删除就诊记录 */
    public boolean delete(int visitId) throws SQLException {
        String sql = "DELETE FROM visit WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, visitId);
            return pst.executeUpdate() > 0;
        }
    }

    /** 查询某宠物的全部就诊记录（按时间倒序） */
    public List<Visit> findByPetId(int petId) throws SQLException {
        String sql = "SELECT * FROM visit WHERE pet_id = ? ORDER BY visit_time DESC";
        List<Visit> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, petId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToVisit(rs));
                }
            }
        }
        return list;
    }

    /** 查询最近 N 条就诊记录（用于概览） */
    public List<Visit> findRecent(int limit) throws SQLException {
        String sql = "SELECT v.*, p.name AS pet_name FROM visit v " +
                     "JOIN pet p ON v.pet_id = p.id ORDER BY v.visit_time DESC LIMIT ?";
        List<Visit> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, limit);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToVisit(rs));
                }
            }
        }
        return list;
    }

    /** 查询全部就诊记录 */
    public List<Visit> findAll() throws SQLException {
        String sql = "SELECT * FROM visit ORDER BY visit_time DESC";
        List<Visit> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rowToVisit(rs));
            }
        }
        return list;
    }

    private Visit rowToVisit(ResultSet rs) throws SQLException {
        Visit v = new Visit();
        v.setId(rs.getInt("id"));
        v.setPetId(rs.getInt("pet_id"));
        v.setVisitTime(rs.getTimestamp("visit_time"));
        v.setDiagnosis(rs.getString("diagnosis"));
        v.setPrescription(rs.getString("prescription"));
        v.setRecord(rs.getString("record"));
        return v;
    }
}
