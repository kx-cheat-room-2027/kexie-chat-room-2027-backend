package com.example.chat_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.chat_server.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
