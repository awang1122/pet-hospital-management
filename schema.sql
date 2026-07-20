-- ============================================
-- 宠物医院就诊管理系统 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS pet_hospital
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE pet_hospital;

-- 宠物信息表
CREATE TABLE IF NOT EXISTS pet (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL COMMENT '宠物名字',
    species     VARCHAR(20)  NOT NULL COMMENT '种类：猫/狗/其他',
    birthday    DATE         DEFAULT NULL COMMENT '生日',
    breed       VARCHAR(50)  DEFAULT NULL COMMENT '品种',
    weight      DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    owner_name  VARCHAR(50)  NOT NULL COMMENT '家长姓名',
    owner_phone VARCHAR(20)  NOT NULL COMMENT '家长电话',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息';

-- 就诊记录表
CREATE TABLE IF NOT EXISTS visit (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    pet_id       INT          NOT NULL COMMENT '宠物ID',
    visit_time   DATETIME     NOT NULL COMMENT '就诊时间',
    diagnosis    VARCHAR(500) DEFAULT NULL COMMENT '诊断结果',
    prescription VARCHAR(500) DEFAULT NULL COMMENT '处方/药品',
    record       TEXT         DEFAULT NULL COMMENT '就诊详细记录',
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊记录';

-- 示例数据
INSERT INTO pet (name, species, birthday, breed, weight, owner_name, owner_phone) VALUES
('豆豆',   '狗', '2022-03-15', '金毛',   28.5, '张三', '13800001111'),
('咪咪',   '猫', '2023-07-20', '英短',    4.2, '李四', '13900002222'),
('小灰灰', '其他', '2024-01-10', '荷兰兔',  1.8, '王五', '13700003333');

INSERT INTO visit (pet_id, visit_time, diagnosis, prescription, record) VALUES
(1, '2026-06-15 09:30:00', '左后腿轻度跛行', '关节康 2粒/日 × 7天', '运动后跛行加重，触诊髋关节无明显异常，建议减少剧烈运动'),
(1, '2026-06-28 14:00:00', '复查：跛行已好转', '关节康 1粒/日 × 7天（减量）', '行走步态基本正常，继续观察一周'),
(2, '2026-06-20 10:00:00', '轻度牙龈炎', '口腔喷剂 2次/日 × 5天', '右侧牙龈红肿，建议更换软质猫粮');
