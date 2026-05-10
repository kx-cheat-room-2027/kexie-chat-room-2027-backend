CREATE DATABASE IF NOT EXISTS chatroom DEFAULT CHARSET utf8mb4;
USE chatroom;
create table chatroom.message
(
    id           varchar(64)      not null
        primary key,
    from_id      varchar(64)      not null comment '消息发送方id',
    is_show_time bit default b'0' null comment '是否显示时间',
    msg_content  text             null comment '消息内容',
    create_time  timestamp(3)     not null comment '创建时间',
    update_time  timestamp(3)     not null comment '更新时间'
)
    comment '消息表' row_format = DYNAMIC;

create table chatroom.msgcontent
(
    formUserId       varchar(20) not null comment '发送方用户id',
    formUserName     varchar(20) not null comment '发送方昵称',
    formUserPortrait int         null comment '发送方头像',
    type             varchar(10) not null comment '消息类型',
    content          text        null comment '文本或者json'
);

create table chatroom.user
(
    id          varchar(64)  not null
        primary key,
    account     varchar(64)  not null comment '用户账号',
    name        varchar(200) not null comment '用户名',
    portrait    text         null comment '头像',
    password    varchar(200) not null comment '密码',
    sex         varchar(64)  null comment '性别',
    email       varchar(200) null comment '邮箱',
    create_time timestamp(3) not null comment '创建时间',
    update_time timestamp(3) not null comment '更新时间'
)
    comment '用户表' row_format = DYNAMIC;

