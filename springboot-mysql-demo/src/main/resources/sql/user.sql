DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id    BIGINT(20)  NOT NULL COMMENT '主键ID',
    name  VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age   INT(20)     NULL DEFAULT NULL COMMENT '年龄',
    sex   INT(1)      NULL DEFAULT NULL COMMENT '性别',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (id)
);

INSERT INTO user (id, name, age, sex, email)
VALUES (1, 'Jone', 18, 0, 'test1@baomidou.com'),
       (2, 'Jack', 20, 1, 'test2@baomidou.com'),
       (3, 'Tom', 28, 1, 'test3@baomidou.com'),
       (4, 'Sandy', 21, 0, 'test4@baomidou.com'),
       (5, 'Billie', 24, 1, 'test5@baomidou.com');
