# 宠物医院就诊管理系统

基于 Java Swing + JDBC + MySQL 的桌面 GUI 应用，实现宠物医院日常就诊业务的信息化管理。

## 功能模块

### 1. 宠物信息管理
- 添加新宠物档案（名字、种类、生日、品种、体重、家长信息）
- 修改 / 删除已有宠物信息
- 电话格式校验、体重范围校验、日期格式校验
- 删除宠物时级联删除其全部就诊记录

### 2. 就诊记录管理
- 为指定宠物添加就诊记录（就诊时间、诊断、处方/药品、详细记录）
- 查看某宠物的全部就诊历史（时间倒序）
- 删除错误或过期的就诊记录

### 3. 信息查询
- 按家长姓名或电话号码模糊搜索宠物
- 点击搜索结果自动展示该宠物的完整就诊历史
- 诊断、处方、详细记录分层展示

## 技术栈

| 层次 | 技术 |
|------|------|
| 前端 | Java Swing, GridBagLayout, JTabbedPane |
| 数据库 | MySQL 8.0, JDBC |
| 架构 | 三层分离：UI 层 → DAO 层 → Database（模型层传数据） |

## 项目结构

```
pet-hospital/
├── src/pet/
│   ├── config/
│   │   └── DatabaseConfig.java   # 数据库连接配置（单例模式）
│   ├── model/
│   │   ├── Pet.java              # 宠物实体类
│   │   └── Visit.java            # 就诊记录实体类
│   ├── dao/
│   │   ├── PetDAO.java           # 宠物数据访问（增删改查）
│   │   └── VisitDAO.java         # 就诊数据访问（增删查）
│   ├── ui/
│   │   ├── PetHospitalGUI.java   # 主窗口入口
│   │   ├── PetPanel.java         # 宠物管理标签页
│   │   ├── VisitPanel.java       # 就诊管理标签页
│   │   └── SearchPanel.java      # 信息查询标签页
│   └── util/
│       └── ValidationUtil.java   # 输入校验工具类
├── db.properties                 # 数据库连接配置
├── schema.sql                    # 数据库初始化脚本
└── README.md
```

## 快速开始

### 1. 环境要求

- JDK 8+
- MySQL 8.0+
- mysql-connector-java.jar

### 2. 创建数据库

在 MySQL 中执行 `schema.sql`：

```bash
mysql -u root -p < schema.sql
```

脚本会自动创建数据库、表结构和 3 条示例数据。

### 3. 配置数据库连接

编辑 `db.properties`，修改为你的 MySQL 密码：

```properties
db.url=jdbc:mysql://localhost:3306/pet_hospital?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=你的密码
```

### 4. 编译运行

```bash
# 编译
javac -cp ".;mysql-connector-java-8.0.33.jar" -encoding UTF-8 -d out src/pet/ui/PetHospitalGUI.java

# 运行
java -cp "out;.;mysql-connector-java-8.0.33.jar" pet.ui.PetHospitalGUI
```

### 5. 截图（替换为实际截图）

> 运行后截取三个标签页的界面，替换此处的占位说明。

## 数据库 ER 图

```
┌──────────────────┐         ┌──────────────────────────┐
│       pet        │         │          visit           │
├──────────────────┤         ├──────────────────────────┤
│ id (PK)          │◄────────│ pet_id (FK → pet.id)     │
│ name             │ 1    N  │ id (PK)                  │
│ species          │         │ visit_time               │
│ birthday         │         │ diagnosis                │
│ breed            │         │ prescription             │
│ weight           │         │ record                   │
│ owner_name       │         │ created_at               │
│ owner_phone      │         └──────────────────────────┘
│ created_at       │
└──────────────────┘
```

## 作者

- 昂翁曲绕 24090057
- 计算机科学与技术 24级
