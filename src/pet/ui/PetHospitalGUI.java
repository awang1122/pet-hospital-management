package pet.ui;

import pet.config.DatabaseConfig;

import javax.swing.*;

/**
 * 宠物医院就诊管理系统 — 主窗口
 *
 * <p>入口类，组装三个功能标签页：
 * <ul>
 *   <li>宠物信息管理 — 增删改查宠物档案</li>
 *   <li>就诊记录管理 — 添加/查看/删除就诊记录（含诊断、处方）</li>
 *   <li>信息查询 — 按家长姓名/电话检索宠物及就诊历史</li>
 * </ul>
 *
 * @author 昂翁曲绕 24090057
 * @version 2.0
 */
public class PetHospitalGUI extends JFrame {

    private PetPanel petPanel;
    private VisitPanel visitPanel;
    private SearchPanel searchPanel;

    public PetHospitalGUI() {
        setTitle("宠物医院就诊管理系统");
        setSize(960, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化数据库连接
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            if (!dbConfig.testConnection()) {
                JOptionPane.showMessageDialog(this,
                        "数据库连接失败，请检查 db.properties 配置。\n" +
                        "URL: " + dbConfig.getUrl(),
                        "连接失败", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "数据库驱动加载失败: " + e.getMessage(),
                    "启动失败", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 创建三个面板
        petPanel = new PetPanel();
        visitPanel = new VisitPanel();
        searchPanel = new SearchPanel();

        // 标签页
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("宠物信息管理", petPanel);
        tabbedPane.addTab("就诊记录管理", visitPanel);
        tabbedPane.addTab("信息查询", searchPanel);

        // 切换标签页时刷新数据
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) {
                petPanel.refreshTable();
            } else if (index == 1) {
                visitPanel.refreshPetComboBox();
            }
        });

        add(tabbedPane);

        // 启动时加载就诊面板的宠物下拉框
        visitPanel.refreshPetComboBox();
    }

    public static void main(String[] args) {
        // 使用系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new PetHospitalGUI().setVisible(true));
    }
}
