package pet.dao;

import pet.config.DatabaseConfig;
import pet.model.Pet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 宠物数据访问对象 — 封装 pet 表所有 CRUD 操作
 */
public class PetDAO {

    private final DatabaseConfig dbConfig;

    public PetDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /** 添加宠物，返回生成的 ID */
    public int insert(Pet pet) throws SQLException {
        String sql = "INSERT INTO pet (name, species, birthday, breed, weight, owner_name, owner_phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, pet.getName());
            pst.setString(2, pet.getSpecies());
            pst.setDate(3, pet.getBirthday());
            pst.setString(4, pet.getBreed());
            pst.setDouble(5, pet.getWeight());
            pst.setString(6, pet.getOwnerName());
            pst.setString(7, pet.getOwnerPhone());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /** 更新宠物信息 */
    public boolean update(Pet pet) throws SQLException {
        String sql = "UPDATE pet SET name=?, species=?, birthday=?, breed=?, " +
                     "weight=?, owner_name=?, owner_phone=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, pet.getName());
            pst.setString(2, pet.getSpecies());
            pst.setDate(3, pet.getBirthday());
            pst.setString(4, pet.getBreed());
            pst.setDouble(5, pet.getWeight());
            pst.setString(6, pet.getOwnerName());
            pst.setString(7, pet.getOwnerPhone());
            pst.setInt(8, pet.getId());
            return pst.executeUpdate() > 0;
        }
    }

    /** 删除宠物（级联删除其就诊记录） */
    public boolean delete(int petId) throws SQLException {
        String sql = "DELETE FROM pet WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, petId);
            return pst.executeUpdate() > 0;
        }
    }

    /** 查询全部宠物 */
    public List<Pet> findAll() throws SQLException {
        String sql = "SELECT * FROM pet ORDER BY id";
        List<Pet> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rowToPet(rs));
            }
        }
        return list;
    }

    /** 按 ID 查询 */
    public Pet findById(int id) throws SQLException {
        String sql = "SELECT * FROM pet WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rowToPet(rs);
            }
        }
        return null;
    }

    /** 按家长姓名或电话模糊搜索 */
    public List<Pet> searchByOwner(String keyword) throws SQLException {
        String sql = "SELECT * FROM pet WHERE owner_name LIKE ? OR owner_phone LIKE ? ORDER BY id";
        List<Pet> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            pst.setString(1, kw);
            pst.setString(2, kw);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToPet(rs));
                }
            }
        }
        return list;
    }

    /** 获取所有宠物 ID 和名字（供下拉框用） */
    public List<Pet> findAllIdAndName() throws SQLException {
        String sql = "SELECT id, name, species FROM pet ORDER BY id";
        List<Pet> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pet p = new Pet();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setSpecies(rs.getString("species"));
                list.add(p);
            }
        }
        return list;
    }

    /** 检查家长电话是否存在（可用于去重提示） */
    public List<Pet> findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM pet WHERE owner_phone = ?";
        List<Pet> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, phone);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToPet(rs));
                }
            }
        }
        return list;
    }

    private Pet rowToPet(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setSpecies(rs.getString("species"));
        p.setBirthday(rs.getDate("birthday"));
        p.setBreed(rs.getString("breed"));
        p.setWeight(rs.getDouble("weight"));
        p.setOwnerName(rs.getString("owner_name"));
        p.setOwnerPhone(rs.getString("owner_phone"));
        return p;
    }
}
