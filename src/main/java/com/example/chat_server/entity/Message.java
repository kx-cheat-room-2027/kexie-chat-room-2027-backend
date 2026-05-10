package com.example.chat_server.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "message", autoResultMap = true)
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    /**
     * 消息发送方id
     */
    @TableField("from_id")
    private String fromId;

    /**
     * 消息内容
     */
    @TableField(value = "msg_content", typeHandler = JacksonTypeHandler.class)
    private MsgContent msgContent;

    /**
     * 是否显示时间
     */
    @TableField("is_show_time")
    private Boolean isShowTime;


    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    //格式化日期时间在序列化（Java → JSON）和反序列化（JSON → Java）时的格式。
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
