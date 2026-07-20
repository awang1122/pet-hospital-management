package pet.ui;

import pet.dao.PetDAO;
import pet.model.Pet;
import pet.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * 宠物信息管理面板 — 增删改查宠物
 */
public class PetPanel extends JPanel {

    private final PetDAO petDAO;
    private JTable petTable;
    private DefaultTableModel tableModel;

    // 输入控件
    private JTextField txtName, txtBirthday, txtBreed, txtWeight, txtOwnerName, txtOwnerPhone;
    private JComboBox<String> cmbSpecies;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // 当前选中的宠物 ID（-1 表示无选中）
    private int selectedPetId = -1;

    public PetPanel() {
        this.petDAO = new PetDAO();
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---- 输入区 ----
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("宠物信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第1行：名字 + 种类
        addField(inputPanel, gbc, "名字:", txtName = new JTextField(), 0, 0);
        addField(inputPanel, gbc, "种类:", cmbSpecies = new JComboBox<>(
                new String[]{"猫", "狗", "兔", "鸟", "鱼", "其他"}), 1, 0);

        // 第2行：生日 + 品种
        addField(inputPanel, gbc, "生日:", txtBirthday = new JTextField("2020-01-01"), 0, 1);
        addField(inputPanel, gbc, "品种:", txtBreed = new JTextField(), 1, 1);

        // 第3行：体重 + 家长姓名
        addField(inputPanel, gbc, "体重(kg):", txtWeight = new JTextField(), 0, 2);
        addField(inputPanel, gbc, "家长姓名:", txtOwnerName = new JTextField(), 1, 2);

        // 第4行：家长电话
        addField(inputPanel, gbc, "家长电话:", txtOwnerPhone = new JTextField(), 0, 3);

        // ---- 按钮区 ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd = new JButton("添加宠物");
        btnUpdate = new JButton("修改宠物");
        btnDelete = new JButton("删除宠物");
        btnClear = new JButton("清空表单");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        // 组合顶部
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        // ---- 表格区 ----
        tableModel = new DefaultTableModel(
                new String[]{"ID", "名字", "种类", "生日", "品种", "体重(kg)", "家长", "电话"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        petTable = new JTable(tableModel);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(petTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ---- 事件绑定 ----
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());

        // 表格选中行 → 回填表单
        petTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = petTable.getSelectedRow();
                if (row >= 0) {
                    fillFormFromRow(row);
                }
            }
        });
    }

    /** 辅助方法：添加标签+输入框到 GridBagLayout */
    private void addField(JPanel panel, GridBagConstraints gbc,
                          String label, JComponent comp, int col, int row) {
        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = col * 2 + 1;
        gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    /** 从表格行回填表单 */
    private void fillFormFromRow(int row) {
        selectedPetId = (int) tableModel.getValueAt(row, 0);
        txtName.setText((String) tableModel.getValueAt(row, 1));
        cmbSpecies.setSelectedItem(tableModel.getValueAt(row, 2));
        txtBirthday.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtBreed.setText((String) tableModel.getValueAt(row, 4));
        txtWeight.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtOwnerName.setText((String) tableModel.getValueAt(row, 6));
        txtOwnerPhone.setText((String) tableModel.getValueAt(row, 7));
    }

    /** 清空表单 */
    private void clearForm() {
        selectedPetId = -1;
        txtName.setText("");
        txtBirthday.setText("2020-01-01");
        txtBreed.setText("");
        txtWeight.setText("");
        txtOwnerName.setText("");
        txtOwnerPhone.setText("");
        cmbSpecies.setSelectedIndex(0);
        petTable.clearSelection();
    }

    /** 添加宠物 */
    private void handleAdd() {
        Pet pet = validateAndBuild();
        if (pet == null) return;

        try {
            int newId = petDAO.insert(pet);
            if (newId > 0) {
                JOptionPane.showMessageDialog(this, "添加成功！宠物编号: " + newId);
                refreshTable();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "添加失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 修改宠物 */
    private void handleUpdate() {
        if (selectedPetId <= 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一只宠物");
            return;
        }
        Pet pet = validateAndBuild();
        if (pet == null) return;
        pet.setId(selectedPetId);

        int confirm = JOptionPane.showConfirmDialog(this,
                "确认修改宠物 \"" + pet.getName() + "\" 的信息？", "确认修改", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (petDAO.update(pet)) {
                JOptionPane.showMessageDialog(this, "修改成功！");
                refreshTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "修改失败：宠物不存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "修改失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 删除宠物 */
    private void handleDelete() {
        if (selectedPetId <= 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一只宠物");
            return;
        }
        String petName = txtName.getText();
        int confirm = JOptionPane.showConfirmDialog(this,
                "删除宠物 \"" + petName + "\" 将同时删除其所有就诊记录。\n确定删除？",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (petDAO.delete(selectedPetId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败：宠物不存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 表单校验 → 构建 Pet 对象；校验失败返回 null */
    private Pet validateAndBuild() {
        String name = txtName.getText().trim();
        String ownerName = txtOwnerName.getText().trim();
        String ownerPhone = txtOwnerPhone.getText().trim();
        String birthday = txtBirthday.getText().trim();

        if (!ValidationUtil.isNotEmpty(name)) {
            JOptionPane.showMessageDialog(this, "宠物名字不能为空");
            txtName.requestFocus();
            return null;
        }
        if (!ValidationUtil.isNotEmpty(ownerName)) {
            JOptionPane.showMessageDialog(this, "家长姓名不能为空");
            txtOwnerName.requestFocus();
            return null;
        }
        if (!ValidationUtil.isNotEmpty(ownerPhone)) {
            JOptionPane.showMessageDialog(this, "家长电话不能为空");
            txtOwnerPhone.requestFocus();
            return null;
        }
        if (!ValidationUtil.isValidPhone(ownerPhone)) {
            JOptionPane.showMessageDialog(this, "电话格式不正确（应为11位手机号）");
            txtOwnerPhone.requestFocus();
            return null;
        }
        if (!ValidationUtil.isValidDate(birthday)) {
            JOptionPane.showMessageDialog(this, "生日格式错误，正确格式：yyyy-MM-dd");
            txtBirthday.requestFocus();
            return null;
        }

        double weight = 0;
        String weightStr = txtWeight.getText().trim();
        if (!weightStr.isEmpty()) {
            try {
                weight = Double.parseDouble(weightStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "体重请输入数字");
                txtWeight.requestFocus();
                return null;
            }
            if (!ValidationUtil.isValidWeight(weight)) {
                JOptionPane.showMessageDialog(this, "体重应在 0 ~ 200 kg 之间");
                txtWeight.requestFocus();
                return null;
            }
        }

        Pet pet = new Pet();
        pet.setName(name);
        pet.setSpecies((String) cmbSpecies.getSelectedItem());
        pet.setBirthday(Date.valueOf(birthday));
        pet.setBreed(txtBreed.getText().trim());
        pet.setWeight(weight);
        pet.setOwnerName(ownerName);
        pet.setOwnerPhone(ownerPhone);
        return pet;
    }

    /** 刷新表格数据 */
    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Pet> pets = petDAO.findAll();
            for (Pet p : pets) {
                Vector<Object> row = new Vector<>();
                row.add(p.getId());
                row.add(p.getName());
                row.add(p.getSpecies());
                row.add(p.getBirthday());
                row.add(p.getBreed());
                row.add(p.getWeight());
                row.add(p.getOwnerName());
                row.add(p.getOwnerPhone());
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载宠物列表失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
