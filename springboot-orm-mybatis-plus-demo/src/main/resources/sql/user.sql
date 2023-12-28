DROP TABLE IF EXISTS user;

CREATE TABLE `user`
(
    `id`    bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`  varchar(30) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
    `age`   int                             DEFAULT NULL COMMENT '年龄',
    `sex`   int                             DEFAULT NULL COMMENT '性别',
    `email` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

INSERT INTO user (name, age, sex, email)
VALUES ('Jone', 18, 0, 'test1@baomidou.com'),
       ('Jack', 20, 1, 'test2@baomidou.com'),
       ('Tom', 28, 1, 'test3@baomidou.com'),
       ('Sandy', 21, 0, 'test4@baomidou.com'),
       ('Billie', 24, 1, 'test5@baomidou.com');
