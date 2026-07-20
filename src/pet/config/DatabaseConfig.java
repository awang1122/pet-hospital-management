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
        Properties props = new Properties();

        // 尝试从 classpath 加载
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {}

        // classpath 没找到，尝试从文件系统加载（Eclipse/VSCode 运行兼容）
        if (props.isEmpty()) {
            java.io.File file = new java.io.File("db.properties");
            if (file.exists()) {
                try (java.io.FileInputStream fin = new java.io.FileInputStream(file)) {
                    props.load(fin);
                } catch (Exception ignored) {}
            }
        }

        url = props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/pet_hospital?useSSL=false&serverTimezone=UTC");
        user = props.getProperty("db.user", "root");
        password = props.getProperty("db.password", "");
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
