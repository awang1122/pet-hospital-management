package pet.ui;

import pet.dao.PetDAO;
import pet.dao.VisitDAO;
import pet.model.Pet;
import pet.model.Visit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * 信息查询面板 — 按家长信息搜索宠物 + 查看就诊历史
 */
public class SearchPanel extends JPanel {

    private final PetDAO petDAO;
    private final VisitDAO visitDAO;

    private JTextField txtSearchKey;
    private JButton btnSearch;
    private JTable searchResultTable;
    private DefaultTableModel searchTableModel;
    private JTextArea txtVisitHistory;

    public SearchPanel() {
        this.petDAO = new PetDAO();
        this.visitDAO = new VisitDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---- 搜索栏 ----
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("家长姓名 / 电话:"));
        txtSearchKey = new JTextField(20);
        topPanel.add(txtSearchKey);
        btnSearch = new JButton("查询");
        topPanel.add(btnSearch);

        // ---- 搜索结果表格 ----
        searchTableModel = new DefaultTableModel(
                new String[]{"宠物ID", "宠物名", "种类", "品种", "家长", "电话"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        searchResultTable = new JTable(searchTableModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(searchResultTable);

        // ---- 就诊历史 ----
        txtVisitHistory = new JTextArea();
        txtVisitHistory.setEditable(false);
        txtVisitHistory.setFont(new Font("宋体", Font.PLAIN, 14));
        JScrollPane visitScroll = new JScrollPane(txtVisitHistory);
        visitScroll.setBorder(BorderFactory.createTitledBorder("选中宠物的就诊记录"));

        // ---- 分割面板 ----
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, visitScroll);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // ---- 事件 ----
        btnSearch.addActionListener(e -> handleSearch());
        txtSearchKey.addActionListener(e -> handleSearch());  // 回车触发

        searchResultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = searchResultTable.getSelectedRow();
                if (row >= 0) {
                    int petId = (int) searchTableModel.getValueAt(row, 0);
                    showVisitHistory(petId);
                }
            }
        });
    }

    /** 按家长姓名或电话搜索 */
    private void handleSearch() {
        String key = txtSearchKey.getText().trim();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入家长姓名或电话");
            return;
        }
        searchTableModel.setRowCount(0);
        txtVisitHistory.setText("");

        try {
            List<Pet> pets = petDAO.searchByOwner(key);
            if (pets.isEmpty()) {
                JOptionPane.showMessageDialog(this, "未找到匹配的宠物");
                return;
            }
            for (Pet p : pets) {
                Vector<Object> row = new Vector<>();
                row.add(p.getId());
                row.add(p.getName());
                row.add(p.getSpecies());
                row.add(p.getBreed());
                row.add(p.getOwnerName());
                row.add(p.getOwnerPhone());
                searchTableModel.addRow(row);
            }
            // 自动选中第一行
            searchResultTable.setRowSelectionInterval(0, 0);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 显示某宠物的就诊历史 */
    private void showVisitHistory(int petId) {
        StringBuilder sb = new StringBuilder();
        try {
            List<Visit> visits = visitDAO.findByPetId(petId);
            if (visits.isEmpty()) {
                sb.append("（暂无就诊记录）");
            } else {
                for (Visit v : visits) {
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    sb.append("就诊时间：").append(v.getVisitTime()).append("\n");
                    if (v.getDiagnosis() != null && !v.getDiagnosis().isEmpty()) {
                        sb.append("诊    断：").append(v.getDiagnosis()).append("\n");
                    }
                    if (v.getPrescription() != null && !v.getPrescription().isEmpty()) {
                        sb.append("处    方：").append(v.getPrescription()).append("\n");
                    }
                    if (v.getRecord() != null && !v.getRecord().isEmpty()) {
                        sb.append("详细记录：").append(v.getRecord()).append("\n");
                    }
                    sb.append("\n");
                }
                sb.append("共 ").append(visits.size()).append(" 条就诊记录");
            }
        } catch (SQLException e) {
            sb.append("查询就诊记录出错: ").append(e.getMessage());
        }
        txtVisitHistory.setText(sb.toString());
    }
}
