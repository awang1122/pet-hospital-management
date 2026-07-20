package pet.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库配置与连接管理（单例）
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private String url;
    private String user;
    private String password;

    private DatabaseConfig() {
        loadConfig();
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                // 回退到与 src 同级的 db.properties
                input = getClass().getClassLoader()
                        .getResourceAsStream("../../db.properties");
            }
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
            }
            url = props.getProperty("db.url",
                    "jdbc:mysql://localhost:3306/pet_hospital?useSSL=false&serverTimezone=UTC");
            user = props.getProperty("db.user", "root");
            password = props.getProperty("db.password", "");
        } catch (Exception e) {
            // 使用默认值
            url = "jdbc:mysql://localhost:3306/pet_hospital?useSSL=false&serverTimezone=UTC";
            user = "root";
            password = "";
            System.err.println("警告：无法加载 db.properties，使用默认配置");
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** 测试数据库连接是否可用 */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getUrl() { return url; }
}
