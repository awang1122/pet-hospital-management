package pet.ui;

import pet.dao.PetDAO;
import pet.dao.VisitDAO;
import pet.model.Pet;
import pet.model.Visit;
import pet.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

/**
 * 就诊记录管理面板 — 添加、查看、删除就诊记录
 */
public class VisitPanel extends JPanel {

    private final VisitDAO visitDAO;
    private final PetDAO petDAO;

    // 宠物选择下拉框（显示"ID - 名字 (种类)"）
    private JComboBox<PetItem> cmbPet;
    private JTextField txtVisitTime;
    private JTextArea txtDiagnosis, txtPrescription, txtRecord;
    private JButton btnAddVisit, btnDeleteVisit;

    // 就诊记录表格
    private JTable visitTable;
    private DefaultTableModel visitTableModel;

    public VisitPanel() {
        this.visitDAO = new VisitDAO();
        this.petDAO = new PetDAO();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---- 输入区 ----
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("新就诊记录"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        // 宠物选择
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(new JLabel("选择宠物:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbPet = new JComboBox<>();
        inputPanel.add(cmbPet, gbc);

        // 就诊时间
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(new JLabel("就诊时间:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtVisitTime = new JTextField("2026-07-20 10:00");
        inputPanel.add(txtVisitTime, gbc);

        // 诊断
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        inputPanel.add(new JLabel("诊断结果:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtDiagnosis = new JTextArea(2, 20);
        txtDiagnosis.setLineWrap(true);
        inputPanel.add(new JScrollPane(txtDiagnosis), gbc);

        // 处方/药品
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        inputPanel.add(new JLabel("处方/药品:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPrescription = new JTextArea(2, 20);
        txtPrescription.setLineWrap(true);
        inputPanel.add(new JScrollPane(txtPrescription), gbc);

        // 详细记录
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        inputPanel.add(new JLabel("详细记录:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtRecord = new JTextArea(3, 20);
        txtRecord.setLineWrap(true);
        inputPanel.add(new JScrollPane(txtRecord), gbc);

        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAddVisit = new JButton("添加就诊记录");
        btnDeleteVisit = new JButton("删除选中记录");
        btnPanel.add(btnAddVisit);
        btnPanel.add(btnDeleteVisit);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        // ---- 表格区 ----
        visitTableModel = new DefaultTableModel(
                new String[]{"ID", "宠物ID", "就诊时间", "诊断", "处方/药品", "记录"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        visitTable = new JTable(visitTableModel);
        visitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(visitTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ---- 事件 ----
        btnAddVisit.addActionListener(e -> handleAddVisit());
        btnDeleteVisit.addActionListener(e -> handleDeleteVisit());

        // 切换宠物时刷新就诊记录表格
        cmbPet.addActionListener(e -> {
            PetItem item = (PetItem) cmbPet.getSelectedItem();
            if (item != null) {
                refreshVisitTable(item.petId);
            }
        });
    }

    /** 添加就诊记录 */
    private void handleAddVisit() {
        PetItem item = (PetItem) cmbPet.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个宠物");
            return;
        }

        String visitTimeStr = txtVisitTime.getText().trim();
        String diagnosis = txtDiagnosis.getText().trim();
        String prescription = txtPrescription.getText().trim();
        String record = txtRecord.getText().trim();

        if (!ValidationUtil.isValidDateTime(visitTimeStr)) {
            JOptionPane.showMessageDialog(this, "就诊时间格式错误，正确格式：yyyy-MM-dd HH:mm");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date parsed = sdf.parse(visitTimeStr);
            Timestamp ts = new Timestamp(parsed.getTime());

            Visit visit = new Visit(item.petId, ts, diagnosis, prescription, record);
            int newId = visitDAO.insert(visit);
            if (newId > 0) {
                JOptionPane.showMessageDialog(this, "就诊记录添加成功！");
                txtDiagnosis.setText("");
                txtPrescription.setText("");
                txtRecord.setText("");
                refreshVisitTable(item.petId);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "添加失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 删除就诊记录 */
    private void handleDeleteVisit() {
        int row = visitTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一条就诊记录");
            return;
        }
        int visitId = (int) visitTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定删除该就诊记录？", "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (visitDAO.delete(visitId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                PetItem item = (PetItem) cmbPet.getSelectedItem();
                if (item != null) refreshVisitTable(item.petId);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 刷新就诊记录表格 */
    private void refreshVisitTable(int petId) {
        visitTableModel.setRowCount(0);
        try {
            List<Visit> visits = visitDAO.findByPetId(petId);
            for (Visit v : visits) {
                Vector<Object> row = new Vector<>();
                row.add(v.getId());
                row.add(v.getPetId());
                row.add(v.getVisitTime());
                row.add(v.getDiagnosis());
                row.add(v.getPrescription());
                row.add(v.getRecord());
                visitTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载就诊记录失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 刷新宠物下拉框 */
    public void refreshPetComboBox() {
        cmbPet.removeAllItems();
        try {
            List<Pet> pets = petDAO.findAllIdAndName();
            for (Pet p : pets) {
                cmbPet.addItem(new PetItem(p.getId(), p.getName(), p.getSpecies()));
            }
            if (cmbPet.getItemCount() > 0) {
                cmbPet.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载宠物列表失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 下拉框条目 — 包装宠物 ID + 名称，显示友好文字
     */
    private static class PetItem {
        int petId;
        String label;

        PetItem(int petId, String name, String species) {
            this.petId = petId;
            this.label = "[" + petId + "] " + name + " (" + species + ")";
        }

        @Override
        public String toString() { return label; }
    }
}
